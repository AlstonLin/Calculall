package com.trutechinnovations.calculall;

/**
 * Object representation of a Function used with Vectors.
 *
 * @author Alston Lin
 * @version 3.0
 */
public abstract class VectorFunction extends Token {
    public static final int MAGNITUDE = 1, UNIT = 2, PROJ = 3;
    private int type;

    public VectorFunction(String symbol, int type) {
        super(symbol);
        this.type = type;
    }

    public abstract Token perform(Vector v);
}
