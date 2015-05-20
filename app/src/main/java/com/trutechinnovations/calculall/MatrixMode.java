package com.trutechinnovations.calculall;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.Spinner;
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
    private static final Basic INSTANCE = new MatrixMode();
    //Variables used only when in ElementView
    private PopupWindow elementsWindow;
    private PopupWindow elementWindow;
    private ArrayList<Token> storedTokens; //Used to store Tokens when switched to the ElementView
    private Matrix matrix;
    private int x, y;

    { //pseudo-constructor
        filename = "history_matrix";
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
            case R.id.diag_button:
                clickDiagonalize();
                break;
            case R.id.decomp_button:
                clickLUP();
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
            case R.id.rank_button:
                clickRank();
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
            default:
                super.onClick(v);
        }
    }

    /**
     * Stores the a variable into the memory; the assignment itself will occur in the given Command.
     *
     * @param addToOutput The String that will be shown in the output along with the value
     * @param assignment  The assignment command that would be executed
     *//*
    protected void storeVariable(String addToOutput, Command<Void, Double> assignment) {
        ToggleButton memButton = (ToggleButton) activity.findViewById(R.id.mem_button);
        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(tokens);
            temp = MatrixUtils.convertToReversePolish(temp);
            Matrix val = (Matrix) MatrixUtils.evaluateExpression(temp);
            ArrayList<Token> outputList = new ArrayList<>();
            outputList.add(new Matrix(val));
            outputList.add(new StringToken(addToOutput));
            display.displayOutput(outputList);
            assignment.execute(val);
            mem = false;
            memButton.setChecked(false);
        } catch (Exception e) { //User did a mistake
            handleExceptions(e);
        }
    }
    *//**
     * When the user presses the A button
     *//*
    public void clickA() {
        if (mem) {
            storeVariable("→ A", new Command<Void, Double>() {
                @Override
                public Void execute(Double val) {
                    Variable.a_value = val;
                    return null;
                }
            });
        } else {
            tokens.add(display.getRealCursorIndex(), VariableFactory.makeA());
            display.setCursorIndex(display.getCursorIndex() + 1);
            updateInput();
        }
    }
    *//**
     * When the user presses the B button
     *//*
    public void clickB() {
        if (mem) {
            storeVariable("→ B", new Command<Void, Double>() {
                @Override
                public Void execute(Double val) {
                    Variable.b_value = val;
                    return null;
                }
            });
        } else {
            tokens.add(display.getRealCursorIndex(), VariableFactory.makeB());
            display.setCursorIndex(display.getCursorIndex() + 1);
            updateInput();
        }
    }
    */

    /**
     * When the user presses the C button
     *//*
    public void clickC() {
        if (mem) {
            storeVariable("→ C", new Command<Void, Double>() {
                @Override
                public Void execute(Double val) {
                    Variable.c_value = val;
                    return null;
                }
            });
        } else {
            tokens.add(display.getRealCursorIndex(), VariableFactory.makeC());
            display.setCursorIndex(display.getCursorIndex() + 1);
            updateInput();
        }
    }*/
    public void clickNum() {
        //Replaces the current fragment with the matrix_num (for now)
        ViewGroup v = (ViewGroup) fragment.getView();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View numView = inflater.inflate(R.layout.matrix_num, null, false);
        if (v != null) {
            v.removeAllViews();
            v.addView(numView);
        }
    }

    public void clickDoneNum() {
        ViewGroup v = (ViewGroup) fragment.getView();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View matrixView = inflater.inflate(R.layout.matrix, null, false);
        if (v != null) {
            v.removeAllViews();
        }
        v.addView(matrixView);
        Button transButton = (Button) matrixView.findViewById(R.id.transpose_button);
        Button powButton = (Button) matrixView.findViewById(R.id.pow_button);
        Button inverseButton = (Button) matrixView.findViewById(R.id.inverse_button);
        Button newButton = (Button) matrixView.findViewById(R.id.new_button);
        transButton.setText(Html.fromHtml(activity.getString(R.string.transpose)));
        powButton.setText(Html.fromHtml(activity.getString(R.string.matrix_pow)));
        inverseButton.setText(Html.fromHtml(activity.getString(R.string.inverse_a)));
        newButton.setText(Html.fromHtml(activity.getString(R.string.newe)));
    }

    public void clickMem() {
        //TODO: FINISH
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
        Spinner xSpinner = (Spinner) layout.findViewById(R.id.x_spinner);
        Spinner ySpinner = (Spinner) layout.findViewById(R.id.y_spinner);
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
     * When the user presses the shift button. Switches the state of the boolean variable shift
     */
    /*
    @Override
    public void clickShift() {
        Button sinButton = (Button) activity.findViewById(R.id.sin_button);
        Button cosButton = (Button) activity.findViewById(R.id.cos_button);
        Button tanButton = (Button) activity.findViewById(R.id.tan_button);
        ToggleButton shiftButton = (ToggleButton) activity.findViewById(R.id.shift_button);
        if (shift) {
            shift = false;
            sinButton.setText("sin");
            sinButton.setTextSize(36);
            cosButton.setText("cos");
            cosButton.setTextSize(36);
            tanButton.setText("tan");
            tanButton.setTextSize(36);
        } else {
            shift = true;
            sinButton.setText("arcsin");
            sinButton.setTextSize(21);
            cosButton.setText("arccos");
            cosButton.setTextSize(21);
            tanButton.setText("arctan");
            tanButton.setTextSize(21);
        }
        shiftButton.setChecked(shift);
        updateInput();
    }
    */

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

    //Functions for the Main View

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
        Token t = MatrixFunctionFactory.makeREF();
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
     * When the user presses the rref button
     */
    public void clickRREF() {
        Token t = MatrixFunctionFactory.makeRREF();
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
        DisplayView display = (DisplayView) activity.findViewById(R.id.display);
        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.condenseDigits(tokens));
            temp = MatrixUtils.convertToReversePolish(temp);
            Token t = MatrixUtils.evaluateExpression(temp);
            ArrayList<Token> output = new ArrayList<>();
            output.add(t);
            display.displayOutput(output);
            saveEquation(tokens, output, filename);
        } catch (Exception e) { //an error was thrown
            super.handleExceptions(e);
        }
        activity.scrollDown();
    }


    /**
     * When the user presses the LUP button
     */
    public void clickLUP() {
        Token t = MatrixFunctionFactory.makeLU();
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

//  public void clickLUP() {
//        final Context context = activity;
//        AsyncTask<ArrayList<Token>, Void, ArrayList<Token>> task;
//        task = new AsyncTask<ArrayList<Token>, Void, ArrayList<Token>>() {
//
//            private Exception error;
//
//            @Override
//            protected void onPreExecute() {
//                if (pd == null) { //Lazy Initialization
//                    //Loading dialog
//                    pd = new ProgressDialog(activity);
//                    pd.setTitle("Calculating...");
//                    pd.setMessage("This may take a while. ");
//                    pd.setCancelable(false);
//                }
//                pd.show();
//                super.onPreExecute();
//            }
//
//            @Override
//            protected ArrayList<Token> doInBackground(ArrayList<Token>... params) {
//                try {
//                    Token t = MatrixUtils.evaluateExpression(MatrixUtils.convertToReversePolish(MatrixUtils.setupExpression(params[0])));
//                    if (!(t instanceof Matrix)) {
//                        Toast.makeText(context, "Input is not a Matrix", Toast.LENGTH_LONG).show();
//                        return null;
//                    } else {
//                        Matrix m = (Matrix) t;
//                        Matrix l = MatrixUtils.getLowerTriangularMatrix(m);
//                        Matrix u = MatrixUtils.getUpperTriangularMatrix(m);
//                        Matrix p = MatrixUtils.getPermutationMatrix(m);
//                        ArrayList<Token> matrices = new ArrayList<Token>(3);
//                        matrices.add(l);
//                        matrices.add(u);
//                        matrices.add(p);
//                        return matrices;
//                    }
//                } catch (Exception e) {
//                    error = e;
//                    return null;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(ArrayList<Token> matrices) {
//                pd.dismiss();
//
//                if (matrices == null) {
//                    if (error == null) {
//                        showMalformedExpressionToast();
//                    } else if (error instanceof UnsupportedOperationException) {
//                        Toast.makeText(context, "Sorry, we were unable to LUP factorize this matrix. LUP factorization for this matrix may not be supported yet.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(context, "Something weird happened in our system, and we can't LUP factorize this matrix. We'll try to fix this as soon as we can. Sorry! :( ".concat(error.getMessage()), Toast.LENGTH_LONG).show();
//                    }//TODO: REMOVE ERROR MSG BEFORE RELEASE
//                } else {
//                    ArrayList<Token> toOutput = new ArrayList<>();
//                    int counter = 0;
//                    while (counter < 3) {
//                        Token m = matrices.get(counter);
//                        if (counter == 0) {
//                            toOutput.add(new StringToken("L = "));
//                        } else if (counter == 1) {
//                            toOutput.add(new StringToken("U = "));
//                        } else if (counter == 2) {
//                            toOutput.add(new StringToken("P = "));
//                        }
//                        toOutput.add(m);
//                        counter++;
//                    }
////                    if (counter == 0) { //No eigenvalues
////                        toOutput.add(new StringToken("No Real Eigenvalues "));
////                    }
//                    display.displayOutput(toOutput);
//                    activity.scrollDown();
//                    //Saves to history
//                    try {
//                        ArrayList<Token> saveInput = new ArrayList<>();
//                        saveInput.addAll(tokens);
//                        saveInput.add(0, new StringToken("LUP factorized form of "));
//                        saveEquation(saveInput, toOutput, filename);
//                    } catch (IOException | ClassNotFoundException e) {
//                        Toast.makeText(activity, "Error saving to history", Toast.LENGTH_LONG).show();
//                    }
//                    super.onPostExecute(matrices);
//                }
//            }
//        };
//
//        if (tokens.size() == 0) { //No tokens
//            Toast.makeText(activity, "There is no matrix. You would need to enter an matrix first, then press the decomposition button.", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        ArrayList<Token> tokens = Utility.condenseDigits(this.tokens);
//        tokens = Utility.setupExpression(tokens);
//        task.execute(tokens);
//  }


    /**
     * When the user presses the λ button
     */
    public void clickLambda() {
//        final Context context = activity;
//        AsyncTask<ArrayList<Token>, Void, ArrayList<Token>> task;
//        task = new AsyncTask<ArrayList<Token>, Void, ArrayList<Token>>() {
//
//            private Exception error;
//
//            @Override
//            protected void onPreExecute() {
//                if (pd == null) { //Lazy Initialization
//                    //Loading dialog
//                    pd = new ProgressDialog(activity);
//                    pd.setTitle("Calculating...");
//                    pd.setMessage("This may take a while. ");
//                    pd.setCancelable(false);
//                }
//                pd.show();
//                super.onPreExecute();
//            }
//
//            @Override
//            protected ArrayList<Token> doInBackground(ArrayList<Token>... params) {
//                try {
//                    Token t = MatrixUtils.evaluateExpression(MatrixUtils.convertToReversePolish(MatrixUtils.setupExpression(params[0])));
//                    if (!(t instanceof Matrix)) {
//                        Toast.makeText(context, "Input is not a Matrix", Toast.LENGTH_LONG).show();
//                        return null;
//                    } else {
//                        Matrix m = (Matrix) t;
//                        double[] ev = MatrixUtils.getRealEigenValues(m);
//                        ArrayList<Token> evs = new ArrayList<Token>(ev.length);
//                        for (int i = 0; i < ev.length; i++) {
//                            evs.add(i, new Number(ev[i]));
//                        }
//                        return evs;
//                    }
//                } catch (Exception e) {
//                    error = e;
//                    return null;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(ArrayList<Token> eigenvals) {
//                pd.dismiss();
//
//                if (eigenvals == null) {
//                    if (error == null) {
//                        showMalformedExpressionToast();
//                    } else if (error instanceof UnsupportedOperationException) {
//                        Toast.makeText(context, "Sorry, we were unable to find the eigenvalue(s) of this matrix. Eigenvalue finding for this matrix may not be supported yet.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(context, "Something weird happened in our system, and we can't find the eigenvalues. We'll try to fix this as soon as we can. Sorry! :(".concat(error.getMessage()), Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    ArrayList<Token> toOutput = new ArrayList<>();
//                    int counter = 0;
//                    toOutput.add(new StringToken("λ = "));
//                    while (counter < eigenvals.size()) {
//                        Token eigenvalue = eigenvals.get(counter);
//                        if (counter != 0) {
//                            toOutput.add(new StringToken(", "));
//                        }
//                        toOutput.add(eigenvalue);
//                        counter++;
//                    }
//                    if (counter == 0) { //No roots
//                        toOutput.add(new StringToken("No Real Eigenvalues"));
//                    }
//                    display.displayOutput(toOutput);
//                    activity.scrollDown();
//                    //Saves to history
//                    try {
//                        ArrayList<Token> saveInput = new ArrayList<>();
//                        saveInput.addAll(tokens);
//                        saveInput.add(0, new StringToken("Eigenvalues of "));
//                        saveEquation(saveInput, toOutput, filename);
//                    } catch (IOException | ClassNotFoundException e) {
//                        Toast.makeText(activity, "Error saving to history", Toast.LENGTH_LONG).show();
//                    }
//                    super.onPostExecute(eigenvals);
//                }
//            }
//        };
//
//        if (tokens.size() == 0) { //No tokens
//            Toast.makeText(activity, "There is no matrix. You would need to enter an matrix first, then press the λ button.", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        ArrayList<Token> tokens = Utility.condenseDigits(this.tokens);
//        tokens = Utility.setupExpression(tokens);
//        task.execute(tokens);
    }

    /**
     * When the user presses the eigenvect button
     */
    public void clickEigenVect() {
//        final Context context = activity;
//        AsyncTask<ArrayList<Token>, Void, ArrayList<Token>> task;
//        task = new AsyncTask<ArrayList<Token>, Void, ArrayList<Token>>() {
//
//            private Exception error;
//
//            @Override
//            protected void onPreExecute() {
//                if (pd == null) { //Lazy Initialization
//                    //Loading dialog
//                    pd = new ProgressDialog(activity);
//                    pd.setTitle("Calculating...");
//                    pd.setMessage("This may take a while. ");
//                    pd.setCancelable(false);
//                }
//                pd.show();
//                super.onPreExecute();
//            }
//
//            @SafeVarargs
//            @Override
//            protected final ArrayList<Token> doInBackground(ArrayList<Token>... params) {
//                try {
//                    Token t = MatrixUtils.evaluateExpression(MatrixUtils.convertToReversePolish(MatrixUtils.setupExpression(params[0])));
//                    if (!(t instanceof Matrix)) {
//                        Toast.makeText(context, "Input is not a Matrix", Toast.LENGTH_LONG).show();
//                        return null;
//                    } else {
//                        Matrix m = (Matrix) t;
//                        Matrix[] evects = MatrixUtils.getEigenVectors(m);
//                        ArrayList<Token> vectors = new ArrayList<Token>(evects.length);
//                        for (int i = 0; i < evects.length; i++) {
//                            vectors.add(i, evects[i]);
//                        }
//                        return vectors;
//                    }
//                } catch (Exception e) {
//                    error = e;
//                    return null;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(ArrayList<Token> vectors) {
//                pd.dismiss();
//
//                if (vectors == null) {
//                    if (error == null) {
//                        showMalformedExpressionToast();
//                    } else if (error instanceof UnsupportedOperationException) {
//                        Toast.makeText(context, "Sorry, we were unable to find the eigenvector(s) of this matrix. Eigenvector finding for this matrix may not be supported yet.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(context, "Something weird happened in our system, and we can't find the eigenvectors. We'll try to fix this as soon as we can. Sorry! :(".concat(error.getMessage()), Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    ArrayList<Token> toOutput = new ArrayList<>();
//                    int counter = 0;
//                    toOutput.add(new StringToken("Eigenvectors: "));
//                    while (counter < vectors.size()) {
//                        Token eigenvalue = vectors.get(counter);
//                        if (counter != 0) {
//                            toOutput.add(new StringToken(", "));
//                        }
//                        toOutput.add(eigenvalue);
//                        counter++;
//                    }
//                    if (counter == 0) { //No eigenvectors
//                        toOutput.add(new StringToken("No eigenvectors were found"));
//                    }
//                    display.displayOutput(toOutput);
//                    activity.scrollDown();
//                    //Saves to history
//                    try {
//                        ArrayList<Token> saveInput = new ArrayList<>();
//                        saveInput.addAll(tokens);
//                        //saveInput.add(0, new StringToken("Eigenvectors of "));
//                        saveEquation(saveInput, toOutput, filename);
//                    } catch (IOException | ClassNotFoundException e) {
//                        Toast.makeText(activity, "Error saving to history", Toast.LENGTH_LONG).show();
//                    }
//                    super.onPostExecute(vectors);
//                }
//            }
//        };
//
//        if (tokens.size() == 0) { //No tokens
//            Toast.makeText(activity, "There is no matrix. You would need to enter an matrix first, then press the eigenvector button.", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        ArrayList<Token> tokens = Utility.condenseDigits(this.tokens);
//        tokens = Utility.setupExpression(tokens);
//        task.execute(tokens);
    }


    /**
     * When the user presses the diag() button
     */
    public void clickDiagonalize() {
        Token t = MatrixFunctionFactory.makeDiag();
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
//  public void clickDiagonalize() {
//        final Context context = activity;
//        AsyncTask<ArrayList<Token>, Void, ArrayList<Token>> task;
//        task = new AsyncTask<ArrayList<Token>, Void, ArrayList<Token>>() {
//
//            private Exception error;
//
//            @Override
//            protected void onPreExecute() {
//                if (pd == null) { //Lazy Initialization
//                    //Loading dialog
//                    pd = new ProgressDialog(activity);
//                    pd.setTitle("Calculating...");
//                    pd.setMessage("This may take a while. ");
//                    pd.setCancelable(false);
//                }
//                pd.show();
//                super.onPreExecute();
//            }
//
//            @Override
//            protected ArrayList<Token> doInBackground(ArrayList<Token>... params) {
//                try {
//                    Token t = MatrixUtils.evaluateExpression(MatrixUtils.convertToReversePolish(MatrixUtils.setupExpression(params[0])));
//                    if (!(t instanceof Matrix)) {
//                        Toast.makeText(context, "Input is not a Matrix", Toast.LENGTH_LONG).show();
//                        return null;
//                    } else {
//                        Matrix m = (Matrix) t;
//                        Matrix d = MatrixUtils.getDiagonalMatrix(m);
//                        Matrix p = MatrixUtils.getDiagonalizingMatrix(m);
//                        Matrix p_inverse = MatrixUtils.findInverse(p);
//                        ArrayList<Token> matrices = new ArrayList<Token>(3);
//                        matrices.add(p);
//                        matrices.add(d);
//                        matrices.add(p_inverse);
//                        return matrices;
//                    }
//                } catch (Exception e) {
//                    error = e;
//                    return null;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(ArrayList<Token> matrices) {
//                pd.dismiss();
//
//                if (matrices == null) {
//                    if (error == null) {
//                        showMalformedExpressionToast();
//                    } else if (error instanceof UnsupportedOperationException) {
//                        Toast.makeText(context, "Sorry, we were unable to diagionalize this matrix. Diagonalization for this matrix may not be supported yet.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(context, "Something weird happened in our system, and we can't diagonalize this matrix. We'll try to fix this as soon as we can. Sorry! :( ".concat(error.getMessage()), Toast.LENGTH_LONG).show();
//                    }//TODO: REMOVE ERROR MSG BEFORE RELEASE
//                } else {
//                    ArrayList<Token> toOutput = new ArrayList<>();
//                    int counter = 0;
//                    while (counter < 3) {
//                        Token m = matrices.get(counter);
//                        if (counter == 0) {
//                            toOutput.add(new StringToken("P = "));
//                        } else if (counter == 1) {
//                            toOutput.add(new StringToken("D = "));
//                        } else if (counter == 2) {
//                            toOutput.add(new StringToken("P^-1 = "));
//                        }
//                        toOutput.add(m);
//                        counter++;
//                    }
////                    if (counter == 0) { //No eigenvalues
////                        toOutput.add(new StringToken("No Real Eigenvalues "));
////                    }
//                    display.displayOutput(toOutput);
//                    activity.scrollDown();
//                    //Saves to history
//                    try {
//                        ArrayList<Token> saveInput = new ArrayList<>();
//                        saveInput.addAll(tokens);
//                        saveInput.add(0, new StringToken("Diagonalized form of "));
//                        saveEquation(saveInput, toOutput, filename);
//                    } catch (IOException | ClassNotFoundException e) {
//                        Toast.makeText(activity, "Error saving to history", Toast.LENGTH_LONG).show();
//                    }
//                    super.onPostExecute(matrices);
//                }
//            }
//        };
//
//        if (tokens.size() == 0) { //No tokens
//            Toast.makeText(activity, "There is no matrix. You would need to enter an matrix first, then press the diagonalize button.", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        ArrayList<Token> tokens = Utility.condenseDigits(this.tokens);
//        tokens = Utility.setupExpression(tokens);
//        task.execute(tokens);
//  }

}