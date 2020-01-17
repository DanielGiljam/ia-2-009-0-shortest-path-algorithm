package com.danielgiljam.ia_2_009_0_shortest_path_algorithm;

import com.danielgiljam.console_dialogue_api.ConsoleDialogueElement;
import com.danielgiljam.console_dialogue_api.ConsoleDialogueManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {

    private static final HashMap<String, Node> NODES = createGraph();

    private static final String HELP_MESSAGE =
            "Ingen hjälp.\n" +
            "------------------------------------------------\n" +
            "v1.0.0\n\n" +
            "© Daniel Giljam 2020\n" +
            "Den här kommandotolksappen är licensierad under MIT licensen.";

    private static final ConsoleDialogueElement FROM = new ConsoleDialogueElement(
            new Runnable() {
                @Override
                public void run() {
                    final Node from = NODES.get(parseInput(FROM.matcher.group(1)));
                    final List<ConsoleDialogueElement> consoleDialogueElementsLvl2 = new ArrayList<>();
                    consoleDialogueElementsLvl2.add(new ConsoleDialogueElement(
                            () -> {
                                System.out.println(
                                        "\nKORTASTE RUTT" +
                                        "\n------------");
                                from.printRouteTo(parseInput(consoleDialogueElementsLvl2.get(0).matcher.group(1)));
                                System.out.println("\n------------------------------------------------");
                            },
                            createEnumPattern(from.getDestinationNames()),
                            true
                    ));
                    new ConsoleDialogueManager(consoleDialogueElementsLvl2, null, null, "Ange destinationen:", false, false);
                }
            },
            createEnumPattern(NODES.keySet().toArray(String[]::new)),
            false
    );

    public static void main(final String[] args) {
        System.out.println(
                "\nALLA TÅGSTATIONER SAMT DIREKTA JÄRNVÄGSFÖRBINDELSER TILL ANDRA ORTER" +
                "\n------------------------------------------------"
        );
	    showNodesAndLinks();
        System.out.println("\n------------------------------------------------");
        final List<ConsoleDialogueElement> consoleDialogueElements = new ArrayList<>();
        consoleDialogueElements.add(FROM);
        new ConsoleDialogueManager(consoleDialogueElements, null, HELP_MESSAGE, "Ange startpunkten:", false, false);
    }

    private static HashMap<String, Node> createGraph() {

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

        final HashMap<String, Node> graph = new HashMap<>();
        graph.put(hki.getName(), hki);
        graph.put(tpe.getName(), tpe);
        graph.put(tku.getName(), tku);
        graph.put(jyv.getName(), jyv);
        graph.put(kpo.getName(), kpo);
        graph.put(lhi.getName(), lhi);

        while (true) {
            if (Node.routingTablesAreReady(graph)) break;
        }

        return graph;
    }

    private static void showNodesAndLinks() {
        int count = NODES.size();
        for (final Node node : NODES.values()) {
            System.out.println(node.getName());
            for (final String neighbourName : node.getNeighbourNames()) {
                System.out.println("\t" + neighbourName);
            }
            count--;
            if (count != 0) System.out.println();
        }
    }

    private static String createEnumPattern(final String[] values) {
        int count = values.length;
        StringBuilder pattern = new StringBuilder("(");
        for (final String value : values) {
            pattern.append(value);
            count--;
            if (count != 0) pattern.append("|");
        }
        pattern.append(")");
        return pattern.toString();
    }

    private static String parseInput(final String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
