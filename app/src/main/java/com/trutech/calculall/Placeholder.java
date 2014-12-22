package com.trutech.calculall;

import java.io.Serializable;

/**
 * Tokens that have no function at all, simply used to mark a point and add a visual.
 */
public class Placeholder extends Token implements Serializable {

    public static final int BLOCK = 0;
    private int type;

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
