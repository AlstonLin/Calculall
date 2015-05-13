package com.trutechinnovations.calculall;

/**
 * Contains static methods that will create Digit pieces.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class DigitFactory {

    public static int DECIMAL = -1, NEGATIVE = -10;

    public static Digit makeOne() {
        return new Digit("1", 1);
    }

    public static Digit makeTwo() {
        return new Digit("2", 2);
    }

    public static Digit makeThree() {
        return new Digit("3", 3);
    }

    public static Digit makeFour() {
        return new Digit("4", 4);
    }

    public static Digit makeFive() {
        return new Digit("5", 5);
    }

    public static Digit makeSix() {
        return new Digit("6", 6);
    }

    public static Digit makeSeven() {
        return new Digit("7", 7);
    }

    public static Digit makeEight() {
        return new Digit("8", 8);
    }

    public static Digit makeNine() {
        return new Digit("9", 9);
    }

    public static Digit makeZero() {
        return new Digit("0", 0);
    }

    public static Digit makeDecimal() {
        return new Digit(".", DECIMAL);
    }

    public static Digit makeNegative() {
        return new Digit("-", NEGATIVE);
    }
}
