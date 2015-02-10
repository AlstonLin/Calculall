package com.trutech.calculall;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Contains various static utility methods for Vector Mode.
 *
 * @version Alpha 2.2
 * @author Alston Lin, Keith Wong, Jason Fok
 */
public class VectorUtilities {

    /**
     * Finds the dot product of the given vectors.
     *
     * @param left First Vector
     * @param right Second Vector
     * @return The dot product
     */
    public static Double findDotProduct(Vector left, Vector right) {
        if (left.getDimensions() == 2 && right.getDimensions() == 2){
            double result = 0;
            result += left.getValues()[0] * right.getValues()[0];
            result += left.getValues()[1] * right.getValues()[1];
            return result;
        }else if (left.getDimensions() == 3 && right.getDimensions() == 3){
            double result = 0;
            result += left.getValues()[0] * right.getValues()[0];
            result += left.getValues()[1] * right.getValues()[1];
            result += left.getValues()[2] * right.getValues()[3];
            return result;
        }else{
            throw new IllegalArgumentException("Attempted to operate two vector of different dimensions");
        }
    }

    /**
     * Returns the magnitude of a vector given a Vector.
     *
     * @param v The vector to find the magnitude of
     * @return The Number that contains the value of the magnitude
     */
    public static double calculateMagnitude(Vector v) {
        if (v.getDimensions() == 2) {
            return Math.sqrt(Math.pow(v.getValues()[0], 2) + Math.pow(v.getValues()[1], 2));
        } else if (v.getDimensions() == 3) {
            return Math.sqrt(Math.pow(v.getValues()[0], 2) + Math.pow(v.getValues()[1], 2) + Math.pow(v.getValues()[2], 2));
        } else{
            throw new IllegalStateException("Evaluating a non 2D/3D vector");
        }
    }


    /**
     * Calculates the scalar equation of a line in vector form in 2D and outputs it to the user
     *
     * @param point The point on the line
     * @param direction The direction vector of the line
     * @return ArrayList<Token> The scalar equation to be output on the screen
     */
    public static ArrayList<Token> calculateScalarEquation(double[] point, double[] direction) {
        ArrayList<Token> output = new ArrayList<Token>();
        //line is in the form [a,b] + t[c,d] where [a,b] is the point and [c,d] is the direction vector
        double a = point[0];
        double b = point[1];
        double c = direction[0];
        double d = direction[1];
        double z = -1 * c * b + d * a;

        if (c == 0 && d == 0) {
            throw new IllegalArgumentException("Error: Not a line!");
        }

        //special case if c = 0
        if (c == 0) {
            output.add(VariableFactory.makeX());
            output.add(new Token("=") {
            });
            output.add(new Number(Utility.round(a, 3)));
            return output;
        }

        //special case if d = 0
        if (d == 0) {
            output.add(VariableFactory.makeY());
            output.add(new Token("=") {
            });
            output.add(new Number(Utility.round(b, 3)));
            return output;
        }

        //Scalar equation is in the form cy - dx + z = 0 , where z = -cb + da

        //for first term
        if (c != 0) {
            output.add(new Number(Utility.round(c, 3)));
            output.add(VariableFactory.makeY());
        }

        //for second term
        if (d > 0) {
            output.add(OperatorFactory.makeSubtract());
            output.add(new Number(Utility.round(Math.abs(d), 3)));
        } else if (d < 0) {
            if (c != 0) {
                output.add(OperatorFactory.makeAdd());
            }
            output.add(new Number(Utility.round(Math.abs(d), 3)));
        }
        if (d != 0) {
            output.add(VariableFactory.makeX());
        }

        //for third term
        if (z > 0) {
            output.add(OperatorFactory.makeAdd());
        }
        if (z < 0) {
            output.add(OperatorFactory.makeSubtract());
        }

        if (z != 0) {
            output.add(new Number(Utility.round(Math.abs(z), 3)));
        }

        // = 0
        output.add(new Token("=") {
        });
        output.add(new Number(0));
        return output;
    }

