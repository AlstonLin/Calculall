package com.trutech.calculall;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;

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
    }

    public void clickRoots(View view) {
        ArrayList<Token> tokens = Utility.condenseDigits(this.tokens);
        tokens = Utility.setupExpression(tokens);
        //Utility.simplifyExpression()
        //At this point, it will be assumed everything is simplified and the function is in decending powers
        HashMap<Double, Double> coefficients = mapCoefficients(tokens); //Degree -> key, coefficient -> value
        double highestDegree = coefficients.get(Double.POSITIVE_INFINITY);
        boolean allIntegers = coefficients.get(Double.NEGATIVE_INFINITY) == 1;
        DisplayView display = (DisplayView)findViewById(R.id.display);
        String toOutput = "";
        if (allIntegers){ //Might be able to use the quadradic and cubic solvers
            ArrayList<Double> roots;
            if (highestDegree == 2){
                double a = coefficients.get(2d);
                double b = coefficients.containsKey(1d) ? coefficients.get(1d) : 0;
                double c = coefficients.get(0d);
                roots = Utility.solveQuadratic(a, b, c);
            }else if (highestDegree == 3){
                double a = coefficients.get(3d);
                double b = coefficients.containsKey(2d) ? coefficients.get(2d) : 0;
                double c = coefficients.containsKey(1d) ? coefficients.get(1d) : 0;
                double d = coefficients.get(0d);
                roots = Utility.solveCubic(a, b, c, d);
            }else if(highestDegree==1){
                double a = coefficients.get(1d);
                double b = coefficients.containsKey(0d) ? coefficients.get(0d) : 0;
                //0=ax+b
                //-b=ax
                //x=-b/a
                roots = new ArrayList<Double>();
                roots.add(-b/a);
            }else{
                roots = new ArrayList<Double>(); //Will only contain 1 root
                //TODO: Complete this using the general root finder
            }
            //Outputs the result
            int counter = 0;
            for (double root : roots){
                if (counter != 0){
                    toOutput += " OR ";
                }
                toOutput += "X = " + Utility.round(root, 6);
                counter++;
            }
            if (counter == 0){ //No roots
                toOutput += "No real roots";
            }
        }else {
            //TODO: Complete this using the general root finder
        }
        display.displayOutput(toOutput);
        scrollDown();
    }

    public void clickDerivative(View view) {
    }

    public void clickIntegrate(View view) {
    }

    /**
     * Graphs the inputted function.
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
                if(negative) {
                    coefficient *= -1;
                }else{
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
            }else if(token instanceof Operator){
                if( ((Operator) token).getType() == Operator.ADD){
                    negative = false;
                }else if( ((Operator) token).getType() == Operator.SUBTRACT){
                    negative = true;
                }

            }else if (token instanceof Number && previous instanceof Operator){
                Operator o = (Operator) previous;
                if ((o.getType() == Operator.ADD || o.getType() == Operator.SUBTRACT) && i == tokens.size() - 1){ //Constant
                    double value = ((Number)token).getValue();
                    value *= o.getType() == Operator.SUBTRACT ? -1 : 1; //Accounts the Subtraction as a negative
                    map.put(0d, value);
                }
            }
        }

        Iterator<Double> keySetIterator = map.keySet().iterator();
        double hd = 0;
        double temp;
        while(keySetIterator.hasNext()){
            temp = keySetIterator.next();
            if(temp > hd){
                hd = temp;
            }
        }
        if(highestDegree < hd){
            highestDegree = hd;
        }
        //Maps if the metadata
        map.put(Double.POSITIVE_INFINITY, highestDegree);
        map.put(Double.NEGATIVE_INFINITY, (double)allIntegers);
        if (!map.containsKey(0d)){
            map.put(0d, 0d);
        }
        return map;
    }

    /**
     * When the user presses the x^2 Button.
     *
     * @param v Not Used
     */
    @Override
    public void clickSquare(View v) {
        tokens.add(OperatorFactory.makeExponent());
        tokens.add(DigitFactory.makeTwo());
        updateInput();
    }

    /**
     * When the user presses the x^3 Button.
     *
     * @param v Not Used
     */
    @Override
    public void clickCube(View v) {
        tokens.add(OperatorFactory.makeExponent());
        tokens.add(DigitFactory.makeThree());
        updateInput();
    }

    /**
     * When the user presses the simplify button
     *
     * @param v Not Used
     */
    public void clickSimplify(View v){
        tokens = JFok.simplifyExpression(tokens);
        updateInput();
    }

    /**
     * Updates the text on the input screen.
     */
    @Override
    protected void updateInput(){
        String inputText = "f(x)=";
        DisplayView display = (DisplayView)findViewById(R.id.display);
        display.displayInputFunction(tokens);
        //Shows bottom
        if (this instanceof Advanced) {
            scrollDown();
        }
    }
}
