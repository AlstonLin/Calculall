package com.trutech.calculall;


import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

import java.util.ArrayList;

/**
 * Utilities specificly for calculus related functions.
 */
public class CalculusUtilities {
    /**
     * Differentiates the given function in ArrayList form.
     *
     * @param function The function to differentiate
     * @return The differentiated function
     */
    public static ArrayList<Token> differentiate(ArrayList<Token> function) {
        String expr = Utility.printExpression(function);
        try {
            String derivativeStr = differentiateStr(expr);
            ArrayList<Token> derivative = convertStringToTokens(derivativeStr);
            derivative = JFok.simplifyExpression(derivative);
            return derivative;
        } catch (SyntaxError e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Differentiates the given function in ArrayList form.
     *
     * @param function The function to differentiate
     * @return The differentiated function
     * @throws java.lang.ClassNotFoundException The integral cannot be stated as an elementary function
     */
    public static ArrayList<Token> integrate(ArrayList<Token> function) throws UnsupportedOperationException {
        String expr = Utility.printExpression(function);
        try {
            String integralStr = integrateStr(expr);
            try {
                ArrayList<Token> integral = convertStringToTokens(integralStr);
                integral = JFok.simplifyExpression(integral);
                //Constant of Integration
                integral.add(OperatorFactory.makeAdd());
                integral.add(VariableFactory.makeC());
                return integral;
            } catch (NumberFormatException e) { //Special function, shows String for now
                ArrayList<Token> tokens = new ArrayList<Token>();
                tokens.add(new StringToken(integralStr));
                //TODO: Add special functions
                return tokens;
            }
        } catch (SyntaxError e) { //Malformed expression
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Uses the Symja library to evaluate the String version of the integral.
     *
     * @param function The function to integrate
     * @return The indefinite integral of the function
     */
    public static String integrateStr(String function) {
        EvalUtilities util = new EvalUtilities(false, true);
        /*
        try {
        */
        IExpr integral = util.evaluate("integrate(" + function + ",x)");
        if (integral.toString().equals(function.toString())) { //Could not integrate into an elementary function
            throw new UnsupportedOperationException();
        }
        return integral.toString();
            /*
        } catch (NoClassDefFoundError e){ //Not elementary function
            throw new UnsupportedOperationException();
        }
        */
    }

    /**
     * Converts the String into an expression tree to use the
     *
     * @param str The expression as a String
     * @return The expression tree
     */
    public static ArrayList<Token> convertStringToTokens(String str) {
        ArrayList<Token> tokens = new ArrayList();
        int charIndex = 0;
        boolean processingDigits = false;
        boolean handled = false; //If the character has been handled
        String temp = ""; //Temporarily stores strings
        while (charIndex < str.length()) {
            char c = str.charAt(charIndex);

            //FILTERS NUMBERS
            if (processingDigits) { //Last character was a digit
                if (Character.isDigit(c)) { //Continues processing digits
                    temp += c;
                    handled = true;
                } else { //No more digits
                    //Adds all digits collected as a Number and puts it into the Tokens list
                    tokens.add(new Number(Integer.parseInt(temp)));
                    temp = "";
                    processingDigits = false;
                }
            } else if (Character.isDigit(c)) { //Initial Digit of a number
                processingDigits = true;
                temp += c;
                //Looks at negatives (turns all consecutive subtractions before it into negatives)
                int tempIndex = tokens.size() - 1;
                while (tempIndex >= 0 && tokens.get(tempIndex) instanceof Operator && ((Operator) tokens.get(tempIndex)).getType() == Operator.SUBTRACT
                        && operatorBefore(tokens, tempIndex)) { //If the previous token is a - AND there is an operator before the -
                    tokens.remove(tempIndex);
                    tokens.add(DigitFactory.makeNegative());
                    tempIndex--;
                }
                handled = true;
            }

            //FILTERS ALPHABETIC CHARACTERS
            if (!handled && Character.isLetter(c)) {
                if (c == 'x' && temp.isEmpty()) {
                    Variable x = VariableFactory.makeX();
                    tokens.add(x);
                    handled = true;
                } else if (c == 'e' && temp.isEmpty()) {
                    Variable e = VariableFactory.makeE();
                    tokens.add(e);
                    handled = true;
                } else {
                    temp += c;
                    //Looks for combinations of characters
                    if (temp.equals("Sin")) {
                        tokens.add(FunctionFactory.makeSinR());
                        temp = "";
                        handled = true;
                    } else if (temp.equals("Cos")) {
                        tokens.add(FunctionFactory.makeCosR());
                        temp = "";
                        handled = true;
                    } else if (temp.equals("Tan")) {
                        tokens.add(FunctionFactory.makeTanR());
                        temp = "";
                        handled = true;
                    } else if (temp.equals("Sec")) {
                        tokens.add(new StringToken("sec"));
                        temp = "";
                        handled = true;
                    } else if (temp.equals("Csc")) {
                        tokens.add(new StringToken("csc"));
                        temp = "";
                        handled = true;
                    } else if (temp.equals("Cot")) {
                        tokens.add(new StringToken("cot"));
                        temp = "";
                        handled = true;
                    } else if (temp.equals("ArcSin")) {
                        tokens.add(FunctionFactory.makeASinR());
                        temp = "";
                        handled = true;
                    } else if (temp.equals("ArcCos")) {
                        tokens.add(FunctionFactory.makeACosR());
                        temp = "";
                        handled = true;
                    } else if (temp.equals("ArcTan")) {
                        tokens.add(FunctionFactory.makeATanR());
                        temp = "";
                        handled = true;
                    } else if (temp.equals("Log")) {
                        tokens.add(FunctionFactory.makeLn());
                        temp = "";
                        handled = true;
                    } else if (temp.equals("Pi")) {
                        tokens.add(VariableFactory.makePI());
                        temp = "";
                        handled = true;
                    } else {
                        handled = true; //Will keep storing the characters
                    }
                }
            }

            //FILTERS OPERATORS
            if (!handled) {
                if (c == '+') {
                    tokens.add(OperatorFactory.makeAdd());
                    handled = true;
                } else if (c == '-') {
                    tokens.add(OperatorFactory.makeSubtract());
                    handled = true;
                } else if (c == '*') {
                    tokens.add(OperatorFactory.makeMultiply());
                    handled = true;
                } else if (c == '/') {
                    tokens.add(OperatorFactory.makeDivide());
                    handled = true;
                } else if (c == '^') {
                    tokens.add(OperatorFactory.makeExponent());
                    handled = true;
                }
            }

            //FILTERS BRACKETS
            if (!handled) {
                if (c == '(') {
                    tokens.add(BracketFactory.makeOpenBracket());
                    handled = true;
                } else if (c == ')') {
                    tokens.add(BracketFactory.makeCloseBracket());
                    handled = true;
                }
            }
            handled = false;

            charIndex++;
        }
        //Handles digits if it is the last token
        if (processingDigits && Character.isDigit(str.charAt(str.length() - 1))) {
            tokens.add(new Number(Integer.parseInt(temp)));
        }
        return tokens;
    }

    /**
     * Checks if the Token before the given index is an Operator.
     *
     * @param tokens    The list of Tokens
     * @param tempIndex the index to check
     * @return If there is an Operator before or not
     */
    private static boolean operatorBefore(ArrayList<Token> tokens, int tempIndex) {
        while (tempIndex > 0 && tokens.get(tempIndex - 1) instanceof Bracket) { //Deos not count brackets
            tempIndex--;
        }
        return tokens.get(tempIndex - 1) instanceof Operator;
    }

    /**
     * Uses the Symja library to evaluate the String version of the derivative
     *
     * @param function The function to find the derivative
     * @return The derivative
     */
    public static String differentiateStr(String function) {
        EvalUtilities util = new EvalUtilities(false, true);
        IExpr derivative = util.evaluate("diff(" + function + ",x)");
        return derivative.toString();
    }
}
