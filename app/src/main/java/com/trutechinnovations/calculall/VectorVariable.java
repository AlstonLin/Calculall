package com.trutechinnovations.calculall;

import java.io.Serializable;

/**
 * The class definition of a Variable that stores a Vector.
 *
 * @author Keith Wong
 * @version 3.0
 */
public class VectorVariable extends Token implements Serializable {
    public static final int U = 1, V = 2, T = 3, S = 4;
    public static Token uValue, vValue;
    private int type;

    protected VectorVariable(int type, String symbol) {
        super(symbol);
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
