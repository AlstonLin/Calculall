package com.trutechinnovations.calculall;

import java.io.Serializable;

/**
 * A Token that holds a String for display purposes.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class StringToken extends Token implements Serializable {
    /**
     * Creates a new Token to be shown on the calculator screen.
     *
     * @param s The string to display
     */
    protected StringToken(String s) {
        super(s);
    }
}
