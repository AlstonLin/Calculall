package com.trutechinnovations.calculall;


import java.util.ArrayList;

/**
 * Created by Jason on 2015-06-28.
 */
public class Constant extends Variable {

    private String name;
    private String HTML; //The HTML encoded symbol
    private double value;
    private String units;

    public Constant(String name, String HTML, String symbol, double value, String units) {
        super(Variable.CONSTANT, symbol);
        this.name = name;
        this.value = value;
        this.units = units;
        this.HTML = HTML;
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
        ArrayList<Token> tokens = new ArrayList<>();
        Token tempToken = new Number(value);
        tokens.add(tempToken);
        return tokens;
    }

    public String getHTML() {
        return HTML;
    }

}