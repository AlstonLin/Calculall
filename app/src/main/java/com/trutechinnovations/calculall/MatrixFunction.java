package com.trutechinnovations.calculall;

/**
 * The Object representation of a mathematical Function that has a Matrix as the input.
 *
 * @author Ejaaz Merali
 * @version 3.0
 */
public abstract class MatrixFunction extends Token {

    public static final int REF = 1, RREF = 2, DET = 3, TRANSPOSE = 4, INVERSE = 5, DIAG = 6,
            EIGENVECT = 7, EIGENVAL = 8, TRACE = 9, RANK = 10, LU = 11;

    /**
     * Should not be used outside of a factory; to create a type of function,
     * see class FunctionFactory.
     *
     * @param symbol The symbol of the Token to be shown on the calculator screen
     * @param type   The type of Function this is, as defined by the class constants
     */
    protected MatrixFunction(String symbol, int type) {
        super(symbol);
        this.type = type;
    }

    /**
     * Performs the function with the given input.
     *
     * @param input The input that is given for the function
     * @return The output of the performed function
     */
    public abstract double[][] perform(double[][] input);

    /**
     * @return The type of Function this is, as defined by the class constants
     */
    public int getType() {
        return type;
    }

}
