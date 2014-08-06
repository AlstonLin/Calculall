package com.trutech.calculall;

import java.util.ArrayList;

public class CRule {

    String pattern;
    int operation;
    int firstOccurPostion;

    /**
     * @param pattern   String pattern to be replaced
     * @param operation Operation number
     */
    public CRule(String pattern, int operation) {
        this.pattern = pattern;
        this.operation = operation;
    }

    /**
     * @param expression Expression that needs to have rules applied to them
     * @return ArrayList<Token>
     */
    public ArrayList<Token> applyRule(ArrayList<Token> expression) {
        //Builds a string to represent the expression
        String stringExpression = buildString(expression);
        //Searches for a pattern in the expression
        BoyerMoore bm = new BoyerMoore(pattern);
        //Index of the first time the pattern happens is set to firstOccurPosition and is -1 if no pattern was found
        firstOccurPostion = bm.search(stringExpression);
        if (firstOccurPostion == -1) {
            return expression;
        } else {
            return applyVectorOperation(expression);
        }
    }

    /**
     * N = Number, M = Multiplication or division, A = add or subtract, S =
     * square root, ( = open bracket, ) = close bracket, D = dot, C = cross, anything else is a Comma
     *
     * @param expression An expression represented in a ArrayList of Token
     * @return String The given expression represented in a ArrayList of Token
     * as a String
     */
    public String buildString(ArrayList<Token> expression) { //TODO Make dot and cross an Operator
        String stringExpression = "";
        for (int i = 0; i < expression.size(); i++) {
            if (expression.get(i) instanceof Number) {
                stringExpression = stringExpression + "N";
            } else if (expression.get(i) instanceof Operator) {
                if (((Operator) (expression.get(i))).getType() == 3 || ((Operator) (expression.get(i))).getType() == 4) {
                    stringExpression = stringExpression + "M";
                } else if (((Operator) (expression.get(i))).getType() == 1 || ((Operator) (expression.get(i))).getType() == 2) {
                    stringExpression = stringExpression + "A";
                }
            } else if (expression.get(i) instanceof Function) {
                if (((Function) (expression.get(i))).getType() == 23) {
                    stringExpression = stringExpression + "S";
                }
            }
        }
        return stringExpression;
    }

    private ArrayList<Token> applyVectorOperation(ArrayList<Token> expression) {
        ArrayList<Token> tempExpression = new ArrayList<>();
        ArrayList<Token> numbers = new ArrayList<>();
        Token operator = new Token("") {};
        //Load all the numbers in the pattern into numbers and the operator tokens in the pattern into operator
        for (int i = firstOccurPostion; i < firstOccurPostion + pattern.length(); i++) {
            if (expression.get(i) instanceof Number) {
                numbers.add(expression.get(i));
            }
         }
        //TODO change all Numbers into integers and put all the integers into an array of integers. Look at process to see how alston did it
        return applyRule(tempExpression);
    }

    private Number evaluate(ArrayList<Token> expression) {
        return (new Number(Utility.evaluateExpression(Utility.convertToReversePolish(expression))));
    }
}
