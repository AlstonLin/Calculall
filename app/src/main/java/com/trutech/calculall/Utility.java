package com.trutech.calculall;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Contains miscellaneous static methods that provide utility.
 *
 * @version 0.3.0
 */
public class Utility {

	//ID of the Buttons (nowhere else to put them)
	public static final int MC = 0, MR = 1, MS = 2, M_ADD = 3, M_REMOVE = 4, BACKSPACE = 5, CE = 6, C = 7, ZERO = 8, ONE = 9
			, TWO = 10, THREE = 11, FOUR = 12, FIVE = 13, SIX = 14, SEVEN = 15, EIGHT = 16, NINE = 17, PLUS = 18, MINUS = 19
			, MULTIPLY = 20, DIV = 21, RECIPROC = 22, DECIMAL_SEP = 23, SIGN = 24, SQRT = 25, PERCENT = 26, CALCULATE = 27, DUMMY = 28;

	/**
	 * Returns the numerical value of a given set of digits.
	 *
	 * @param digits A list of digits to find the value of
	 * @return The value of the given digits
	 */
	public static double valueOf (List<Digit> digits){
		double value = 0;
		int indexOfDecimal = -1;
		boolean negative = false;

		//Does negatives first
		while (digits.get(0).getValue() == DigitFactory.NEGATIVE){ //Only accepts negatives at the beginning
			digits.remove(0);
			negative = negative ? false : true; //Allows for multiple negatives
		}

		//Finds what index the decimal is in
		for (int i = 0; i < digits.size(); i++){
			if (digits.get(i).getValue() == -1){
				indexOfDecimal = i;
			}
		}

		//Sets the starting power based on where the decimal place was found, if any
		int power = indexOfDecimal == -1 ? digits.size() - 1 : indexOfDecimal - 1;
		for (Digit digit : digits){
			if (digit.getValue() == -1){ //Decimal place
			} else if (digit.getValue() == DigitFactory.NEGATIVE){
				throw new IllegalArgumentException("Invalid placement of negatives in Digits");
			} else {
				value += digit.getValue() * Math.pow(10, power);
				power--;
			}
		};

		return negative ? value * -1 : value;
	}
	
	/**
	 * Sets up the given expression to be processed and allows established
	 * conventions (such as (a + b)(c + d) implying (a + b) * (c + d)).
	 * 
	 * @param toSetup The expression to set up 
	 * @return The expression with the added Tokens to make the 
	 */
	public static ArrayList<Token> setupExpression(ArrayList<Token> toSetup){
		ArrayList<Token> newExpression = new ArrayList<Token>();
		for (Token t : toSetup){
			Token last = newExpression.isEmpty() ? null : newExpression.get(newExpression.size() - 1); //Last token in the new expression
			if (t instanceof Bracket){
				Bracket b = (Bracket)t;
				if (b.getType() == Bracket.OPEN && last instanceof Bracket && ((Bracket)last).getType() == Bracket.CLOSE){ //Ex. (2 + 1)(3 + 4)
					newExpression.add(OperatorFactory.makeMultiply()); //Implies multiplication between the two expressions in the brackets
				} else if (last instanceof Number && b.getType() == Bracket.OPEN){ //Ex. 3(2 + 1)
					newExpression.add(OperatorFactory.makeMultiply());
				}
			} else if (t instanceof Number){ 
				if (last instanceof Number){ //Ex. 5A
					newExpression.add(OperatorFactory.makeMultiply());
				}
			} else if (t instanceof Function){ 
				if (last instanceof Number || last instanceof Function 
						|| (last instanceof Bracket && ((Bracket)last).getType() == Bracket.CLOSE)){ //Ex. 2f(x) or f(x)g(x) or (1 + 2)f(x)
					newExpression.add(OperatorFactory.makeMultiply());
				}
			}
			newExpression.add(t);
		}
		return newExpression;
	}
	
