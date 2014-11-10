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
        //Index of the first time the pattern happens is set to firstOccurPosition and is -1 if
        //no pattern was found
        firstOccurPosition = bm.search(stringExpression);
        //Base case for recursive implementation or no further rule can be applied
        if (firstOccurPosition == -1 || VRuleSet.getValidOutput()) {
            //Valid output would be used to escape the recursive implementation for patterns that
            //have no operations performed on them but is still used to check if the output is valid
            if (operation != VRuleSet.CHECK) {
                VRuleSet.setValidOutput(false);
            }
            return expression;
        } else {
             return applyVectorOperation(expression);
        }
    }

    /**
     * N = Number, A = add, S = subtract, D = dot, C = cross, [ = open bracket, ] = closed bracket, | = magnitude bar, , = comma
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
                } else if (((Operator) (expression.get(i))).getType() == Operator.ANGLE) {
                    stringExpression = stringExpression + "a";
                }
            } else if (expression.get(i) instanceof Bracket) {
                if (((Bracket) (expression.get(i))).getType() == Bracket.SQUAREOPEN) {
                    stringExpression = stringExpression + "[";
                } else if (((Bracket) (expression.get(i))).getType() == Bracket.SQUARECLOSED) {
                    stringExpression = stringExpression + "]";
                } else if (((Bracket) (expression.get(i))).getType() == Bracket.MAGNITUDEBAR) {
                    stringExpression = stringExpression + "|";
                }

            } else if (expression.get(i).getSymbol() == "proj") {
                stringExpression = stringExpression + "p";
            }
            else {
                stringExpression = stringExpression + expression.get(i).getSymbol();
            }

            /*else if (expression.get(i).getSymbol() == ",") {
                stringExpression = stringExpression + ",";
            }  else if (expression.get(i).getSymbol() == "N") {
                stringExpression = stringExpression + "N";
            } else if (expression.get(i).getSymbol() == "E") {
                stringExpression = stringExpression + "E";
            } else if (expression.get(i).getSymbol() == "S") {
                stringExpression = stringExpression + "S";
            } else if (expression.get(i).getSymbol() == "W") {
                stringExpression = stringExpression + "W";
            } else if (expression.get(i).getSymbol() == "°") {
                stringExpression = stringExpression + "°";
            } else if (expression.get(i).getSymbol() == "proj(") {
                stringExpression = stringExpression + "proj";
            }*/

        }
        return stringExpression;
    }

    public ArrayList<Token> applyVectorOperation(ArrayList<Token> expression) {
        ArrayList<Token> tempExpression = new ArrayList<Token>();
        ArrayList<Token> numbers = new ArrayList<Token>();
        //Load all the numbers in the pattern into numbers and the operator tokens in the pattern into operators
        for (int i = firstOccurPosition; i < firstOccurPosition + pattern.length(); i++) {
            if (expression.get(i) instanceof Number) {
                numbers.add(expression.get(i));
            }
        }


            double[] leftVector = new double[dimension], rightVector = new double[dimension];

            //Load all Numbers of each vector into an array of doubles to send for calculations
            for (Token n : numbers) {
                if (numbers.indexOf(n) < dimension) {
                    leftVector[numbers.indexOf(n)] = ((Number) n).getValue();
                } else {
                    rightVector[numbers.indexOf(n) - dimension] = ((Number) n).getValue();
                }
            }


        //Load Tokens that are before the pattern into tempExpression
        for (int i = 0; i < firstOccurPosition; i++) {
            tempExpression.add(expression.get(i));
        }


        // Add the new vector or number to the expression
        if (operation == VRuleSet.DOT) {
            tempExpression.add(new Number(Utility.round(Utility.calculateDotProduct(leftVector, rightVector), 4)));
        } else if (operation == VRuleSet.CROSS) {
            ArrayList<Token> newVector = Utility.convertDoublesToVector(Utility.calculateCrossProduct(leftVector, rightVector));
            tempExpression.addAll(newVector);
        } else if (operation == VRuleSet.ADD) {
            ArrayList<Token> newVector = Utility.convertDoublesToVector(Utility.calculateAddOrSubtract(leftVector, rightVector, VRuleSet.ADD));
            tempExpression.addAll(newVector);
        } else if (operation == VRuleSet.SUBTRACT) {
            ArrayList<Token> newVector = Utility.convertDoublesToVector(Utility.calculateAddOrSubtract(leftVector, rightVector, VRuleSet.SUBTRACT));
            tempExpression.addAll(newVector);
        } else if (operation == VRuleSet.MAGNITUDE) {
            tempExpression.add(new Number(Utility.round(Utility.calculateMagnitude(leftVector),4)));
        } else if (operation == VRuleSet.MULTIPLY) {
            double multiplier = 0;
            double[] vector = new double [dimension];
            //Load all Numbers of each vector into an array of doubles and a double for the multiplier to send for calculations
            for (Token n : numbers) {
                if (numbers.indexOf(n) == 0) {
                    multiplier = ((Number) n).getValue();
                } else {
                    vector[numbers.indexOf(n)-1] = ((Number) n).getValue();
                }
            }
            //Store the new vector into tempExpression
            tempExpression.addAll(Utility.convertDoublesToVector(Utility.multiplyVector(multiplier, vector)));
        } else if (operation == VRuleSet.UNITVECTOR && VRuleSet.getPressedUnitVButton()) {
            tempExpression.addAll(Utility.findUnitVector(leftVector));
            VRuleSet.setPressedUnitVButton(false);
        } else if (operation == VRuleSet.ARGUMENT && VRuleSet.getPressedArgumentButton()) {
            tempExpression.add(new Number (Utility.round(Utility.calculateArgument(leftVector),4)));
            tempExpression.add(new Token ("°"){});
            VRuleSet.setPressedArgumentButton(false);
        } else if (operation == VRuleSet.TRUEB && VRuleSet.getPressedTrueBButton()) {
            tempExpression.add(new Number (Utility.round(Utility.calculateTrueBearing(leftVector),4)));
            tempExpression.add(new Token ("°"){});
            VRuleSet.setPressedTrueBButton(false);
        }else if (operation == VRuleSet.BEAR && VRuleSet.getPressedBearButton()) {
            tempExpression.addAll(Utility.calculateBearing(leftVector));
            VRuleSet.setPressedBearButton(false);
        }else if (operation == VRuleSet.ANGLE) {
            tempExpression.add(new Number(Utility.round(Utility.findAngleBetweenVector(leftVector, rightVector),4)));
            tempExpression.add(new Token ("°"){});
        } else if (operation == VRuleSet.PROJ) {
            tempExpression.addAll(Utility.findProjection(leftVector,rightVector));
        } else if (operation == VRuleSet.SCALAR&& VRuleSet.getPressedScalarEqnButton()) {
            tempExpression.addAll(Utility.calculateScalarEquation(leftVector,rightVector));
            VRuleSet.setScalarEqnOutput(true);
            VRuleSet.setPressedScalarEqnButton(false);
        }else if (operation == VRuleSet.CHECK && (expression.size() == 3 || expression.size() == 2 || VRuleSet.getScalarEqnOutput() || expression.size() == 5 || expression.size() == 7)) {
            VRuleSet.setValidOutput(true); //Method to make make sure output is valid
            tempExpression = expression;
        } else if (operation == VRuleSet.UNITVECTOR || operation == VRuleSet.ANGLE ||
                operation == VRuleSet.ARGUMENT || operation == VRuleSet.TRUEB || operation == VRuleSet.BEAR){
            VRuleSet.setValidOutput(true); //Method to make make sure output is valid
            //This is applied when no operation can be performed such as the ones for the vector calculations
            for (int i = firstOccurPosition; i < firstOccurPosition + pattern.length(); i++) {
                tempExpression.add(expression.get(i));
            }
        }
        //TODO: Find a way to output bearing with angles

        //Throw errors when there is unnessary tokens after a angle calculation
        if ((operation == VRuleSet.BEAR || operation == VRuleSet.ANGLE || operation == VRuleSet.TRUEB)) {
            if (!(expression.get(expression.size()-1).getSymbol() == "°" || expression.get(expression.size()).getSymbol() == "N"
                    || expression.get(expression.size()).getSymbol() == "E" || expression.get(expression.size()).getSymbol() == "W"
                    || expression.get(expression.size()).getSymbol() == "S")) {
                throw new IllegalArgumentException();
            }
        }


        //Add the last bit of the expression to tempExpression
        for (int i = 0; i < expression.size() - pattern.length() - firstOccurPosition; i++) {
            tempExpression.add(expression.get(i + firstOccurPosition + pattern.length()));
        }
        return applyRule(tempExpression);
    }
}
