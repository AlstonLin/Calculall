package com.trutech.calculall;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jason on 2014-08-14.
 */
public abstract class Vector extends Token implements Serializable {
    public static final int U = 1, V = 2, W = 3, S = 4;
    public static transient ArrayList<Token> u_value = null, v_value = null, w_value = null, s_value = null; //Values of the variables
    private int type;
    private String symbol;

    /**
     * Constructor for a Token that represents a user-defined value (algebraic variable)
     *
     * @param type   The type of variable as defined by the class constants
     * @param symbol The symbol as it will be shown as on the display
     */
    protected Vector(int type, String symbol) {
        super(symbol);
        this.symbol = symbol;
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getType() {
        return type;
    }


    /**
     * @return The vector
     */
    public abstract ArrayList<Token> getVector();

}
