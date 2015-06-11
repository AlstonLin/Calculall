package com.trutechinnovations.calculall;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a digit, operation, brackets, or a function on the calculator screen; cannot be used by itself
 * (must use a subclass of this).
 *
 * @author Alston Lin
 * @version 3.0
 */
public abstract class Token implements Serializable {

    protected int type;
    private ArrayList<Token> dependencies = new ArrayList<Token>(); //Tokens that are dependent with this token
    private String symbol;

    /**
     * Creates a new Token to be shown on the calculator screen.
     *
     * @param symbol The symbol of the Token to be shown on the calculator screen
     */
    protected Token(String symbol) {
        this.symbol = symbol;
    }

    /**
     * @return The symbol for this Token to be shown on the display
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Adds a dependent token to the list; if this token is removed so would the dependencies.
     *
     * @param t The token that is dependent on this one
     */
    public void addDependency(Token t) {
        dependencies.add(t);
    }

    /**
     * @return The Tokens that are dependent on this one
     */
    public ArrayList<Token> getDependencies() {
        return dependencies;
    }

    public int getType() {
        return type;
    }
}

