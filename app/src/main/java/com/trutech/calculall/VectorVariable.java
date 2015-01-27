package com.trutech.calculall;

import java.io.Serializable;

/**
 * Created by Keith on 2015-01-16.
 */
public class VectorVariable extends Token implements Serializable {
    static final int u = 1,v =2;
    static Vector uValue, vValue;
    int type;

    protected VectorVariable(int type, String symbol){
        super(symbol);
        this.type = type;
    }
}
