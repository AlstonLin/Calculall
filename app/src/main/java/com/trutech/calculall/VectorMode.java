package com.trutech.calculall;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class VectorMode extends Basic {
    private int directionMode = 1;
    public static final int STANDARD = 1, TRUEBEARING = 2, BEARING = 3; //directionmode options
    private boolean mem = false;
    //private ToggleButton vMemButton;
    //private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vector);
    }


    public ArrayList<Token> processVectors () {
        return Utility.simplifyVector(Utility.convertVariablesToTokens(condenseDigits()));
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
     * When the user presses the MEM button; toggles memory storage
     *
     * @param v Not Used
     */
    public void clickMem(View v) {
        ToggleButton vMemButton = (ToggleButton) findViewById(R.id.memButton);
        mem = !mem;
        vMemButton.setChecked(mem);
    }

    /**
     * When the user presses the A button
     *
     * @param v Not Used
     */
    public void clickVA(View v) {
        TextView output = (TextView) findViewById(R.id.txtStack);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                VRuleSet.setAppliedRule();
                ArrayList<Token> val = processVectors();
                tokens.clear();
                output.setText(Utility.convertTokensToString(val) + "→ A");
                Vector.a_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(VectorFactory.makeA());
            }
            updateInput();
        } catch (Exception e) { //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When the user presses the B button
     *
     * @param v Not Used
     */
    public void clickVB(View v) {
        TextView output = (TextView) findViewById(R.id.txtStack);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                VRuleSet.setAppliedRule();
                ArrayList<Token> val = processVectors();
                tokens.clear();
                output.setText(Utility.convertTokensToString(val) + "→ B");
                Vector.b_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(VectorFactory.makeB());
            }
            updateInput();
        } catch (Exception e) { //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When the user presses the C button
     *
     * @param v Not Used
     */
    public void clickVC(View v) {
        TextView output = (TextView) findViewById(R.id.txtStack);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                VRuleSet.setAppliedRule();
                ArrayList<Token> val = processVectors();
                tokens.clear();
                output.setText(Utility.convertTokensToString(val) + "→ C");
                Vector.c_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(VectorFactory.makeC());
            }
            updateInput();
        } catch (Exception e) { //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When the user presses the A button
     *
     * @param v Not Used
     */
    public void clickVX(View v) {
        TextView output = (TextView) findViewById(R.id.txtStack);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                VRuleSet.setAppliedRule();
                ArrayList<Token> val = processVectors();
                tokens.clear();
                output.setText(Utility.convertTokensToString(val) + "→ X");
                Vector.x_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(VectorFactory.makeX());
            }
            updateInput();
        } catch (Exception e) { //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When the user presses the Y button
     *
     * @param v Not Used
     */
    public void clickVY(View v) {
        TextView output = (TextView) findViewById(R.id.txtStack);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                VRuleSet.setAppliedRule();
                ArrayList<Token> val = processVectors();
                tokens.clear();
                output.setText(Utility.convertTokensToString(val) + "→ Y");
                Vector.y_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(VectorFactory.makeY());
            }
            updateInput();
        } catch (Exception e) { //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When the user presses the A button
     *
     * @param v Not Used
     */
    public void clickVZ(View v) {
        TextView output = (TextView) findViewById(R.id.txtStack);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                VRuleSet.setAppliedRule();
                ArrayList<Token> val = processVectors();
                tokens.clear();
                output.setText(Utility.convertTokensToString(val) + "→ Z");
                Vector.z_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(VectorFactory.makeZ());
            }
            updateInput();
        } catch (Exception e) { //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
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
     * When the user presses the | Button.
     *
     * @param v Not Used
     */
    public void clickMagnitude (View v) {
        tokens.add(BracketFactory.createMagnitudeBar());
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
     * When the user presses the , Button.
     *
     * @param v Not Used
     */
    public void clickComma(View v){
        tokens.add(new Token (","){});
        updateInput();
    }

    /**
     * @return the angleMode
     */
    public int getDirectionMode() {
        return directionMode;
    }

    public void clickDirectionMode(View v) {
        Button angleModeButton = (Button) findViewById(R.id.directionMode);
        if (directionMode == BEARING) {
            convBtoS();
            directionMode = STANDARD;
            angleModeButton.setText(getString(R.string.standard));
        } else if (directionMode == TRUEBEARING) {
            convTtoB();
            directionMode = BEARING;
            angleModeButton.setText(getString(R.string.bear));
        } else if (directionMode == STANDARD) {
            convStoT();
            directionMode = TRUEBEARING;
            angleModeButton.setText(getString(R.string.trueB));
        }
        updateInput();
    }

    public boolean switchedDirectionMode = false;
    public void convBtoS() {
        //Converts the number displayed from gradians into degrees ie multiplies the number by 9/10

        TextView input = (TextView) findViewById(R.id.txtInput);
        TextView output = (TextView) findViewById(R.id.txtStack);
        try {
            ArrayList<Token> val = processVectors();
            if(switchedDirectionMode){
                tokens.set(tokens.size()-1, new Token(" → STANDARD"){});
            }else {
                tokens.add(new Token(" → STANDARD"){});
            }
            updateInput();
            output.setText(val*9/10+"");
            ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
            scrollView.pageScroll(ScrollView.FOCUS_DOWN);
            switchedDirectionMode = true;
        } catch (Exception e) { //User made a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    public void convTtoB() {
        //Converts the number displayed from radians into gradians ie multiplies the number by 100/pi
        TextView input = (TextView) findViewById(R.id.txtInput);
        TextView output = (TextView) findViewById(R.id.txtStack);
        try {
            double val = process();
            if(switchedDirectionMode){
                tokens.set(tokens.size()-1, new Token(" → GRAD"){});
            }else {
                tokens.add(new Token(" → GRAD"){});
            }
            updateInput();
            output.setText(val*100/Math.PI+"");
            ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
            scrollView.pageScroll(ScrollView.FOCUS_DOWN);
            switchedDirectionMode = true;
        } catch (Exception e) { //User made a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    public void convStoT() {
        //Converts the number displayed from degrees into radians ie multiplies the number by pi/180
        TextView input = (TextView) findViewById(R.id.txtInput);
        TextView output = (TextView) findViewById(R.id.txtStack);
        try {
            double val = process();
            if(switchedDirectionMode){
                tokens.set(tokens.size()-1, new Token(" → RAD"){});
            }else {
                tokens.add(new Token(" → RAD"){});
            }
            updateInput();
            output.setText(val*Math.PI/180+"");
            ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
            scrollView.pageScroll(ScrollView.FOCUS_DOWN);
            switchedDirectionMode = true;
        } catch (Exception e) { //User made a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

}
