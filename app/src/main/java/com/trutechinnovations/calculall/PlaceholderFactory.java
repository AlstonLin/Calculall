package com.trutechinnovations.calculall;

/**
 * Statically create Placeholders through anonymous classes.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class PlaceholderFactory {

    public static Placeholder makeSuperscriptBlock() {
        return new Placeholder("□", Placeholder.SUPERSCRIPT_BLOCK);
    }

    public static Placeholder makeBaseBlock() {
        return new Placeholder("□", Placeholder.BASE_BLOCK);
    }

    public static Placeholder makeComma() {
        return new Placeholder(",", Placeholder.COMMA);
    }
}
