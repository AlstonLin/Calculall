package com.trutech.calculall;

import java.util.ArrayList;

public class CRule {

    String pattern;
    int operation;
    int dimension;
    int firstOccurPosition;

    /**
     * @param pattern   String pattern to be replaced
     * @param operation Operation number
     */
    public CRule(String pattern, int operation, int dimension) {
        this.pattern = pattern;
        this.operation = operation;
        this.dimension = dimension;
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
     * N = Number, A = add, S = subtract, D = dot, C = cross, , = comma
     *
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
                if (((Operator) (expression.get(i))).getType() == Operator.ADD) {
                    stringExpression = stringExpression + "A";
                } else if (((Operator) (expression.get(i))).getType() == Operator.SUBTRACT) {
                    stringExpression = stringExpression + "S";
                } else if (((Operator) (expression.get(i))).getType() == Operator.DOT) {
                    stringExpression = stringExpression + "D";
                } else if (((Operator) (expression.get(i))).getType() == Operator.CROSS) {
                    stringExpression = stringExpression + "C";
                }
            } else if (expression.get(i).getSymbol() == ",") {
                stringExpression = stringExpression + ",";
            }
        }

    return stringExpression;
}

    private ArrayList<Token> applyVectorOperation(ArrayList<Token> expression) {
        ArrayList<Token> tempExpression = new ArrayList<>();
        ArrayList<Token> numbers = new ArrayList<>();
        Token operator = new Token("") {
        };
        //Load all the numbers in the pattern into numbers and the operator token in the pattern into operator
        for (int i = firstOccurPosition; i < firstOccurPosition + pattern.length(); i++) {
            if (expression.get(i) instanceof Number) {
                numbers.add(expression.get(i));
            }
        }
        //TODO change all Numbers into integers and put all the integers into an array of integers. Look at process to see how alston did it
        double[] leftVector = new double [dimension], rightVector = new double [dimension];

        for (Token n: numbers){
            if (numbers.indexOf(n) < dimension) {
                leftVector[numbers.indexOf(n)] = ((Number) n).getValue();
            }
            else {
                rightVector[numbers.indexOf(n) - dimension] = ((Number) n).getValue();
            }
        }
        //TODO send array of doubles to ahsen's methods

        return applyRule(tempExpression);
    }

    private Number evaluate(ArrayList<Token> expression) {
        return (new Number(Utility.evaluateExpression(Utility.convertToReversePolish(expression))));
    }
}
