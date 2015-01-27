package com.trutech.calculall;

import java.io.Serializable;

/**
 * Created by Alston on 1/13/2015.
 */
public abstract class VectorOperator extends Token implements Serializable{
    public static final int ADD  = 1, SUBTRACT = 2, DOT = 3, CROSS = 4, ANGLE = 5;
    private int type;
    private int precedence;

    public VectorOperator(String symbol, int type, int precedence){
        super(symbol);
        this.type = type;
        this.precedence = precedence;
    }

    public abstract Vector operate (Vector left, Vector right);

}
