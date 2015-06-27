package com.trutechinnovations.calculall;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
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
    private static final Basic INSTANCE = new MatrixMode();
    protected PopupWindow reductionWindow;
    protected PopupWindow decompWindow;
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
            case R.id.divide_button:
                clickDivide();
                break;
            case R.id.frac_mode:
                clickFracMode();
                break;
            case R.id.exit_reduction_button:
                clickExitReduction();
                break;
            case R.id.exit_decomp_button:
                clickExitDecomp();
                break;
            default:
                super.onClick(v);
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
        updateInput();
        clickEquals();
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
        openReduction(false);
    }

    /**
     * When the user presses the rref button
     */
    public void clickRREF() {
        openReduction(true);
//        Token t = MatrixFunctionFactory.makeRREF();
//        Bracket b = BracketFactory.makeOpenBracket();
//        if (t != null) {
//            t.addDependency(b);
//            b.addDependency(t);
//        }
//        tokens.add(display.getRealCursorIndex(), t);
//        tokens.add(display.getRealCursorIndex() + 1, b);
//        display.setCursorIndex(display.getCursorIndex() + 2);
//        updateInput();
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
        DisplayView display = (DisplayView) activity.findViewById(R.id.display);
        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.subVariables(Utility.condenseDigits(tokens)));
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
        DisplayView display = (DisplayView) activity.findViewById(R.id.display);
        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.subVariables(Utility.condenseDigits(tokens)));
            temp = MatrixUtils.convertToReversePolish(temp);
            Token t = MatrixUtils.evaluateExpression(temp, false);
            if (t instanceof Matrix) {
                double[] eigenValues = MathUtilities.getEigenValues(MatrixUtils.evaluateMatrixEntries((Matrix) t));

                String outputStr = "Eigen Values: ";
                boolean first = true;
                for (double eigVal : eigenValues) {
                    if (!first) {
                        outputStr += " , ";
                    }
                    outputStr += new Number(eigVal).getSymbol();
                    first = false;
                }

                ArrayList<Token> input = new ArrayList<>();
                input.addAll(tokens);
                input.add(0, new StringToken("Eigen Values of "));

                ArrayList<Token> output = new ArrayList<>();
                output.add(new StringToken(outputStr));

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
        DisplayView display = (DisplayView) activity.findViewById(R.id.display);
        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.subVariables(Utility.condenseDigits(tokens)));
            temp = MatrixUtils.convertToReversePolish(temp);
            Token t = MatrixUtils.evaluateExpression(temp, false);
            if (t instanceof Matrix) {
                ArrayList<Vector> ev = MatrixUtils.getEigenVectors(MatrixUtils.evaluateMatrixEntries((Matrix) t));

                String outputStr = "Eigenspace Basis: ";
                boolean first = true;
                for (Vector v : ev) {
                    if (!first) {
                        outputStr += " , ";
                    }
                    outputStr += v.getSymbol();
                    first = false;
                }

                ArrayList<Token> input = new ArrayList<>();
                input.addAll(tokens);
                input.add(0, new StringToken("Eigenspace Basis of "));

                ArrayList<Token> output = new ArrayList<>();
                output.add(new StringToken(outputStr));

                display.displayOutput(output);

                saveEquation(input, output, filename);
                activity.scrollDown();
            } else {
                throw new IllegalArgumentException("The result must be a single Matrix to find the Eigen Vectors");
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
        //Inflates the XML file so you get the View to add to the PopupWindow
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.reduction_view, null, false);

        //Creates the popupWindow, with the width matching the parent's and height matching the parent's
        reductionWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        //Retrieves the user data from saved memory

        ArrayList<Token[]> steps = new ArrayList<>();
        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.condenseDigits(tokens));
            temp = MatrixUtils.convertToReversePolish(temp);
            Token t = MatrixUtils.evaluateExpression(temp, false);
            if (t instanceof Matrix) {
                double[][] a = ((Matrix) t).getEntriesAsDbls();
                if (rref) {
                    steps = MatrixUtils.knitSteps(a, MatrixUtils.getRREFSteps(a));
                } else {
                    steps = MatrixUtils.knitSteps(a, MatrixUtils.getREFSteps(a));
                }
                if (steps.size() == 0) {
                    Token[] temp1 = new Token[2];
                    temp1[0] = new StringToken("No Steps to show");
                    temp1[1] = new StringToken("");
                    steps.add(temp1);
                }
            } else {
                throw new IllegalArgumentException("The result must be a single Matrix to be row reducible");
            }
        } catch (Exception e) { //an error was thrown
            super.handleExceptions(e);
        }


        //Finds the ListView from the inflated consts XML so it could be manipulated
        ListView lv = (ListView) layout.findViewById(R.id.reductionList);

        //Attaches the custom Adapter to the ListView so that it can configure the items and their Views within it
        lv.setAdapter(new ReductionAdapter(steps, activity));

        //Displays the created PopupWindow on top of the LinearLayout with ID frame, which is being shown by the Activity
        reductionWindow.showAtLocation(activity.findViewById(R.id.frame), Gravity.CENTER, 0, 0);
    }

    /**
     * Exits the reduction view.
     */
    public void clickExitReduction() {
        reductionWindow.dismiss();
    }

    /**
     * Opens the matrix decompositions window
     */
    public void openDecomp() {
        //Inflates the XML file so you get the View to add to the PopupWindow
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.decomposition_view, null, false);

        //Creates the popupWindow, with the width matching the parent's and height matching the parent's
        decompWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        //Retrieves the user data from saved memory

        ArrayList<Token[]> decomps = new ArrayList<>();
        try {
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.condenseDigits(tokens));
            temp = MatrixUtils.convertToReversePolish(temp);
            Token t = MatrixUtils.evaluateExpression(temp, false);
            if (t instanceof Matrix) {
                double[][] a = MatrixUtils.evaluateMatrixEntries((Matrix) t);
                String[][] decompositions = {
                        {"LUP factorization",
                                "Finds matrices <b>P</b>, <b>L</b> and <b>U</b> such that" +
                                        "<div><b>PA = LU</b></div>" +
                                        "where <b>L</b> and <b>U</b> are lower and upper triangular, respectively, and <b>P</b> is the pivot matrix.",
                                "LUP"},
                        {"Diagonalization",
                                "Finds matrices <b>P</b> and <b>D</b> such that" +
                                        "<div><b>A = PDP</b><sup>-1</sup></div>" +
                                        "where <b>D</b> is a diagonal matrix with the eigenvalues of <b>A</b> as its diagonal entries, " +
                                        "and <b>P</b> is a matrix with the eigenvectors of <b>A</b> as its columns.",
                                "DIAG"},
                        {"QR decomposition",
                                "Finds matrices <b>Q</b> and <b>R</b> such that" +
                                        "<div><b>A = QR</b></div>" +
                                        "where <b>Q</b> is orthogonal, <b>R</b> is upper triangular.",
                                "QR"},
                        {"Rank Revealing QR decomposition",
                                "Finds matrices <b>P</b>, <b>Q</b> and <b>R</b> such that" +
                                        "<div><b>AP = QR</b></div>" +
                                        "where <b>Q</b> is orthogonal, <b>R</b> is upper triangular, and <b>P</b> is the pivot matrix.",
                                "RRQR"},
                        {"Cholesky decomposition",
                                "Finds a matrix <b>L</b> such that" +
                                        "<div><b>A = LL</b><sup>T</sup></div>" +
                                        "where <b>L</b> is lower triangular.",
                                "Cholesky"},
                        {"Singular value decomposition",
                                "Finds matrices <b>U</b>, <b>Σ</b> and <b>V</b> such that" +
                                        "<div><b>A = UΣV</b><sup>T</sup></div>" +
                                        "where <b>U</b> and <b>V</b> are orthogonal and <b>Σ</b> is a " +
                                        "diagonal matrix with the singular values of <b>A</b> as its diagonal entries.",
                                "SVD"}
                };

                for (int i = 0; i < decompositions.length; i++) {
                    Token[] tempToken = new Token[3];
                    if (i == 0) {
                        if (((Matrix) t).isSquare() && MatrixUtils.findDeterminant(a) != 0) {//LUP
                            for (int j = 0; j < tempToken.length; j++) {
                                tempToken[j] = new StringToken(decompositions[i][j]);
                            }
                            decomps.add(tempToken);
                        }
                    } else if (i == 1) {
                        if (((Matrix) t).isSquare()) {//Diagonalization
                            for (int j = 0; j < tempToken.length; j++) {
                                tempToken[j] = new StringToken(decompositions[i][j]);
                            }
                            decomps.add(tempToken);
                        }
                    } else if (i == 4) { //Cholesky
                        if (((Matrix) t).isSymmetric() && MatrixUtils.findDeterminant(a) != 0) {
                            for (int j = 0; j < tempToken.length; j++) {
                                tempToken[j] = new StringToken(decompositions[i][j]);
                            }
                            decomps.add(tempToken);
                        }
                    } else {//QR, RRQR, SVD
                        for (int j = 0; j < tempToken.length; j++) {
                            tempToken[j] = new StringToken(decompositions[i][j]);
                        }
                        decomps.add(tempToken);
                    }
                }

                if (decomps.size() == 0) {
                    Token[] tempToken = new Token[3];
                    tempToken[0] = new StringToken("No decompositions available for this matrix");
                    tempToken[1] = new StringToken("");
                    tempToken[2] = new StringToken("");
                    decomps.add(tempToken);
                }
            } else {
                throw new IllegalArgumentException("The result must be a single Matrix to be factorizable");
            }
        } catch (Exception e) { //an error was thrown
            super.handleExceptions(e);
        }


        //Finds the ListView from the inflated consts XML so it could be manipulated
        ListView lv = (ListView) layout.findViewById(R.id.decompList);

        //Attaches the custom Adapter to the ListView so that it can configure the items and their Views within it
        lv.setAdapter(new DecompAdapter(decomps, activity));

        //Displays the created PopupWindow on top of the LinearLayout with ID frame, which is being shown by the Activity
        decompWindow.showAtLocation(activity.findViewById(R.id.frame), Gravity.CENTER, 0, 0);
    }

    /**
     * Exits the decomp view.
     */
    public void clickExitDecomp() {
        decompWindow.dismiss();
    }

    /**
     * The custom Adapter for the ListView in the row reduction steps.
     */
    private class ReductionAdapter extends BaseAdapter {

        private MainActivity activity;
        private ArrayList<Token[]> reduction; //The data that will be shown in the ListView

        public ReductionAdapter(ArrayList<Token[]> reduction, MainActivity activity) {
            this.reduction = reduction;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return reduction.size();
        }

        @Override
        public Object getItem(int position) {
            return reduction.get(position);
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
                convertView = inflater.inflate(R.layout.reduction_element, parent, false);
            }

            //Sets up the child Views within each item in the ListView
            OutputView intermediate = (OutputView) convertView.findViewById(R.id.input);
            OutputView step = (OutputView) convertView.findViewById(R.id.step);

            //Sets the font size of each OutputView
            intermediate.setFontSize(activity.getFontSize());
            step.setFontSize((int) (activity.getFontSize() * CONSTANTS_IO_RATIO));

            //Enters the appropriate expressions to the OutputView
            Token[] entry = reduction.get(position);
            ArrayList<Token> temp = new ArrayList<>();
            temp.add(entry[0]);
            intermediate.display(temp);
            temp.clear();
            temp.add(entry[1]);
            step.display(temp);
            temp.clear();

            //To respond to user touches
            final Token INPUT = reduction.get(position)[0]; //Makes a constant reference so that intermediate matrices can be accessed by an inner class
            convertView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        ArrayList<Token> input = new ArrayList<>();
                        //Removes any StringTokens
                        if (!(INPUT instanceof StringToken)) {
                            input.add(INPUT);
                        }
                        //Adds the input expression to the current tokens
                        tokens.addAll(input); //Adds the input of the entry
                        reductionWindow.dismiss(); //Exits reductionWindow once a matrix has been selected
                        updateInput();
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            return convertView;
        }
    }

    /**
     * The custom Adapter for the ListView in the row reduction steps.
     */
    private class DecompAdapter extends BaseAdapter {

        private MainActivity activity;
        private ArrayList<Token[]> decompositions; //The data that will be shown in the ListView

        public DecompAdapter(ArrayList<Token[]> decompositions, MainActivity activity) {
            this.decompositions = decompositions;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return decompositions.size();
        }

        @Override
        public Object getItem(int position) {
            return decompositions.get(position);
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
                //Inflates the XML file to get the View of the decomposition element
                LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.decomposition_element, parent, false);
            }

            //Sets up the child Views within each item in the ListView
            TextView type = (TextView) convertView.findViewById(R.id.type);
            TextView info = (TextView) convertView.findViewById(R.id.info);

            //Sets the font size of each OutputView
            /*
            type.setFontSize((int) (activity.getFontSize() * CONSTANTS_IO_RATIO));
            info.setFontSize((int) (activity.getFontSize() * CONSTANTS_IO_RATIO * CONSTANTS_IO_RATIO));
            */

            //Enters the appropriate expressions to the TextViews
            Token[] entry = decompositions.get(position);
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = activity.getTheme();
            theme.resolveAttribute(R.attr.displayTextColor, typedValue, true);
            type.setText(Html.fromHtml(entry[0].getSymbol()));
            type.setTextColor(typedValue.data);
            info.setText(Html.fromHtml(entry[1].getSymbol()));
            info.setTextColor(typedValue.data);

            //To respond to user touches
            final Token INPUT = decompositions.get(position)[2]; //Makes a constant reference so that intermediate matrices can be accessed by an inner class
            convertView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        decompWindow.dismiss(); //Exits decompWindow once a matrix decomposition has been selected
                        if (INPUT instanceof StringToken) {
                            String s = INPUT.getSymbol();
                            if (s.equals("LUP")) {
                                clickLUP();
                            } else if (s.equals("DIAG")) {
                                clickDiagonalize();
                            } else if (s.equals("QR")) {
                                clickQR();
                            } else if (s.equals("RRQR")) {
                                clickRRQR();
                            } else if (s.equals("Cholesky")) {
                                clickCholesky();
                            } else if (s.equals("SVD")) {
                                clickSVD();
                            }
                        }
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