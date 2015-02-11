package com.trutech.calculall;

/**
 * Indicates that the Implementing Object has a getPrecedence() method.
 */
public interface OrderComparable {
    public int getPrecedence();

    public boolean isLeftAssociative();
}
