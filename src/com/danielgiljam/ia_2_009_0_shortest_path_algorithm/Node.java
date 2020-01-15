package com.danielgiljam.ia_2_009_0_shortest_path_algorithm;

import java.util.*;

class Node {

    private final String name;
    private final GeoCoordinate location;
    private final Neighbours neighbours = new Neighbours();
    private final RoutingTable routingTable = new RoutingTable();

    Node(final String name, final double latitude, final double longitude) {
        this.name = name;
        location = new GeoCoordinate(latitude, longitude);
    }

    String getName() {
        return name;
    }

    String[] getNeighbourNames() {
        return neighbours.keySet().toArray(String[]::new);
    }

    void addNeighbour(final Node node) {
        neighbours.put(node);
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

        Neighbours(final Neighbours neighbours) {
            super(neighbours);
        }

        Neighbours() {
            super();
        }

        void put(final Node node) {
            super.put(node.name, new Neighbour(node));
        }

        private class Neighbour extends NodeWrapper {

            Neighbour(Node node) {
                super(node, GeoCoordinate.getDistance(location, node.location));
                routingTable.put(node, node, this.distance);
            }
        }
    }

    private static class RoutingTable extends HashMap<String, RoutingTable.Route> {

        private RoutingTable(final RoutingTable routingTable) {
            super(routingTable);
        }

        private RoutingTable() {
            super();
        }

        private void put(final Node nextHop, final Node destination, final double distance) {
            super.put(destination.name, new Route(nextHop, destination, distance));
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

            Node nextHop() {
                return !hops.isEmpty() ? hops.get(hops.size() - 1) : null;
            }
        }
    }
}
