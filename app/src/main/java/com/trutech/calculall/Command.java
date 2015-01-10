package com.trutech.calculall;

/**
 * Uses the Command Design Pattern to effectively have lambda expressions in a pre-Java 8 environment.
 * Note: E is the return Object, F is the param Object1
 */
public interface Command<E, F> {
    public E execute(F o);
}
