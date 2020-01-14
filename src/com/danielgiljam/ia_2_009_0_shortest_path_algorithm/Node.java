package com.danielgiljam.ia_2_009_0_shortest_path_algorithm;

import java.util.Vector;

public class Node {

    private final String name;
    private final double latitude;
    private final double longitude;
    private final Vector<Node> neighbours;

    public Node(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.neighbours = new Vector<>();
    }

    public void addNeighbour(Node neighbour) {
        neighbours.add(neighbour);
    }
}
