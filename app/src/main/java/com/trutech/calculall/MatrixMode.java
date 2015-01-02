package com.trutech.calculall;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;

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
}
