package com.trutech.calculall;

/**
 * Uses the Command Design Pattern to effectively have lambda expressions in a pre-Java 8 environment.
 * Note: E is the return Object, F is the param Object.
 *
 * @version Alpha 2.0
 * @author Alston Lin
 */
public interface Command<E, F> {
    public E execute(F o);
}