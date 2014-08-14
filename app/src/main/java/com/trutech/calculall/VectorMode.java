package com.trutech.calculall;

import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class VectorMode extends Basic {

    private boolean mem = false;
    private ToggleButton memButton;
    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vector);
    }

    private ArrayList<Token> processVectors () {
        return Utility.simplifyVector(condenseDigits());
    }


    /**
     * When the user presses the + Button.
     *
     * @param v Not Used
     */
    public void clickAdd(View v){
        tokens.add(OperatorFactory.makeAdd());
        updateInput();
    }

    /**
     * When the user presses the - Button.
     *
     * @param v Not Used
     */
    public void clickSubtract(View v){
        tokens.add(OperatorFactory.makeSubtract());
        updateInput();
    }


    /**
     * When the user presses the equals Button.
     *
     * @param v Not Used
     */
    public void clickVectorEquals(View v){
         TextView output = (TextView) findViewById(R.id.txtStack);
        try{
            String s = Utility.convertTokensToString(processVectors());
            output.setText(s);
        }catch (Exception e){ //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.pageScroll(ScrollView.FOCUS_DOWN);
    }

    /**
     * When the user presses the MEM button; toggles memory storage
     *
     * @param v Not Used
     */
    public void clickMem(View v){
        if(mem){
            mem = false;
        }else{
            mem = true;
        }
        memButton.setChecked(mem);
    }

    /**
     * When the user presses the A button
     *
     * @param v Not Used
     */
    public void clickA(View v){
        double val = process();
        if(mem){
            tokens.clear();
            output.setText(val + "→ A");
            Variable.a_value = val;
            mem = false;
        }else{
            tokens.add(VariableFactory.makeA());
        }
        memButton.setChecked(mem);
        updateInput();
    }

    /**
     * When the user presses the B button
     *
     * @param v Not Used
     */
    public void clickB(View v){
        double val = process();
        if(mem){
            tokens.clear();
            output.setText(val + "→B");
            Variable.b_value = val;
            mem = false;
        }else{
            tokens.add(VariableFactory.makeB());
        }
        memButton.setChecked(mem);
        updateInput();
    }

    /**
     * When the user presses the C button
     *
     * @param v Not Used
     */
    public void clickC(View v){
        double val = process();
        if(mem){
            tokens.clear();
            output.setText(val + "→C");
            Variable.c_value = val;
            mem = false;
        }else{
            tokens.add(VariableFactory.makeC());
        }
        memButton.setChecked(mem);
        updateInput();
    }

    /**
     * When the user presses the X button
     *
     * @param v Not Used
     */
    public void clickX(View v){
        double val = process();
        if(mem){
            tokens.clear();
            output.setText(val + "→X");
            Variable.x_value = val;
            mem = false;
        }else{
            tokens.add(VariableFactory.makeX());
        }
        memButton.setChecked(mem);
        updateInput();
    }

    /**
     * When the user presses the Y button
     *
     * @param v Not Used
     */
    public void clickY(View v){
        double val = process();
        if(mem){
            tokens.clear();
            output.setText(val + "→Y");
            Variable.y_value = val;
            mem = false;
        }else{
            tokens.add(VariableFactory.makeY());
        }
        memButton.setChecked(mem);
        updateInput();
    }

    /**
     * When the user presses the Z button
     *
     * @param v Not Used
     */
    public void clickZ(View v){
        double val = process();
        if(mem){
            tokens.clear();
            output.setText(val + "→Z");
            Variable.z_value = val;
            mem = false;
        }else{
            tokens.add(VariableFactory.makeZ());
        }
        memButton.setChecked(mem);
        updateInput();
    }

    /**
     * When the user presses the [ Button.
     *
     * @param v Not Used
     */
    public void clickOpenSquareBracket(View v){
        tokens.add(BracketFactory.createOpenSquareBracket());
        updateInput();
    }

    /**
     * When the user presses the ] Button.
     *
     * @param v Not Used
     */
    public void clickCloseSquareBracket(View v){
        tokens.add(BracketFactory.createCloseSquareBracket());
        updateInput();
    }

    /**
     * When the user presses the • Button.
     *
     * @param v Not Used
     */
    public void clickDot(View v){
        tokens.add(OperatorFactory.makeDot());
        updateInput();
    }

    /**
     * When the user presses the × Button.
     *
     * @param v Not Used
     */
    public void clickCross(View v){
        tokens.add(OperatorFactory.makeCross());
        updateInput();
    }

    /**
     * When the user presses the 2 Button.
     *
     * @param v Not Used
     */
    public void clickComma(View v){
        tokens.add(new Token (","){});
        updateInput();
    }

}
