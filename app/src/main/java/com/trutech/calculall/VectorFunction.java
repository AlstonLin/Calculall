package com.trutech.calculall;

/**
 * Object representation of a Function used with Vectors.
 *
 * @author Alston Lin
 * @version Alpha 2.0
 */
public abstract class VectorFunction extends Token {
    public static final int ABS = 1, UNIT = 2;
    private int type;

    public VectorFunction(String symbol, int type) {
        super(symbol);
        this.type = type;
    }

    public abstract Token perform(Vector v);
}
