/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.trutech.calculall;

import java.util.ArrayList;

/**
 *
 * @author Alston
 */
public class TestCalc {

    public static ArrayList<Token> tokens;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        tokens = new ArrayList<Token>();
        tokens.add(BracketFactory.createOpenBracket());
        tokens.add(new Number(3));
        tokens.add(OperatorFactory.makeAdd());
        tokens.add(FunctionFactory.makeCbrt());
        tokens.add(new Number(4));
        tokens.add(OperatorFactory.makeMultiply());
        tokens.add(FunctionFactory.makeCbrt());
        tokens.add(new Number(2));
        tokens.add(OperatorFactory.makeMultiply());
        tokens.add(FunctionFactory.makeCbrt());
        tokens.add(new Number(5));
        tokens.add(BracketFactory.createCloseBracket());
        tokens.add(OperatorFactory.makeMultiply());
        tokens.add(FunctionFactory.makeCbrt());
        tokens.add(new Number(5));
        ArrayList<Token> simplified; /*= Utility.simplifyExpression(tokens);
        for (Token t : simplified) {
            if (t instanceof Number) {
                System.out.print(((Number) t).getValue());
            } else {
                System.out.print(t.getSymbol());
            }
        }*/
        System.out.println("\n");
        simplified = CRule.simplifier(tokens);
        for (Token t : simplified) {
            if (t instanceof Number) {
                System.out.print(((Number) t).getValue());
            } else {
                System.out.print(t.getSymbol());
            }
        }
    }

}
