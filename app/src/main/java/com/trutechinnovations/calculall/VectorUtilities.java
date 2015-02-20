package com.trutechinnovations.calculall;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Contains various static utility methods for Vector Mode.
 *
 * @author Alston Lin, Keith Wong, Jason Fok
 * @version Alpha 2.2
 */
public class VectorUtilities {

    /**
     * Finds the dot product of the given vectors.
     *
     * @param left  First Vector
     * @param right Second Vector
     * @return The dot product
     */
    public static Double findDotProduct(Vector left, Vector right) {
        if (left.getDimensions() == right.getDimensions()) {
            double result = 0;
            for (int i = 0; i < left.getDimensions(); i++) {
                result += left.getValues()[i] * right.getValues()[i];
            }
            return result;
        } else {
            throw new IllegalArgumentException("Attempted to operate on two vector of different dimensions");
        }
    }

    /**
     * Returns the magnitude of a vector given a Vector.
     *
     * @param v The vector to find the magnitude of
     * @return The Number that contains the value of the magnitude
     */
    public static double calculateMagnitude(Vector v) {
        double sum = 0;
        for (int i = 0; i < v.getDimensions(); i++) {
            sum += v.getValues()[i] * v.getValues()[i];
        }
        return Math.sqrt(sum);
    }

    /**
     * Finds the cross product of the given two vectors.
     *
     * @param leftVector  The left vector to cross
     * @param rightVector The right vector to cross
     * @return The resultant cross product
     */
    public static Vector findCrossProduct(Vector leftVector, Vector rightVector) {
        if (leftVector.getDimensions() == 3 && rightVector.getDimensions() == 3) {
            double[] values = new double[3];
            values[0] = (leftVector.getValues()[1] * rightVector.getValues()[2]) - (leftVector.getValues()[2] * rightVector.getValues()[1]); //U2V3 - U3V2
            values[1] = (leftVector.getValues()[2] * rightVector.getValues()[0]) - (leftVector.getValues()[0] * rightVector.getValues()[2]); //U3V1 - U1V3
            values[2] = (leftVector.getValues()[0] * rightVector.getValues()[1]) - (leftVector.getValues()[1] * rightVector.getValues()[0]); //U1V2 - U2V1
            return new Vector(values);
        } else {
            throw new IllegalArgumentException("Can only have a cross product of two 3D vectors.");
        }
    }

    /**
     * Calculates the scalar equation of a line in vector form in 2D and outputs it to the user
     *
     * @param point     The point on the line
     * @param direction The direction vector of the line
     * @return ArrayList<Token> The scalar equation to be output on the screen
     */
    public static ArrayList<Token> calculateScalarEquationLine(Vector point, Vector direction) {
        ArrayList<Token> output = new ArrayList<Token>();
        //line is in the form [a,b] + t[c,d] where [a,b] is the point and [c,d] is the direction vector
        double a = point.getValues()[0];
        double b = point.getValues()[1];
        double c = direction.getValues()[0];
        double d = direction.getValues()[1];
        double z = -1 * c * b + d * a;

        if (c == 0 && d == 0) {
            throw new IllegalArgumentException("Error: Not a line!");
        }

        //special case if c = 0
        if (c == 0) {
            output.add(VariableFactory.makeX());
            output.add(new Token("=") {
            });
            output.add(new Number(a));
            return output;
        }

        //special case if d = 0
        if (d == 0) {
            output.add(VariableFactory.makeY());
            output.add(new Token("=") {
            });
            output.add(new Number(b));
            return output;
        }

        //Scalar equation is in the form cy - dx + z = 0 , where z = -cb + da

        //for first term
        if (c != 0) {
            output.add(new Number(c));
            output.add(VariableFactory.makeY());
        }

        //for second term
        if (d > 0) {
            output.add(OperatorFactory.makeSubtract());
            output.add(new Number(Math.abs(d)));
        } else if (d < 0) {
            if (c != 0) {
                output.add(OperatorFactory.makeAdd());
            }
            output.add(new Number(Math.abs(d)));
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
            output.add(new Number(Math.abs(z)));
        }

        // = 0
        output.add(new Token(" = ") {
        });
        output.add(new Number(0));
        return output;
    }