	/**
	 * Uses the shunting yard algorithm to change the expression from infix to reverse polish.
	 *
	 * @param infix The infix expression
	 * @return The expression in reverse polish
	 * @throws IllegalArgumentException The infix notation is invalid
	 */
	public static ArrayList<Token> convertToReversePolish(ArrayList<Token> infix) {
		ArrayList<Token> reversePolish = new ArrayList<Token>();
		Stack<Token> stack = new Stack<Token>();
		for (Token token : infix){
			if (token instanceof Number){ //Adds directly to the queue if it's a token
				reversePolish.add(token);
			} else if (token instanceof Function){ //Adds to the stack if it's a function
				stack.push(token);
			} else if (token instanceof Operator){
				if (!stack.empty()){ //Make sure it's not empty to prevent bugs
					Token top = stack.lastElement();
					while (top != null && ((top instanceof Operator && ((Operator)token).isLeftAssociative()
							&& ((Operator)top).getPrecedence() >= ((Operator)token).getPrecedence()) || top instanceof Function)){ //Operator is left associative and higher precendence / is a function
						reversePolish.add(stack.pop()); //Pops top element to the queue
						top = stack.isEmpty() ? null : stack.lastElement(); //Assigns the top element of the stack if it exists
					}
				}
				stack.push(token);
			} else if (token instanceof Bracket){
				Bracket bracket = (Bracket)token;
				if (bracket.getType() == Bracket.OPEN){ //Pushes the bracket to the stack if it's open
					stack.push(bracket);
				} else{ //For close brackets, pop operators onto the list until a open bracket is found
					Token top = stack.lastElement();
					while (!(top instanceof Bracket)){ //While it has not found an open bracket
						reversePolish.add(stack.pop()); //Pops the top element
						if (stack.isEmpty()){ //Mismatched brackets
							throw new IllegalArgumentException();
						}
						top = stack.lastElement();
					}
					stack.pop(); //Removes the bracket
				}
			}
		}
		//All tokens read at this point
		while (!stack.isEmpty()){ //Puts the remaining tokens in the stack to the queue
			reversePolish.add(stack.pop());
		}
		return reversePolish;
	}

	/**
	 * Evaluates a given expression in reverse polish notation and returns the resulting value.
	 *
	 * @param tokens The expression in reverse polish
	 * @return The value of the expression
	 * @throws IllegalArgumentException The user has inputted an invalid expression
	 */
	public static double evaluateExpression (ArrayList<Token> tokens){
		Stack<Number> stack = new Stack<Number>();
		for (Token token : tokens){
			if (token instanceof Number){ //Adds all numbers directly to the stack
				stack.push((Number)token);
			} else if (token instanceof Operator){
				//Operates the first and second top operators
				Number right = stack.pop();
				Number left = stack.pop();
				stack.push (new Number (((Operator)token).operate(left.getValue(), right.getValue()))); //Adds the result back to the stack
			} else if (token instanceof Function){ //Function uses the top number on the stack
				Number top = stack.pop(); //Function performs on the first number
				stack.push(new Number(((Function)token).perform(top.getValue()))); //Adds the result back to the stack
			} else{ //This should never bee reached
				throw new IllegalArgumentException();
			}
		}
		if (stack.size() != 1){
			throw new IllegalArgumentException(); //There should only be 1 token left on the stack
		} else{
			return stack.pop().getValue();
		}
	}

    private ArrayList<Token> evaluateVector (ArrayList<Token> tokens) {

    }

	/**
	 * Simplifies and Rationalizes the given expression.
	 * 
	 * @param expression The un-simplified expression
	 * @return The simplified expression
	 */
	public static ArrayList<Token> simplifyExpression (ArrayList<Token> expression){
		ArrayList<Token> num = new ArrayList<Token>();
		ArrayList<Token> den = new ArrayList<Token>();
		int intNum = 0;
		int intDen = 0;
		int divisionIndex;
		for (Token token: expression){
			if (token instanceof Operator) {
				if ((((Operator)(token)).getType() == 4)) {
					divisionIndex = expression.indexOf(token);
					for (int i = 0; i < divisionIndex; i++) {
						num.add(expression.get(i));
					}
					for (int i = divisionIndex + 1; i < expression.size(); i++) {
						den.add(expression.get(i));
					}
				}
			}
		}

		return null;
	}

    public static ArrayList<Token> simplifyVector (ArrayList<Token> expression) {
        VRuleSet vRuleSet = new VRuleSet();
        return vRuleSet.reduce(expression);
    }

