package com.danielgiljam.console_dialogue_api;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>The Console Dialogue Manager Class</h1>
 * As class: manages resources needed to maintain a console dialogue and executes transitions between console dialogue levels.
 * <br>
 * As instance: manages and executes the lifecycle of one level in a console dialogue.
 * <p>
 * First of the two parts that make up my ConsoleDialogueAPI, a lightweight programming interface for creating console dialogues.
 * <p>
 * Abbreviations you might encounter in this documentation:
 * <br>
 * CDM = {@link ConsoleDialogueManager}
 * <br>
 * CDE = {@link ConsoleDialogueElement}
 * <br>
 * <br>
 * [UPDATE 2020-01-12, Daniel Giljam]: Made some improvements and customizations for this specific case/implementation (v1.1-unofficial).
 *
 * @author Daniel Giljam
 * @version 1.1-unofficial
 * @since 2018-02-14
 */
public class ConsoleDialogueManager {

    /**
     * The default message shown when user input didn't match any of the patterns defined in the CDEs.
     */
    private static final String UNABLE_TO_INTERPRET_MESSAGE = "Din inmatning kunde inte tolkas. Skriv \"hjälp\" för hjälp.";

    /**
     * The regex string defining the pattern used for matching by the default help CDE.
     */
    private static final String HELP_PATTERN = "hjälp";

    /**
     * The currently active initial message.
     */
    private static String activeInitialMessage = "";

    /**
     * The message shown by the default help CDE.
     */
    private static String activeHelpMessage = "";

    /**
     * The default active "input feed".
     */
    private static String activeInputFeed = ">";

    /**
     * The default help CDE.
     */
    private static final ConsoleDialogueElement help = new ConsoleDialogueElement(() -> System.out.println("\n" + activeHelpMessage), HELP_PATTERN, false);

    /**
     * Scanner -object for fetching what the user types in the terminal.
     * (The delimiter is set to "\n" so that the Scanner.next() -function delivers everything the user types on the terminal line as one whole string, whitespaces included.)
     */
    private static final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    /**
     * List that hold all the active CDEs + the order they "check" in.
     */
    private static List<ConsoleDialogueElement> activeCDEs = new ArrayList<>();

    /**
     * This instance's CDEs + a variable for potentially backing up previously active CDEs.
     */
    private List<ConsoleDialogueElement> cdElements;
    private List<ConsoleDialogueElement> cdElementsBackup = new ArrayList<>();

    /**
     * Holds instance-specific "initial message" - a message shown at the creation of an CDM
     * + a variable for potentially backing up a previously active inital message.
     */
    private String initialMessage;
    private String initialMessageBackup = "";

    /**
     * This instance's help message + a variable for potentially backing up a previously active help message.
     */
    private String helpMessage;
    private String helpMessageBackup = "";

    /**
     * This instance's input line "prefix" ("input feed") + a variable for potentially backing up a previously
     * active "input feed".
     */
    private String inputFeed;
    private String inputFeedBackup = ">";

    /**
     * <h2>CDM Constructor</h2>
     * Saves parameters in their corresponding instance-specific variables, then sets off the InitializeCD() -method.
     *
     * @param cdElements     a List containing the CDEs
     * @param initialMessage a string that will be the CDM's initial message
     * @param helpMessage    a string that will be set as the CDM's help message
     * @param queue          boolean that, if set to true, tells the CDM to just queue up the CDEs and then move on with the code, rather than start the dialogue immediately
     * @param merge          boolean that if set to true, tells the CDM to not replace the "queue", but rather add on top of what was already there
     */
    public ConsoleDialogueManager(List<ConsoleDialogueElement> cdElements, String initialMessage, String helpMessage, String inputFeed, boolean queue, boolean merge) {
        this.cdElements = cdElements;
        this.initialMessage = initialMessage;
        this.helpMessage = helpMessage;
        this.inputFeed = inputFeed;
        InitializeCD(queue, merge);
    }