    /**
     * Converts the series of Numbers, Vector Brackets and Commas into Vectors.
     *
     * @param input The expression to parse Vectors
     * @return Expression with the Vectors parsed
     */
    public static ArrayList<Token> parseVectors(ArrayList<Token> input){
        input = Utility.condenseDigits(input);
        ArrayList<Token> output = new ArrayList<>();
        ArrayList<Number> listOfNumbers = new ArrayList<>();
        ArrayList<Token> temp = new ArrayList<>();
        boolean newVector = false;
        for (Token t : input) {
            if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.SQUAREOPEN) {
                newVector = true;
            } else if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.SQUARECLOSED){
                newVector = false;
                double[] nums = new double[listOfNumbers.size()];
                int i = 0;
                for (Number num : listOfNumbers){
                    nums[i] = num.getValue();
                    i++;
                }
                output.add(new Vector(nums));
            } else if (newVector && t instanceof Placeholder && ((Placeholder)t).getType() == Placeholder.COMMA){ //A comma
                listOfNumbers.add(new Number(Utility.process(temp)));
                temp.clear();
            } else if (newVector){ //Inside a vector
                temp.add(t);
            } else { //Nothing special
                output.add(t);
            }
        }
        return output;
    }

    /**
     * Uses the shunting yard algorithm to change the expression from infix to reverse polish for Vectors.
     *
     * @param infix The infix expression
     * @return The expression in reverse polish
     * @throws java.lang.IllegalArgumentException The infix notation is invalid
     */
    public static ArrayList<Token> convertToReversePolish(ArrayList<Token> infix) {
        ArrayList<Token> reversePolish = new ArrayList<Token>();
        Stack<Token> stack = new Stack<Token>();
        for (Token token : infix) {
            if (token instanceof Number || token instanceof Vector) { //Adds directly to the queue if it's a token
                reversePolish.add(token);
            } else if (token instanceof Function) { //Adds to the stack if it's a function
                stack.push(token);
            } else if (token instanceof Operator) {
                if (!stack.empty()) { //Make sure it's not empty to prevent bugs
                    Token top = stack.lastElement();
                    while (top != null && ((top instanceof Operator && ((Operator) token).isLeftAssociative()
                            && ((Operator) top).getPrecedence() >= ((Operator) token).getPrecedence()) || top instanceof Function)) { //Operator is left associative and higher precendence / is a function
                        reversePolish.add(stack.pop()); //Pops top element to the queue
                        top = stack.isEmpty() ? null : stack.lastElement(); //Assigns the top element of the stack if it exists
                    }
                }
                stack.push(token);
            }else if (token instanceof VectorOperator){
                if (!stack.empty()) { //Make sure it's not empty to prevent bugs
                    Token top = stack.lastElement();
                    while (top != null && ((top instanceof VectorOperator && ((VectorOperator) top).getPrecedence() >= ((VectorOperator) token).getPrecedence()) || top instanceof Function)) { //Operator is left associative and higher precendence / is a function
                        reversePolish.add(stack.pop()); //Pops top element to the queue
                        top = stack.isEmpty() ? null : stack.lastElement(); //Assigns the top element of the stack if it exists
                    }
                }
                stack.push(token);
            }else if (token instanceof Bracket) {
                Bracket bracket = (Bracket) token;
                if (bracket.getType() == Bracket.OPEN || bracket.getType() == Bracket.SUPERSCRIPT_OPEN
                        || bracket.getType() == Bracket.NUM_OPEN || bracket.getType() == Bracket.DENOM_OPEN) { //Pushes the bracket to the stack if it's open
                    stack.push(bracket);
                } else if (bracket.getType() == Bracket.CLOSE || bracket.getType() == Bracket.SUPERSCRIPT_CLOSE
                        || bracket.getType() == Bracket.NUM_CLOSE || bracket.getType() == Bracket.DENOM_CLOSE) { //For close brackets, pop operators onto the list until a open bracket is found
                    Token top = stack.lastElement();
                    while (!(top instanceof Bracket)) { //While it has not found an open bracket
                        reversePolish.add(stack.pop()); //Pops the top element
                        if (stack.isEmpty()) { //Mismatched brackets
                            throw new IllegalArgumentException();
                        }
                        top = stack.lastElement();
                    }
                    stack.pop(); //Removes the bracket
                }
            }
        }
        //All tokens read at this point
        while (!stack.isEmpty()) { //Puts the remaining tokens in the stack to the queue
            reversePolish.add(stack.pop());
        }
        return reversePolish;
    }

    /**
     * Finds the Scalar product between a scalar and a Vector.
     *
     * @param scalar The scalar
     * @param vector The vector
     * @return The product between the scalar and the vector
     */
    public static Vector findScalarProduct(double scalar, Vector vector){
        double[] values = vector.getValues();
        double[] newValues = new double[values.length];
        for (int i = 0; i < values.length; i++){
            newValues[i] = values[i] * scalar;
        }
        return new Vector(newValues);
    }

    /**
     * Evaluates a given expression in reverse polish notation and returns the resulting value.
     *
     * @param tokens The expression in reverse polish
     * @return The value of the expression
     * @throws java.lang.IllegalArgumentException The user has inputted an invalid expression
     */
    public static Token evaluateExpression(ArrayList<Token> tokens) {
        Stack<Token> stack = new Stack<Token>();
        for (Token token : tokens) {
            if (token instanceof Number || token instanceof Vector) { //Adds all numbers directly to the stack
                stack.push(token);
            }else if (token instanceof VectorOperator) {
                //Operates the first and second top operators
                Token right = stack.pop();
                Token left = stack.pop();
                stack.push(((VectorOperator)token).operate(left, right)); //Adds the result back to the stack
            } else if (token instanceof VectorFunction) { //Function uses the top number on the stack
                Token top = stack.pop(); //Function performs on the first number
                if (!(top instanceof Vector)){
                    throw new IllegalArgumentException("Cannot perform this Function on a non-Vector");
                }
                stack.push(((VectorFunction)token).perform((Vector) top)); //Adds the result back to the stack
            } else if (token instanceof Function){
                Token top = stack.pop(); //Function performs on the first number
                if (!(top instanceof Number)){
                    throw new IllegalArgumentException("Cannot perform this Function on a non-Number");
                }
                stack.push(new Number(((Function) token).perform(((Number)top).getValue()))); //Adds the result back to the stack
            } else{ //This should never be reached
                throw new IllegalArgumentException();
            }
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid Expression"); //There should only be 1 token left on the stack
        } else {
            return stack.pop();
        }
    }

    /**
     * Sets up the given Vector expression.
     *
     * @param toSetup The expression the set up
     * @return The set up expression
     */
    public static ArrayList<Token> setupVectorExpression(ArrayList<Token> toSetup){
        ArrayList<Token> toReturn = Utility.setupExpression(toSetup);
        //Converts the Magnitude bars to the function
        return toReturn;
    }


}
