package com.trutechinnovations.calculall;


import java.util.ArrayList;

/**
 * Created by Jason on 2015-06-28.
 */
public class Constant extends Variable {

    private String name;
    private double value;
    private String units;

    public Constant(String name, String symbol, double value, String units) {
        super(Variable.CONSTANT, symbol);
        this.name = name;
        this.value = value;
        this.units = units;
    }

    public double getNumericValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getUnits() {
        return units;
    }

    public ArrayList<Token> getValue() {
        String temp = Double.toString(value);
        ArrayList<Token> tokens = new ArrayList<>();
        Token tempToken;

        //Converts the string value into an arraylist of tokens
        for (int i = 0; i < temp.length(); i++) {
            switch (temp.charAt(i)) {
                case '1':
                    tempToken = DigitFactory.makeOne();
                    break;
                case '2':
                    tempToken = DigitFactory.makeTwo();
                    break;
                case '3':
                    tempToken = DigitFactory.makeThree();
                    break;
                case '4':
                    tempToken = DigitFactory.makeFour();
                    break;
                case '5':
                    tempToken = DigitFactory.makeFive();
                    break;
                case '6':
                    tempToken = DigitFactory.makeSix();
                    break;
                case '7':
                    tempToken = DigitFactory.makeSeven();
                    break;
                case '8':
                    tempToken = DigitFactory.makeEight();
                    break;
                case '9':
                    tempToken = DigitFactory.makeNine();
                    break;
                case '0':
                    tempToken = DigitFactory.makeZero();
                    break;
                case '.':
                    tempToken = DigitFactory.makeDecimal();
                    break;
                case '-':
                    tempToken = DigitFactory.makeNegative();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid value for the constant");
            }
            tokens.add(tempToken);
        }

        return tokens;
    }


}