    /**
     * <h3>InitializeCD</h3>
     * Method that:
     * 1. Backs up static variables
     * (if previously initialized)
     * 2. Assigns the content of the instance-specific variables to the corresponding static "active" variables
     * (if the merge boolean in the constructor was true, then - as the boolean says - the content of the instance-specific variables
     * will be merged with any content pre-occupying the static "active" variables, else - if merge is false - any pre-occupying content
     * will be overwritten in the static "active" variables)
     * 3. Displays the initial message (if there is one)
     * 4. Starts the ConsoleDialogue() -method
     * (if the queue boolean in the constructor was true, then the ConsoleDialogue() -method won't start,
     * which means this CDM has run it's course)
     *
     * @param queue boolean that, if set to true, tells the CDM to just queue up the CDEs and then move on with the code, rather than start the dialogue immediately (forwarded from constructor)
     * @param merge boolean that if set to true, tells the CDM to not replace the "queue", but rather add on top of what was already there (forwarded from constructor)
     */
    private void InitializeCD(boolean queue, boolean merge) {
        cdElementsBackup = activeCDEs;
        initialMessageBackup = activeInitialMessage;
        helpMessageBackup = activeHelpMessage;
        inputFeedBackup = activeInputFeed;
        if (merge) {
            activeCDEs.addAll(0, cdElements);
            activeInitialMessage += "\n" + initialMessage;
            if (helpMessage != null) activeHelpMessage += "\n" + helpMessage;
            if (inputFeed != null) activeInputFeed = inputFeed;
        } else {
            activeCDEs = cdElements;
            activeInitialMessage = initialMessage;
            if (helpMessage != null) activeHelpMessage = helpMessage;
            activeInputFeed = inputFeed != null ? inputFeed : "> ";
        }
        if (initialMessage != null) System.out.println("\n" + initialMessage);
        if (!queue) ConsoleDialogue();
    }

    /**
     * <h3>ConsoleDialogue</h3>
     * The essence of this class. Explained in greater detail in comments within the code.
     */
    public void ConsoleDialogue() {

        // Variable name stands for "process communication"
        // and refers to its purpose to communicate to this method's loops when to stop.
        // First boolean - processComm[0] or the "last word" - communicates with the while -loop
        // and the second boolean - processComm[1] or "the aha" - communicates with the foreach -loop nested inside the while -loop.
        boolean[] processComm = new boolean[]{false, false};

        // The while -loop - or the "prompter" - "keeps the dialogue going" by prompting the user for new inputs.
        // When an input is received it runs it through a nested foreach -loop plus some default lines of code
        // that spit out an answer and/or does some actions based on the input.
        // Then - if "the last word still hasn't been said" - the while -loop simply prompts the user for more input and "the wheel keeps turning".
        while (!processComm[0]) {

            // "listening" for user input
            System.out.print("\n" + activeInputFeed + " ");
            final String input = scanner.next();

            // resetting "the aha"
            processComm[1] = false;

            // The foreach -loop - or "the interpreter" - interprets the input by running it through each CDEs check() -function.
            // "The aha" controls when the loop "stops" by rendering the remaining iterations redundant because of the non-matching if -statement conditions.
            for (ConsoleDialogueElement CDElement : activeCDEs) {
                if (!processComm[1]) processComm = CDElement.check(input);
            }

            // if "the aha" never occurred and there is a help message to display, and the users input checks positively with the help CDE, then this will display the help message
            if (!processComm[1] && !activeHelpMessage.isEmpty()) processComm = help.check(input);

            // if "the aha" never occurred (ergo the user input didn't check positively with any CDE) then this message will display
            if (!processComm[1]) System.out.println(UNABLE_TO_INTERPRET_MESSAGE);

        }

        // When "the last word is said", the collapseCD() -method is triggered, cleaning up after the instance and preparing it for the end of its lifecycle.
        CollapseCD();
    }

    /**
     * <h3>CollapseCD</h3>
     * Restores the backed up values of static variables.
     */
    private void CollapseCD() {
        activeCDEs = cdElementsBackup;
        activeInitialMessage = initialMessageBackup;
        activeHelpMessage = helpMessageBackup;
        activeInputFeed = inputFeedBackup;
    }
}
