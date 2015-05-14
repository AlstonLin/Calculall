package com.trutechinnovations.calculall;

import java.io.Serializable;

/**
 * Represents a single Digit of a number.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class Digit extends Token implements Serializable {
    private static final long serialVersionUID = 752647221;
    private int value;

    /**
     * Should not be used outside of a factory; to create a type of digit,
     * see class DigitFactory.
     *
     * @param symbol The symbol of the Token to be shown on the calculator screen
     * @param value  The value of the Digit (0 - 9) or as defined by DigitFactory class constants
     */
    protected Digit(String symbol, int value) {
        super(symbol);
        this.value = value;
    }

    /**
     * @return The value of the Digit (0 - 9) or as defined by DigitFactory class constants
     */
    public int getValue() {
        return value;
    }
}
