package com.trutech.calculall;

/**
 * A token that holds a String for display purposes
 */
public class StringToken extends Token {
    /**
     * Creates a new Token to be shown on the calculator screen.
     *
     * @param s The string to display
     */
    protected StringToken(String s) {
        super(s);
    }
}
