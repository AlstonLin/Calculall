package com.trutech.calculall;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Ejaaz on 1/1/2015.
 */
public class MatrixMode extends FunctionMode {
    public static final int ROUND_TO = 9;

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
        output = (OutputView) findViewById(R.id.output);
        display = (DisplayView) findViewById(R.id.display);
        display.setOutput(output);
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
     * When the user presses the equals Button.
     *
     * @param v Not Used
     */
    public void clickEquals(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        try {
            Matrix m = Utility.evaluateMatrixExpression(tokens);
            tokens.clear();
            display.setCursorIndex(0);
            tokens.add(display.getRealCursorIndex(), m);
            display.setCursorIndex(display.getCursorIndex() + 1);
        } catch (Exception e) { //an error was thrown
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        scrollDown();
    }
}
