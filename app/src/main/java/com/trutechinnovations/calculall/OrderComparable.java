package com.trutechinnovations.calculall;

/**
 * Indicates that the Implementing Object has a getPrecedence() method.
 *
 * @author Alston Lin
 * @version 3.0
 */
public interface OrderComparable {
    public int getPrecedence();

    public boolean isLeftAssociative();
}
