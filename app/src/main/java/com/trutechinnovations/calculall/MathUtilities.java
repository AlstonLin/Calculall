package com.trutechinnovations.calculall;


import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;

import java.util.ArrayList;

/**
 * Utilities specifically for calculus related functions.
 *
 * @author Alston Lin
 * @version Alpha 2.0
 */
public class MathUtilities {
    private static EvalUtilities util = new EvalUtilities(false, true);

    /**
     * Differentiates the given function in ArrayList form.
     *
     * @param function The function to differentiate
     * @return The differentiated function
     */
    public static ArrayList<Token> differentiate(ArrayList<Token> function) {
        String expr = Utility.machinePrintExpression(Utility.setupExpression(function));
        try {
            String derivativeStr = differentiateStr(expr);
            ArrayList<Token> derivative = convertStringToTokens(derivativeStr);
            derivative = JFok.jFokExpression(derivative);
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
     */
    public static ArrayList<Token> integrate(ArrayList<Token> function) throws UnsupportedOperationException {
        String expr = Utility.machinePrintExpression(Utility.setupExpression(function));
        try {
            String integralStr = integrateStr(expr);
            ArrayList<Token> integral = convertStringToTokens(integralStr);
            integral = replaceAppellF1(integral);
            integral = JFok.jFokExpression(integral);
            //Constant of Integration
            integral.add(OperatorFactory.makeAdd());
            integral.add(VariableFactory.makeC());
            return integral;
        } catch (SyntaxError e) { //Malformed expression
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Replaces the AppellF1 functions with their values
     *
     * @param expression The original expression
     * @return The expression with AppellF1 instances replaced
     */
    public static ArrayList<Token> replaceAppellF1(ArrayList<Token> expression) {
        boolean replaced = false; //If there was a AppellF1 function replaced
        ArrayList<Token> newExpr = new ArrayList<>();
        for (int i = 0; i < expression.size(); i++) {
            Token t = expression.get(i);
            if (t instanceof Function && ((Function) t).getType() == Function.APPELLF1) {
                replaced = true;
                i += 2; //Open bracket
                ArrayList<Token> function = new ArrayList<>();
                int bracketCount = 1;
                while (bracketCount != 0) {
                    Token token = expression.get(i);
                    if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.OPEN) {
                        bracketCount++;
                    } else if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.CLOSE) {
                        bracketCount--;
                    }
                    function.add(token);
                    i++;
                }
                function.remove(function.size() - 1); //Last close bracket
                if (Utility.machinePrintExpression(function).equals("2.0,1.0/2.0,1.0/2.0,3.0,x,-x")) {
                    //AppellF1(2,1/2,1/2,3/x,-x) = -2sqrt(1-x^2)/x^2
                    newExpr.add(DigitFactory.makeNegative());
                    newExpr.add(DigitFactory.makeTwo());
                    newExpr.add(OperatorFactory.makeMultiply());
                    newExpr.add(FunctionFactory.makeSqrt());
                    newExpr.add(BracketFactory.makeOpenBracket());
                    newExpr.add(DigitFactory.makeOne());
                    newExpr.add(OperatorFactory.makeSubtract());
                    newExpr.add(VariableFactory.makeX());
                    newExpr.add(OperatorFactory.makeExponent());
                    newExpr.add(DigitFactory.makeTwo());
                    newExpr.add(BracketFactory.makeCloseBracket());
                    newExpr.add(OperatorFactory.makeDivide());
                    newExpr.add(VariableFactory.makeX());
                    newExpr.add(OperatorFactory.makeExponent());
                    newExpr.add(DigitFactory.makeTwo());
                }
            } else {
                newExpr.add(t);
            }
        }
        return replaced ? expand(newExpr) : newExpr;
    }

    /**
     * Uses the Symja library to evaluate the String version of the integral.
     *
     * @param function The function to integrate
     * @return The indefinite integral of the function
     */
    public static String integrateStr(String function) {
        String str = "Factor(integrate(" + function + ",x))";
        IExpr integral = util.evaluate(str);
        if (integral.toString().contains("Integrate")) { //Could not integrate into an elementary funct
            // ion
            throw new UnsupportedOperationException();
        }
        return integral.toString();
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
                if (Character.isDigit(c) || c == '.') { //Continues processing digits
                    temp += c;
                    handled = true;
                } else { //No more digits
                    //Adds all digits collected as a Number and puts it into the Tokens list
                    tokens.add(new Number(Double.parseDouble(temp)));
                    temp = "";
                    processingDigits = false;
                }
            } else if (Character.isDigit(c) || c == '.') { //Initial Digit of a number
                //Special case: AppellF1
                if (c == '1' && temp.equals("AppellF")) {
                    tokens.add(FunctionFactory.makeAppellF1());
                    temp = "";
                    handled = true;
                } else {
                    processingDigits = true;
                    temp += c;
                    handled = true;
                }
            }

            //FILTERS ALPHABETIC CHARACTERS
            if (!handled && Character.isLetter(c)) {
                temp += c;
                //Variables
                if (temp.equals("pi") || temp.equals("Pi")) { //IDK, it changes from caps to non-caps
                    tokens.add(VariableFactory.makePI());
                    temp = "";
                } else if (temp.equals("x")) {
                    tokens.add(VariableFactory.makeX());
                    temp = "";
                } else if (temp.equals("e")) {
                    tokens.add(VariableFactory.makeE());
                    temp = "";
                } else {
                    if (str.length() == charIndex + 1 || !Character.isLetter(str.charAt(charIndex + 1))) { //Only checks at the end of a letter sequence
                        //Looks for combinations of characters for functions
                        switch (temp) {
                            case "Sin":
                                tokens.add(FunctionFactory.makeSin());
                                temp = "";
                                break;
                            case "Cos":
                                tokens.add(FunctionFactory.makeCos());
                                temp = "";
                                break;
                            case "Tan":
                                tokens.add(FunctionFactory.makeTan());
                                temp = "";
                                break;
                            case "Csc":
                                tokens.add(FunctionFactory.makeCsc());
                                temp = "";
                                break;
                            case "Sec":
                                tokens.add(FunctionFactory.makeSec());
                                temp = "";
                                break;
                            case "Cot":
                                tokens.add(FunctionFactory.makeCot());
                                temp = "";
                                break;
                            case "Erf":
                                tokens.add(FunctionFactory.makeErf());
                                temp = "";
                                break;
                            case "Erfi":
                                tokens.add(FunctionFactory.makeErfi());
                                temp = "";
                                break;
                            case "Gamma":
                                tokens.add(FunctionFactory.makeGamma());
                                temp = "";
                                break;
                            case "ArcSin":
                                tokens.add(FunctionFactory.makeASin());
                                temp = "";
                                break;
                            case "ArcCos":
                                tokens.add(FunctionFactory.makeACos());
                                temp = "";
                                break;
                            case "ArcTan":
                                tokens.add(FunctionFactory.makeATan());
                                temp = "";
                                break;
                            case "Log":
                                tokens.add(FunctionFactory.makeLn());
                                temp = "";
                                break;
                            case "Sinh":
                                tokens.add(FunctionFactory.makeSinh());
                                temp = "";
                                break;
                            case "Cosh":
                                tokens.add(FunctionFactory.makeCosh());
                                temp = "";
                                break;
                            case "Tanh":
                                tokens.add(FunctionFactory.makeTanh());
                                temp = "";
                                break;
                            case "ArcSinh":
                                tokens.add(FunctionFactory.makeASinh());
                                temp = "";
                                break;
                            case "ArcCosh":
                                tokens.add(FunctionFactory.makeACosh());
                                temp = "";
                                break;
                            case "ArcTanh":
                                tokens.add(FunctionFactory.makeATanh());
                                temp = "";
                        }
                    }
                }
                handled = true;
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
                } else if (c == ')') {
                    tokens.add(BracketFactory.makeCloseBracket());
                } else if (c == ',') {
                    tokens.add(PlaceholderFactory.makeComma());
                } else {
                    throw new UnsupportedOperationException();
                }
            }
            handled = false;

            charIndex++;
        }
        //Handles digits if it is the last token
        if (processingDigits && Character.isDigit(str.charAt(str.length() - 1))) {
            tokens.add(new Number(Double.parseDouble(temp)));
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
        if (tempIndex == 0) {
            return false;
        }
        while (tempIndex > 1 && tokens.get(tempIndex - 1) instanceof Bracket) { //Deos not count brackets
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
        IExpr derivative = util.evaluate("diff(" + function + ",x)");
        return derivative.toString();
    }

    /**
     * Expands the Expression.
     *
     * @param expression The expression to expand
     * @return The expanded expression
     */
    public static ArrayList<Token> expand(ArrayList<Token> expression) {
        String simplifyStr = "Expand(" + Utility.machinePrintExpression(Utility.setupExpression(expression)) + ")";
        try {
            ArrayList<Token> simplified = convertStringToTokens(util.evaluate(simplifyStr).toString());
            simplified = JFok.jFokExpression(simplified);
            return simplified;
        } catch (SyntaxError e) {
            return null; //Malformed Expression
        }
    }

    /**
     * Factors the Expression.
     *
     * @param expression The expression to factor
     * @return The factored expression
     */
    public static ArrayList<Token> factor(ArrayList<Token> expression) {
        String simplifyStr = "Factor(" + Utility.machinePrintExpression(Utility.setupExpression(expression)) + ")";
        try {
            String simplifiedStr = util.evaluate(simplifyStr).toString();
            if (simplifiedStr.contains("Factor")) { //Cannot factor further
                simplifiedStr = simplifiedStr.substring(7, simplifiedStr.length() - 1);
            }
            ArrayList<Token> simplified = convertStringToTokens(simplifiedStr);
            simplified = JFok.jFokExpression(simplified);
            return simplified;
        } catch (SyntaxError e) {
            return null; //Malformed Expression
        }
    }

    /**
     * Finds the roots of the expression.
     *
     * @param expression The expression to find the roots of
     * @return The roots of the expression in a List of expressions
     */
    public static ArrayList<ArrayList<Token>> findRoots(ArrayList<Token> expression) {
        String solveStr = "Solve(" + Utility.machinePrintExpression(Utility.setupExpression(expression)) + "==0,x)";
        try {
            //Uses the library to get the result as a String
            IExpr roots = util.evaluate(solveStr);
            String rootsStr = roots.toString();
            rootsStr = rootsStr.replace("\n", "");
            if (rootsStr.contains("Solve")) {
                throw new UnsupportedOperationException("Cannot find the root");
            }
            //Converts the String into a List of Floats
            String temp = "";
            ArrayList<ArrayList<Token>> rootsList = new ArrayList<>();
            for (int i = 5; i < rootsStr.length() - 2; i++) { //Deos not count the beginning and end {{x-> and }}
                char c = rootsStr.charAt(i);
                if (c == '}') { //Solutions seperated by {},
                    if (!temp.contains("I")) { //REAL NUMBERS ONLY
                        rootsList.add(convertStringToTokens(temp));
                    }
                    i += 5; //Skips the },{x->
                    temp = "";
                } else {
                    temp += c;
                }
            }
            //Real numbers only
            if (!temp.contains("I")) {
                rootsList.add(convertStringToTokens(temp)); //For the last root
            }
            //Validates the roots (makes sure that it is actually a root)
            for (int i = 0; i < rootsList.size(); i++) {
                ArrayList<Token> root = rootsList.get(i);
                float value = (float) Utility.valueAt(expression, Utility.evaluateExpression(Utility.convertToReversePolish(Utility.setupExpression(root))));
                final float ERROR_MARGIN = 1e-6f;
                if (!(value > -ERROR_MARGIN && value < ERROR_MARGIN)) {
                    rootsList.remove(root);
                }
            }
            return rootsList;
        } catch (SyntaxError e) { //Malformed Expression
            return null;
        } catch (WrongArgumentType e) {
            return new ArrayList<>(); //Expression was in the form of E/E; No real roots
        }
    }
}
