package com.trutech.calculall;

/**
 * A piece that receives a collection of pieces as an input, and then outputs a resulting number.
 *
 * @version 0.4.0
 */

public abstract class Function extends Token {

    public static final int SIN = 1, COS = 2, TAN = 3, SINH = 4, COSH = 5, TANH = 6, ARCSIN = 7, ARCCOS = 8, ARCTAN = 9,
            ARCSINH = 10, ARCCOSH = 11, ARCTANH = 12, FACTORIAL = 13, ABS = 14, FLOOR = 15, CEILING = 16, LOG10 = 17,
            LN = 19, EXP = 20, SQRT = 21, RECIP = 23, CSC = 24, SEC = 25, COT = 26, ERF = 27, APPELLF1 = 28, ERFI = 29, GAMMA = 30;
    private int type;

    /**
     * Should not be used outside of a factory; to create a type of function,
     * see class FunctionFactory.
     *
     * @param symbol The symbol of the Token to be shown on the calculator screen
     * @param type   The type of Function this is, as defined by the class constants
     */
    protected Function(String symbol, int type) {
        super(symbol);
        this.type = type;
    }

    /**
     * Performs the function with the given input.
     *
     * @param input The input that is given for the function
     * @return The output of the performed function
     */
    public abstract double perform(double input);

    /**
     * @return The type of Function this is, as defined by the class constants
     */
    public int getType() {
        return type;
    }

}
