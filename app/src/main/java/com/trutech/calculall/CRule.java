package com.trutech.calculall;

import java.util.ArrayList;

public class CRule {

    String pattern;
    String replacement;
    Boolean applyFunction;
    int firstOccurPostion;

    /**
     * @param pattern       String pattern to be replaced
     * @param replacement   String replacement
     * @param applyFunction True if a method should be called to simplify
     *                      expression
     */
    public CRule(String pattern, String replacement, Boolean applyFunction) {
        this.pattern = pattern;
        this.replacement = replacement;
        this.applyFunction = applyFunction;
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
        //Index of the first time the pattern happens is set to firstOccurPostion and is -1 if no pattern was found
        firstOccurPostion = bm.search(stringExpression);
        if (firstOccurPostion == -1) {
            return expression;
        } else {
            return applyMathRules(expression);
        }
    }

    /**
     * N = Number, M = Multiplication or division, A = add or subtract, S =
     * square root, C = cube root ( = open bracket, ) = close bracket R =
     * Variable root
     *
     * @param expression An expression represented in a ArrayList of Token
     * @return String The given expression represented in a ArrayList of Token
     * as a String
     */
    public String buildString(ArrayList<Token> expression) {
        String stringExpression = "";
        for (int i = 0; i < expression.size(); i++) {
            if (expression.get(i) instanceof Number) {
                stringExpression = stringExpression + "N";
            } else if (expression.get(i) instanceof Operator) {
                if (((Operator) (expression.get(i))).getType() == 3 || ((Operator) (expression.get(i))).getType() == 4) {
                    stringExpression = stringExpression + "M";
                } else if (((Operator) (expression.get(i))).getType() == 1 || ((Operator) (expression.get(i))).getType() == 2) {
                    stringExpression = stringExpression + "A";
                } else if (((Operator) expression.get(i)).getType() == Operator.VARROOT) {
                    stringExpression = stringExpression + "R";
                }
            } else if (expression.get(i) instanceof Function) {
                if (((Function) (expression.get(i))).getType() == 23) {
                    stringExpression = stringExpression + "S";
                } else if (((Function) (expression.get(i))).getType() == 24) {
                    stringExpression = stringExpression + "C";
                }
            }
        }
        return stringExpression;
    }

    private ArrayList<Token> applyMathRules(ArrayList<Token> expression) {
        ArrayList<Token> tempExpression = new ArrayList<Token>();
        ArrayList<Token> numbers = new ArrayList<Token>();
        ArrayList<Token> multAndDiv = new ArrayList<Token>();

        //Load Tokens that are before the pattern into tempExpression
        for (int i = 0; i < firstOccurPostion; i++) {
            tempExpression.add(expression.get(i));
        }

        //Load all the numbers in the pattern into numbers and all the multiplication or division tokens in the pattern into multAndDiv
        for (int i = firstOccurPostion; i < firstOccurPostion + pattern.length(); i++) {
            if (expression.get(i) instanceof Number) {
                numbers.add(expression.get(i));
            } else if (expression.get(i) instanceof Operator) {
                if (((Operator) (expression.get(i))).getType() == 3 || ((Operator) (expression.get(i))).getType() == 4) {
                    multAndDiv.add(expression.get(i));
                }
            }
        }

        //Start rewriting expressions
        for (int i = 0; i < replacement.length(); i++) {
            if (replacement.charAt(i) == '(') {
                tempExpression.add(BracketFactory.createOpenBracket());
            } else if (replacement.charAt(i) == ')') {
                tempExpression.add(BracketFactory.createCloseBracket());
            } else if (replacement.charAt(i) == 'R' && replacement.charAt(i - 1) == 'N') {
                tempExpression.add(numbers.get(0));
                numbers.remove(0);
                tempExpression.add(OperatorFactory.makeVariableRoot());
            } else if (replacement.charAt(i) == 'N') {
                tempExpression.add(numbers.get(0));
                numbers.remove(0);
            } else if (replacement.charAt(i) == 'M') {
                tempExpression.add(multAndDiv.get(0));
                multAndDiv.remove(0);
            } else if (replacement.charAt(i) == 'S') {
                tempExpression.add(FunctionFactory.makeSqrt());
            } else if (replacement.charAt(i) == 'C') {
                tempExpression.add(FunctionFactory.makeCbrt());
            }
        }

        int tempExpressionSizeBefore = tempExpression.size();
        //Add the last bit of the expression to tempExpression            
        for (int i = 0; i < expression.size() - tempExpressionSizeBefore + 1; i++) {
            tempExpression.add(expression.get(i + firstOccurPostion + pattern.length()));
        }
        return applyRule(tempExpression);
    }

    /**
     * @param expression Fraction that needs to be reduced
     * @return ArrayList<Token>
     */
    private ArrayList<Token> reduceFraction(ArrayList<Token> expression) {
        double num = ((Number) (expression.get(0))).getValue();
        double den = ((Number) (expression.get(2))).getValue();
        double gcd = gcd(num, den);
        ArrayList<Token> tempFraction = new ArrayList<Token>();
        if (num % den != 0) {
            if (expression.size() == 3) {
                tempFraction.add(new Number(num / gcd));
                tempFraction.add(expression.get(2));
                tempFraction.add(new Number(den / gcd));
                return tempFraction;
            }
            return expression;
        } else {
            tempFraction.add(new Number(num / den));
            return tempFraction;
        }
    }

    // recursive implementation
    public static double gcd(double p, double q) {
        if (q == 0) {
            return p;
        } else {
            return gcd(q, p % q);
        }
    }

    private static Number evaluate(ArrayList<Token> expression) {
        return (new Number(Utility.evaluateExpression(Utility.convertToReversePolish(expression))));
    }

    public static ArrayList<Token> simplifier(ArrayList<Token> e) {
        ArrayList<Token> expression = e;
        ArrayList<Token> tempExpression = new ArrayList<Token>();
        ArrayList<Token> expressionFrag = new ArrayList<Token>();
        ArrayList<Token> finalExp = new ArrayList<Token>();
        int bracket = 0;
        int openBrack = 0, closeBrack = 0;
        for (int j = 0; j < expression.size(); j++) {
            for (int i = 0; i < expression.size(); i++) {
                if (expression.get(i) instanceof Bracket) {
                    if (((Bracket) expression.get(i)).getType() == 1) {
                        if (bracket == 0) {
                            openBrack = i;
                        }
                        bracket++;
                    } else if (((Bracket) expression.get(i)).getType() == 2) {
                        bracket--;
                        if (bracket == 0) {
                            closeBrack = i;
                        }
                    }
                }
            }
            if ((openBrack != 0 || closeBrack != 0)&& (closeBrack > openBrack)) {
                expressionFrag.addAll(expression.subList(openBrack,closeBrack));
                expression.subList(openBrack,closeBrack).clear();
                tempExpression.addAll(simplifier(expressionFrag));
            } else {
                tempExpression.addAll(Utility.simplifyExpression(expression));
            }
            finalExp.addAll(tempExpression);
            tempExpression.clear();
        }
        return finalExp;
    }
}
