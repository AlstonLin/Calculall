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
    public static final int ARGUMENT = 1, TRUEBEARING = 2, BEARING = 3; //directionmode options
    private boolean mem = false;

    private int angleMode = 1;
    public static final int DEGREE = 1, RADIAN = 2, GRADIAN = 3; //angleMode options
    private boolean shift = false;
    //private ToggleButton vMemButton;
    //private TextView output;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vector);
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
     * @return whether or not shift is active
     */
    public boolean getShift() {
        return shift;
    }

    /**
     * When the user presses the shift button. Switches the state of the boolean variable shift
     *
     * @param v Not Used
     */
    public void clickShift(View v) {
        Button sinButton = (Button) findViewById(R.id.sinButton);
        Button cosButton = (Button) findViewById(R.id.cosButton);
        Button tanButton = (Button) findViewById(R.id.tanButton);
        Button powButton = (Button) findViewById(R.id.powButton);
        Button expButton = (Button) findViewById(R.id.powerButton);
        Button logButton = (Button) findViewById(R.id.logButton);
        Button rootButton = (Button) findViewById(R.id.rootButton);
        Button squareButton = (Button) findViewById(R.id.squareButton);
        ToggleButton shiftButton = (ToggleButton) findViewById(R.id.shiftButton);

        if (shift) {
            shift = false;
            powButton.setText(Html.fromHtml(getString(R.string.powOfTen)));
            logButton.setText(getString(R.string.log10));
            rootButton.setText(getString(R.string.sqrt));
            squareButton.setText(getString(R.string.square));
            expButton.setText(Html.fromHtml(getString(R.string.exponent)));

             sinButton.setText("sin");
             sinButton.setTextSize(16);
             cosButton.setText("cos");
             cosButton.setTextSize(16);
             tanButton.setText("tan");
             tanButton.setTextSize(16);
        }
        else {
            shift = true;
            powButton.setText(Html.fromHtml(getString(R.string.powOfE)));
            logButton.setText(getString(R.string.ln));
            rootButton.setText(getString(R.string.cbrt));
            squareButton.setText(getString(R.string.cube));
            expButton.setText(Html.fromHtml(getString(R.string.varRoot)));

            sinButton.setText("arcsin");
            sinButton.setTextSize(14);
            cosButton.setText("arccos");
            cosButton.setTextSize(14);
            tanButton.setText("arctan");
            tanButton.setTextSize(14);

        }
        shiftButton.setChecked(shift);
        updateInput();
    }

    /**
     * @return the angleMode
     */
    public int getAngleMode() {
        return angleMode;
    }

    public void clickAngleMode(View v) {
        Button angleModeButton = (Button) findViewById(R.id.angleMode);
        if (angleMode == GRADIAN) {
            convGtoD();
            angleMode = DEGREE;
            angleModeButton.setText(getString(R.string.deg));
        } else if (angleMode == RADIAN) {
            convRtoG();
            angleMode = GRADIAN;
            angleModeButton.setText(getString(R.string.grad));
        } else if (angleMode == DEGREE) {
            convDtoR();
            angleMode = RADIAN;
            angleModeButton.setText(getString(R.string.rad));
        }
        updateInput();
    }

    public boolean switchedAngleMode = false;
    public void convGtoD() {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        try {
            double val = process();
            if(switchedAngleMode){
                tokens.set(tokens.size()-1, new Token(" → DEG"){});
            }else {
                tokens.add(new Token(" → DEG"){});
            }
            updateInput();
            display.displayOutput(val * 9 / 10 + "");
            scrollDown();
            switchedAngleMode = true;
        } catch (Exception e) { //User made a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    public void convRtoG() {
        //Converts the number displayed from radians into gradians ie multiplies the number by 100/pi
        DisplayView display = (DisplayView)findViewById(R.id.display);
        try {
            double val = process();
            if(switchedAngleMode){
                tokens.set(tokens.size()-1, new Token(" → GRAD"){});
            }else {
                tokens.add(new Token(" → GRAD"){});
            }
            updateInput();
            display.displayOutput(val * 100 / Math.PI + "");
            scrollDown();
            switchedAngleMode = true;
        } catch (Exception e) { //User made a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    public void convDtoR() {
        //Converts the number displayed from degrees into radians ie multiplies the number by pi/180
        DisplayView display = (DisplayView)findViewById(R.id.display);
        try {
            double val = process();
            if(switchedAngleMode){
                tokens.set(tokens.size()-1, new Token(" → RAD"){});
            }else {
                tokens.add(new Token(" → RAD"){});
            }
            updateInput();
            display.displayOutput(val * Math.PI / 180 + "");
            scrollDown();
            switchedAngleMode = true;
        } catch (Exception e) { //User made a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When the user presses the ( Button.
     *
     * @param v Not Used
     */
    public void clickOpenBracket(View v) {
        tokens.add(BracketFactory.createOpenBracket());
        updateInput();
    }

    /**
     * When the user presses the ) Button.
     *
     * @param v Not Used
     */
    public void clickCloseBracket(View v) {
        tokens.add(BracketFactory.createCloseBracket());
        updateInput();
    }

    /**
     * When the user presses the 10^x Button.
     *
     * @param v Not Used
     */
    public void clickPowerOfTen(View v) {
        tokens.add(FunctionFactory.makePowOfTen());
        updateInput();
    }


    /**
     * When the user presses the e^x Button.
     *
     * @param v Not Used
     */
    public void clickExp(View v) {
        tokens.add(FunctionFactory.makeExp());
        updateInput();
    }

    /**
     * When the user presses the C^x button. Where C is either 10 or e
     *
     * @param v Not Used
     */
    public void clickPower(View v) {
        if (!shift) {
            clickPowerOfTen(v);
        } else {
            clickExp(v);
        }
        updateInput();
    }

    /**
     * When the user presses the ln(x) Button.
     *
     * @param v Not Used
     */
    public void clickLn(View v) {
        tokens.add(FunctionFactory.makeLn());
        updateInput();
    }

    /**
     * When the user presses the log(x) or log_10(x)Button.
     *
     * @param v Not Used
     */
    public void clickLog_10(View v) {
        tokens.add(FunctionFactory.makeLog_10());
        updateInput();
    }


    /**
     * When the user presses the log/ln button.
     *
     * @param v Not Used
     */
    public void clickLog(View v) {
        if (!shift) {
            clickLog_10(v);
        } else {
            clickLn(v);
        }
        updateInput();
    }

    public void clickExponent(View v) {
        tokens.add(OperatorFactory.makeExponent());
        tokens.add(BracketFactory.createOpenBracket());
        updateInput();
    }

    public void clickVarRoot(View v) {
        tokens.add(OperatorFactory.makeVariableRoot());
        updateInput();
    }

    public void clickPow(View v) {
        if (!shift) {
            clickExponent(v);
        } else {
            clickVarRoot(v);
        }
        updateInput();
    }

    /**
     * When the user presses the x^2 Button.
     *
     * @param v Not Used
     */
    public void clickSquare(View v) {
        tokens.add(FunctionFactory.makeSquare());
        updateInput();
    }

    /**
     * When the user presses the x^3 Button.
     *
     * @param v Not Used
     */
    public void clickCube(View v) {
        tokens.add(FunctionFactory.makeCube());
        updateInput();
    }

    /**
     * When the user presses the square or cube button.
     *
     * @param v Not Used
     */
    public void clickSmallExp(View v) {
        if (!shift) {
            clickSquare(v);
        } else {
            clickCube(v);
        }
        updateInput();
    }

    /**
     * When the user presses the cuberoot Button.
     *
     * @param v Not Used
     */
    public void clickCbrt(View v) {
        tokens.add(FunctionFactory.makeCbrt());
        updateInput();
    }

    /**
     * When the user presses the root button.
     *
     * @param v Not Used
     */
    public void clickRoot(View v) {
        if (!shift) {
            clickSqrt(v);
        } else {
            clickCbrt(v);
        }
        updateInput();
    }

    /**
     * When the user presses the x^-1 button.
     *
     * @param v Not Used
     */
    public void clickReciprocal(View v) {
        tokens.add(FunctionFactory.makeReciprocal());
        updateInput();
    }

    /**
     * When the user presses the n! Button.
     *
     * @param v Not Used
     */
    public void clickFactorial(View v) {
        tokens.add(OperatorFactory.makeFactorial());
        updateInput();
    }

    /**
     * When the user presses the nCk Button.
     *
     * @param v Not Used
     */
    public void clickCombination(View v) {
        tokens.add(OperatorFactory.makeCombination());
        updateInput();
    }

    /**
     * When the user presses the nPk Button.
     *
     * @param v Not Used
     */
    public void clickPermutation(View v) {
        tokens.add(OperatorFactory.makePermutation());
        updateInput();
    }

    /**
     * When the user presses the e Button.
     *
     * @param v Not Used
     */
    public void clickE(View v) {
        tokens.add(VariableFactory.makeE());
        updateInput();
    }

    /**
     * When the user presses the pi Button.
     *
     * @param v Not Used
     */
    public void clickPi(View v) {
        tokens.add(VariableFactory.makePI());
        updateInput();
    }


    /**
     * When the user presses the sin(x) button.
     * Picks the correct function based
     * on the modifiers which are currently enabled
     *
     * @param v Not Used
     */
    public void clickSin(View v) {

        if (shift) {
            clickASin(v);
        } else {
            clickSin1(v);
        }
        updateInput();
    }

    /**
     * When the user presses the sin(x) Button.
     *
     * @param v Not Used
     */
    public void clickSin1(View v) {
        if (angleMode == DEGREE) {
            tokens.add(FunctionFactory.makeSinD());
        } else if (angleMode == RADIAN) {
            tokens.add(FunctionFactory.makeSinR());
        } else if (angleMode == GRADIAN) {
            tokens.add(FunctionFactory.makeSinG());
        }
    }

    /**
     * When the user presses the arcsin(x) or sin^-1(x) Button.
     *
     * @param v Not Used
     */
    public void clickASin(View v) {
        if (angleMode == DEGREE) {
            tokens.add(FunctionFactory.makeASinD());
        } else if (angleMode == RADIAN) {
            tokens.add(FunctionFactory.makeASinR());
        } else if (angleMode == GRADIAN) {
            tokens.add(FunctionFactory.makeASinG());
        }
    }

    /**
     * When the user presses the cos(x) button.
     * Picks the correct function based
     * on the modifiers which are currently enabled
     *
     * @param v Not Used
     */
    public void clickCos(View v) {
        if (shift) {
            clickACos(v);
        } else {
            clickCos1(v);
        }
        updateInput();
    }

    /**
     * When the user presses the cos(x) Button.
     *
     * @param v Not Used
     */
    public void clickCos1(View v) {
        if (angleMode == DEGREE) {
            tokens.add(FunctionFactory.makeCosD());
        } else if (angleMode == RADIAN) {
            tokens.add(FunctionFactory.makeCosR());
        } else if (angleMode == GRADIAN) {
            tokens.add(FunctionFactory.makeCosG());
        }
    }

    /**
     * When the user presses the arccos(x) or cos^-1(x) Button.
     *
     * @param v Not Used
     */
    public void clickACos(View v) {
        if (angleMode == DEGREE) {
            tokens.add(FunctionFactory.makeACosD());
        } else if (angleMode == RADIAN) {
            tokens.add(FunctionFactory.makeACosR());
        } else if (angleMode == GRADIAN) {
            tokens.add(FunctionFactory.makeACosG());
        }
    }

    /**
     * When the user presses the cos(x) button.
     * Picks the correct function based
     * on the modifiers which are currently enabled
     *
     * @param v Not Used
     */
    public void clickTan(View v) {
         if (shift) {
            clickATan(v);
        } else {
            clickTan1(v);
        }
        updateInput();
    }

    /**
     * When the user presses the tan(x) Button.
     *
     * @param v Not Used
     */
    public void clickTan1(View v) {
        if (angleMode == DEGREE) {
            tokens.add(FunctionFactory.makeTanD());
        } else if (angleMode == RADIAN) {
            tokens.add(FunctionFactory.makeTanR());
        } else if (angleMode == GRADIAN) {
            tokens.add(FunctionFactory.makeTanG());
        }
    }

    /**
     * When the user presses the arctan(x) or tan^-1(x) Button.
     *
     * @param v Not Used
     */
    public void clickATan(View v) {
        if (angleMode == DEGREE) {
            tokens.add(FunctionFactory.makeATanD());
        } else if (angleMode == RADIAN) {
            tokens.add(FunctionFactory.makeATanR());
        } else if (angleMode == GRADIAN) {
            tokens.add(FunctionFactory.makeATanG());
        }
    }

    /**
     * When the user presses the U button
     *
     * @param v Not Used
     */
    public void clickVU(View v) {
        DisplayView display = (DisplayView)findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                ArrayList<Token> val = processVectors();
                tokens.clear();
                display.displayOutput(Utility.convertTokensToString(val) + "→ U");
                Vector.u_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(VectorFactory.makeU());
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
        DisplayView display = (DisplayView)findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                ArrayList<Token> val = processVectors();
                tokens.clear();
                display.displayOutput(Utility.convertTokensToString(val) + "→ V");
                Vector.v_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(VectorFactory.makeV());
            }
            updateInput();
        } catch (Exception e) { //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When the user presses the W button
     *
     * @param v Not Used
     */
    public void clickVW(View v) {
        DisplayView display = (DisplayView)findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                ArrayList<Token> val = processVectors();
                tokens.clear();
                display.displayOutput(Utility.convertTokensToString(val) + "→ W");
                Vector.w_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(VectorFactory.makeW());
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
    public void clickVS(View v) {
        DisplayView display = (DisplayView)findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                ArrayList<Token> val = processVectors();
                tokens.clear();
                display.displayOutput(Utility.convertTokensToString(val) + "→ S");
                Vector.s_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(VectorFactory.makeS());
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
        DisplayView display = (DisplayView)findViewById(R.id.display);
        try {
            VRuleSet.setPressedBearButton(true);
            String s = Utility.convertTokensToString(processVectors());
            s = s.indexOf(".") < 0  ? s : (s.indexOf("E")>0 ? s.substring(0,s.indexOf("E")).replaceAll("0*$", "")
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
        DisplayView display = (DisplayView)findViewById(R.id.display);
        try {
            VRuleSet.setPressedTrueBButton(true);
            String s = Utility.convertTokensToString(processVectors());
            s = s.indexOf(".") < 0  ? s : (s.indexOf("E")>0 ? s.substring(0,s.indexOf("E")).replaceAll("0*$", "")
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
    public void clickUnitVector(View v){
        DisplayView display = (DisplayView)findViewById(R.id.display);
        VRuleSet.setPressedUnitVButton(true);
        try {
            String s = Utility.convertTokensToString(processVectors());
            s = s.indexOf(".") < 0  ? s : (s.indexOf("E")>0 ? s.substring(0,s.indexOf("E")).replaceAll("0*$", "")
                    .replaceAll("\\.$", "").concat(s.substring(s.indexOf("E"))) : s.replaceAll("0*$", "")
                    .replaceAll("\\.$", "")); //Removes trailing zeroes
            display.displayOutput(s);
        }catch (Exception e){ //User did a mistake
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