    /**
     * Calculates the scalar equation of a plane.
     *
     * @param point Any point on the plane
     * @param d1    The first direction vector
     * @param d2    The second, perpendicular direction vector
     * @return The scalar equation of the plane
     */
    public static ArrayList<Token> calculateScalarEquationPlane3D(Vector point, Vector d1, Vector d2) {
        if (calculateMagnitude(findCrossProduct(d1, d2)) == 0) {
            throw new IllegalArgumentException("The two direction vectors cannot be collinear");
        }
        Vector normal = findCrossProduct(d1, d2);
        ArrayList<Token> scalar = new ArrayList<>();
        //X
        if (normal.getValues()[0] != 0) {
            scalar.add(new Number(normal.getValues()[0]));
            scalar.add(new StringToken("X"));
        }
        //Y
        if (normal.getValues()[1] != 0) {
            if (normal.getValues()[1] > 0) {
                scalar.add(OperatorFactory.makeAdd());
            } else {
                scalar.add(OperatorFactory.makeSubtract());
            }
            scalar.add(new Number(Math.abs(normal.getValues()[1])));
            scalar.add(new StringToken("Y"));
        }
        //Z
        if (normal.getValues()[2] != 0) {
            if (normal.getValues()[2] > 0) {
                scalar.add(OperatorFactory.makeAdd());
            } else {
                scalar.add(OperatorFactory.makeSubtract());
            }
            scalar.add(new Number(Math.abs(normal.getValues()[2])));
            scalar.add(new StringToken("Z"));
        }
        //Constant
        double constant = findDotProduct(normal, point) * -1;
        if (constant > 0) {
            scalar.add(OperatorFactory.makeAdd());
            scalar.add(new Number(constant));
        } else if (constant < 0) {
            scalar.add(OperatorFactory.makeSubtract());
            scalar.add(new Number(-1 * constant));
        }
        scalar.add(new Token(" = ") {
        });
        scalar.add(new Number(0));
        return scalar;
    }


