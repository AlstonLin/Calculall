/*
 * Copyright (c) 2016 TruTech Innovations Inc - All Rights Reserved
 */

package com.trutechinnovations.calculall;

import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Contains the back-end for Vector Mode. The mode will be able to compute most vector
 * problems in a grade twelve level.
 *
 * @author Alston Lin, Keith Wong, Jason Fok
 * @version 3.0
 */

public class VectorMode extends Advanced {
    private static final String FILENAME = "history_vector";
    private static final VectorMode INSTANCE = new VectorMode();

    { //pseudo-constructor
        filename = "history_vector";
    }

    private boolean mem = false;

    /**
     * Allows for the Singleton pattern so there would be only one instance.
     *
     * @return The singleton instance
     */
    public static VectorMode getInstance() {
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
            case R.id.mem_button_v:
                clickMem();
                break;
            case R.id.var_u:
                clickU();
                break;
            case R.id.var_v:
                clickV();
                break;
            case R.id.var_t:
                clickVT();
                break;
            case R.id.var_s:
                clickVS();
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
            case R.id.angle_mode_vector:
                clickAngleMode();
                break;
            case R.id.comma_button:
                clickComma();
                break;
            default:
                super.onClick(v);
        }
    }

    public void clickAngleMode() {
        Button angleModeButton = (Button) activity.findViewById(R.id.angle_mode_vector);
        if (Function.angleMode == Function.GRADIAN) {
            Function.angleMode = Function.DEGREE;
            angleModeButton.setText("DEG");
        } else if (Function.angleMode == Function.RADIAN) {
            Function.angleMode = Function.GRADIAN;
            angleModeButton.setText("GRAD");
        } else if (Function.angleMode == Function.DEGREE) {
            Function.angleMode = Function.RADIAN;
            angleModeButton.setText("RAD");
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

    /**
     * When the user presses the ANS button
     */
    public void clickAns() {
        tokens.add(display.getRealCursorIndex(), VariableFactory.makeAnsVec());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * Stores the a variable into the memory; the assignment itself will occur in the given Command.
     *
     * @param addToOutput The String that will be shown in the output along with the value
     * @param assignment  The assignment command that would be executed
     */
    protected void storeVariable(String addToOutput, Command<Void, ArrayList<Token>> assignment) {
        ToggleButton memButton = (ToggleButton) activity.findViewById(R.id.mem_button_v);
        try {
            ArrayList<Token> outputList = new ArrayList<>();
            outputList.addAll(tokens);
            outputList.add(new StringToken(addToOutput));
            display.displayOutput(outputList);

            ArrayList<Token> val = new ArrayList<>();
            val.addAll(tokens);
            assignment.execute(val);
            mem = false;
            memButton.setChecked(false);
        } catch (Exception e) { //User did a mistake
            handleExceptions(e);
        }
    }

    /**
     * When the user presses the A button
     */
    public void clickA() {
        if (mem) {
            storeVariable("→ A", new Command<Void, ArrayList<Token>>() {
                @Override
                public Void execute(ArrayList<Token> val) {
                    VariableFactory.aValue = val;
                    return null;
                }
            });
        } else {
            tokens.add(display.getRealCursorIndex(), VariableFactory.makeA());
            display.setCursorIndex(display.getCursorIndex() + 1);
            updateInput();
        }
    }

    /**
     * When the user presses the U button
     */
    public void clickU() {
        if (mem) {
            storeVariable("→ U", new Command<Void, ArrayList<Token>>() {
                @Override
                public Void execute(ArrayList<Token> val) {
                    VariableFactory.uValue = val;
                    return null;
                }
            });
        } else {
            tokens.add(display.getRealCursorIndex(), VariableFactory.makeU());
            display.setCursorIndex(display.getCursorIndex() + 1);
            updateInput();
        }
    }

    /**
     * When the user presses the V button
     */
    public void clickV() {
        if (mem) {
            storeVariable("→ V", new Command<Void, ArrayList<Token>>() {
                @Override
                public Void execute(ArrayList<Token> val) {
                    VariableFactory.vValue = val;
                    return null;
                }
            });
        } else {
            tokens.add(display.getRealCursorIndex(), VariableFactory.makeV());
            display.setCursorIndex(display.getCursorIndex() + 1);
            updateInput();
        }
    }

    /**
     * When the user presses the equals Button in Vector Mode.
     */
    public void clickVectorEquals() {
        try {
            ArrayList<Token> toOutput = VectorUtilities.processVectors(Utility.multiplyConstants(tokens));
            display.displayOutput(toOutput);
            activity.scrollDown();
            saveEquation(tokens, toOutput, FILENAME);
            saveAns(toOutput);
        } catch (Exception e) { //User did a mistake
            handleExceptions(e);
        }
    }

    /**
     * Saves the answer into the VariableFactory class.
     *
     * @param ans The expression to save
     */
    public void saveAns(ArrayList<Token> ans) {
        VariableFactory.ansValueVec = ans;
    }

    /**
     * When the user presses the MEM button; toggles memory storage
     */
    public void clickMem() {
        ToggleButton vMemButton = (ToggleButton) activity.findViewById(R.id.mem_button_v);
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
        ToggleButton memButton = (ToggleButton) activity.findViewById(R.id.mem_button_v);
        try {
            ArrayList<Token> output = VectorUtilities.processVectors(tokens); //Should only have 1 Vector result
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
     * When the user presses the T button.
     */
    public void clickVT() {
        tokens.add(display.getRealCursorIndex(), VariableFactory.makeT());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the S button.
     */
    public void clickVS() {
        tokens.add(display.getRealCursorIndex(), VariableFactory.makeS());
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
     * When the user presses the , Button.
     */
    public void clickUnitVector() {
        Token function = VectorFunctionFactory.makeUnit();
        Token bracket = BracketFactory.makeOpenBracket();
        function.addDependency(bracket);
        bracket.addDependency(function);
        tokens.add(display.getRealCursorIndex(), function);
        tokens.add(display.getRealCursorIndex() + 1, bracket);
        display.setCursorIndex(display.getCursorIndex() + 2);
        updateInput();
    }

    /**
     * When the user presses the Scalar Button.
     */
    public void clickScalar() {
        try {
            ArrayList<Token> tokens = VectorUtilities.parseVectors(VectorUtilities.setupVectorExpression(Utility.setupExpression(Utility.condenseDigits(Utility.subVariables(this.tokens)))));
            //Checks if it is 2D or 3D
            ArrayList<Token> constantTerms = new ArrayList<>();
            ArrayList<Token> directionVectorsS = new ArrayList<>();
            ArrayList<Token> directionVectorsT = new ArrayList<>();
            for (int i = 0; i < tokens.size(); i++) {
                Token t = tokens.get(i);
                if (t instanceof Variable && t.getType() == Variable.T) {
                    i++; //Goes to the next token
                    //If the Vector is right after
                    if (i < tokens.size() && tokens.get(i) instanceof Vector) {
                        directionVectorsT.add(tokens.get(i));
                    } else if (i < tokens.size() && tokens.get(i) instanceof Bracket && tokens.get(i).getType() == Bracket.OPEN) { //If the Vector is in a bracketed expression
                        int bracketCount = -1;
                        i++;
                        ArrayList<Token> inside = new ArrayList<>();
                        while (i < tokens.size() && bracketCount < 0) {
                            Token token = tokens.get(i);
                            if (token instanceof Bracket && token.getType() == Bracket.OPEN) {
                                bracketCount--;
                            } else if (token instanceof Bracket && token.getType() == Bracket.CLOSE) {
                                bracketCount++;
                            }
                            inside.add(token);
                            i++;
                        }
                        directionVectorsT.addAll(inside);
                    }
                } else if (t instanceof Variable && t.getType() == Variable.S) {
                    i++; //Goes to the next token
                    //If the Vector is right after
                    if (i < tokens.size() && tokens.get(i) instanceof Vector) {
                        directionVectorsS.add(tokens.get(i));
                    } else if (i < tokens.size() && tokens.get(i) instanceof Bracket && tokens.get(i).getType() == Bracket.OPEN) { //If the Vector is in a bracketed expression
                        int bracketCount = -1;
                        i++;
                        ArrayList<Token> inside = new ArrayList<>();
                        while (i < tokens.size() && bracketCount < 0) {
                            Token token = tokens.get(i);
                            if (token instanceof Bracket && token.getType() == Bracket.OPEN) {
                                bracketCount--;
                            } else if (token instanceof Bracket && token.getType() == Bracket.CLOSE) {
                                bracketCount++;
                            }
                            inside.add(token);
                            i++;
                        }
                        directionVectorsS.addAll(inside);
                    }
                } else if (t instanceof VectorOperator && t.getType() == VectorOperator.ADD && i + 1 < tokens.size() && tokens.get(i + 1) instanceof Variable) { //Ignores addition before s & t
                    //Do nothing
                } else {
                    constantTerms.add(t);
                }
            }
            //Now evaluates all the expressions
            ArrayList<Token> result1 = VectorUtilities.processVectors(directionVectorsT);
            ArrayList<Token> result2 = VectorUtilities.processVectors(directionVectorsS);
            Token t1 = result1.size() == 0 ? null : result1.get(0);
            Token t2 = result2.size() == 0 ? null : result2.get(0);

            if (constantTerms.size() > 0 && constantTerms.get(0) instanceof VectorOperator) { //Removes any front operators in the constant terms first
                VectorOperator o = (VectorOperator) constantTerms.remove(0);
                if (o.getType() == VectorOperator.ADD) {
                    //Deos nothing
                } else if (o.getType() == VectorOperator.SUBTRACT) {
                    constantTerms.add(0, VectorOperatorFactory.makeCross());
                    constantTerms.add(0, new Number(-1));
                } else { //Only ADD and SUBTRACT are valid
                    throw new IllegalArgumentException("Illegal Vector Equation of line / plane");
                }
            }
            ArrayList<Token> constantResults = VectorUtilities.processVectors(constantTerms);
            double[] zeroVector = {0, 0, 0};
            Token constant = constantResults.size() == 0 ? new Vector(zeroVector) : constantResults.get(0);
            if (!(constant instanceof Vector)) { //Constant must be a Vector
                throw new IllegalArgumentException("Illegal Vector Equation of line / plane");
            }

            if (t1 != null && t2 != null) { //3D
                if (t1 instanceof Vector && t2 instanceof Vector) {
                    //Makes sure theyre all 3D vectors
                    if (((Vector) t1).getDimensions() != 3 || ((Vector) t2).getDimensions() != 3 || ((Vector) constant).getDimensions() != 3) {
                        throw new IllegalArgumentException("The vectors in the scalar equation of a plane must be in 3D");
                    }
                    ArrayList<Token> scalar = VectorUtilities.calculateScalarEquationPlane3D((Vector) constant, (Vector) t1, (Vector) t2);
                    display.displayOutput(scalar);
                    activity.scrollDown();
                    //Saves the output
                    ArrayList<Token> toInput = new ArrayList<>();
                    toInput.add(new StringToken("Scalar of "));
                    toInput.addAll(tokens);
                    try {
                        saveEquation(toInput, scalar, FILENAME);
                    } catch (Exception e) { //User did a mistake
                        handleExceptions(e);
                    }
                } else {
                    throw new IllegalArgumentException("Illegal Vector Equation of plane");
                }
            } else if (t1 != null) {
                if (t1 instanceof Vector) {
                    if (((Vector) t1).getDimensions() != 2 || (((Vector) constant).getDimensions() != 2 && VectorUtilities.calculateMagnitude((Vector) constant) != 0)) {
                        throw new IllegalArgumentException("Can only have a scalar equation of a line in 2D");
                    }
                    ArrayList<Token> scalar = VectorUtilities.calculateScalarEquationLine((Vector) constant, (Vector) t1);
                    display.displayOutput(scalar);
                    activity.scrollDown();
                    //Saves the output
                    ArrayList<Token> toInput = new ArrayList<>();
                    toInput.add(new StringToken("Scalar of "));
                    toInput.addAll(tokens);
                    try {
                        saveEquation(toInput, scalar, FILENAME);
                    } catch (Exception e) { //User did a mistake
                        handleExceptions(e);
                    }
                } else {
                    throw new IllegalArgumentException("Illegal Vector Equation of line");
                }
            } else if (t2 != null) {
                if (t2 instanceof Vector) {
                    if (((Vector) t2).getDimensions() != 2 || (((Vector) constant).getDimensions() != 2 && VectorUtilities.calculateMagnitude((Vector) constant) != 0)) {
                        throw new IllegalArgumentException("Can only have a scalar equation of a line in 2D");
                    }
                    ArrayList<Token> scalar = VectorUtilities.calculateScalarEquationLine((Vector) constant, (Vector) t2);
                    display.displayOutput(scalar);
                    activity.scrollDown();
                    //Saves the output
                    ArrayList<Token> toInput = new ArrayList<>();
                    toInput.add(new StringToken("Scalar of "));
                    toInput.addAll(tokens);
                    try {
                        saveEquation(toInput, scalar, FILENAME);
                    } catch (Exception e) { //User did a mistake
                        handleExceptions(e);
                    }
                } else {
                    throw new IllegalArgumentException("Illegal Vector Equation of line");
                }
            } else {
                throw new IllegalArgumentException("Vector equations must include arbitrary variables (s/t)");
            }
        } catch (Exception e) {
            handleExceptions(e);
        }
    }

}