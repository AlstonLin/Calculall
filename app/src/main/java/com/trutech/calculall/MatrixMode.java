package com.trutech.calculall;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Created by Ejaaz on 1/1/2015.
 */
public class MatrixMode extends Advanced {
    public static final int DEFAULT_ROWS = 3;
    public static final int DEFAULT_COLS = 3;
    public static final int MAX_DIMENSIONS = 7;

    //Variables used only when in ElementView
    private ArrayList<Token> storedTokens; //Used to store Tokens when switched to the ElementView
    private Matrix matrix;
    private int x, y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix);
        setupButtons();
    }

    /**
     * Programmaticly sets the texts that can't be defined with XML.
     */
    private void setupButtons() {
        Button transButton = (Button) findViewById(R.id.transposeButton);
        Button powButton = (Button) findViewById(R.id.powButton);
        Button inverseButton = (Button) findViewById(R.id.inverseButton);
        transButton.setText(Html.fromHtml(getString(R.string.transpose)));
        powButton.setText(Html.fromHtml(getString(R.string.matrix_pow)));
        inverseButton.setText(Html.fromHtml(getString(R.string.inverse_a)));
    }

    /**
     * When the user presses the New button.
     *
     * @param v Not Used
     */
    public void clickNew(View v) {
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
     *
     * @param v Not Used
     */
    public void clickEdit(View v) {
        if (tokens.size() != 0) {
            Token m = tokens.get(display.getRealCursorIndex());
            if (m instanceof Matrix) {
                editMatrix((Matrix) m);
                updateInput();
                return;
            }
        }
        Toast.makeText(this, "Place the cursor before a Matrix to edit it.", Toast.LENGTH_LONG).show();
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
        setContentView(R.layout.elements_layout);
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
        GridView gv = (GridView) findViewById(R.id.elements_grid);
        gv.setNumColumns(m.getNumOfCols());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.element, strs);
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
        Spinner xSpinner = (Spinner) findViewById(R.id.xSpinner);
        Spinner ySpinner = (Spinner) findViewById(R.id.ySpinner);
        Integer[] items = new Integer[MAX_DIMENSIONS];
        for (int i = 0; i < MAX_DIMENSIONS; i++) {
            items[i] = i + 1;
        }

        ArrayAdapter<Integer> spinnerAdapterX = new ArrayAdapter<Integer>(this, R.layout.spinner_item, items);
        ArrayAdapter<Integer> spinnerAdapterY = new ArrayAdapter<Integer>(this, R.layout.spinner_item, items);
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
    }

    /**
     * Edits an individual entry (Opens the ElementView)
     *
     * @param m      The Matrix to edit
     * @param rowNum The row of the entry
     * @param colNum The column of the entry
     */
    private void editEntry(Matrix m, int rowNum, int colNum) {
        setContentView(R.layout.element_layout);
        //Sets up the tokens already in the element
        storedTokens = tokens;
        tokens = m.getEntry(rowNum, colNum); //Switches pointers (temporarily) to a new set of tokens
        //Clears tokens if the only token is a Number of zero
        if (tokens.size() == 1 && tokens.get(0) instanceof Number && ((Number) tokens.get(0)).getValue() == 0) {
            tokens.clear();
        }
        //Configures the UI for ElementView
        display = (DisplayView) findViewById(R.id.display);
        output = (OutputView) findViewById(R.id.output);
        display.setOutput(output);
        updateInput();
        matrix = m;
        x = rowNum;
        y = colNum;
    }

    //Functions for ElementView

    /**
     * When the user presses the shift button. Switches the state of the boolean variable shift
     *
     * @param v Not Used
     */
    @Override
    public void clickShift(View v) {
        Button sinButton = (Button) findViewById(R.id.sinButton);
        Button cosButton = (Button) findViewById(R.id.cosButton);
        Button tanButton = (Button) findViewById(R.id.tanButton);
        ToggleButton shiftButton = (ToggleButton) findViewById(R.id.shiftButton);

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

    /**
     * When the user presses the x Button.
     *
     * @param v Not Used
     */
    public void clickMultiplyElement(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeMultiply());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the / Button.
     *
     * @param v Not Used
     */
    public void clickDivideElement(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeDivide());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the + Button.
     *
     * @param v Not Used
     */
    public void clickAddElement(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeAdd());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the - Button.
     *
     * @param v Not Used
     */
    public void clickSubtractElement(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeSubtract());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the sqrt Button.
     *
     * @param v Not Used
     */
    public void clickSqrtElement(View v) {
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeSqrt());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    //Functions for the Main View

    /**
     * When the user has finished with ElementView.
     *
     * @param v Not Used
     */
    public void clickDone(View v) {
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
    }

    /**
     * Returns from ElementsView to the main View
     *
     * @param v Not Used
     */
    public void clickReturn(View v) {
        setContentView(R.layout.activity_matrix);
        setupButtons();
        display = (DisplayView) findViewById(R.id.display);
        output = (OutputView) findViewById(R.id.output);
        display.setOutput(output);
        updateInput();
    }

    /**
     * When the user presses the det(A) button.
     *
     * @param v Not Used
     */
    public void clickDeterminant(View v) {
        tokens.add(display.getRealCursorIndex(), MatrixFunctionFactory.makeDeterminant());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the tr(A) button.
     *
     * @param v Not Used
     */
    public void clickTrace(View v) {
        tokens.add(display.getRealCursorIndex(), MatrixFunctionFactory.makeTrace());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the A^-1 button.
     *
     * @param v Not Used
     */
    public void clickInverse(View v) {
        tokens.add(display.getRealCursorIndex(), MatrixFunctionFactory.makeInverse());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the A^T button.
     *
     * @param v Not Used
     */
    public void clickTranspose(View v) {
        tokens.add(display.getRealCursorIndex(), MatrixFunctionFactory.makeTranspose());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the diag() button
     *
     * @param v Not Used
     */
    public void clickDiagonalize(View v) {
        Matrix m;
        Matrix[] matrices = null;
        try {
            m = Utility.evaluateMatrixExpression(tokens);
            matrices = ((Matrix.AugmentedMatrix) MatrixFunctionFactory.makeDiagonalize().perform(m)).getMatrices();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
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
     *
     * @param v Not Used
     */
    public void clickLambda(View v) {
        Matrix m;
        Matrix eigenvals = null;
        try {
            m = Utility.evaluateMatrixExpression(tokens);
            eigenvals = MatrixFunctionFactory.makeEigenVal().perform(m);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        tokens.clear();
        display.setCursorIndex(0);
        tokens.add(display.getRealCursorIndex(), eigenvals);
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the eigenvect button
     *
     * @param v Not Used
     */
    public void clickEigenVect(View v) {
        Matrix m;
        Matrix[] matrices = null;
        try {
            m = Utility.evaluateMatrixExpression(tokens);
            matrices = ((Matrix.AugmentedMatrix) MatrixFunctionFactory.makeEigenVectors().perform(m)).getMatrices();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
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
     *
     * @param v Not Used
     */
    public void clickLUP(View v) {
        Matrix m;
        Matrix[] matrices = null;
        try {
            m = Utility.evaluateMatrixExpression(tokens);
            matrices = ((Matrix.AugmentedMatrix) MatrixFunctionFactory.makeLUP().perform(m)).getMatrices();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
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
     *
     * @param v Not Used
     */
    public void clickREF(View v) {
        tokens.add(display.getRealCursorIndex(), MatrixFunctionFactory.makeREF());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the rref button
     *
     * @param v Not Used
     */
    public void clickRREF(View v) {
        tokens.add(display.getRealCursorIndex(), MatrixFunctionFactory.makeRREF());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the ref button
     *
     * @param v Not Used
     */
    public void clickRank(View v) {
        tokens.add(display.getRealCursorIndex(), MatrixFunctionFactory.makeRank());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the sqrt button
     *
     * @param v Not Used
     */
    public void clickSqrt(View v) {
        tokens.add(display.getRealCursorIndex(), MatrixFunctionFactory.makeSqrt());
        display.setCursorIndex(display.getRealCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the + button
     *
     * @param v Not Used
     */
    public void clickAdd(View v) {
        tokens.add(display.getRealCursorIndex(), MatrixOperatorFactory.makeMatrixAdd());
        display.setCursorIndex(display.getRealCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the - button
     *
     * @param v Not Used
     */
    public void clickSubtract(View v) {
        tokens.add(display.getRealCursorIndex(), MatrixOperatorFactory.makeMatrixSubtract());
        display.setCursorIndex(display.getRealCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the * button
     *
     * @param v Not Used
     */
    public void clickMultiply(View v) {
        tokens.add(display.getRealCursorIndex(), MatrixOperatorFactory.makeMatrixMultiply());
        display.setCursorIndex(display.getRealCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the ^ button
     *
     * @param v Not Used
     */
    public void clickExponent(View v) {
        tokens.add(display.getRealCursorIndex(), MatrixOperatorFactory.makeMatrixExponent());
        display.setCursorIndex(display.getRealCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the [A|B] button
     *
     * @param v Not Used
     */
    public void clickAugment(View v) {
        tokens.add(display.getRealCursorIndex(), MatrixOperatorFactory.makeAugment());
        display.setCursorIndex(display.getRealCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the A^y button
     *
     * @param v Not Used
     */
    public void clickPower(View v) {
        tokens.add(display.getRealCursorIndex(), MatrixOperatorFactory.makeAugment());
        display.setCursorIndex(display.getRealCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the equals Button.
     *
     * @param v Not Used
     */
    public void clickEquals(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        try {
            Matrix m = Utility.evaluateMatrixExpression(Utility.convertToReversePolishMatrix(Utility.setupExpression(tokens)));
            tokens.clear();
            display.setCursorIndex(0);
            tokens.add(display.getRealCursorIndex(), m);
            display.setCursorIndex(display.getCursorIndex() + 1);
        } catch (Exception e) { //an error was thrown
            Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
        }
        scrollDown();
    }
}
