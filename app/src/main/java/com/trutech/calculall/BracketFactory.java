package com.trutech.calculall;

/**
 * Contains static methods that will create Brackets.
 *
 * @version 0.3.0
 */
public class BracketFactory {

    public static Bracket createOpenBracket(){
        return new Bracket("(", Bracket.OPEN);
    }

    public static Bracket createCloseBracket(){
        return new Bracket(")", Bracket.CLOSE);
    }

    public static Bracket createSquareOpenBracket(){
        return new Bracket("[", Bracket.SQUAREOPEN);
    }

    public static Bracket createSquareCloseBracket(){
        return new Bracket("]", Bracket.SQUARECLOSE);
    }
}
