package com.trutech.calculall;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Ability to define functions and perform various actions with them, such as finding
 * roots, integration, differentiation, and graphing.
 *
 * @version 0.4.0
 */
@SuppressWarnings("ResourceType")
public class FunctionMode extends Advanced {
    public static final int ROUND_TO = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        //Programmatically sets the texts that can't be defined with XML
        Button powButton = (Button) findViewById(R.id.powButton);
        Button expButton = (Button) findViewById(R.id.powerButton);
        powButton.setText(Html.fromHtml(getString(R.string.powOfTen)));
        expButton.setText(Html.fromHtml(getString(R.string.exponent)));
        updateInput();
        output = (OutputView) findViewById(R.id.output);
        display = (DisplayView) findViewById(R.id.display);
        display.setOutput(output);
    }

    public void clickRoots(View view) {
        ArrayList<Token> tokens = Utility.condenseDigits(this.tokens);
        tokens = Utility.setupExpression(tokens);
        double[] roots = ApacheUtility.findRoots(tokens);
        String toOutput = "";
            //Outputs the result
            int counter = 0;
        while (counter < roots.length) {
            double root = roots[counter];
                if (counter != 0) {
                    toOutput += " OR ";
                }
            toOutput += "X = " + Utility.round(root, ROUND_TO);
                counter++;
            }
            if (counter == 0) { //No roots
                toOutput += "No real roots";
            }
        display.displayOutput(toOutput);
        scrollDown();
    }

    public void clickDerivative(View view) {
        ArrayList<Token> derivative = CalculusUtilities.differentiate(tokens);
        if (derivative == null) {
            Toast.makeText(this, "Invalid Expression", Toast.LENGTH_SHORT).show();
        } else {
            display.displayOutput(derivative);
        }
    }

    public void clickIntegrate(View view) {
        try {
            ArrayList<Token> integral = CalculusUtilities.integrate(tokens);
            if (integral == null) {
                Toast.makeText(this, "Invalid Expression", Toast.LENGTH_SHORT).show();
            } else {
                display.displayOutput(integral);
            }
        } catch (UnsupportedOperationException e) {
            Toast.makeText(this, "The integral cannot be expressed as an elementary function", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * When the user presses the simplify button
     *
     * @param v Not Used
     */
    public void clickSimplify(View v) {
        ArrayList<Token> outputTokens = JFok.convertToStandardForm(tokens);
        String toOutput = "";
        for (Token t : outputTokens) {
            if (t instanceof Number) {
                toOutput += ((Number) t).getValue();
            } else {
                toOutput += t.getSymbol();
            }
        }
        display.displayOutput(toOutput);
        updateInput();
    }

    /**
     * Graphs the inputted function.
     *
     * @param view
     */
    public void clickGraph(View view) {
        setContentView(new GraphView(this, this, tokens));
    }

    /**
     * Maps the coefficients to the degree of the x value they are from.
     *
     * @param tokens The expression to map
     * @return A map that uses the degree of the x as the key and the coefficient of that as the value. It will also
     * map the highest degree found as Double.POSITIVE_INFINITY for the key and whether or not all the degrees are integers
     * for the Double.NEGATIVE_INFINITY key (1 = true, 0 = false)
     * @throws java.lang.IllegalArgumentException The user has inputted an invalid expression
     */
    private HashMap<Double, Double> mapCoefficients(ArrayList<Token> tokens) {
        HashMap<Double, Double> map = new HashMap<Double, Double>();
        double highestDegree = 0;
        int allIntegers = 1; //1 = All degrees are integers, 0 = not
        boolean negative = false;
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            Token previous = i - 1 >= 0 ? tokens.get(i - 1) : null;
            Token beforePrevious = i - 2 >= 0 ? tokens.get(i - 2) : null;
            Token next = i + 1 < tokens.size() ? tokens.get(i + 1) : null;
            Token afterNext = i + 2 < tokens.size() ? tokens.get(i + 2) : null;
            if (token instanceof Variable) { //Has to be an X; there's no other variables
                //Assumed a value of 1 until defined (ex. 5X has a degree of 1)
                double coefficient = 1;
                double degree = 1;
                //Looks for the coefficient first if any
                if (beforePrevious != null && previous instanceof Operator && ((Operator) previous).getType() == Operator.MULTIPLY) { //It has a coefficient as the previous number
                    if (beforePrevious instanceof Number) {
                        coefficient = ((Number) beforePrevious).getValue();
                    } else {
                        throw new IllegalArgumentException(); //The user inputted a invalid function
                    }
                }
                if (negative) {
                    coefficient *= -1;
                } else {
                    coefficient *= 1;
                }
                //Now it looks for the term if any
                if (afterNext != null && next instanceof Operator && ((Operator) next).getType() == Operator.EXPONENT) { //It has a coefficient as the previous number
                    if (afterNext instanceof Number) {
                        degree = (int) ((Number) afterNext).getValue();
                        //Checks if its the highest degree
                        if (degree > highestDegree) {
                            highestDegree = degree;
                        }
                        //Checks if the degree is an integer
                        if (degree % 1 != 0) { //Not an integer
                            allIntegers = 0;
                        }
                    } else {
                        throw new IllegalArgumentException(); //The user inputted a invalid function
                    }
                }
                //Now maps the coefficient to the term
                map.put(degree, coefficient);
            } else if (token instanceof Operator) {
                if (((Operator) token).getType() == Operator.ADD) {
                    negative = false;
                } else if (((Operator) token).getType() == Operator.SUBTRACT) {
                    negative = true;
                }

            } else if (token instanceof Number && previous instanceof Operator) {
                Operator o = (Operator) previous;
                if ((o.getType() == Operator.ADD || o.getType() == Operator.SUBTRACT) && i == tokens.size() - 1) { //Constant
                    double value = ((Number) token).getValue();
                    value *= o.getType() == Operator.SUBTRACT ? -1 : 1; //Accounts the Subtraction as a negative
                    map.put(0d, value);
                }
            }
        }

        Iterator<Double> keySetIterator = map.keySet().iterator();
        double hd = 0;
        double temp;
        while (keySetIterator.hasNext()) {
            temp = keySetIterator.next();
            if (temp > hd) {
                hd = temp;
            }
        }
        if (highestDegree < hd) {
            highestDegree = hd;
        }
        //Maps if the metadata
        map.put(Double.POSITIVE_INFINITY, highestDegree);
        map.put(Double.NEGATIVE_INFINITY, (double) allIntegers);
        if (!map.containsKey(0d)) {
            map.put(0d, 0d);
        }
        return map;
    }

}
