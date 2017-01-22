/*
 * Copyright (c) 2016 TruTech Innovations Inc - All Rights Reserved
 */

package com.trutechinnovations.calculall;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Contains the back-end of the Matrix Mode. The mode will be able to perform most
 * tasks relating to Linear Algebra.
 *
 * @author Alston Lin, Ejaaz Merali
 * @version 3.0
 */
public class MatrixMode extends FunctionMode {
    public static final int DEFAULT_ROWS = 3;
    public static final int DEFAULT_COLS = 3;
    public static final int MAX_DIMENSIONS = 7;
    private static final MatrixMode INSTANCE = new MatrixMode();

    public Command<Void, Void> lastAction; // The last method the user has used to compute
    //Variables used only when in ElementView
    private PopupWindow elementsWindow;
    private PopupWindow elementWindow;
    private ArrayList<Token> storedTokens; //Used to store Tokens when switched to the ElementView
    private Matrix matrix;
    private int x, y;
    private ProgressDialog pd;

    { //pseudo-constructor
        filename = "history_matrix";
    }

    /**
     * Allows for the Singleton pattern so there would be only one instance.
     *
     * @return The singleton instance
     */
    public static MatrixMode getInstance() {
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
            case R.id.new_button:
                clickNew();
                break;
            case R.id.edit_button:
                clickEdit();
                break;
            case R.id.ref_button:
                clickREF();
                break;
            case R.id.rref_button:
                clickRREF();
                break;
            case R.id.det_button:
                clickDeterminant();
                break;
            case R.id.decomp_button:
                openDecomp();
                break;
            case R.id.tr_button:
                clickTrace();
                break;
            case R.id.lambda_button:
                clickLambda();
                break;
            case R.id.ev_button:
                clickEigenVect();
                break;
            case R.id.ev_button_image:
                clickEigenVect();
                break;
            case R.id.rank_button:
                clickRank();
                break;
            case R.id.mem_button_m:
                clickMem();
                break;
            case R.id.transpose_button:
                clickTranspose();
                break;
            case R.id.pow_button:
                clickExponent();
                break;
            case R.id.inverse_button:
                clickInverse();
                break;
            case R.id.num_button:
                clickNum();
                break;
            case R.id.add_button:
                clickAdd();
                break;
            case R.id.entry_add_button:
                clickAddElement();
                break;
            case R.id.entry_subtract_button:
                clickSubtractElement();
                break;
            case R.id.entry_multiply_button:
                clickMultiplyElement();
                break;
            case R.id.entry_divide_button:
                clickDivideElement();
                break;
            case R.id.entry_sqrt_button:
                clickSqrtElement();
                break;
            case R.id.minus_button:
                clickSubtract();
                break;
            case R.id.multiply_button:
                clickMultiply();
                break;
            case R.id.equals_button:
                clickEquals();
                break;
            case R.id.return_button:
                clickReturn();
                break;
            case R.id.done_button:
                clickDone();
                break;
            case R.id.shift_button:
                clickShift();
                break;
            case R.id.done_num_button:
                clickDoneNum();
                break;
            case R.id.left_scroll:
                scrollLeft();
                break;
            case R.id.right_scroll:
                scrollRight();
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
            case R.id.divide_button:
                clickDivide();
                break;
            case R.id.frac_mode:
                clickFracMode();
                break;
            default:
                super.onClick(v);
        }
    }

    /**
     * Stores the a variable into the memory; the assignment itself will occur in the given Command.
     *
     * @param addToOutput The String that will be shown in the output along with the value
     * @param assignment  The assignment command that would be executed
     */
    protected void storeVariable(String addToOutput, Command<Void, ArrayList<Token>> assignment) {
        ToggleButton memButton = (ToggleButton) activity.findViewById(R.id.mem_button_m);
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
                    VariableFactory.matrixAValue = val;
                    return null;
                }
            });
        } else {
            tokens.add(display.getRealCursorIndex(), VariableFactory.makeMatrixA());
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
                    VariableFactory.matrixBValue = val;
                    return null;
                }
            });
        } else {
            tokens.add(display.getRealCursorIndex(), VariableFactory.makeMatrixB());
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
                    VariableFactory.matrixCValue = val;
                    return null;
                }
            });
        } else {
            tokens.add(display.getRealCursorIndex(), VariableFactory.makeMatrixC());
            display.setCursorIndex(display.getCursorIndex() + 1);
            updateInput();
        }
    }

    /**
     * When the user presses the ANS button
     */
    public void clickAns() {
        tokens.add(display.getRealCursorIndex(), VariableFactory.makeAnsMat());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    public void clickNum() {
        //Replaces the current fragment with the matrix_num (for now)
        ViewGroup v = (ViewGroup) fragment.getView();
        if (v != null) {
            View numView = v.findViewById(R.id.matrix_num);
            numView.setVisibility(View.VISIBLE);
        }
    }

    public void clickDoneNum() {
        ViewGroup v = (ViewGroup) fragment.getView();
        if (v != null) {
            final View numView = v.findViewById(R.id.matrix_num);
            numView.setVisibility(View.GONE);
        }
    }

    /**
     * When the user presses the shift button. Switches the state of the boolean variable shift.
     */
    public void clickShift() {
        shift = !shift;
        ToggleButton shiftButton = (ToggleButton) elementWindow.getContentView().findViewById(R.id.shift_button);
        shiftButton.setChecked(shift);
        //Changes the mode for all the Buttons
        for (MultiButton b : multiButtons) {
            b.changeMode(shift, hyperbolic);
        }
        updateInput();
    }

    /**
     * When the user presses the New button.
     */
    public void clickNew() {
        //Creates a new empty matrix and edits it
        ArrayList[][] entries = new ArrayList[DEFAULT_ROWS][DEFAULT_COLS];
        for (int i = 0; i < entries.length; i++) {
            for (int j = 0; j < entries[i].length; j++) {
                ArrayList<Token> entry = new ArrayList<>();
                entry.add(new Number(0));
                entries[i][j] = entry;
            }
        }
        Matrix m = new Matrix(entries);
        tokens.add(display.getRealCursorIndex(), m);
        editMatrix(m);
        display.setCursorIndex(display.getRealCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the Edit button.
     */
    public void clickEdit() {
        if (tokens.size() != 0 && display.getRealCursorIndex() < tokens.size()) {
            Token m = tokens.get(display.getRealCursorIndex());
            if (m instanceof Matrix) {
                editMatrix((Matrix) m);
                updateInput();
                return;
            }
        }
        Toast.makeText(activity, "Place the cursor before a Matrix to edit it.", Toast.LENGTH_LONG).show();
    }

    /**
     * Opens the UI to Edit the given Matrix
     *
     * @param m The Matrix to edit
     */
    private void editMatrix(Matrix m) {
        loadElementsView(m);
    }

    /**
     * Loads the elements UI to edit individual elements
     *
     * @param m The Matrix to edit
     */
    private void loadElementsView(final Matrix m) {
        PopupWindow old = elementsWindow;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.elements_layout, null, false);
        elementsWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        elementsWindow.setBackgroundDrawable(new BitmapDrawable());
        elementsWindow.showAtLocation(activity.findViewById(R.id.frame), Gravity.CENTER, 0, 0);
        //Generates an array of Strings representing the Matrix entries
        String[] strs = new String[m.getNumOfRows() * m.getNumOfCols()];
        for (int i = 0; i < m.getNumOfRows(); i++) {
            for (int j = 0; j < m.getNumOfCols(); j++) {
                ArrayList<Token> entry = m.getEntry(i, j);
                String str = Utility.printExpression(entry);
                strs[i * m.getNumOfCols() + j] = str;
            }
        }
        //Edits the GridView
        GridView gv = (GridView) layout.findViewById(R.id.elements_grid);
        gv.setNumColumns(m.getNumOfCols());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.element, strs);
        gv.setAdapter(adapter);
        //Sets what happens when an element is clicked
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                int rowNum = position / m.getNumOfCols();
                int colNum = position % m.getNumOfCols();
                editEntry(m, rowNum, colNum);
            }
        });
        //Sets the Spinners
        AppCompatSpinner xSpinner = (AppCompatSpinner) layout.findViewById(R.id.x_spinner);
        AppCompatSpinner ySpinner = (AppCompatSpinner) layout.findViewById(R.id.y_spinner);
        Integer[] items = new Integer[MAX_DIMENSIONS];
        for (int i = 0; i < MAX_DIMENSIONS; i++) {
            items[i] = i + 1;
        }

        ArrayAdapter<Integer> spinnerAdapterX = new ArrayAdapter<>(fragment.getActivity(), R.layout.spinner_item, items);
        ArrayAdapter<Integer> spinnerAdapterY = new ArrayAdapter<>(fragment.getActivity(), R.layout.spinner_item, items);
        xSpinner.setAdapter(spinnerAdapterX);
        ySpinner.setAdapter(spinnerAdapterY);

        xSpinner.setSelection(m.getNumOfRows() - 1);
        ySpinner.setSelection(m.getNumOfCols() - 1);
        xSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                if (position + 1 != m.getNumOfRows()) { //Makes sure its actually changed
                    m.changeSize(position + 1, m.getNumOfCols());
                    loadElementsView(m); //Reloads the View
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                if (position + 1 != m.getNumOfCols()) { //Makes sure its actually changed
                    m.changeSize(m.getNumOfRows(), position + 1);
                    loadElementsView(m); //Reloads the View
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (old != null && old.isShowing()) old.dismiss(); //Closes previous window
    }

    /**
     * Edits an individual entry (Opens the ElementView)
     *
     * @param m      The Matrix to edit
     * @param rowNum The row of the entry
     * @param colNum The column of the entry
     */
    private void editEntry(Matrix m, int rowNum, int colNum) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.element_layout, null, false);
        elementWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        elementWindow.setBackgroundDrawable(new BitmapDrawable());
        elementWindow.showAtLocation(activity.findViewById(R.id.frame), Gravity.CENTER, 0, 0);

        //Sets up the Buttons
        setupElementButtons(layout);
        //Sets up the tokens already in the element
        storedTokens = tokens;
        tokens = m.getEntry(rowNum, colNum); //Switches pointers (temporarily) to a new set of tokens
        //Clears tokens if the only token is a Number of zero
        if (tokens.size() == 1 && tokens.get(0) instanceof Number && ((Number) tokens.get(0)).getValue() == 0) {
            tokens.clear();
        }
        //Sets up the variables and parameters
        display = (DisplayView) layout.findViewById(R.id.element_display);
        display.setOutput((OutputView) layout.findViewById(R.id.output));
        display.setFontSize(activity.getFontSize());
        display.displayInput(tokens);
        updateInput();
        matrix = m;
        x = rowNum;
        y = colNum;
    }

    /**
     * Moves the cursor on the display right if possible.
     */
    public void scrollRight() {
        display.scrollRight();
    }

    /**
     * Scrolls the display left (if possible).
     */
    protected void scrollLeft() {
        display.scrollLeft();
    }

    /**
     * Sets up the buttons in ElementView.
     *
     * @param layout The ElementView
     */
    private void setupElementButtons(View layout) {
        MultiButton sinButton = (MultiButton) layout.findViewById(R.id.sin_button);
        MultiButton cosButton = (MultiButton) layout.findViewById(R.id.cos_button);
        MultiButton tanButton = (MultiButton) layout.findViewById(R.id.tan_button);
        Spanned[] sinStrings = new Spanned[4];
        Spanned[] cosStrings = new Spanned[4];
        Spanned[] tanStrings = new Spanned[4];
        Command[] sinCommands = new Command[2];
        Command[] cosCommands = new Command[2];
        Command[] tanCommands = new Command[2];
        sinStrings[0] = SpannedString.valueOf("sin");
        sinStrings[1] = Html.fromHtml(activity.getString(R.string.arcsin));
        sinStrings[2] = SpannedString.valueOf("sinh");
        sinStrings[3] = Html.fromHtml(activity.getString(R.string.arcsinh));

        cosStrings[0] = SpannedString.valueOf("cos");
        cosStrings[1] = Html.fromHtml(activity.getString(R.string.arccos));
        cosStrings[2] = SpannedString.valueOf("cosh");
        cosStrings[3] = Html.fromHtml(activity.getString(R.string.arccosh));

        tanStrings[0] = SpannedString.valueOf("tan");
        tanStrings[1] = Html.fromHtml(activity.getString(R.string.arctan));
        tanStrings[2] = SpannedString.valueOf("tanh");
        tanStrings[3] = Html.fromHtml(activity.getString(R.string.arctanh));

        sinCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                clickSin();
                return null;
            }
        };
        sinCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                clickASin();
                return null;
            }
        };

        cosCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                clickCos();
                return null;
            }
        };
        cosCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                clickACos();
                return null;
            }
        };

        tanCommands[0] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                clickTan();
                return null;
            }
        };
        tanCommands[1] = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                clickATan();
                return null;
            }
        };

        sinButton.setModeTexts(sinStrings);
        sinButton.setOnClicks(sinCommands);

        cosButton.setModeTexts(cosStrings);
        cosButton.setOnClicks(cosCommands);

        tanButton.setModeTexts(tanStrings);
        tanButton.setOnClicks(tanCommands);

        ArrayList<MultiButton> buttons = new ArrayList<>();
        buttons.add(sinButton);
        buttons.add(cosButton);
        buttons.add(tanButton);

        setMultiButtons(buttons);
    }

    /**
     * When the user presses the x Button.
     */
    public void clickMultiplyElement() {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeMultiply());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the / Button.
     */
    public void clickDivideElement() {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeDivide());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the + Button.
     */
    public void clickAddElement() {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeAdd());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the - Button.
     */
    public void clickSubtractElement() {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeSubtract());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the sqrt Button.
     */
    public void clickSqrtElement() {
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeSqrt());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user has finished with ElementView.
     */
    public void clickDone() {
        //If there are no Tokens, default Token of a Zero Number will be placed
        if (tokens.size() == 0) {
            tokens.add(new Number(0));
            //Stores the tokens into the Matrix
            matrix.setEntry(x, y, tokens);
        } else {
            //Stores the tokens into the Matrix
            matrix.setEntry(x, y, tokens);
        }
        //Returns the display and input back to MatrixMode
        tokens = storedTokens;
        storedTokens = null;
        editMatrix(matrix);
        elementWindow.dismiss();
    }

    //Functions for the Main View

    /**
     * Returns from ElementsView to the main View
     */
    public void clickReturn() {
        elementsWindow.dismiss();
        display = activity.getDisplay();
        display.setOutput((OutputView) activity.findViewById(R.id.output));
        updateInput();
    }

    /**
     * When the user presses the det(A) button.
     */
    public void clickDeterminant() {
        Token t = MatrixFunctionFactory.makeDeterminant();
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

    public void clickFracMode() {
        if (fracMode == DEC) {
            fracMode = FRAC;
        } else if (fracMode == FRAC) {
            fracMode = DEC;
        }
        if (lastAction != null) {
            lastAction.execute(null); //Re-performs the last calculation, but with the frac mode switched
        }
    }

    /**
     * When the user presses the tr(A) button.
     */
    public void clickTrace() {
        Token t = MatrixFunctionFactory.makeTrace();
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
     * When the user presses the A^-1 button.
     */
    public void clickInverse() {
        Token t = MatrixFunctionFactory.makeInverse();
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
     * When the user presses the A^T button.
     */
    public void clickTranspose() {
        Token t = MatrixFunctionFactory.makeTranspose();
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
     * When the user presses the ref button
     */
    public void clickREF() {
        lastAction = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                clickREF();
                return null;
            }
        };
        openReduction(false);
    }

    /**
     * When the user presses the rref button
     */
    public void clickRREF() {
        lastAction = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                clickRREF();
                return null;
            }
        };
        openReduction(true);
    }

    /**
     * When the user presses the ref button
     */
    public void clickRank() {
        Token t = MatrixFunctionFactory.makeRank();
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
     * When the user presses the + button
     */
    public void clickAdd() {
        tokens.add(display.getRealCursorIndex(), MatrixOperatorFactory.makeMatrixAdd());
        display.setCursorIndex(display.getRealCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the - button
     */
    public void clickSubtract() {
        tokens.add(display.getRealCursorIndex(), MatrixOperatorFactory.makeMatrixSubtract());
        display.setCursorIndex(display.getRealCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the * button
     */
    public void clickMultiply() {
        tokens.add(display.getRealCursorIndex(), MatrixOperatorFactory.makeMatrixMultiply());
        display.setCursorIndex(display.getRealCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the * button
     */
    public void clickDivide() {
        tokens.add(display.getRealCursorIndex(), MatrixOperatorFactory.makeMatrixDivide());
        display.setCursorIndex(display.getRealCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the A^y button
     */
    public void clickExponent() {
        tokens.add(display.getRealCursorIndex(), MatrixOperatorFactory.makeMatrixExponent());
        display.setCursorIndex(display.getRealCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the equals Button.
     */
    public void clickEquals() {
        lastAction = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                clickEquals();
                return null;
            }
        };
        DisplayView display = (DisplayView) activity.findViewById(R.id.display);
        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.addMissingBrackets(Utility.subVariables(Utility.multiplyConstants(Utility.condenseDigits(tokens)))));
            temp = MatrixUtils.convertToReversePolish(temp);
            Token t = MatrixUtils.evaluateExpression(temp, fracMode == FRAC);
            ArrayList<Token> output = new ArrayList<>();
            output.add(t);
            display.displayOutput(output);
            if (!MatrixUtils.easterEgg.isEmpty()) {
                Toast.makeText(activity, MatrixUtils.easterEgg, Toast.LENGTH_LONG).show();
                MatrixUtils.easterEgg = "";
            }
            saveEquation(tokens, output, filename);
            activity.scrollDown();
            saveAns(output);
        } catch (Exception e) { //an error was thrown
            super.handleExceptions(e);
        }
    }

    /**
     * Saves the answer into the VariableFactory class.
     *
     * @param ans The expression to save
     */
    public void saveAns(ArrayList<Token> ans) {
        VariableFactory.ansValueMat = ans;
    }

    /**
     * When the user presses the LUP button
     */
    public void clickLUP() {
        DisplayView display = (DisplayView) activity.findViewById(R.id.display);
        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.condenseDigits(tokens));
            temp = MatrixUtils.convertToReversePolish(temp);
            Token t = MatrixUtils.evaluateExpression(temp, false);
            if (t instanceof Matrix) {
                double[][] a = MatrixUtils.evaluateMatrixEntries((Matrix) t);
                double[][][] lup = MatrixUtils.getLUDecomposition(a);
                Matrix u = new Matrix(lup[0]);
                Matrix l = new Matrix(lup[1]);
                Matrix p = new Matrix(lup[2]);

                if (fracMode == FRAC) {
                    u.fractionalize();
                    l.fractionalize();
                    p.fractionalize();
                }

                ArrayList<Token> input = new ArrayList<>();
                input.add(0, new StringToken("LUP Decomposition of "));
                input.addAll(tokens);
                updateInput();

                ArrayList<Token> output = new ArrayList<>();
                output.add(new StringToken("P = "));
                output.add(p);
                output.add(new StringToken(" , L = "));
                output.add(l);
                output.add(new StringToken(" , U = "));
                output.add(u);

                display.displayOutput(output);

                saveEquation(input, output, filename);
                activity.scrollDown();
            } else {
                throw new IllegalArgumentException("The result must be a single Matrix to find the LUP factorization");
            }
        } catch (Exception e) { //an error was thrown
            super.handleExceptions(e);
        }
    }

    /**
     * When the user presses the λ button
     */
    public void clickLambda() {
        lastAction = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                clickLambda();
                return null;
            }
        };
        DisplayView display = (DisplayView) activity.findViewById(R.id.display);
        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.subVariables(Utility.condenseDigits(tokens)));
            temp = MatrixUtils.convertToReversePolish(temp);
            Token t = MatrixUtils.evaluateExpression(temp, false);
            if (t instanceof Matrix) {
                double[] eigenValues = MatrixUtils.dedupe(MatrixUtils.roundInfinitesimals(MatrixUtils.getEigenValues(MatrixUtils.evaluateMatrixEntries((Matrix) t))));

                ArrayList<Token> input = new ArrayList<>();
                input.addAll(tokens);
                input.add(0, new StringToken("Eigenvalues of "));

                ArrayList<Token> output = new ArrayList<>();
                output.add(new StringToken("Eigenvalues: "));
                boolean first = true;
                for (int i = 0; i < eigenValues.length; i++) {
                    if (!first) {
                        output.add(new StringToken(" , "));
                    }
                    if (fracMode == FRAC) {
                        output.addAll(JFok.fractionalize(new Number(eigenValues[i])));
                    } else {
                        ArrayList<Token> result = new ArrayList<>();
                        result.add(new Number(eigenValues[i]));
                        output.addAll(result);
                    }
                    first = false;
                }

                display.displayOutput(output);

                saveEquation(input, output, filename);
                activity.scrollDown();
            } else {
                throw new IllegalArgumentException("The result must be a single Matrix to find the eigenvalues");
            }
        } catch (Exception e) { //an error was thrown
            super.handleExceptions(e);
        }
    }

    /**
     * When the user presses the eigenvect button
     */
    public void clickEigenVect() {
        lastAction = new Command<Void, Void>() {
            @Override
            public Void execute(Void o) {
                clickEigenVect();
                return null;
            }
        };
        DisplayView display = (DisplayView) activity.findViewById(R.id.display);
        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.subVariables(Utility.condenseDigits(tokens)));
            temp = MatrixUtils.convertToReversePolish(temp);
            Token t = MatrixUtils.evaluateExpression(temp, false);
            if (t instanceof Matrix) {
                ArrayList<Vector> ev = MatrixUtils.getEigenVectors(MatrixUtils.evaluateMatrixEntries((Matrix) t));

                String outputStr = "Eigenvectors: ";
                boolean first = true;
                for (Vector v : ev) {
                    if (!first) {
                        outputStr += " , ";
                    }
                    if (fracMode == FRAC) {
                        double[][] tempArray = new double[1][v.getDimensions()];
                        tempArray[0] = v.getValues();
                        Matrix tempMatrix = new Matrix(tempArray);
                        tempMatrix.fractionalize();
                        String tempStr = tempMatrix.getSymbol();
                        tempStr = tempStr.replaceAll("\\\\", "").trim();
                        tempStr = "[".concat(tempStr);
                        tempStr += "]";
                        outputStr += tempStr;
                    } else {
                        outputStr += v.getSymbol();
                    }
                    first = false;
                }

                ArrayList<Token> input = new ArrayList<>();
                input.addAll(tokens);
                input.add(0, new StringToken("Eigenvectors of "));

                ArrayList<Token> output = new ArrayList<>();
                output.add(new StringToken(outputStr));

                display.displayOutput(output);

                saveEquation(input, output, filename);
                activity.scrollDown();
            } else {
                throw new IllegalArgumentException("The result must be a single Matrix to find the Eigenvectors");
            }
        } catch (Exception e) { //an error was thrown
            super.handleExceptions(e);
        }
    }

    /**
     * When the user presses the diag() button
     */
    public void clickDiagonalize() {
        DisplayView display = (DisplayView) activity.findViewById(R.id.display);
        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.condenseDigits(tokens));
            temp = MatrixUtils.convertToReversePolish(temp);
            Token t = MatrixUtils.evaluateExpression(temp, false);
            if (t instanceof Matrix) {
                double[][] a = MatrixUtils.evaluateMatrixEntries((Matrix) t);
                double[][][] diag = MatrixUtils.getEigenDecomposition(a);
                Matrix p = new Matrix(diag[0]);
                Matrix d = new Matrix(diag[1]);
                Matrix inv_p = new Matrix(diag[2]);

                if (fracMode == FRAC) {
                    p.fractionalize();
                    d.fractionalize();
                    inv_p.fractionalize();
                }

                ArrayList<Token> input = new ArrayList<>();
                input.add(0, new StringToken("Eigen Decomposition of "));
                input.addAll(tokens);
                updateInput();

                ArrayList<Token> output = new ArrayList<>();
                output.add(new StringToken("P = "));
                output.add(p);
                output.add(new StringToken(" , D = "));
                output.add(d);
                output.add(new StringToken(" , inv(P) = "));
                output.add(inv_p);

                display.displayOutput(output);

                saveEquation(input, output, filename);
                activity.scrollDown();
            } else {
                throw new IllegalArgumentException("The result must be a single Matrix to find the Eigen Decomposition");
            }
        } catch (Exception e) { //an error was thrown
            super.handleExceptions(e);
        }
    }

    /**
     * When the user presses the QR button
     */
    public void clickQR() {
        DisplayView display = (DisplayView) activity.findViewById(R.id.display);
        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.condenseDigits(tokens));
            temp = MatrixUtils.convertToReversePolish(temp);
            Token t = MatrixUtils.evaluateExpression(temp, false);
            if (t instanceof Matrix) {
                double[][] a = MatrixUtils.evaluateMatrixEntries((Matrix) t);
                double[][][] qr = MatrixUtils.getQRDecomposition(a);
                Matrix q = new Matrix(qr[0]);
                Matrix r = new Matrix(qr[1]);

                if (fracMode == FRAC) {
                    q.fractionalize();
                    r.fractionalize();
                }

                ArrayList<Token> input = new ArrayList<>();
                input.add(0, new StringToken("QR Decomposition of "));
                input.addAll(tokens);
                updateInput();

                ArrayList<Token> output = new ArrayList<>();
                output.add(new StringToken("Q = "));
                output.add(q);
                output.add(new StringToken(" , "));
                output.add(new StringToken("R = "));
                output.add(r);

                display.displayOutput(output);

                saveEquation(input, output, filename);
                activity.scrollDown();
            } else {
                throw new IllegalArgumentException("The result must be a single Matrix to find the QR decomposition");
            }
        } catch (Exception e) { //an error was thrown
            super.handleExceptions(e);
        }
    }

    /**
     * When the user presses the RRQR button
     */
    public void clickRRQR() {
        DisplayView display = (DisplayView) activity.findViewById(R.id.display);
        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.condenseDigits(tokens));
            temp = MatrixUtils.convertToReversePolish(temp);
            Token t = MatrixUtils.evaluateExpression(temp, false);
            if (t instanceof Matrix) {
                double[][] a = MatrixUtils.evaluateMatrixEntries((Matrix) t);
                double[][][] rrqr = MatrixUtils.getRRQRDecomposition(a);
                Matrix q = new Matrix(rrqr[0]);
                Matrix r = new Matrix(rrqr[1]);
                Matrix p = new Matrix(rrqr[2]);

                if (fracMode == FRAC) {
                    q.fractionalize();
                    r.fractionalize();
                    p.fractionalize();
                }

                ArrayList<Token> input = new ArrayList<>();
                input.add(0, new StringToken("RRQR Decomposition of "));
                input.addAll(tokens);
                updateInput();

                ArrayList<Token> output = new ArrayList<>();
                output.add(new StringToken("P = "));
                output.add(p);
                output.add(new StringToken(" , "));
                output.add(new StringToken("Q = "));
                output.add(q);
                output.add(new StringToken(" , "));
                output.add(new StringToken("R = "));
                output.add(r);

                display.displayOutput(output);

                saveEquation(input, output, filename);
                activity.scrollDown();
            } else {
                throw new IllegalArgumentException("The result must be a single Matrix to find the RRQR decomposition");
            }
        } catch (Exception e) { //an error was thrown
            super.handleExceptions(e);
        }
    }

    /**
     * When the user presses the Cholesky button
     */
    public void clickCholesky() {
        DisplayView display = (DisplayView) activity.findViewById(R.id.display);
        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.condenseDigits(tokens));
            temp = MatrixUtils.convertToReversePolish(temp);
            Token t = MatrixUtils.evaluateExpression(temp, false);
            if (t instanceof Matrix) {
                double[][] a = MatrixUtils.evaluateMatrixEntries((Matrix) t);
                double[][][] ch = MatrixUtils.getCholeskyDecomposition(a);
                Matrix l = new Matrix(ch[0]);
                Matrix lt = new Matrix(ch[1]);

                if (fracMode == FRAC) {
                    l.fractionalize();
                    lt.fractionalize();
                }

                ArrayList<Token> input = new ArrayList<>();
                input.add(0, new StringToken("Cholesky Decomposition of "));
                input.addAll(tokens);
                updateInput();

                ArrayList<Token> output = new ArrayList<>();
                output.add(new StringToken("L = "));
                output.add(l);
                output.add(new StringToken(" , "));
                output.add(new StringToken("trans(L) = "));
                output.add(lt);

                display.displayOutput(output);

                saveEquation(input, output, filename);
                activity.scrollDown();
            } else {
                throw new IllegalArgumentException("The result must be a single Matrix to find the Cholesky decomposition");
            }
        } catch (Exception e) { //an error was thrown
            super.handleExceptions(e);
        }
    }

    /**
     * When the user presses the SVD button
     */
    public void clickSVD() {
        DisplayView display = (DisplayView) activity.findViewById(R.id.display);
        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.condenseDigits(tokens));
            temp = MatrixUtils.convertToReversePolish(temp);
            Token t = MatrixUtils.evaluateExpression(temp, false);
            if (t instanceof Matrix) {
                double[][] a = MatrixUtils.evaluateMatrixEntries((Matrix) t);
                double[][][] svd = MatrixUtils.getSVDecomposition(a);
                Matrix u = new Matrix(svd[0]);
                Matrix s = new Matrix(svd[1]);
                Matrix vt = new Matrix(svd[2]);

                if (fracMode == FRAC) {
                    u.fractionalize();
                    s.fractionalize();
                    vt.fractionalize();
                }

                ArrayList<Token> input = new ArrayList<>();
                input.add(0, new StringToken("SVD of "));
                input.addAll(tokens);
                updateInput();

                ArrayList<Token> output = new ArrayList<>();
                output.add(new StringToken("U = "));
                output.add(u);
                output.add(new StringToken(" , "));
                output.add(new StringToken("Σ = "));
                output.add(s);
                output.add(new StringToken(" , "));
                output.add(new StringToken("trans(V) = "));
                output.add(vt);

                display.displayOutput(output);

                saveEquation(input, output, filename);
                activity.scrollDown();
            } else {
                throw new IllegalArgumentException("The result must be a single Matrix to find the SVD");
            }
        } catch (Exception e) { //an error was thrown
            super.handleExceptions(e);
        }
    }

    /**
     * Opens the row reduction steps
     *
     * @param rref is the RREF being computed?(TRUE) or just the REF?(FALSE)
     */
    public void openReduction(boolean rref) {
        Intent intent = new Intent(activity,ReductionActivity.class);
        intent.putExtra(ReductionActivity.RREF,rref);
        activity.startActivity(intent);
    }

    /**
     * Opens the matrix decompositions window
     */
    public void openDecomp() {
        Intent intent  = new Intent(activity, DecompositionSelectionActivity.class);
        activity.startActivity(intent);
    }

    public PopupWindow getElementsWindow() {
        return elementsWindow;
    }

    public PopupWindow getElementWindow() {
        return elementWindow;
    }
}