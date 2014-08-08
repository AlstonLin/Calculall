package com.trutech.calculall;

import java.util.ArrayList;

public class VRule {

    private String pattern;
    private int operation;
    private int dimension;
    private int firstOccurPosition;

    /**
     * @param pattern   String pattern to be replaced
     * @param operation Operation number
     */
    public VRule(String pattern, int operation, int dimension) {
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
        firstOccurPosition = bm.search(stringExpression);
        if (firstOccurPosition == -1) {
            return expression;
        } else {
            return applyVectorOperation(expression);
        }
    }

    /**
     * N = Number, A = add, S = subtract, D = dot, C = cross, [ = open bracket, ] = closed bracket, , = comma
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
            } else if (expression.get(i) instanceof Bracket) {
                if (((Bracket) (expression.get(i))).getType() == Bracket.SQUAREOPEN) {
                    stringExpression = stringExpression + "[";
                } else if (((Bracket) (expression.get(i))).getType() == Bracket.SQUARECLOSED) {
                    stringExpression = stringExpression + "]";

                }
            } else if (expression.get(i).getSymbol() == ",") {
                stringExpression = stringExpression + ",";
            }


            return stringExpression;
        }
    }

    private ArrayList<Token> applyVectorOperation(ArrayList<Token> expression) {
        ArrayList<Token> tempExpression = new ArrayList<Token>();
        ArrayList<Token> numbers = new ArrayList<Token>();
        ArrayList<Token> operators = new ArrayList<Token>();
        //Load all the numbers in the pattern into numbers and the operator token in the pattern into operator
        for (int i = firstOccurPosition; i < firstOccurPosition + pattern.length(); i++) {
            if (expression.get(i) instanceof Number) {
                numbers.add(expression.get(i));
            }
        }
        double[] leftVector = new double[dimension], rightVector = new double[dimension];

        //Load all numbers of each vector into an array of doubles
        for (Token n : numbers) {
            if (numbers.indexOf(n) < dimension) {
                leftVector[numbers.indexOf(n)] = ((Number) n).getValue();
            } else {
                rightVector[numbers.indexOf(n) - dimension] = ((Number) n).getValue();
            }
        }
        //TODO send array of doubles to ahsen's methods and convert the doubles into tokens

        //Load Tokens that are before the pattern into tempExpression
        for (int i = 0; i < firstOccurPosition; i++) {
            tempExpression.add(expression.get(i));
        }

        //Load all the numbers in the pattern into numbers and all the multiplication or division tokens in the pattern into multAndDiv
        for (int i = firstOccurPosition; i < firstOccurPosition + pattern.length(); i++) {
            if (expression.get(i) instanceof Number) {
                numbers.add(expression.get(i));
            } else if (expression.get(i) instanceof Operator) {
                if (((Operator) (expression.get(i))).getType() == 3) {
                    operators.add(expression.get(i));
                }
            }
        }

        // Add the new vector to the expression
        if (operation == VRuleSet.DOT) {
            tempExpression.add(calculateDotProduct(leftVector, rightVector));
        } else if (operation == VRuleSet.CROSS) {
            tempExpression.add(Utility.convertDoublesToVector(calculateCrossProduct(leftVector, rightVector)));
        }

        int tempExpressionSizeBefore = tempExpression.size();
        //Add the last bit of the expression to tempExpression
        for (int i = 0; i < expression.size() - tempExpressionSizeBefore + 1; i++) {
            tempExpression.add(expression.get(i + firstOccurPosition + pattern.length()));
        }

        return applyRule(tempExpression);
    }

    private Number evaluate(ArrayList<Token> expression) {
        return (new Number(Utility.evaluateExpression(Utility.convertToReversePolish(expression))));
    }
}
