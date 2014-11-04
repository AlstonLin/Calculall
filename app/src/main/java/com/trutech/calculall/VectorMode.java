package com.trutech.calculall;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class VectorMode extends Basic {
    public static final int ARGUMENT = 1, TRUEBEARING = 2, BEARING = 3; //directionmode options
    public boolean switchedDirectionMode = false;
    private int directionMode = 1;
    private int tokenSize = -1;
    //private ToggleButton vMemButton;
    //private TextView output;
    private boolean mem = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vector);
        display = (DisplayView) findViewById(R.id.display);
    }

    public ArrayList<Token> processVectors() {
        return Utility.simplifyVector(Utility.convertVariablesToTokens(Utility.setupExpression(Utility.addMissingBrackets(Utility.condenseDigits(tokens)))));
    }

    /**
     * When the user presses the equals Button.
     *
     * @param v Not Used
     */
    public void clickVectorEquals(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);

        //Used to check if the user added extra tokens after clicking the direction mode button at least once
        //and change switchedDirectionMode and changedTokens accordingly
        if (tokenSize != tokens.size() || changedTokens) {
            switchedDirectionMode = false;
            changedTokens = false;
        }
        tokenSize = tokens.size();

        //If the pressed the = button and wants to find the direction of a angle, it'll be the set to what
        //directionMode is
        if (!switchedDirectionMode) {
            if (directionMode == ARGUMENT) {
                VRuleSet.setPressedArgumentButton(true);
            } else if (directionMode == TRUEBEARING) {
                VRuleSet.setPressedTrueBButton(true);
            } else if (directionMode == BEARING) {
                VRuleSet.setPressedBearButton(true);
            }
        }
        try {
            String s = Utility.convertTokensToString(processVectors());
            s = s.indexOf(".") < 0 ? s : (s.indexOf("E") > 0 ? s.substring(0, s.indexOf("E")).replaceAll("0*$", "")
                    .replaceAll("\\.$", "").concat(s.substring(s.indexOf("E"))) : s.replaceAll("0*$", "")
                    .replaceAll("\\.$", "")); //Removes trailing zeroes
            display.displayOutput(s);
        } catch (Exception e) { //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
        scrollDown();
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
        DisplayView display = (DisplayView) findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                ArrayList<Token> val = processVectors();
                tokens.clear();
                display.displayOutput(Utility.convertTokensToString(val) + "→ A");
                Vector.a_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(display.getRealCursorIndex(), VectorFactory.makeA());
                display.setCursorIndex(display.getCursorIndex() + 1);
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
        DisplayView display = (DisplayView) findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                ArrayList<Token> val = processVectors();
                tokens.clear();
                display.displayOutput(Utility.convertTokensToString(val) + "→ B");
                Vector.b_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(display.getRealCursorIndex(), VectorFactory.makeB());
                display.setCursorIndex(display.getCursorIndex() + 1);
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
        DisplayView display = (DisplayView) findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                ArrayList<Token> val = processVectors();
                tokens.clear();
                display.displayOutput(Utility.convertTokensToString(val) + "→ C");
                Vector.c_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(display.getRealCursorIndex(), VectorFactory.makeC());
                display.setCursorIndex(display.getCursorIndex() + 1);
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
        DisplayView display = (DisplayView) findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                ArrayList<Token> val = processVectors();
                tokens.clear();
                display.displayOutput(Utility.convertTokensToString(val) + "→ X");
                Vector.x_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(display.getRealCursorIndex(), VectorFactory.makeX());
                display.setCursorIndex(display.getCursorIndex() + 1);
            }
            updateInput();
        } catch (Exception e) { //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When the user presses the V button
     *
     * @param v Not Used
     */
    public void clickVV(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                ArrayList<Token> val = processVectors();
                tokens.clear();
                display.displayOutput(Utility.convertTokensToString(val) + "→ V");
                Vector.x_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(display.getRealCursorIndex(), VectorFactory.makeX());
                display.setCursorIndex(display.getCursorIndex() + 1);
            }
            updateInput();
        } catch (Exception e) { //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When the user presses the T button
     *
     * @param v Not Used
     */
    public void clickVT(View v) {
        tokens.add(display.getRealCursorIndex(), new Token("t") {
        });
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the [ Button.
     *
     * @param v Not Used
     */
    public void clickOpenSquareBracket(View v) {
        tokens.add(display.getRealCursorIndex(), BracketFactory.makeOpenSquareBracket());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the ] Button.
     *
     * @param v Not Used
     */
    public void clickCloseSquareBracket(View v) {
        tokens.add(display.getRealCursorIndex(), BracketFactory.makeCloseSquareBracket());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the | Button.
     *
     * @param v Not Used
     */
    public void clickMagnitude(View v) {
        tokens.add(display.getRealCursorIndex(), BracketFactory.makeMagnitudeBar());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the â€¢ Button.
     *
     * @param v Not Used
     */
    public void clickDot(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeDot());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the Ã— Button.
     *
     * @param v Not Used
     */
    public void clickCross(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeCross());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the , Button.
     *
     * @param v Not Used
     */
    public void clickComma(View v) {
        tokens.add(display.getRealCursorIndex(), new Token(",") {
        });
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the ∠ Button.
     *
     * @param v Not Used
     */
    public void clickAngle(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeAngle());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the proj Button.
     *
     * @param v Not Used
     */
    public void clickProjection(View v) {
        tokens.add(display.getRealCursorIndex(), new Token("proj") {
        });
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * @return the angleMode
     */
    public int getDirectionMode() {
        return directionMode;
    }

    public void clickDirectionMode(View v) {
        Button directionModeButton = (Button) findViewById(R.id.argumentButton);

        //Used to check if the user added extra tokens after clicking the direction mode button at least once
        //and change switchedDirectionMode and changedTokens accordingly
        if (tokenSize != tokens.size() || changedTokens) {
            switchedDirectionMode = false;
            changedTokens = false;
        }
        tokenSize = tokens.size();

        if (directionMode == BEARING) {
            convBtoA();
            directionMode = ARGUMENT;
            directionModeButton.setText(getString(R.string.argument));
        } else if (directionMode == TRUEBEARING) {
            convTtoB();
            directionMode = BEARING;
            directionModeButton.setText(getString(R.string.bear));
        } else if (directionMode == ARGUMENT) {
            convAtoT();
            directionMode = TRUEBEARING;
            directionModeButton.setText(getString(R.string.trueB));
        }
        updateInput();
    }

    public void convBtoA() {
        //Converts the number displayed from bearing into argument
        DisplayView display = (DisplayView) findViewById(R.id.display);
        try {
            VRuleSet.setPressedArgumentButton(true);
            String s = Utility.convertTokensToString(processVectors());
            s = s.indexOf(".") < 0 ? s : (s.indexOf("E") > 0 ? s.substring(0, s.indexOf("E")).replaceAll("0*$", "")
                    .replaceAll("\\.$", "").concat(s.substring(s.indexOf("E"))) : s.replaceAll("0*$", "")
                    .replaceAll("\\.$", "")); //Removes trailing zeroes

/*            if(switchedDirectionMode){
                tokens.add(new Token(" → ARG"){});
            }else {
                tokens.add(new Token(" → ARG"){});
            }*/
            updateInput();
            display.displayOutput(s);
            scrollDown();
            switchedDirectionMode = true;
        } catch (Exception e) { //User made a mistake
            VRuleSet.setPressedArgumentButton(false);
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    public void convTtoB() {
        //Converts the number displayed from bearing into argument ie multiplies the number by 9/10
        DisplayView display = (DisplayView) findViewById(R.id.display);
        try {
            VRuleSet.setPressedBearButton(true);
            String s = Utility.convertTokensToString(processVectors());
            s = s.indexOf(".") < 0 ? s : (s.indexOf("E") > 0 ? s.substring(0, s.indexOf("E")).replaceAll("0*$", "")
                    .replaceAll("\\.$", "").concat(s.substring(s.indexOf("E"))) : s.replaceAll("0*$", "")
                    .replaceAll("\\.$", "")); //Removes trailing zeroes

/*            if(switchedDirectionMode){
                tokens.add(new Token(" → BEA"){});
            }else {
                tokens.add(new Token(" → BEA "){});
            }*/
            updateInput();
            display.displayOutput(s);
            scrollDown();
            switchedDirectionMode = true;
        } catch (Exception e) { //User made a mistake
            VRuleSet.setPressedBearButton(false);
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    public void convAtoT() {
        //Converts the number displayed from bearing into argument ie multiplies the number by 9/10
        DisplayView display = (DisplayView) findViewById(R.id.display);
        try {
            VRuleSet.setPressedTrueBButton(true);
            String s = Utility.convertTokensToString(processVectors());
            s = s.indexOf(".") < 0 ? s : (s.indexOf("E") > 0 ? s.substring(0, s.indexOf("E")).replaceAll("0*$", "")
                    .replaceAll("\\.$", "").concat(s.substring(s.indexOf("E"))) : s.replaceAll("0*$", "")
                    .replaceAll("\\.$", "")); //Removes trailing zeroes

/*            if(switchedDirectionMode){
                tokens.add(new Token(" → TRU"){});
            }else {
                tokens.add(new Token(" → TRU"){});
            }*/
            updateInput();
            display.displayOutput(s);
            scrollDown();
            switchedDirectionMode = true;
        } catch (Exception e) { //User made a mistake
            VRuleSet.setPressedTrueBButton(false);
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When the user presses the , Button.
     *
     * @param v Not Used
     */
    public void clickUnitVector(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        VRuleSet.setPressedUnitVButton(true);
        try {
            String s = Utility.convertTokensToString(processVectors());
            s = s.indexOf(".") < 0 ? s : (s.indexOf("E") > 0 ? s.substring(0, s.indexOf("E")).replaceAll("0*$", "")
                    .replaceAll("\\.$", "").concat(s.substring(s.indexOf("E"))) : s.replaceAll("0*$", "")
                    .replaceAll("\\.$", "")); //Removes trailing zeroes
            display.displayOutput(s);
        } catch (Exception e) { //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
        scrollDown();
    }

    /**
     * When the user presses the Scalar Button.
     *
     * @param v Not Used
     */
    public void clickScalar(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        VRuleSet.setPressedScalarEqnButton(true);
        try {
            String s = Utility.convertTokensToString(processVectors());
            s = s.indexOf(".") < 0 ? s : (s.indexOf("E") > 0 ? s.substring(0, s.indexOf("E")).replaceAll("0*$", "")
                    .replaceAll("\\.$", "").concat(s.substring(s.indexOf("E"))) : s.replaceAll("0*$", "")
                    .replaceAll("\\.$", "")); //Removes trailing zeroes
            display.displayOutput(s);
        } catch (Exception e) { //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
        scrollDown();
    }
  /*  *//**
     * When the user presses the , Button.
     *
     * @param v Not Used
     *//*
    public void clickArgument(View v){
        TextView output = (TextView) findViewById(R.id.txtStack);
        VRuleSet.setPressedArgumentButton(true);
        try {
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

    *//**
     * When the user presses the , Button.
     *
     * @param v Not Used
     *//*
    public void clickTrueB(View v){
        TextView output = (TextView) findViewById(R.id.txtStack);
        VRuleSet.setPressedTrueBButton(true);
        try {
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

    *//**
     * When the user presses the , Button.
     *
     * @param v Not Used
     *//*
    public void clickBear(View v){
        TextView output = (TextView) findViewById(R.id.txtStack);
        VRuleSet.setPressedBearButton(true);
        try {
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
    }*/


}
