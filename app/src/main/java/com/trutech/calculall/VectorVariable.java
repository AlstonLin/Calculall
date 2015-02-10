package com.trutech.calculall;

import java.io.Serializable;

/**
 * The class definition of a Variable that stores a Vector.
 *
 * @version Alpha 2.2
 * @author Keith Wong
 */
public class VectorVariable extends Token implements Serializable {
    public static final int U = 1,V = 2, T = 3;
    public static Token uValue, vValue;
    private int type;

    protected VectorVariable(int type, String symbol){
        super(symbol);
        this.type = type;
    }
}
