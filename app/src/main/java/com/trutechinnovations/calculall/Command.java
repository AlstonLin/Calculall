package com.trutechinnovations.calculall;

/**
 * Uses the Command Design Pattern to effectively have lambda expressions in a pre-Java 8 environment.
 * Note: E is the return Object, F is the param Object.
 *
 * @author Alston Lin
 * @version 3.0
 */
public interface Command<E, F> {
    public E execute(F o);
}