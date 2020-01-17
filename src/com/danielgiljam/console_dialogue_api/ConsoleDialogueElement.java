package com.danielgiljam.console_dialogue_api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>The Console Dialogue Element Class</h1>
 * Responsible for the detecting and executing of one console dialogue command or the interpreting and parsing of one kind of expected user input
 * <p>
 * The second of the two parts that make up my ConsoleDialogueAPI, a lightweight programming interface for creating console dialogues.
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
public class ConsoleDialogueElement {

    /**
     * A slot for the value returned by the check() -function to the "last word" boolean in the process communication variable in the ConsoleDialogue() -method.
     */
    boolean lastWord;

    /**
     * A slot for the code that should be executed after a positive check.
     */
    private Runnable action;

    /**
     * The Pattern object – initiated by the constructor with the regex string provided as parameter.
     */

    private Pattern pattern;

    /**
     * Used to see if the Pattern object's pattern matches with a given string (user input, of course, in this case).
     */
    public Matcher matcher;

    /**
     * <h2>CDE Constructor</h2>
     * Saves parameters in their corresponding instance-specific variables.
     *
     * @param action   a runnable with the code that should be executed after a positive check
     * @param pattern  a regex string defining the pattern used for matching by this CDE
     * @param terminal if true, means that "the element is terminal" – a "positive check" with this CDE doesn't only stop "the interpreter", but also ends the "prompter" (see comments within the ConsoleDialogue() -method's code for explanations on what the words surrounded by double quotation marks mean)
     */
    public ConsoleDialogueElement(Runnable action, String pattern, boolean terminal) {
        this.lastWord = terminal;
        this.action = action;
        this.pattern = Pattern.compile("\\A\\s*" + pattern + "\\s*\\z", Pattern.CASE_INSENSITIVE);
    }

    /**
     * <h3>check</h3>
     * Explained in greater detail in comments within the code.
     *
     * @param input the latest user input string
     * @return array containing two booleans that in the ConsoleDialogue() -method become the new values of the "process communication" variables
     */
    boolean[] check(String input) {

        // refreshes Matcher with the latest user input string
        matcher = pattern.matcher(input);

        // If this instance's Pattern matches with the string,
        // the runnable is executed,
        // and new values are returned to the process communicator in the ConsoleDialogue() -method,
        // telling "the interpreter" to stop, and - depending on the value of the lastWord variable - telling the "prompter" whether to stop or not.
        // In this case the check is positive.
        // Else, the "passive" values are returned to the process communicator in the ConsoleDialogue() -method,
        // ergo values that won't trigger any changes in the loops.
        // In this case the check is negative.
        if (matcher.matches()) {
            action.run();
            return new boolean[]{lastWord, true};
        } else return new boolean[]{false, false};
    }
}
