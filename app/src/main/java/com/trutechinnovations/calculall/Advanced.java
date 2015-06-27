package com.trutechinnovations.calculall;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Contains the back-end of the advanced calculator mode. The advanced mode will be able to
 * perform the most of the operations of a standard scientific calculator.
 *
 * @author Alston Lin, Ejaaz Merali
 * @version Alpha 2.0
 */
public class Advanced extends Basic {

    public static final int DEC = 1, FRAC = 2;
    public static final int CONSTANTS_SIZE = 25;
    public static final double CONSTANTS_IO_RATIO = 0.7; //The size of the output / input in the
    private static final String FILENAME = "history_advanced";
    private static final String FILENAMECONST = "const_advanced";
    private static final Basic INSTANCE = new Advanced();
    private static String filenameConst = "const_advanced";
    protected int fracMode = DEC;
    // constants list
    //Fields
    protected ArrayList<MultiButton> multiButtons;
    protected boolean hyperbolic = false, shift = false, mem = false;
    private Dialog graphDialog;
    private PopupWindow constWindow;
    private PopupWindow pw;
    private Dialog constantsDialog;

    { //pseudo-constructor
        filename = "history_advanced";
        filenameConst = "const_advanced";
    }

    /**
     * Allows for the Singleton pattern so there would be only one instance.
     *
     * @return The singleton instance
     */
    public static Basic getInstance() {
        return INSTANCE;
    }

    public void setMultiButtons(ArrayList<MultiButton> multiButtons) {
        this.multiButtons = multiButtons;
    }

    /**
     * When a Button has been clicked, calls the appropriate method.
     *
     * @param v The Button that has been clicked
     */
    @Override
    public void onClick(View v) {
        //First: If it is a MultiButton
        if (v instanceof MultiButton) {
            ((MultiButton) v).onClick();
            updateInput();
            return;
        }
        switch (v.getId()) {
            case R.id.equals_button:
                clickEquals();
                break;
            case R.id.shift_button:
                clickShift();
                break;
            case R.id.hyp_button:
                clickHyp();
                break;
            case R.id.mem_button:
                clickMem();
                break;
            case R.id.var_a:
                clickA();
                break;
            case R.id.var_b:
                clickB();
                break;
            case R.id.var_c:
                clickC();
                break;
            case R.id.pi_button:
                clickPi();
                break;
            case R.id.e_button:
                clickE();
                break;
            case R.id.permutation:
                clickPermutation();
                break;
            case R.id.combination:
                clickCombination();
                break;
            case R.id.angle_mode:
                clickAngleMode();
                break;
            case R.id.frac_button:
                clickFrac();
                break;
            case R.id.reciprocal:
                clickReciprocal();
                break;
            case R.id.frac_mode:
                clickFracMode();
                break;
            case R.id.open_bracket_button:
                clickOpenBracket();
                break;
            case R.id.closed_bracket_button:
                clickCloseBracket();
                break;
            case R.id.ans_button:
                clickAns();
                break;
            case R.id.const_button:
                clickConst();
                break;
            case R.id.exit_const_button:
                clickExitConst();
                break;
            default:
                super.onClick(v);
        }
    }

    /**
     * When the user presses the equals Button.
     */
    public void clickEquals() {
        try {
            if (fracMode == DEC) {
                super.clickEquals();
            } else if (fracMode == FRAC) {
                double value = Utility.process(tokens);
                if (Double.isInfinite(value)) {
                    throw new ArithmeticException("Math Error");
                }
                ArrayList<Token> output = Utility.subVariables(tokens, false);
                output = JFok.simplifyExpression(output);
                display.displayOutput(output);
                saveEquation(tokens, output, FILENAME);
                VariableFactory.ansValueAdv = output;
            }
        } catch (Exception e) { //User did a mistake
            handleExceptions(e);
        }
        activity.scrollDown();
    }

