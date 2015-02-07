package com.trutech.calculall;

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
 * @version Alpha 2.0
 * @author Alston Lin, Ejaaz Merali
 */
public class MatrixMode extends Advanced {
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
     * @return The singleton instance
     */
    public static Basic getInstance(){
        return INSTANCE;
    }

    /**
     * When a Button has been clicked, calls the appropriate method.
     *
     * @param v The Button that has been clicked
     */
    @Override
    public void onClick(View v){
        switch(v.getId()){
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
                clickPower();
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
            case R.id.minus_button:
                clickSubtract();
                break;
            case R.id.multiply_button:
                clickMultiply();
                break;
            case R.id.augment_button:
                clickAugment();
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
            default:
                super.onClick(v);
        }
    }

    public void clickNum(){
        //Replaces the current fragment with the matrix_num (for now)
        ViewGroup v = (ViewGroup) fragment.getView();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View numView = inflater.inflate(R.layout.matrix_num, null, false);
        v.removeAllViews();
        v.addView(numView);
    }

    public void clickDoneNum(){
        ViewGroup v = (ViewGroup) fragment.getView();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View matrixView = inflater.inflate(R.layout.matrix, null, false);
        v.removeAllViews();
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

    public void clickMem(){
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
        for (MultiButton b : multiButtons){
            b.changeMode(shift, hyperbolic);
        }
        updateInput();
    }

    /**
     * When the user presses the New button.
     */
    public void clickNew() {
        //Creates a new empty matrix and edits it
        ArrayList<Token>[][] entries = new ArrayList[DEFAULT_ROWS][DEFAULT_COLS];
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.element, strs);
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

        ArrayAdapter<Integer> spinnerAdapterX = new ArrayAdapter<Integer>(fragment.getActivity(), R.layout.spinner_item, items);
        ArrayAdapter<Integer> spinnerAdapterY = new ArrayAdapter<Integer>(fragment.getActivity(), R.layout.spinner_item, items);
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
        display = (DisplayView) layout.findViewById(R.id.display);
        display.displayInput(tokens);
        display.setOutput((OutputView) layout.findViewById(R.id.output));
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

        sinCommands[0] = new Command<Void, Void>(){
            @Override
            public Void execute(Void o) {
                clickSin();
                return null;
            }
        };
        sinCommands[1] = new Command<Void, Void>(){
            @Override
            public Void execute(Void o) {
                clickASin();
                return null;
            }
        };

        cosCommands[0] = new Command<Void, Void>(){
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

        tanCommands[0] = new Command<Void, Void>(){
            @Override
            public Void execute(Void o) {
                clickTan();
                return null;
            }
        };
        tanCommands[1] = new Command<Void, Void>(){
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
        }
        //Stores the tokens into the Matrix
        matrix.setEntry(x, y, tokens);
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
        tokens.add(display.getRealCursorIndex(), MatrixFunctionFactory.makeDeterminant());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the tr(A) button.
     */
    public void clickTrace() {
        tokens.add(display.getRealCursorIndex(), MatrixFunctionFactory.makeTrace());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the A^-1 button.
     */
    public void clickInverse() {
        tokens.add(display.getRealCursorIndex(), MatrixFunctionFactory.makeInverse());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the A^T button.
     */
    public void clickTranspose() {
        tokens.add(display.getRealCursorIndex(), MatrixFunctionFactory.makeTranspose());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the diag() button
     */
    public void clickDiagonalize() {
        Matrix m;
        Matrix[] matrices = null;
        try {
            m = Utility.evaluateMatrixExpression(tokens);
            matrices = ((Matrix.AugmentedMatrix) MatrixFunctionFactory.makeDiagonalize().perform(m)).getMatrices();
        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        tokens.clear();
        display.setCursorIndex(0);
        for (int i = 0; i < matrices.length; i++) {
            tokens.add(display.getRealCursorIndex(), matrices[i]);
            display.setCursorIndex(display.getCursorIndex() + 1);
        }
        updateInput();
    }

    /**
     * When the user presses the λ button
     */
    public void clickLambda() {
        Matrix m;
        Matrix eigenvals = null;
        try {
            m = Utility.evaluateMatrixExpression(tokens);
            eigenvals = MatrixFunctionFactory.makeEigenVal().perform(m);
        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        tokens.clear();
        display.setCursorIndex(0);
        tokens.add(display.getRealCursorIndex(), eigenvals);
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the eigenvect button
     */
    public void clickEigenVect() {
        Matrix m;
        Matrix[] matrices = null;
        try {
            m = Utility.evaluateMatrixExpression(tokens);
            matrices = ((Matrix.AugmentedMatrix) MatrixFunctionFactory.makeEigenVectors().perform(m)).getMatrices();
        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        tokens.clear();
        display.setCursorIndex(0);
        for (int i = 0; i < matrices.length; i++) {
            tokens.add(display.getRealCursorIndex(), matrices[i]);
            display.setCursorIndex(display.getCursorIndex() + 1);
        }
        updateInput();
    }

    /**
     * When the user presses the LUP button
     */
    public void clickLUP() {
        Matrix m;
        Matrix[] matrices = null;
        try {
            m = Utility.evaluateMatrixExpression(tokens);
            matrices = ((Matrix.AugmentedMatrix) MatrixFunctionFactory.makeLUP().perform(m)).getMatrices();
        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        tokens.clear();
        display.setCursorIndex(0);
        for (int i = 0; i < matrices.length; i++) {
            tokens.add(display.getRealCursorIndex(), matrices[i]);
            display.setCursorIndex(display.getCursorIndex() + 1);
        }
        updateInput();
    }

    /**
     * When the user presses the ref button
     */
    public void clickREF() {
        tokens.add(display.getRealCursorIndex(), MatrixFunctionFactory.makeREF());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the rref button
     */
    public void clickRREF() {
        tokens.add(display.getRealCursorIndex(), MatrixFunctionFactory.makeRREF());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the ref button
     */
    public void clickRank() {
        tokens.add(display.getRealCursorIndex(), MatrixFunctionFactory.makeRank());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the sqrt button
     */
    public void clickSqrt() {
        tokens.add(display.getRealCursorIndex(), MatrixFunctionFactory.makeSqrt());
        display.setCursorIndex(display.getRealCursorIndex() + 1);
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
     * When the user presses the ^ button
     */
    public void clickExponent() {
        tokens.add(display.getRealCursorIndex(), MatrixOperatorFactory.makeMatrixExponent());
        display.setCursorIndex(display.getRealCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the [A|B] button
     */
    public void clickAugment() {
        tokens.add(display.getRealCursorIndex(), MatrixOperatorFactory.makeAugment());
        display.setCursorIndex(display.getRealCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the A^y button
     */
    public void clickPower() {
        tokens.add(display.getRealCursorIndex(), MatrixOperatorFactory.makeAugment());
        display.setCursorIndex(display.getRealCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the equals Button.
     */
    public void clickEquals() {
        DisplayView display = (DisplayView) activity.findViewById(R.id.display);
        try {
            Matrix m = Utility.evaluateMatrixExpression(Utility.convertToReversePolishMatrix(Utility.setupExpression(tokens)));
            tokens.clear();
            display.setCursorIndex(0);
            tokens.add(display.getRealCursorIndex(), m);
            display.setCursorIndex(display.getCursorIndex() + 1);
        } catch (Exception e) { //an error was thrown
            Toast.makeText(activity, "ERROR", Toast.LENGTH_LONG).show();
        }
        activity.scrollDown();
    }
}
