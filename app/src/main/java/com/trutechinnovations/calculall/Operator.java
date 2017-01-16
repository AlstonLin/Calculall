package com.trutechinnovations.calculall;

import java.io.Serializable;

/**
 * Receives input from the digits surrounding the operation piece and results in a new number.
 *
 * @author Alston Lin, Ejaaz Merali
 * @version 3.0
 */
public abstract class Operator extends Token implements Serializable, OrderComparable {

    public static final int ADD = 1, SUBTRACT = 2, MULTIPLY = 3, DIVIDE = 4, EXPONENT = 5, PERMUTATION = 6,
            COMBINATION = 7, FACTORIAL = 8, VARROOT = 9, FRACTION = 10;
    public static final int ADD_SUBTRACT = 2, MULTIPLY_DIVIDE = 3, EXPONENT_PRECEDENCE = 5; //Precedences
    private int precedence;
    private boolean leftAssociative, commutative, anticommutative, associative;

    /**
     * Should not be used outside of a factory; to create a type of operation,
     * see class OperatorFactory.
     *
     * @param symbol          The symbol of the Token to be shown on the calculator screen
     * @param type            The type of Operator (defined by the class constants)
     * @param precedence      Defines the order the operator is used (bigger = higher priority)
     * @param leftAssociative If the operator is left or right associative
     * @param c               If the operator is commutative, anticommutative, or noncommutative
     * @param a               If the operator is associative
     */
    protected Operator(String symbol, int type, int precedence, boolean leftAssociative, int c, boolean a) {
        super(symbol);
        this.leftAssociative = leftAssociative;
        this.type = type;
        this.precedence = precedence;
        this.setCommutativity(c);
        this.associative = a;
    }

    /**
     * Performs the operation with the given surrounding values.
     *
     * @param left  The value left of the operation
     * @param right The value right of the operation
     * @return The result of the operation
     */
    public abstract double operate(double left, double right) throws NumberTooLargeException;

    /**
     * @return The type of operation this is (see class constants for possible values)
     */
    public int getType() {
        return type;
    }

    /**
     * @return true if the operator is left associative, false if it's right
     */
    public boolean isLeftAssociative() {
        return leftAssociative;
    }

    /**
     * @return true if the operator is associative, false if it's right
     */
    public boolean isAssociative() {
        return associative;
    }

    /**
     * @return true if the operator is commutative, false if it is not
     */
    public boolean isCommutative() {
        return commutative;
    }

    /**
     * @return true if the operator is anticommutative, false if it is not
     */
    public boolean isAntiCommutative() {
        return anticommutative;
    }

    /**
     * @param c if it is 1 the operator is commutative, 0:  noncommutative, -1: anticommutative
     */
    private void setCommutativity(int c) {
        if (c > 0) {
            commutative = true;
            anticommutative = false;
        } else if (c == 0) {
            commutative = false;
            anticommutative = false;
        } else {
            commutative = false;
            anticommutative = true;
        }
    }

    /**
     * @return The order of which the Operator is operated (higher = more priority)
     */
    public int getPrecedence() {
        return precedence;
    }
}