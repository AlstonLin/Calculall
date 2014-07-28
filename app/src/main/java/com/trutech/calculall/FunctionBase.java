package com.trutech.calculall;

public abstract class FunctionBase extends Token {

	protected FunctionBase(String symbol) {
		super(symbol);
	}
	
	/**
	 * Performs the function with the specified base (first number after)
	 * and input (second number after)
	 * @param base The base of the function
	 * @param input The input that the function will use
	 * @return The result of the function
	 */
	public abstract double perform (double base, double input);
}
