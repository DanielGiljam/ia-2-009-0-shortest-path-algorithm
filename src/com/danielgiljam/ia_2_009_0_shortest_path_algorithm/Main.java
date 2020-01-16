package com.danielgiljam.ia_2_009_0_shortest_path_algorithm;

import java.util.Vector;

public class Main {

    public static void main(String[] args) {
	    final Vector<Node> nodes = createGraph();
	    showNodesAndLinks(nodes);
    }

    private static Vector<Node> createGraph() {

        final Node hki = new Node("Helsingfors", 60.1640504, 24.7600896);
        final Node tpe = new Node("Tammerfors", 61.6277369, 23.5501169);
        final Node tku = new Node("Åbo", 60.4327477, 22.0853171);
        final Node jyv = new Node("Jyväskylä", 62.1373432, 25.0954598);
        final Node kpo = new Node("Kuopio", 62.9950487, 26.556762);
        final Node lhi = new Node("Lahtis", 60.9948736, 25.5747703);

        hki.addNeighbour(tpe); // Tammerfors
        hki.addNeighbour(tku); // Åbo
        hki.addNeighbour(lhi); // Lahtis

        tpe.addNeighbour(hki); // Helsingfors
        tpe.addNeighbour(tku); // Åbo
        tpe.addNeighbour(jyv); // Jyväskylä
        tpe.addNeighbour(lhi); // Lahtis

        tku.addNeighbour(hki); // Helsingfors
        tku.addNeighbour(tpe); // Tammerfors

        jyv.addNeighbour(tpe); // Tammerfors

        kpo.addNeighbour(lhi); // Lahtis

        lhi.addNeighbour(hki); // Helsingfors
        lhi.addNeighbour(tpe); // Tammerfors
        lhi.addNeighbour(kpo); // Kuopio

        final Vector<Node> graph = new Vector<>();
        graph.add(hki);
        graph.add(tpe);
        graph.add(tku);
        graph.add(jyv);
        graph.add(kpo);
        graph.add(lhi);

        while (true) {
            if (Node.routingTablesAreReady(graph)) break;
        }

        return graph;
    }

    private static void showNodesAndLinks(final Vector<Node> nodes) {
        for (final Node node : nodes) {
            System.out.println(node.getName());
            for (final String neighbourName : node.getNeighbourNames()) {
                System.out.println("\t" + neighbourName);
            }
            System.out.println();
        }
    }
}