    public void clickAngleMode() {
        Button angleModeButton = (Button) activity.findViewById(R.id.angle_mode);
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

    public void clickFracMode() {
        if (fracMode == DEC) {
            fracMode = FRAC;
        } else if (fracMode == FRAC) {
            fracMode = DEC;
        }
        updateInput();
        clickEquals();
    }

    /**
     * When the user presses the hyp button. Switches the state of the boolean variable hyperbolic
     */
    public void clickHyp() {
        ToggleButton hypButton = (ToggleButton) activity.findViewById(R.id.hyp_button);
        hyperbolic = !hyperbolic;
        hypButton.setChecked(hyperbolic);
        updateMultiButtons();
        updateInput();
    }

    /**
     * When the user presses the shift button. Switches the state of the boolean variable shift.
     */
    public void clickShift() {
        shift = !shift;
        ToggleButton shiftButton = (ToggleButton) activity.findViewById(R.id.shift_button);
        shiftButton.setChecked(shift);
        //Changes the mode for all the Buttons
        updateMultiButtons();
        updateInput();
    }

    /**
     * Changes all the MultiButtons in the layout and updates them according to the current toggle button values.
     */
    public void updateMultiButtons() {
        for (MultiButton b : multiButtons) {
            b.changeMode(shift, hyperbolic);
        }
    }

    /**
     * When the user presses the MEM button; toggles memory storage
     */
    public void clickMem() {
        ToggleButton memButton = (ToggleButton) activity.findViewById(R.id.mem_button);
        mem = !mem;
        memButton.setChecked(mem);
    }

    /**
     * Stores the a variable into the memory; the assignment itself will occur in the given Command.
     *
     * @param addToOutput The String that will be shown in the output along with the value
     * @param assignment  The assignment command that would be executed
     */
    protected void storeVariable(String addToOutput, Command<Void, ArrayList<Token>> assignment) {
        ToggleButton memButton = (ToggleButton) activity.findViewById(R.id.mem_button);
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
     * When the user presses the ANS button
     */
    public void clickAns() {
        tokens.add(display.getRealCursorIndex(), VariableFactory.makeAnsAdv());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
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
     * When the user presses the B button
     */
    public void clickB() {
        if (mem) {
            storeVariable("→ B", new Command<Void, ArrayList<Token>>() {
                @Override
                public Void execute(ArrayList<Token> val) {
                    VariableFactory.bValue = val;
                    return null;
                }
            });
        } else {
            tokens.add(display.getRealCursorIndex(), VariableFactory.makeB());
            display.setCursorIndex(display.getCursorIndex() + 1);
            updateInput();
        }
    }

    /**
     * When the user presses the C button
     */
    public void clickC() {
        if (mem) {
            storeVariable("→ C", new Command<Void, ArrayList<Token>>() {
                @Override
                public Void execute(ArrayList<Token> val) {
                    VariableFactory.cValue = val;
                    return null;
                }
            });
        } else {
            tokens.add(display.getRealCursorIndex(), VariableFactory.makeC());
            display.setCursorIndex(display.getCursorIndex() + 1);
            updateInput();
        }
    }

    /**
     * When the user presses the ( Button.
     */
    public void clickOpenBracket() {
        tokens.add(display.getRealCursorIndex(), BracketFactory.makeOpenBracket());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the ) Button.
     */
    public void clickCloseBracket() {
        tokens.add(display.getRealCursorIndex(), BracketFactory.makeCloseBracket());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the 10^x Button.
     */
    public void clickPowerOfTen() {
        Token multiply = OperatorFactory.makeMultiply();
        Token one = DigitFactory.makeOne();
        Token zero = DigitFactory.makeZero();
        Token exp = OperatorFactory.makeExponent();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();

        tokens.add(display.getRealCursorIndex(), multiply);
        tokens.add(display.getRealCursorIndex() + 1, one);
        tokens.add(display.getRealCursorIndex() + 2, zero);
        tokens.add(display.getRealCursorIndex() + 3, exp);
        tokens.add(display.getRealCursorIndex() + 4, openBracket);
        tokens.add(display.getRealCursorIndex() + 5, PlaceholderFactory.makeSuperscriptBlock());
        tokens.add(display.getRealCursorIndex() + 6, closeBracket);

        exp.addDependency(openBracket);
        exp.addDependency(closeBracket);
        display.setCursorIndex(display.getCursorIndex() + 4);
        updateInput();
    }

    /**
     * When the user presses the e^x Button.
     */
    public void clickExp() {
        clickExponent();
        clickE();
        //display.setCursorIndex(display.getRealCursorIndex() + 1);
    }

    /**
     * When the user presses the ln(x) Button.
     */
    public void clickLn() {
        Token t = FunctionFactory.makeLn();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
        updateInput();
    }

    /**
     * When the user presses the log(x) or log_10(x)Button.
     */
    public void clickLog_10() {
        Token t = FunctionFactory.makeLog_10();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
        updateInput();
    }

    public void clickExponent() {
        ArrayList<Token> list = new ArrayList<>();
        list.add(PlaceholderFactory.makeSuperscriptBlock());
        addTokenInExponent(list);
    }

    public void clickVarRoot() {
        Token root = OperatorFactory.makeVariableRoot();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();
        Bracket b = BracketFactory.makeOpenBracket();

        root.addDependency(b);
        root.addDependency(openBracket);
        root.addDependency(closeBracket);

        if (display.getRealCursorIndex() != 0) {
            //Whats on the numerator depends on the token before
            Token tokenBefore = tokens.get(display.getRealCursorIndex() - 1);
            if (tokenBefore instanceof Digit) {
                LinkedList<Digit> digits = new LinkedList<Digit>();
                int i = display.getRealCursorIndex() - 1;
                while (i >= 0 && tokens.get(i) instanceof Digit) {
                    Token t = tokens.get(i);
                    digits.addFirst((Digit) t);
                    tokens.remove(t);
                    i--;
                }
                tokens.add(display.getRealCursorIndex() - digits.size(), openBracket);
                tokens.addAll(display.getRealCursorIndex() - digits.size() + 1, digits);
                tokens.add(display.getRealCursorIndex() + 1, closeBracket);
                tokens.add(display.getRealCursorIndex() + 2, root);
                tokens.add(display.getRealCursorIndex() + 3, b);

                display.setCursorIndex(display.getCursorIndex() + 3);
                return;
            } else if (tokenBefore instanceof Bracket && ((Bracket) tokenBefore).getType() == Bracket.CLOSE) {
                LinkedList<Token> expression = new LinkedList<Token>();
                int i = display.getRealCursorIndex() - 2;
                int bracketCount = 1;
                expression.add(tokens.remove(display.getRealCursorIndex() - 1));
                while (i >= 0 && bracketCount != 0) {
                    Token t = tokens.remove(i);
                    if (t instanceof Bracket) {
                        Bracket bracket = (Bracket) t;
                        if (bracket.getType() == Bracket.OPEN) {
                            bracketCount--;
                        } else if (bracket.getType() == Bracket.CLOSE) {
                            bracketCount++;
                        }
                    }
                    expression.addFirst(t);
                    i--;
                }
                tokens.add(i + 1, openBracket);
                tokens.addAll(i + 2, expression);

                tokens.add(display.getRealCursorIndex() + 1, closeBracket);
                tokens.add(display.getRealCursorIndex() + 2, root);
                tokens.add(display.getRealCursorIndex() + 3, b);
                display.setCursorIndex(display.getCursorIndex() + 3);
                return;
            }
        }

        tokens.add(display.getRealCursorIndex(), openBracket);
        tokens.add(display.getRealCursorIndex() + 1, PlaceholderFactory.makeSuperscriptBlock());
        tokens.add(display.getRealCursorIndex() + 2, closeBracket);
        tokens.add(display.getRealCursorIndex() + 3, root);
        tokens.add(display.getRealCursorIndex() + 4, b);

        display.setCursorIndex(display.getCursorIndex() + 4);
        updateInput();
    }

    /**
     * When the user presses the sqrt Button.
     */
    @Override
    public void clickSqrt() {
        Token t = FunctionFactory.makeSqrt();
        Bracket b = BracketFactory.makeOpenBracket();
        t.addDependency(b);
        b.addDependency(t);
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
        updateInput();
    }

    /**
     * When the user presses the x^2 Button.
     */
    public void clickSquare() {
        ArrayList<Token> list = new ArrayList<>();
        list.add(DigitFactory.makeTwo());
        addTokenInExponent(list);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the x^3 Button.
     */
    public void clickCube() {
        ArrayList<Token> list = new ArrayList<>();
        list.add(DigitFactory.makeThree());
        addTokenInExponent(list);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the cuberoot Button.
     */
    public void clickCbrt() {
        Token root = OperatorFactory.makeVariableRoot();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();
        Bracket b = BracketFactory.makeOpenBracket();

        root.addDependency(b);
        root.addDependency(openBracket);
        root.addDependency(closeBracket);

        tokens.add(display.getRealCursorIndex(), openBracket);
        tokens.add(display.getRealCursorIndex() + 1, DigitFactory.makeThree());
        tokens.add(display.getRealCursorIndex() + 2, closeBracket);
        tokens.add(display.getRealCursorIndex() + 3, root);
        tokens.add(display.getRealCursorIndex() + 4, b);

        display.setCursorIndex(display.getCursorIndex() + 4);
        updateInput();
    }

    /**
     * Gets the index where the fraction starts.
     *
     * @param expression The expression to look at
     * @param numClose   The index where the closing bracket would be placed
     * @return The index where the NUM_OPEN bracket should go
     */
    private int getFracStart(ArrayList<Token> expression, int numClose) {
        Token tokenBefore = expression.get(numClose - 1);
        if (tokenBefore instanceof Digit || tokenBefore instanceof Variable) {
            LinkedList<Token> digits = new LinkedList<>();
            int i = numClose - 1;
            if (tokenBefore instanceof Variable) {
                while (i >= 0 && expression.get(i) instanceof Variable) {
                    i--;
                }
            } else { //Digit
                while (i >= 0 && expression.get(i) instanceof Digit) {
                    i--;
                }
            }
            return i + 1;
        } else if (tokenBefore instanceof Bracket && tokenBefore.getType() == Bracket.CLOSE) {
            int i = numClose - 2;
            int bracketCount = 1;
            while (i >= 0 && bracketCount != 0) {
                Token t = expression.get(i);
                if (t instanceof Bracket) {
                    Bracket b = (Bracket) t;
                    if (b.getType() == Bracket.OPEN) {
                        bracketCount--;
                    } else if (b.getType() == Bracket.CLOSE) {
                        bracketCount++;
                    }
                }
                i--;
            }
            //Includes the function if there is one
            if (i >= 0 && expression.get(i) instanceof Function) {
                i--;
            }
            return i + 1;
        } else if (tokenBefore instanceof Bracket && tokenBefore.getType() == Bracket.SUPERSCRIPT_CLOSE) {
            //Goes to the token before the start of the superscript
            int i = numClose - 2;
            int bracketCount = 1;
            while (i >= 0 && bracketCount != 0) {
                Token t = expression.get(i);
                if (t instanceof Bracket) {
                    Bracket b = (Bracket) t;
                    if (b.getType() == Bracket.SUPERSCRIPT_OPEN) {
                        bracketCount--;
                    } else if (b.getType() == Bracket.SUPERSCRIPT_CLOSE) {
                        bracketCount++;
                    }
                }
                i--;
            }
            //Frac will start at whatever it wouldve been at what the exponent is over
            return getFracStart(expression, i);
        } else {
            return numClose;
        }
    }

    /**
     * When the user presses the FRAC Button.
     */
    public void clickFrac() {
        Token fracOpen = BracketFactory.makeFracOpen();
        Token fracClose = BracketFactory.makeFracClose();
        Token frac = OperatorFactory.makeFraction();
        Token numOpenBracket = BracketFactory.makeNumOpen();
        Token numCloseBracket = BracketFactory.makeNumClose();
        Token denomOpenBracket = BracketFactory.makeDenomOpen();
        Token denomCloseBracket = BracketFactory.makeDenomClose();

        frac.addDependency(numOpenBracket);
        frac.addDependency(numCloseBracket);
        frac.addDependency(denomOpenBracket);
        frac.addDependency(denomCloseBracket);
        frac.addDependency(fracOpen);
        frac.addDependency(fracClose);

        Placeholder p = PlaceholderFactory.makeSuperscriptBlock();
        frac.addDependency(p);

        if (display.getRealCursorIndex() == 0) { //Empty Expression
            tokens.add(display.getRealCursorIndex(), fracOpen);
            tokens.add(display.getRealCursorIndex() + 1, numOpenBracket);
            tokens.add(display.getRealCursorIndex() + 2, PlaceholderFactory.makeSuperscriptBlock());
            tokens.add(display.getRealCursorIndex() + 3, numCloseBracket);
            tokens.add(display.getRealCursorIndex() + 4, frac);
            tokens.add(display.getRealCursorIndex() + 5, denomOpenBracket);
            tokens.add(display.getRealCursorIndex() + 6, p);
            tokens.add(display.getRealCursorIndex() + 7, denomCloseBracket);
            tokens.add(display.getRealCursorIndex() + 8, fracClose);
        } else {
            int startIndex = getFracStart(tokens, display.getRealCursorIndex());
            //Removes the numerator from the expression
            ArrayList<Token> inside = new ArrayList<>();
            for (int i = 0; i < display.getRealCursorIndex() - startIndex; i++) {
                inside.add(tokens.remove(startIndex));
            }
            tokens.add(startIndex, fracOpen);
            tokens.add(startIndex + 1, numOpenBracket);
            tokens.addAll(startIndex + 2, inside);
            tokens.add(display.getRealCursorIndex() + 2, numCloseBracket);
            tokens.add(display.getRealCursorIndex() + 3, frac);
            tokens.add(display.getRealCursorIndex() + 4, denomOpenBracket);
            tokens.add(display.getRealCursorIndex() + 5, p);
            tokens.add(display.getRealCursorIndex() + 6, denomCloseBracket);
            tokens.add(display.getRealCursorIndex() + 7, fracClose);
        }
        display.setCursorIndex(display.getCursorIndex() + 2);
        updateInput();
    }

    /**
     * When the user presses the x^-1 button.
     */
    public void clickReciprocal() {
        ArrayList<Token> list = new ArrayList<>();
        list.add(DigitFactory.makeNegative());
        list.add(DigitFactory.makeOne());
        addTokenInExponent(list);
        display.setCursorIndex(display.getCursorIndex() + 3);
    }

    /**
     * When the user presses the nCk Button.
     */
    public void clickCombination() {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeCombination());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the nPk Button.
     */
    public void clickPermutation() {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makePermutation());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the e Button.
     */
    public void clickE() {
        tokens.add(display.getRealCursorIndex(), VariableFactory.makeE());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the pi Button.
     */
    public void clickPi() {
        tokens.add(display.getRealCursorIndex(), VariableFactory.makePI());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the sin(x) Button.
     */
    public void clickSin() {
        Token t = FunctionFactory.makeSin();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the arcsin(x) or sin^-1(x) Button.
     */
    public void clickASin() {
        Token t = FunctionFactory.makeASin();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the cos(x) Button.
     */
    public void clickCos() {
        Token t = FunctionFactory.makeCos();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the arccos(x) or cos^-1(x) Button.
     */
    public void clickACos() {
        Token t = FunctionFactory.makeACos();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the tan(x) Button.
     */
    public void clickTan() {
        Token t = FunctionFactory.makeTan();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the arctan(x) or tan^-1(x) Button.
     */
    public void clickATan() {
        Token t = FunctionFactory.makeATan();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the sinh(x) Button.
     */
    public void clickSinh() {
        Token t = FunctionFactory.makeSinh();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the arsinh(x) Button.
     */
    public void clickASinh() {
        Token t = FunctionFactory.makeASinh();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the cosh(x) Button.
     */
    public void clickCosh() {
        Token t = FunctionFactory.makeCosh();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the arcosh(x) Button.
     */
    public void clickACosh() {
        Token t = FunctionFactory.makeACosh();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the tanh(x) Button.
     */
    public void clickTanh() {
        Token t = FunctionFactory.makeTanh();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the artanh(x) Button.
     */
    public void clickATanh() {
        Token t = FunctionFactory.makeATanh();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user clicks the const button.
     */
    public void clickConst() {
        try {
            openConst(filename);
        } catch (IOException | ClassNotFoundException e) {
            Toast.makeText(activity, "Error saving to consts", Toast.LENGTH_LONG).show();
        }
    }

/*    *//**
     * Exits the constants view.
     *//*
    public void clickExitConst() {
        constantsDialog.dismiss();
    }

    *//**
     * When the user presses the CONST button
     *//*
    //TODO: Implement by looking at how the settings are implmented
    public void clickConst() {
       AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.constants, null);
        builder.setView(layout);
        constantsDialog = builder.create();
        constantsDialog.show();


        //List<Constant> cnts = new ArrayList<>(Arrays.asList(Constant.values()));
        //ArrayAdapter<Constant> constAdapter = new ArrayAdapter<Integer>(this, R.layout.constants, Constant.values());

}*/

    /**
     * Exits the consts view.
     */
    public void clickExitConst() {
        constWindow.dismiss();
    }

        /**
         * Opens the constants list.
         *
         * @param filename The file name of the consts file
         */
    public void openConst(String filename) throws IOException, ClassNotFoundException {
        //Inflates the XML file so you get the View to add to the PopupWindow
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.constants_view, null, false);

        //Creates the popupWindow, with the width matching the parent's and height matching the parent's
        constWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        //Retrieves the user data from saved memory
        ArrayList<Object[]> consts;
        try {
            FileInputStream stream = activity.openFileInput(filename);
            ObjectInputStream objectStream = new ObjectInputStream(stream);
            consts = (ArrayList<Object[]>) objectStream.readObject();
            //Reverses the order so that the most recent is at the top
            Collections.reverse(consts);
        } catch (FileNotFoundException e) { //No consts
            consts = new ArrayList<>();

            ArrayList<Token> list1 = new ArrayList<>();
            ArrayList<Token> list2 = new ArrayList<>();

            list1.add(new StringToken("No Consts to show"));
            list2.add(new StringToken(""));

            ArrayList<Token>[] message = new ArrayList[2];
            message[0] = list1;
            message[1] = list2;
            consts.add(message);
        }

        //Finds the ListView from the inflated consts XML so it could be manipulated
        ListView lv = (ListView) layout.findViewById(R.id.constantsList);

        //Attaches the custom Adapter to the ListView so that it can configure the items and their Views within it
        lv.setAdapter(new ConstantsAdapter(consts, activity));

        //Displays the created PopupWindow on top of the LinearLayout with ID frame, which is being shown by the Activity
        constWindow.showAtLocation(activity.findViewById(R.id.frame), Gravity.CENTER, 0, 0);
    }

    /**
     * Adds all the tokens in the expression into an exponent.
     *
     * @param toAdd The tokens to add
     */
    private void addTokenInExponent(ArrayList<Token> toAdd) {
        Token exponent = OperatorFactory.makeExponent();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();
        //Determines if a placeholder should be placed
        Token lastToken = tokens.size() == 0 ? null : tokens.get(display.getRealCursorIndex() - 1);
        int addIndex = display.getRealCursorIndex();
        if (lastToken == null || !(lastToken instanceof Number || lastToken instanceof Variable
                || lastToken instanceof Bracket && (((Bracket) lastToken).getType() == Bracket.CLOSE || ((Bracket) lastToken).getType() == Bracket.DENOM_CLOSE))) {
            tokens.add(addIndex, PlaceholderFactory.makeBaseBlock());
            addIndex += 1;
        }
        tokens.add(addIndex, exponent);
        tokens.add(addIndex + 1, openBracket);
        addIndex += 2;
        for (Token token : toAdd) {
            tokens.add(addIndex, token);
            addIndex++;
        }
        tokens.add(addIndex, closeBracket);

        exponent.addDependency(openBracket);
        exponent.addDependency(closeBracket);
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    public boolean isShift() {
        return shift;
    }

    public boolean isMem() {
        return mem;
    }

    public boolean isHyperbolic() {
        return hyperbolic;
    }

    public enum Constant {
        SPEED_OF_LIGHT(299792458), MAGNETIC_VAL((4 * Math.PI) * 1e-7);
        private double value;

        Constant(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

    }

    /**
     * The custom Adapter for the ListView in the consts list.
     */
    private class ConstantsAdapter extends BaseAdapter {

        private MainActivity activity;
        private ArrayList<Object[]> consts; //The data that will be shown in the ListView

        public ConstantsAdapter(ArrayList<Object[]> consts, MainActivity activity) {
            this.consts = consts;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return consts.size();
        }

        @Override
        public Object getItem(int position) {
            return consts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Prepares the View of each item in the ListView that this Adapter will be attached to.
         *
         * @param position    The index of the item
         * @param convertView The old view that may be reused, or null if not possible
         * @param parent      The parent view
         * @return The newly prepared View that will visually represent the item in the ListView in the given position
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) { //For efficiency purposes so that it does not unnecessarily inflate Views
                //Inflates the XML file to get the View of the consts element
                LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.constants_element, parent, false);
            }

            //Sets up the child Views within each item in the ListView
            OutputView input = (OutputView) convertView.findViewById(R.id.input);
            OutputView output = (OutputView) convertView.findViewById(R.id.output);

            //Sets the font size of each OutputView
            input.setFontSize(activity.getFontSize());
            output.setFontSize((int) (activity.getFontSize() * CONSTANTS_IO_RATIO));

            //Enters the appropriate expressions to the OutputView
            Object[] entry = consts.get(position);
            input.display((ArrayList<Token>) entry[0]);
            output.display((ArrayList<Token>) entry[1]);

            //To respond to user touches
            final ArrayList<Token> INPUT = (ArrayList<Token>) consts.get(position)[0]; //Makes a constant reference so that consts can be accessed by an inner class
            convertView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        ArrayList<Token> input = new ArrayList<>();
                        //Removes any StringTokens
                        for (Token t : INPUT) {
                            if (!(t instanceof StringToken)) {
                                input.add(t);
                            }
                        }
                        //Adds the input expression to the current tokens
                        tokens.addAll(input); //Adds the input of the entry
                        constWindow.dismiss(); //Exits consts once an Item has been selected
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            return convertView;
        }
    }

}