    public static ArrayList<Token> convertDoublesToVector (double[] vector) {
        ArrayList<Token> newVector = new ArrayList<Token>();
        newVector.add(BracketFactory.createOpenSquareBracket());
        newVector.add(new Number (vector[0]));
        newVector.add(new Token(","){});
        newVector.add(new Number (vector[1]));
        newVector.add(new Token(","){});
        if (vector.length > 2) {
            newVector.add(new Number (vector[0]));
            newVector.add(new Token(","){});
        }
        newVector.add(BracketFactory.createCloseSquareBracket());
        return newVector;
    }


	/**
	 * Finds the roots of any given function, if any
	 * 
	 * @param function The function to finds the roots of
	 * @return A list of roots for the function
	 */
	public static ArrayList<Double> findRoots (ArrayList<Token> function){
		return null;
	}

	/**
	 * Finds the derivative of a given function 
	 * 
	 * @param function The function that will be differentiated 
	 * @return The differentiated function 
	 */
	public ArrayList<Token> differentiate (ArrayList<Token> function) {
		for(int i=0;i<function.size();i++) {

		}
		return null;
	}

	/**
	 * Finds the integral of a given function 
	 * 
	 * @param function The function that will be integrated
	 * @return The integrated function 
	 */
	public ArrayList<Token> integrate (ArrayList<Token> function) {
		for(int i=0;i<function.size();i++) {

		}
		return null;
	}

	/**
	 * Finds the factorial of the given integer
	 * 
	 * @param n The base of the factorial
	 * @return The value of the factorial
	 */
	public static int factorial(int n) {
		if(n==1){
			return 1;
		} else{
			return n*factorial(n-1);
		}
	}



	public static ArrayList<Double> solveQuadratic(double a, double b, double c){
		ArrayList<Double> roots = new ArrayList<Double>();
		if((b*b - 4*a*c) < 0){
			roots = null;
		}else if((b*b - 4*a*c) == 0){
			roots.add( -b/(2*a));
		}else{
			roots.add((-b + Math.sqrt((b*b - 4*a*c)))/(2*a));
			roots.add((-b - Math.sqrt((b*b - 4*a*c)))/(2*a));
		}
		return roots;
	}

	public static ArrayList<Complex> solveQuadraticC(double a, double b, double c){
		ArrayList<Complex> roots = new ArrayList<Complex>();
		if((b*b - 4*a*c) < 0){
			roots.add((Complex.sqrt((b*b - 4*a*c))).add(-b).times(1/(2*a)));
			roots.add(new Complex(-b,0).subtract(Complex.sqrt((b*b - 4*a*c))).times(1/(2*a)));
		}else if((b*b - 4*a*c) == 0){
			roots.add(new Complex((-b/(2*a)),0));
		}else{
			roots.add(new Complex((-b + Math.sqrt((b*b - 4*a*c)))/(2*a),0));
			roots.add(new Complex((-b - Math.sqrt((b*b - 4*a*c)))/(2*a),0));
		}
		return roots;
	}

	/*
    public static ArrayList<Complex> solveCubic(double a, double b, double c, double d){
        ArrayList<Complex> roots = new ArrayList<Complex>();
        Complex expression = new Complex(2*b*b*b - 9*a*b*c + 27*a*a*d, 0);
        Complex exp2 = Complex.sqrt(Complex.pow(expression,2) - 4*(Math.pow((b*b - 3*a*c),3)));
        Complex rad1 = Complex.cbrt((expression.add(exp2)).times(0.5));
        Complex rad2 = Complex.cbrt((expression.subtract(exp2)).times(0.5));
        roots.add(new Complex(
                (-b)/(3*a) - (rad1/(3*a))
        ))
    }*/



	/**
	 * Finds the slope of a function at the given point using the first principle method
	 * @param function the function whose slope needs to be found
	 * @param a the x value of the point at which the slope needs to be found
	 * @return the slope of the function at a aka f'(a)

    public static double slopeAtPnt(ArrayList<Token> function, double a){
    	final double FofA = evaluateFunc(function, a);
    	double slope=0;
    	double fOfB, fOfB2;
    	double slopeR=0,slopeL=0;
    	for(double inc = 1;inc>0;inc /= 10){
    		for(double i = a+10*inc, j = a-10*inc; i>a && j<a; i -= inc, j+=inc){
    			if(evaluateFunc(function,i) == evaluateFunc(function,j)){

    			}
    			}
    		}
    	}
		return slope;

    }

    private static double evaluateFunc(ArrayList<Token> function, double a){
    	//TODO: return f(a)
    	return 0;
    }
	 */

}
