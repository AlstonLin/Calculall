package com.trutech.calculall;

/**
 * Statically create Placeholders through anonymous classes.
 *
 * @author Alston Lin
 * @version Alpha 2.0
 */
public class PlaceholderFactory {

    public static Placeholder makeBlock() {
        return new Placeholder("□", Placeholder.BLOCK);
    }

    public static Placeholder makeComma() {
        return new Placeholder(",", Placeholder.COMMA);
    }
}
