package com.trutech.calculall;

import android.view.View;

/**
 * Represents a digit, operation, brackets, or a function on the calculator screen; cannot be used by itself
 * (must use a subclass of this).
 *
 * @version 0.4.0
 */
public abstract class Token {

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
     *
     * @param v
     */
    public void clickRoots(View v) {

    }
}

