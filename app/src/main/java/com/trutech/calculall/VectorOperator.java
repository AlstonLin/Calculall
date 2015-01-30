package com.trutech.calculall;

import java.io.Serializable;

/**
 * Object representation of an Operator to be used by Vectors.
 *
 * @author Alston Lin
 * @version Alpha 2.0
 */
public abstract class VectorOperator extends Token implements Serializable {
    public static final int ADD = 1, SUBTRACT = 2, DOT = 3, CROSS = 4, ANGLE = 5;
    private int type;
    private int precedence;

    public VectorOperator(String symbol, int type, int precedence) {
        super(symbol);
        this.type = type;
        this.precedence = precedence;
    }

    public abstract Vector operate(Vector left, Vector right);

}
