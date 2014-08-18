package com.trutech.calculall;

import java.util.ArrayList;

/**
 * Created by Jason on 2014-08-14.
 */
public abstract class Vector extends Token {
    public static final int A = 1, B = 2, C = 3, X = 4, Y = 5, Z = 6;
    public static ArrayList<Token> a_value=null, b_value=null, c_value=null, x_value=null, y_value=null, z_value=null; //Values of the variables

    private int type;
    private String symbol;

    /**
     * Constructor for a Token that represents a user-defined value (algebraic variable)
     *
     * @param type The type of variable as defined by the class constants
     * @param symbol The symbol as it will be shown as on the display
     */
    protected Vector(int type, String symbol) {
        super(symbol);
        this.symbol = symbol;
        this.type = type;
    }

    public String getSymbol () {
        return symbol;
    }
    public int getType(){
        return type;
    }


    /**
     * @return The vector
     */
    public abstract ArrayList<Token> getVector();

}
