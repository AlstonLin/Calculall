package com.trutechinnovations.calculall;

import java.io.Serializable;

/**
 * Object representation of an Operator to be used by Vectors.
 *
 * @author Alston Lin
 * @version 3.0
 */
public abstract class VectorOperator extends Token implements Serializable, OrderComparable {
    public static final int ADD = 1, SUBTRACT = 2, DOT = 3, CROSS = 4, ANGLE = 5;
    private int type;
    private int precedence;
    private boolean leftAssociative;

    public VectorOperator(String symbol, int type, int precedence, boolean leftAssociative) {
        super(symbol);
        this.type = type;
        this.precedence = precedence;
        this.leftAssociative = leftAssociative;
    }

    public abstract Token operate(Token left, Token right);

    public int getPrecedence() {
        return precedence;
    }

    public boolean isLeftAssociative() {
        return leftAssociative;
    }

    public int getType() {
        return type;
    }

}