    /**
     * Converts the series of Numbers, Vector Brackets and Commas into Vectors.
     *
     * @param input The expression to parse Vectors
     * @return Expression with the Vectors parsed
     */
    public static ArrayList<Token> parseVectors(ArrayList<Token> input) {
        ArrayList<Token> output = new ArrayList<>();
        ArrayList<Number> listOfNumbers = new ArrayList<>();
        ArrayList<Token> temp = new ArrayList<>();
        boolean newVector = false;
        for (Token t : input) {
            if (t instanceof Bracket && t.getType() == Bracket.SQUAREOPEN) {
                newVector = true;
            } else if (t instanceof Bracket && t.getType() == Bracket.SQUARECLOSED) {
                //Adds last entry
                ArrayList<Token> entry = processVectors(temp);
                if (!(entry.size() == 1 && entry.get(0) instanceof Number)) {
                    throw new IllegalArgumentException("Invalid Vector entry");
                }
                listOfNumbers.add((Number) entry.get(0));
                temp.clear();
                //Resets the boolean
                newVector = false;
                //Adds all the numerical entries into the Vector
                double[] nums = new double[listOfNumbers.size()];
                int i = 0;
                for (Number num : listOfNumbers) {
                    nums[i] = num.getValue();
                    i++;
                }
                //Creates the Vector
                output.add(new Vector(nums));
                listOfNumbers.clear();
            } else if (newVector && t instanceof Placeholder && t.getType() == Placeholder.COMMA) { //A comma
                //Adds last entry
                ArrayList<Token> entry = processVectors(temp);
                if (!(entry.size() == 1 && entry.get(0) instanceof Number)) {
                    throw new IllegalArgumentException("Invalid Vector entry");
                }
                listOfNumbers.add((Number) entry.get(0));
                temp.clear();
            } else if (newVector) { //Inside a vector
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
            } else if (token instanceof Function || token instanceof VectorFunction) { //Adds to the stack if it's a function
                stack.push(token);
            } else if (token instanceof OrderComparable) {
                if (!stack.empty()) { //Make sure it's not empty to prevent bugs
                    Token top = stack.lastElement();
                    while (top != null && ((top instanceof OrderComparable && ((OrderComparable) token).isLeftAssociative()
                            && ((OrderComparable) top).getPrecedence() >= ((OrderComparable) token).getPrecedence()) || (top instanceof Function || top instanceof VectorFunction))) { //Operator is left associative and higher precendence / is a function
                        reversePolish.add(stack.pop()); //Pops top element to the queue
                        top = stack.isEmpty() ? null : stack.lastElement(); //Assigns the top element of the stack if it exists
                    }
                }
                stack.push(token);
            } else if (token instanceof Bracket) {
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
    public static Vector findScalarProduct(double scalar, Vector vector) {
        double[] values = vector.getValues();
        double[] newValues = new double[values.length];
        for (int i = 0; i < values.length; i++) {
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
        if (tokens.size() == 0) { //No Expression
            return null; //Returns no expreesion
        }
        Stack<Token> stack = new Stack<Token>();
        for (Token token : tokens) {
            if (token instanceof Number || token instanceof Vector) { //Adds all numbers directly to the stack
                stack.push(token);
            } else if (token instanceof VectorOperator) {
                //Operates the first and second top operators
                Token right = stack.pop();
                Token left = stack.pop();
                stack.push(((VectorOperator) token).operate(left, right)); //Adds the result back to the stack
            } else if (token instanceof Operator) {
                //Operates the first and second top operators
                Token right = stack.pop();
                Token left = stack.pop();
                if (!(right instanceof Number && left instanceof Number)) {
                    throw new IllegalArgumentException("Error performing an operation");
                }
                stack.push(new Number(((Operator) token).operate(((Number) left).getValue(), ((Number) right).getValue()))); //Adds the result back to the stack
            } else if (token instanceof VectorFunction) { //Function uses the top number on the stack
                Token top = stack.pop(); //Function performs on the first number
                if (!(top instanceof Vector)) {
                    throw new IllegalArgumentException("Cannot perform this Function on a non-Vector");
                }
                stack.push(((VectorFunction) token).perform((Vector) top)); //Adds the result back to the stack
            } else if (token instanceof Function) {
                Token top = stack.pop(); //Function performs on the first number
                if (!(top instanceof Number)) {
                    throw new IllegalArgumentException("Cannot perform this Function on a non-Number");
                }
                stack.push(new Number(((Function) token).perform(((Number) top).getValue()))); //Adds the result back to the stack
            } else { //This should never be reached
                throw new IllegalArgumentException();
            }
        }
        if (stack.size() == 0) { //Empty Expression
            return null;
        } else if (stack.size() != 1) {
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
    public static ArrayList<Token> setupVectorExpression(ArrayList<Token> toSetup) {
        //Converts the Magnitude bars to the function
        ArrayList<Token> toReturn = new ArrayList<>();
        for (int i = 0; i < toSetup.size(); i++) {
            Token t = toSetup.get(i);
            Token previous = i == 0 ? null : toSetup.get(i - 1);
            //Applies pattern N[V] -> N * [V]
            if (t instanceof Bracket && (t.getType() == Bracket.SQUAREOPEN || t.getType() == Bracket.MAGNITUDE_OPEN) && previous instanceof Number) {
                toReturn.add(VectorOperatorFactory.makeCross());
            }
            if (t instanceof Bracket && t.getType() == Bracket.MAGNITUDE_OPEN) { //Replaces Magnitude bars
                ArrayList<Token> inside = new ArrayList<>();
                int bracketCount = -1;
                i++;
                while (i < toSetup.size() && bracketCount < 0) {
                    Token token = toSetup.get(i);
                    if (token instanceof Bracket && token.getType() == Bracket.MAGNITUDE_OPEN) {
                        bracketCount--;
                    } else if (token instanceof Bracket && token.getType() == Bracket.MAGNITUDE_CLOSE) {
                        bracketCount++;
                    }
                    inside.add(token);
                    i++;
                }
                i--; //Last iteration not counted
                if (bracketCount != 0) { //Mismatches brackets
                    throw new IllegalArgumentException("Mismatched Magnitude Brackets!");
                }
                inside.remove(inside.size() - 1); //Removes the last magnitude bracket
                inside = setupVectorExpression(inside); //Sets itself up recursively
                //Now inserts it into the Magnitude function
                toReturn.add(VectorFunctionFactory.makeMagnitude());
                toReturn.add(BracketFactory.makeOpenBracket());
                toReturn.addAll(inside);
                toReturn.add(BracketFactory.makeCloseBracket());
            } else {
                toReturn.add(t);
            }
        }
        return toReturn;
    }


    /**
     * Calculates the Vector expression.
     *
     * @param tokens The result of the expression
     */
    public static ArrayList<Token> processVectors(ArrayList<Token> tokens) {
        ArrayList<Token> parsedTokens = parseVectors(setupVectorExpression(Utility.setupExpression(Utility.condenseDigits(subVariables(tokens)))));
        //Makes sure theres no Variables left
        for (Token t : tokens) {
            if (t instanceof VectorVariable && (t.getType() == VectorVariable.T || t.getType() == VectorVariable.S)) {
                throw new IllegalArgumentException("s and t can only be used for finding Scalar Equations");
            }
        }
        Token result = evaluateExpression(convertToReversePolish(parsedTokens));
        ArrayList<Token> toOutput = new ArrayList<>();
        if (result != null) {
            toOutput.add(result);
        }
        return toOutput;
    }

    public static ArrayList<Token> subVariables(ArrayList<Token> tokens) {
        ArrayList<Token> toReturn = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t instanceof VectorVariable) {
                if (t.getType() == VectorVariable.U) {
                    toReturn.add(VectorVariable.uValue);
                } else if (t.getType() == VectorVariable.V) {
                    toReturn.add(VectorVariable.vValue);
                } else {
                    toReturn.add(t);
                }
            } else if (t instanceof Variable) {
                toReturn.add(new Number(((Variable) t).getValue()));
            } else {
                toReturn.add(t);
            }
        }
        return toReturn;
    }
}