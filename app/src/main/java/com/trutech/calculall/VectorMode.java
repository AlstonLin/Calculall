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
        memButton = (ToggleButton)findViewById(R.id.memButton);
        output = (TextView) findViewById(R.id.txtStack);
    }


    private ArrayList<Token> processVectors () {
        return Utility.simplifyVector(condenseDigits());
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
            s = s.indexOf(".") < 0  ? s : (s.indexOf("E")>0 ? s.substring(0,s.indexOf("E")).replaceAll("0*$", "")
                    .replaceAll("\\.$", "").concat(s.substring(s.indexOf("E"))) : s.replaceAll("0*$", "")
                    .replaceAll("\\.$", "")); //Removes trailing zeroes
            output.setText(s);
        }catch (Exception e){ //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.pageScroll(ScrollView.FOCUS_DOWN);
    }


    /**
     * Transforms all the digits into numbers as well as replacing Variables with numbers.
     *
     * @return The tokens with the digits condensed and variables substituted
     */
    public ArrayList<Token> condenseDigits(){
        ArrayList<Token> newTokens = new ArrayList<Token>();
        ArrayList<Digit> digits = new ArrayList<Digit>();
        boolean atDigits = false; //Tracks if it's currently tracking digits
        for (Token token : tokens){
            if (atDigits){ //Going through digits
                if (token instanceof Digit){ //Number keeps going
                    digits.add((Digit) token);
                }else { //Number ended
                    atDigits = false;
                    newTokens.add(new Number(Utility.valueOf(digits))); //Adds the sum of all the digits
                    digits.clear();
                    if (token instanceof Variable){ //Substitutes Variables
                        newTokens.add(new Number(((Variable)token).getValue()));
                    }else {
                        newTokens.add(token);
                    }
                }
            }else{ //Not going through digits
                if (token instanceof Digit) { //Start of a number
                    atDigits = true;
                    digits.add((Digit) token);
                } else if (token instanceof Variable){ //Substitutes Variables
                    newTokens.add(new Number(((Variable)token).getValue()));
                } else{ //Not a digit; adds to the new list
                    newTokens.add(token);
                }
            }
        }
        if (!digits.isEmpty() && atDigits){ //Digits left
            newTokens.add(new Number (Utility.valueOf(digits)));
        }
        return newTokens;
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
            output.setText(val + "â†’ A");
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
            output.setText(val + "â†’B");
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
            output.setText(val + "â†’C");
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
            output.setText(val + "â†’X");
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
            output.setText(val + "â†’Y");
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
            output.setText(val + "â†’Z");
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
     * When the user presses the â€¢ Button.
     *
     * @param v Not Used
     */
    public void clickDot(View v){
        tokens.add(OperatorFactory.makeDot());
        updateInput();
    }

    /**
     * When the user presses the Ã— Button.
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
