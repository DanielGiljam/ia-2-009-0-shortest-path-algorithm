package com.danielgiljam.ia_2_009_0_pathfinder;

import java.util.*;
import java.util.concurrent.CompletableFuture;

class Node {

    private final String name;
    private final GeoCoordinate location;
    private final Neighbours neighbours;
    private final RoutingTable routingTable;

    private CompletableFuture<Void> routingTableBroadcast;

    Node(final String name, final double latitude, final double longitude) {
        this.name = name;
        location = new GeoCoordinate(latitude, longitude);
        neighbours = new Neighbours();
        routingTable = new RoutingTable(this);
    }

    String getName() {
        return name;
    }

    String[] getNeighbourNames() {
        return neighbours.keySet().toArray(String[]::new);
    }

    String[] getDestinationNames() {
        return routingTable.keySet().toArray(String[]::new);
    }

    void addNeighbour(final Node node) {
        neighbours.put(node);
        node.neighbours.put(this);
    }

    void printRouteTo(final String destination) {
        final RoutingTable.Route route = routingTable.get(destination);
        if (route.hops.size() == 0) {
            System.out.println("Du har angett samma ort som starpunkt och destination.");
            return;
        } else {
            int i = 1;
            System.out.printf("%d. %s%n", i, this.name);
            i++;
            final List<Node> hops = new ArrayList<>(route.hops);
            Collections.reverse(hops);
            for (final Node hop : hops) {
                System.out.printf("%d. %s%n", i, hop.name);
                i++;
            }
        }
        System.out.printf("%nDistans: %.0f km%n", route.distance);
    }

    private void broadcastRoutingTable() {
        routingTableBroadcast = CompletableFuture.supplyAsync(
                () -> {
                    RoutingTable neighboursRoutingTable;
                    double distance;
                    List<Node> hops;
                    for (final Neighbours.Neighbour neighbour : neighbours.values()) {
                        neighboursRoutingTable = neighbour.node.routingTable;
                        boolean newRoutesWereAdded = false;
                        for (final RoutingTable.Route route : routingTable.values()) {
                            distance = route.distance + neighbour.distance;
                            if (neighboursRoutingTable.accepts(route.name, distance)) {
                                hops = new ArrayList<>(route.hops);
                                hops.add(this);
                                neighboursRoutingTable.put(route.node, hops, distance);
                                newRoutesWereAdded = true;
                            }
                        }
                        if (newRoutesWereAdded) neighbour.node.broadcastRoutingTable();
                    }
                    return null;
                }
        );
    }

    static boolean routingTablesAreReady(final HashMap<String, Node> nodes) {
        boolean ready = true;
        for (final Node node : nodes.values()) {
            if (node.routingTableBroadcast != null && !node.routingTableBroadcast.isDone()) ready = false;
        }
        return ready;
    }

    private static class NodeWrapper {
        protected final String name;
        protected final Node node;
        protected final double distance;
        NodeWrapper(final Node node, final double distance) {
            this.name = node.name;
            this.node = node;
            this.distance = distance;
        }
    }

    private class Neighbours extends HashMap<String, Neighbours.Neighbour> {

        Neighbours() {
            super();
        }

        void put(final Node node) {
            super.put(node.name, new Neighbour(node));
        }

        private class Neighbour extends NodeWrapper {

            Neighbour(final Node node) {
                super(node, GeoCoordinate.getDistance(location, node.location));
                if (routingTable.accepts(node.name, this.distance)) routingTable.put(node, node, this.distance);
                broadcastRoutingTable();
            }
        }
    }

    private static class RoutingTable extends HashMap<String, RoutingTable.Route> {

        private RoutingTable(final Node node) {
            this.put(node.name, new Route(node, new ArrayList<>(), 0));
        }

        private void put(final Node destination, final Node nextHop, final double distance) {
            super.put(destination.name, new Route(destination, nextHop, distance));
        }

        private void put(final Node destination, final List<Node> hops, final double distance) {
            super.put(destination.name, new Route(destination, hops, distance));
        }

        private boolean accepts(final String key, final double distance) {
            return !this.containsKey(key) || distance < this.get(key).distance;
        }

        private static class Route extends NodeWrapper {

            private final List<Node> hops = new ArrayList<>();

            private Route(final Node destination, final Node nextHop, final double distance) {
                super(destination, distance);
                hops.add(nextHop);
            }

            private Route(final Node destination, final List<Node> hops, final double distance) {
                super(destination, distance);
                this.hops.addAll(hops);
            }
        }
    }
}
