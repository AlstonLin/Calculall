package com.trutech.calculall;

/**
 * Contains static methods that will create Brackets.
 *
 * @version 0.4.0
 */
public class BracketFactory {

    public static Bracket createOpenBracket(){
        return new Bracket("(", Bracket.OPEN);
    }

    public static Bracket createCloseBracket(){
        return new Bracket(")", Bracket.CLOSE);
    }

    public static Bracket createOpenSquareBracket(){
        return new Bracket("[", Bracket.SQUAREOPEN);
    }

    public static Bracket createCloseSquareBracket(){
        return new Bracket("]", Bracket.SQUARECLOSE);
    }
}
