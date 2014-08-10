package com.trutech.calculall;

/**
 * Receives input from the digits surrounding the operation piece and results in a new number.
 *
 * @version 0.4.0
 */
public abstract class Operator extends Token {

    public static int ADD = 1, SUBTRACT = 2, MULTIPLY = 3, DIVIDE = 4, EXPONENT = 5, PERMUTATION = 6, 
    		COMBINATION = 7, FACTORIAL = 8, VARROOT = 9, DOT = 10, CROSS = 11;
    private int type;
    private int precedence;
    private boolean leftAssociative;

    /**
     * Should not be used outside of a factory; to create a type of operation,
     * see class OperatorFactory.
     *
     * @param symbol The symbol of the Token to be shown on the calculator screen
     * @param type The type of Operator (defined by the class constants)
     * @param precedence Defines the order the operator is used (bigger = higher priority)
     * @param leftAssociative If the operator is left or right associative
     */
    protected Operator(String symbol, int type, int precedence, boolean leftAssociative) {
        super(symbol);
        this.leftAssociative = leftAssociative;
        this.type = type;
        this.precedence = precedence;
    }

    /**
     * Performs the operation with the given surrounding values.
     *
     * @param left The value left of the operation
     * @param right The value right of the operation
     * @return The result of the operation
     */
    public abstract double operate (double left, double right);

    /**
     *
     * @return The type of operation this is (see class constants for possible values)
     */
    public int getType() {
        return type;
    }

    /**
     *
     * @return true if the operator is left associative, false if it's right
     */
    public boolean isLeftAssociative() {
        return leftAssociative;
    }

    /**
     *
     * @return The order of which the Operator is operated (higher = more priority)
     */
    public int getPrecedence() {
        return precedence;
    }
}
