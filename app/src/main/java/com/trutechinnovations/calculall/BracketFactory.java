package com.trutechinnovations.calculall;

/**
 * Contains static methods that will create Brackets.
 *
 * @author Alston Lin, Ejaaz Merali, Jason Fok
 * @version 3.0
 */
public class BracketFactory {

    public static Bracket makeOpenBracket() {
        return new Bracket("(", Bracket.OPEN);
    }

    public static Bracket makeCloseBracket() {
        return new Bracket(")", Bracket.CLOSE);
    }

    public static Bracket makeOpenSquareBracket() {
        return new Bracket("[", Bracket.SQUAREOPEN);
    }

    public static Bracket makeCloseSquareBracket() {
        return new Bracket("]", Bracket.SQUARECLOSED);
    }

    public static Bracket makeMagnitudeOpen() {
        return new Bracket("‖(", Bracket.MAGNITUDE_OPEN);
    }

    public static Bracket makeMagnitudeClose() {
        return new Bracket(")‖", Bracket.MAGNITUDE_CLOSE);
    }

    public static Bracket makeSuperscriptOpen() {
        return new Bracket("", Bracket.SUPERSCRIPT_OPEN);
    }

    public static Bracket makeSuperscriptClose() {
        return new Bracket("", Bracket.SUPERSCRIPT_CLOSE);
    }

    public static Bracket makeNumOpen() {
        return new Bracket("", Bracket.NUM_OPEN);
    }

    public static Bracket makeNumClose() {
        return new Bracket("", Bracket.NUM_CLOSE);
    }

    public static Bracket makeDenomOpen() {
        return new Bracket("", Bracket.DENOM_OPEN);
    }

    public static Bracket makeDenomClose() {
        return new Bracket("", Bracket.DENOM_CLOSE);
    }

    public static Bracket makeFracOpen() {
        return new Bracket("", Bracket.FRACTION_OPEN);
    }

    public static Bracket makeFracClose() {
        return new Bracket("", Bracket.FRACTION_CLOSE);
    }

}
