
package com.trutechinnovations.calculall;

import java.io.Serializable;

/**
 * Tokens that have no function at all, simply used to mark a point and add a visual.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class Placeholder extends Token implements Serializable {

    public static final int SUPERSCRIPT_BLOCK = 0, COMMA = 1, BASE_BLOCK = 2;

    /**
     * Creates a new Placeholder to be shown on the calculator screen.
     *
     * @param symbol The symbol of the Token to be shown on the calculator screen
     */
    protected Placeholder(String symbol, int type) {
        super(symbol);
        this.type = type;
    }

    /**
     * @return The type of Placeholder this is
     */
    public int getType() {
        return type;
    }
}