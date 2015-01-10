package com.trutech.calculall;

/**
 * Statically create Placeholders through anonymous classes.
 *
 * @author Alston
 * @version 0.10.0
 */
public class PlaceholderFactory {

    public static Placeholder makeBlock() {
        return new Placeholder("□", Placeholder.BLOCK);
    }

    public static Placeholder makeComma() {
        return new Placeholder(",", Placeholder.COMMA);
    }
}
