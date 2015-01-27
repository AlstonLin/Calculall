package com.trutech.calculall;

/**
 * Created by Alston on 1/13/2015.
 */
public abstract class VectorFunction extends Token{
    public static final int ABS = 1, UNIT = 2;
    private int type;

    public VectorFunction(String symbol, int type){
        super(symbol);
        this.type = type;
    }

    public abstract Token perform(Vector v);
}
