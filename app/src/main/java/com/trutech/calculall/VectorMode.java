package com.trutech.calculall;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class VectorMode extends Advanced {
    public static final int ARGUMENT = 1, TRUEBEARING = 2, BEARING = 3; //directionmode options
    public static final int DEGREE = 1, RADIAN = 2, GRADIAN = 3; //angleMode options
    private static final String FILENAME = "history_vector";
    public boolean switchedDirectionMode = false;
    public boolean switchedAngleMode = false;
    private int directionMode = 1;
    private int tokenSize = -1;
    private boolean mem = false;
    private int angleMode = 1;
    private boolean shift = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vector);
        output = (OutputView) findViewById(R.id.output);
        display = (DisplayView) findViewById(R.id.display);
        display.setOutput(output);
    }

    public ArrayList<Token> processVectors() {
        return Utility.simplifyVector(Utility.convertVariablesToTokens(Utility.setupExpression(Utility.addMissingBrackets(Utility.condenseDigits(tokens)))));
    }
    /**
     * Uses the shunting yard algorithm to change the expression from infix to reverse polish.
     *
     * @param infix The infix expression
     * @return The expression in reverse polish
     * @throws java.lang.IllegalArgumentException The infix notation is invalid
     */
    public static ArrayList<Token> convertToReversePolish(ArrayList<Token> infix) {
        ArrayList<Token> reversePolish = new ArrayList<Token>();
        Stack<Token> stack = new Stack<Token>();
        for (Token token : infix) {
            if (token instanceof Number || token instanceof Variable) { //Adds directly to the queue if it's a token
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
     * Evaluates a given expression in reverse polish notation and returns the resulting value.
     *
     * @param tokens The expression in reverse polish
     * @return The value of the expression
     * @throws java.lang.IllegalArgumentException The user has inputted an invalid expression
     */
    public static double evaluateExpression(ArrayList<Token> tokens) {
        Stack<Number> stack = new Stack<Number>();
        for (Token token : tokens) {
            if (token instanceof Number) { //Adds all numbers directly to the stack
                stack.push((Number) token);
            } else if (token instanceof Operator) {
                //Operates the first and second top operators
                Number right = stack.pop();
                Number left = stack.pop();
                stack.push(new Number(((Operator) token).operate(left.getValue(), right.getValue()))); //Adds the result back to the stack
            } else if (token instanceof Function) { //Function uses the top number on the stack
                Number top = stack.pop(); //Function performs on the first number
                stack.push(new Number(((Function) token).perform(top.getValue()))); //Adds the result back to the stack
            } else { //This should never be reached
                throw new IllegalArgumentException();
            }
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Stack size is empty"); //There should only be 1 token left on the stack
        } else {
            return stack.pop().getValue();
        }
    }

    /**
     * When the user presses the equals Button.
     *
     * @param v Not Used
     */
    public void clickVectorEquals(View v) throws IOException, ClassNotFoundException {
        DisplayView display = (DisplayView) findViewById(R.id.display);

        //Used to check if the user added extra tokens after clicking the direction mode button at least once
        //and change switchedDirectionMode and changedTokens accordingly
        if (tokenSize != tokens.size() || changedTokens) {
            switchedDirectionMode = false;
            changedTokens = false;
        }
        tokenSize = tokens.size();

        //If the pressed the = button and wants to find the direction of a angle, it'll be the set to what
        //directionMode is
        if (!switchedDirectionMode) {
            if (directionMode == ARGUMENT) {
                VRuleSet.setPressedArgumentButton(true);
            } else if (directionMode == TRUEBEARING) {
                VRuleSet.setPressedTrueBButton(true);
            } else if (directionMode == BEARING) {
                VRuleSet.setPressedBearButton(true);
            }
        }
        try {
            ArrayList<Token> output = processVectors();
            display.displayOutput(output);
            saveEquation(tokens, output, FILENAME);
        } catch (Exception e) { //User did a mistake
            handleExceptions(e);
        }
        scrollDown();
    }

    /**
     * When the user presses the MEM button; toggles memory storage
     *
     * @param v Not Used
     */
    public void clickMem(View v) {
        ToggleButton vMemButton = (ToggleButton) findViewById(R.id.memButton);
        mem = !mem;
        vMemButton.setChecked(mem);
    }

    /**
     * @return whether or not shift is active
     */
    public boolean getShift() {
        return shift;
    }

    /**
     * When the user presses the shift button. Switches the state of the boolean variable shift
     *
     * @param v Not Used
     */
    public void clickShift(View v) {
        Button sinButton = (Button) findViewById(R.id.sinButton);
        Button cosButton = (Button) findViewById(R.id.cosButton);
        Button tanButton = (Button) findViewById(R.id.tanButton);
        Button powButton = (Button) findViewById(R.id.powButton);
        Button expButton = (Button) findViewById(R.id.powerButton);
        Button logButton = (Button) findViewById(R.id.logButton);
        Button rootButton = (Button) findViewById(R.id.rootButton);
        Button squareButton = (Button) findViewById(R.id.squareButton);
        ToggleButton shiftButton = (ToggleButton) findViewById(R.id.shiftButton);

        if (shift) {
            shift = false;
            powButton.setText(Html.fromHtml(getString(R.string.powOfTen)));
            logButton.setText(getString(R.string.log10));
            rootButton.setText(getString(R.string.sqrt));
            squareButton.setText(getString(R.string.square));
            expButton.setText(Html.fromHtml(getString(R.string.exponent)));

            sinButton.setText("sin");
            sinButton.setTextSize(16);
            cosButton.setText("cos");
            cosButton.setTextSize(16);
            tanButton.setText("tan");
            tanButton.setTextSize(16);
        } else {
            shift = true;
            powButton.setText(Html.fromHtml(getString(R.string.powOfE)));
            logButton.setText(getString(R.string.ln));
            rootButton.setText(getString(R.string.cbrt));
            squareButton.setText(getString(R.string.cube));
            expButton.setText(Html.fromHtml(getString(R.string.varRoot)));

            sinButton.setText("arcsin");
            sinButton.setTextSize(14);
            cosButton.setText("arccos");
            cosButton.setTextSize(14);
            tanButton.setText("arctan");
            tanButton.setTextSize(14);

        }
        shiftButton.setChecked(shift);
        updateInput();
    }

    /**
     * @return the angleMode
     */
    public int getAngleMode() {
        return angleMode;
    }

    public void clickAngleMode(View v) {
        Button angleModeButton = (Button) findViewById(R.id.angleMode);
        if (angleMode == GRADIAN) {
            convGtoD();
            angleMode = DEGREE;
            angleModeButton.setText(getString(R.string.deg));
        } else if (angleMode == RADIAN) {
            convRtoG();
            angleMode = GRADIAN;
            angleModeButton.setText(getString(R.string.grad));
        } else if (angleMode == DEGREE) {
            convDtoR();
            angleMode = RADIAN;
            angleModeButton.setText(getString(R.string.rad));
        }
        updateInput();
    }

    /**
     * Stores the a variable into the memory; the assignment itself will occur in the given Command.
     *
     * @param addToOutput The String that will be shown in the output along with the value
     * @param assignment  The assignment command that would be executed
     */
    protected void storeVector(String addToOutput, Command<Void, ArrayList<Token>> assignment) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            ArrayList<Token> output = processVectors();
            assignment.execute(output);

            output.add(new StringToken(addToOutput));
            display.displayOutput(output);
            mem = false;
            memButton.setChecked(false);
        } catch (Exception e) { //User did a mistake
            handleExceptions(e);
        }
    }


    /**
     * When the user presses the T button
     *
     * @param v Not Used
     */
    public void clickVT(View v) {
        tokens.add(display.getRealCursorIndex(), new StringToken("t"));
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }


    /**
     * When the user presses the [ Button.
     *
     * @param v Not Used
     */
    public void clickOpenSquareBracket(View v) {
        tokens.add(display.getRealCursorIndex(), BracketFactory.makeOpenSquareBracket());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the ] Button.
     *
     * @param v Not Used
     */
    public void clickCloseSquareBracket(View v) {
        tokens.add(display.getRealCursorIndex(), BracketFactory.makeCloseSquareBracket());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the | Button.
     *
     * @param v Not Used
     */
    public void clickMagnitude(View v) {
        tokens.add(display.getRealCursorIndex(), BracketFactory.makeMagnitudeBar());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the â€¢ Button.
     *
     * @param v Not Used
     */
    public void clickDot(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeDot());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the Ã— Button.
     *
     * @param v Not Used
     */
    public void clickCross(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeCross());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the , Button.
     *
     * @param v Not Used
     */
    public void clickComma(View v) {
        tokens.add(display.getRealCursorIndex(), new Token(",") {
        });
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the ∠ Button.
     *
     * @param v Not Used
     */
    public void clickAngle(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeAngle());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the proj Button.
     *
     * @param v Not Used
     */
    public void clickProjection(View v) {
        tokens.add(display.getRealCursorIndex(), new Token("proj") {
        });
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    public void clickDirectionMode(View v) {
        Button directionModeButton = (Button) findViewById(R.id.argumentButton);

        //Used to check if the user added extra tokens after clicking the direction mode button at least once
        //and change switchedDirectionMode and changedTokens accordingly
        if (tokenSize != tokens.size() || changedTokens) {
            switchedDirectionMode = false;
            changedTokens = false;
        }
        tokenSize = tokens.size();

        if (directionMode == BEARING) {
            convBtoA();
            directionMode = ARGUMENT;
            directionModeButton.setText(getString(R.string.argument));
        } else if (directionMode == TRUEBEARING) {
            convTtoB();
            directionMode = BEARING;
            directionModeButton.setText(getString(R.string.bear));
        } else if (directionMode == ARGUMENT) {
            convAtoT();
            directionMode = TRUEBEARING;
            directionModeButton.setText(getString(R.string.trueB));
        }
        updateInput();
    }

    public void convBtoA() {
        //Converts the number displayed from bearing into argument
        DisplayView display = (DisplayView) findViewById(R.id.display);
        try {
            VRuleSet.setPressedArgumentButton(true);
            updateInput();
            display.displayOutput(processVectors());
            scrollDown();
            switchedDirectionMode = true;
        } catch (Exception e) { //User made a mistake
            VRuleSet.setPressedArgumentButton(false);
            handleExceptions(e);
        }
    }

    public void convTtoB() {
        //Converts the number displayed from bearing into argument ie multiplies the number by 9/10
        DisplayView display = (DisplayView) findViewById(R.id.display);
        try {
            VRuleSet.setPressedBearButton(true);
            updateInput();
            display.displayOutput(processVectors());
            scrollDown();
            switchedDirectionMode = true;
        } catch (Exception e) { //User made a mistake
            VRuleSet.setPressedBearButton(false);
            handleExceptions(e);
        }
    }

    public void convAtoT() {
        //Converts the number displayed from bearing into argument ie multiplies the number by 9/10
        DisplayView display = (DisplayView) findViewById(R.id.display);
        try {
            VRuleSet.setPressedTrueBButton(true);
            updateInput();
            display.displayOutput(processVectors());
            scrollDown();
            switchedDirectionMode = true;
        } catch (Exception e) { //User made a mistake
            VRuleSet.setPressedTrueBButton(false);
            handleExceptions(e);
        }
    }

    /**
     * When the user presses the , Button.
     *
     * @param v Not Used
     */
    public void clickUnitVector(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        VRuleSet.setPressedUnitVButton(true);
        try {
            display.displayOutput(processVectors());
        } catch (Exception e) { //User did a mistake
            handleExceptions(e);
        }
        scrollDown();
    }

    /**
     * When the user presses the Scalar Button.
     *
     * @param v Not Used
     */
    public void clickScalar(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        VRuleSet.setPressedScalarEqnButton(true);
        try {
            display.displayOutput(processVectors());
        } catch (Exception e) { //User did a mistake
            handleExceptions(e);
        }
        scrollDown();
    }

    public ArrayList<Token> parseVectors(ArrayList<Token> input){
        input = Utility.condenseDigits(input);
        ArrayList<Token> output = new ArrayList<>();
        ArrayList<Number> listOfNumbers = new ArrayList<>();
        boolean newVector = false;
        for (Token t : input) {
            if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.SQUAREOPEN) {
                newVector = true;
            }
            else if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.SQUARECLOSED){
                newVector = false;
                double[] nums = new double[listOfNumbers.size()];
                int i = 0;
                for (Number num : listOfNumbers){
                    nums[i] = num.getValue();
                    i++;
                }
                output.add(new Vector(nums));
            }
            else if (newVector == true && t instanceof Number){
                listOfNumbers.add((Number) t);
            }
            else
            {
                output.add(t);
            }
        }
        return output;
    }

}
