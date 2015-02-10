package com.trutech.calculall;

import android.view.View;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Contains the back-end for Vector Mode. The mode will be able to compute most vector
 * problems in a grade twelve level.
 *
 * @author Alston Lin, Keith Wong, Jason Fok
 * @version Alpha 2.0
 */

public class VectorMode extends Advanced {
    public static final int DEGREE = 1, RADIAN = 2, GRADIAN = 3; //angleMode options
    private static final String FILENAME = "history_vector";
    private boolean mem = false;
    private int angleMode = 1;
    private static final Basic INSTANCE = new VectorMode();

    { //pseudo-constructor
        filename = "history_vector";
    }

    /**
     * Allows for the Singleton pattern so there would be only one instance.
     *
     * @return The singleton instance
     */
    public static Basic getInstance() {
        return INSTANCE;
    }

    /**
     * When a Button has been clicked, calls the appropriate method.
     *
     * @param v The Button that has been clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.var_u:
                clickU();
                break;
            case R.id.var_v:
                clickV();
                break;
            case R.id.var_t:
                clickVT();
                break;
            case R.id.add_button:
                clickAdd();
                break;
            case R.id.subtract_button:
                clickSubtract();
                break;
            case R.id.angle_between_vectors:
                clickAngle();
                break;
            case R.id.magnitude_open_button:
                clickMagnitudeOpen();
                break;
            case R.id.magnitude_close_button:
                clickMagnitudeClose();
                break;
            case R.id.unit_vector:
                clickUnitVector();
                break;
            case R.id.projection_button:
                clickProjection();
                break;
            case R.id.scalar_equation:
                clickScalar();
                break;
            case R.id.open_square_bracket:
                clickOpenSquareBracket();
                break;
            case R.id.closed_square_button:
                clickCloseSquareBracket();
                break;
            case R.id.dot_button:
                clickDot();
                break;
            case R.id.cross_button:
                clickCross();
                break;
            case R.id.equals_button:
                clickVectorEquals();
                break;
            case R.id.comma_button:
                clickComma();
                break;
            default:
                super.onClick(v);
        }
    }


    public void clickAdd() {
        tokens.add(display.getRealCursorIndex(), VectorOperatorFactory.makeAdd());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    public void clickSubtract() {
        tokens.add(display.getRealCursorIndex(), VectorOperatorFactory.makeSubtract());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    private void clickCross() {
        tokens.add(display.getRealCursorIndex(), VectorOperatorFactory.makeCross());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    private void clickDot() {
        tokens.add(display.getRealCursorIndex(), VectorOperatorFactory.makeDot());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    private void clickAngle() {
        tokens.add(display.getRealCursorIndex(), VectorOperatorFactory.makeAngle());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    public void clickU() {
        if (mem) {
            storeVector("→ U", new Command<Void, Token>() {
                @Override
                public Void execute(Token val) {
                    VectorVariable.uValue = val;
                    return null;
                }
            });
        } else {
            tokens.add(display.getRealCursorIndex(), VectorVariableFactory.makeU());
            display.setCursorIndex(display.getCursorIndex() + 1);
            updateInput();
        }
    }

    public void clickV() {
        if (mem) {
            storeVector("→ V", new Command<Void, Token>() {
                @Override
                public Void execute(Token val) {
                    VectorVariable.vValue = val;
                    return null;
                }
            });
        } else {
            tokens.add(display.getRealCursorIndex(), VectorVariableFactory.makeV());
            display.setCursorIndex(display.getCursorIndex() + 1);
            updateInput();
        }
    }

    /**
     * When the user presses the equals Button in Vector Mode.
     */
    public void clickVectorEquals() {
        try {
            ArrayList<Token> toOutput = processVectors(tokens);
            display.displayOutput(toOutput);
            activity.scrollDown();
            saveEquation(tokens, toOutput, FILENAME);
        } catch (Exception e) { //User did a mistake
            handleExceptions(e);
        }
    }

    /**
     * Calculates the Vector expression.
     *
     * @param tokens The result of the expression
     */
    public ArrayList<Token> processVectors(ArrayList<Token> tokens){
        ArrayList<Token> parsedTokens = VectorUtilities.parseVectors(tokens);
        Token result = VectorUtilities.evaluateExpression(VectorUtilities.convertToReversePolish(parsedTokens));
        ArrayList<Token> toOutput = new ArrayList<>();
        toOutput.add(result);
        return toOutput;
    }

    /**
     * When the user presses the MEM button; toggles memory storage
     */
    public void clickMem() {
        ToggleButton vMemButton = (ToggleButton) activity.findViewById(R.id.mem_button);
        mem = !mem;
        vMemButton.setChecked(mem);
    }


    /**
     * Stores the a variable into the memory; the assignment itself will occur in the given Command.
     *
     * @param addToOutput The String that will be shown in the output along with the value
     * @param assignment  The assignment command that would be executed
     */
    protected void storeVector(String addToOutput, Command<Void, Token> assignment) {
        DisplayView display = (DisplayView) activity.findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) activity.findViewById(R.id.mem_button);
        try {
            ArrayList<Token> output = processVectors(tokens); //Should only have 1 Vector result
            assignment.execute(output.get(0));

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
     */
    public void clickVT() {
        tokens.add(display.getRealCursorIndex(), VectorVariableFactory.makeT());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }


    /**
     * When the user presses the [ Button.
     */
    public void clickOpenSquareBracket() {
        tokens.add(display.getRealCursorIndex(), BracketFactory.makeOpenSquareBracket());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the ] Button.
     */
    public void clickCloseSquareBracket() {
        tokens.add(display.getRealCursorIndex(), BracketFactory.makeCloseSquareBracket());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the ||( Button.
     */
    public void clickMagnitudeOpen() {
        tokens.add(display.getRealCursorIndex(), BracketFactory.makeMagnitudeOpen());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the )|| Button.
     */
    public void clickMagnitudeClose() {
        tokens.add(display.getRealCursorIndex(), BracketFactory.makeMagnitudeClose());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the , Button.
     */
    public void clickComma() {
        tokens.add(display.getRealCursorIndex(), PlaceholderFactory.makeComma());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the proj Button.
     */
    public void clickProjection() {
        //tokens.add(display.getRealCursorIndex(), VectorFunctionFactory.make); //TODO: MAKE THIS WORK
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the , Button.
     */
    public void clickUnitVector() {
        tokens.add(display.getRealCursorIndex(), VectorFunctionFactory.makeUnit());
        tokens.add(display.getRealCursorIndex() + 1, BracketFactory.makeOpenBracket());
        display.setCursorIndex(display.getCursorIndex() + 2);
        updateInput();
    }

    /**
     * When the user presses the Scalar Button.
     */
    public void clickScalar() {
        //TODO: FINISH THIS
    }

}
