package com.trutechinnovations.calculall;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents either a user-defined value or a variable for a function, represented by
 * english letters (ex. x, y, z). It also includes constants as well (such as Pi and e)
 *
 * @author Alston Lin
 * @version Alpha 2.0
 */
public abstract class Variable extends Token implements Serializable {


    public static final int A = 1, B = 2, C = 3, X = 4, Y = 5, PI = 7, E = 8, ANS = 9, CONSTANT = 10;
    public static final int C0 = 11, MU0 = 12;
    public static final double PI_VALUE = Math.PI, E_VALUE = Math.E;
    //public static final double SPEED_OF_LIGHT_VAL = 299792458, MAGNETIC_VAL = (4*Math.PI)*1e-7;
    public boolean negative = false;

    public enum Constant {
        SPEED_OF_LIGHT(299792458), MAGNETIC_VAL((4*Math.PI)*1e-7);
        private double value;

        private Constant(double value) {
            this.value = value;
        }

        public double getValue () {
            return value;
        }


    }


    /**
     * Constructor for a Token that represents a user-defined value (algebraic variable)
     *
     * @param type   The type of variable as defined by the class constants
     * @param symbol The symbol as it will be shown as on the display
     */
    protected Variable(int type, String symbol) {
        super(symbol);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public String getSymbol() {
        String symbol = "";
        if (negative) {
            symbol = "-";
        }
        symbol += super.getSymbol();
        return symbol;
    }


    /**
     * @return The value of the variable/constant
     */
    public abstract ArrayList<Token> getValue();
}
