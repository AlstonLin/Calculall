package com.trutech.calculall;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.LinkedList;

@SuppressWarnings("unused")
/**
 * The activity for the advanced calculator mode. The advanced mode will be able to
 * perform the most of the operations of a standard scientific calculator.
 *
 * @version 0.4.0
 */
public class Advanced extends Basic {
    public static final int DEGREE = 1, RADIAN = 2, GRADIAN = 3; //angleMode options
    public static final int DEC = 1, BIN = 2, OCT = 3, HEX = 4;//number bases
    private int fracMode = DEC;
    public static final int MIXED = 2, IMP = 3;
    public boolean switchedAngleMode = false;
    private int angleMode = 1;
    private int base = 1;
    private boolean hyperbolic = false;
    private boolean shift = false;
    private boolean mem = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced);
        //Programmaticly sets the texts that can't be defined with XML
        Button powButton = (Button) findViewById(R.id.powButton);
        Button expButton = (Button) findViewById(R.id.powerButton);
        powButton.setText(Html.fromHtml(getString(R.string.powOfTen)));
        expButton.setText(Html.fromHtml(getString(R.string.exponent)));
        display = (DisplayView) findViewById(R.id.display);
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

    public void convGtoD() {
        //Converts the number displayed from gradians into degrees ie multiplies the number by 9/10

        //we probably won't convert the functions but if we do we can use this:
        /*for(int i=0;i<tokens.size();i++){
            if(tokens.get(i) instanceof Function){
                if(((Function) tokens.get(i)).getType() == Function.SIN){
                    tokens.set(i,FunctionFactory.makeSinD());
                }else if((Function) tokens.get(i)).getType() == Function.COS){
                    tokens.set(i, FunctionFactory.makeCosD());
                }else if((Function) tokens.get(i)).getType() == Function.TAN){
                    tokens.set(i, FunctionFactory.makeTanD());
                }else if((Function) tokens.get(i)).getType() == Function.ARCSIN){
                    tokens.set(i, FunctionFactory.makeASinD());
                }else if((Function) tokens.get(i)).getType() == Function.ARCCOS){
                    tokens.set(i, FunctionFactory.makeACosD());
                }else if((Function) tokens.get(i)).getType() == Function.ARCTAN){
                    tokens.set(i, FunctionFactory.makeATanD());
                }
            }
        }*/
        DisplayView display = (DisplayView) findViewById(R.id.display);
        try {
            double val = process();
            if (switchedAngleMode) {
                tokens.set(tokens.size() - 1, new Token(" → DEG") {
                });
            } else {
                tokens.add(new Token(" → DEG") {
                });
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
        DisplayView display = (DisplayView) findViewById(R.id.display);
        try {
            double val = process();
            if (switchedAngleMode) {
                tokens.set(tokens.size() - 1, new Token(" → GRAD") {
                });
            } else {
                tokens.add(new Token(" → GRAD") {
                });
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
        DisplayView display = (DisplayView) findViewById(R.id.display);
        try {
            double val = process();
            if (switchedAngleMode) {
                tokens.set(tokens.size() - 1, new Token(" → RAD") {
                });
            } else {
                tokens.add(new Token(" → RAD") {
                });
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
     * @return the fracMode
     */
    public int getFracMode() {
        return fracMode;
    }

    public void clickFracMode(View v) {
        Button fracModeButton = (Button) findViewById(R.id.fracMode);
        if (fracMode == DEC) {
            convDtoM();
            fracMode = MIXED;
            fracModeButton.setText(getString(R.string.mixedFrac));
        } else if (fracMode == IMP) {
            convItoD();
            fracMode = DEC;
            fracModeButton.setText(getString(R.string.radix));
        } else if (fracMode == MIXED) {
            convMtoI();
            fracMode = IMP;
            fracModeButton.setText(getString(R.string.impFrac));
        }
        updateInput();
    }

    public void convMtoI() {
        //Converts the number displayed from a mixed fraction into an improper fraction
    }

    public void convItoD() {
        //Converts the number displayed from an improper fraction into a decimal number
    }

    public void convDtoM() {
        //Converts the number displayed from a decimal number into a mixed fraction
    }

    /**
     * @return the number base
     */
    public int getBase() {
        return base;
    }

    public void clickBase(View v) {
        Button baseButton = (Button) findViewById(R.id.baseButton);
        if (base == DEC) {
            convDecToBin();
            base = BIN;
            baseButton.setText(getString(R.string.base2));
        } else if (base == BIN) {
            convBinToOct();
            base = OCT;
            baseButton.setText(getString(R.string.base8));
        } else if (base == OCT) {
            convOctToHex();
            base = HEX;
            baseButton.setText(getString(R.string.base16));
        } else if (base == HEX) {
            convHexToDec();
            base = DEC;
            baseButton.setText(getString(R.string.base10));
        }
        updateInput();
    }

    public void convDecToBin() {
        //Converts the number displayed from base10 to base2
    }

    public void convBinToOct() {
        //Converts the number displayed from base2 to base8
    }

    public void convOctToHex() {
        //Converts the number displayed from base8 to base16
    }

    public void convHexToDec() {
        //Converts the number displayed from base16 to base10
    }

    /**
     * @return whether or not shift is hyperbolic
     */
    public boolean getHyp() {
        return hyperbolic;
    }

    /**
     * When the user presses the hyp button. Switches the state of the boolean variable hyperbolic
     *
     * @param v Not Used
     */
    public void clickHyp(View v) {
        Button sinButton = (Button) findViewById(R.id.sinButton);
        Button cosButton = (Button) findViewById(R.id.cosButton);
        Button tanButton = (Button) findViewById(R.id.tanButton);
        ToggleButton hypButton = (ToggleButton) findViewById(R.id.hypButton);

        if (hyperbolic) {
            hyperbolic = false;
            if (shift) {
                sinButton.setText("arcsin");
                sinButton.setTextSize(14);
                cosButton.setText("arccos");
                cosButton.setTextSize(14);
                tanButton.setText("arctan");
                tanButton.setTextSize(14);
            } else {
                sinButton.setText("sin");
                sinButton.setTextSize(16);
                cosButton.setText("cos");
                cosButton.setTextSize(16);
                tanButton.setText("tan");
                tanButton.setTextSize(16);
            }
        } else {
            hyperbolic = true;
            if (shift) {
                sinButton.setText("arsinh");
                sinButton.setTextSize(14);
                cosButton.setText("arcosh");
                cosButton.setTextSize(14);
                tanButton.setText("artanh");
                tanButton.setTextSize(14);
            } else {
                sinButton.setText("sinh");
                sinButton.setTextSize(16);
                cosButton.setText("cosh");
                cosButton.setTextSize(16);
                tanButton.setText("tanh");
                tanButton.setTextSize(16);
            }
        }
        hypButton.setChecked(hyperbolic);
        updateInput();
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

            if (hyperbolic) {
                sinButton.setText("sinh");
                sinButton.setTextSize(16);
                cosButton.setText("cosh");
                cosButton.setTextSize(16);
                tanButton.setText("tanh");
                tanButton.setTextSize(16);
            } else {
                sinButton.setText("sin");
                sinButton.setTextSize(16);
                cosButton.setText("cos");
                cosButton.setTextSize(16);
                tanButton.setText("tan");
                tanButton.setTextSize(16);
            }
        } else {
            shift = true;
            powButton.setText(Html.fromHtml(getString(R.string.powOfE)));
            logButton.setText(getString(R.string.ln));
            rootButton.setText(getString(R.string.cbrt));
            squareButton.setText(getString(R.string.cube));
            expButton.setText(Html.fromHtml(getString(R.string.varRoot)));

            if (hyperbolic) {
                sinButton.setText("arsinh");
                sinButton.setTextSize(14);
                cosButton.setText("arcosh");
                cosButton.setTextSize(14);
                tanButton.setText("artanh");
                tanButton.setTextSize(14);
            } else {
                sinButton.setText("arcsin");
                sinButton.setTextSize(14);
                cosButton.setText("arccos");
                cosButton.setTextSize(14);
                tanButton.setText("arctan");
                tanButton.setTextSize(14);
            }
        }
        shiftButton.setChecked(shift);
        updateInput();
    }

    /**
     * When the user presses the MEM button; toggles memory storage
     *
     * @param v Not Used
     */
    public void clickMem(View v) {
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        mem = !mem;
        memButton.setChecked(mem);
    }

    /**
     * When the user presses the A button
     *
     * @param v Not Used
     */
    public void clickA(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                double val = process();
                tokens.clear();
                display.displayOutput(val + "→ A");
                Variable.a_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(display.getRealCursorIndex(), VariableFactory.makeA());
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
    public void clickB(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                double val = process();
                tokens.clear();
                display.displayOutput(val + "→B");
                Variable.b_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(display.getRealCursorIndex(), VariableFactory.makeB());
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
    public void clickC(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                double val = process();
                tokens.clear();
                display.displayOutput(val + "→C");
                Variable.c_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(display.getRealCursorIndex(), VariableFactory.makeC());
                display.setCursorIndex(display.getCursorIndex() + 1);
            }
            updateInput();
        } catch (Exception e) { //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When the user presses the X button
     *
     * @param v Not Used
     */
    public void clickX(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                double val = process();
                tokens.clear();
                display.displayOutput(val + "→X");
                Variable.x_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(display.getRealCursorIndex(), VariableFactory.makeX());
                display.setCursorIndex(display.getCursorIndex() + 1);
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
    public void clickY(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                double val = process();
                tokens.clear();
                display.displayOutput(val + "→Y");
                Variable.y_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(display.getRealCursorIndex(), VariableFactory.makeY());
                display.setCursorIndex(display.getCursorIndex() + 1);
            }
            updateInput();
        } catch (Exception e) { //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When the user presses the Z button
     *
     * @param v Not Used
     */
    public void clickZ(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        ToggleButton memButton = (ToggleButton) findViewById(R.id.memButton);
        try {
            if (mem) {
                double val = process();
                tokens.clear();
                display.displayOutput(val + "→Z");
                Variable.z_value = val;
                mem = false;
                memButton.setChecked(mem);
            } else {
                tokens.add(display.getRealCursorIndex(), VariableFactory.makeZ());
                display.setCursorIndex(display.getCursorIndex() + 1);
            }
            updateInput();
        } catch (Exception e) { //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * When the user presses the ( Button.
     *
     * @param v Not Used
     */
    public void clickOpenBracket(View v) {
        tokens.add(display.getRealCursorIndex(), BracketFactory.makeOpenBracket());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the ) Button.
     *
     * @param v Not Used
     */
    public void clickCloseBracket(View v) {
        tokens.add(display.getRealCursorIndex(), BracketFactory.makeCloseBracket());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the 10^x Button.
     *
     * @param v Not Used
     */
    public void clickPowerOfTen(View v) {
        Token powTen = OperatorFactory.makePowOfTen();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();

        tokens.add(display.getRealCursorIndex(), powTen);
        tokens.add(display.getRealCursorIndex() + 1, openBracket);
        tokens.add(display.getRealCursorIndex() + 2, PlaceholderFactory.makeBlock());
        tokens.add(display.getRealCursorIndex() + 3, closeBracket);

        powTen.addDependency(openBracket);
        powTen.addDependency(closeBracket);
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }


    /**
     * When the user presses the e^x Button.
     *
     * @param v Not Used
     */
    public void clickExp(View v) {
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeExp());
        display.setCursorIndex(display.getCursorIndex() + 1);
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
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeLn());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the log(x) or log_10(x)Button.
     *
     * @param v Not Used
     */
    public void clickLog_10(View v) {
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeLog_10());
        display.setCursorIndex(display.getCursorIndex() + 1);
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
        Token exponent = OperatorFactory.makeExponent();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();

        tokens.add(display.getRealCursorIndex(), exponent);
        tokens.add(display.getRealCursorIndex() + 1, openBracket);
        tokens.add(display.getRealCursorIndex() + 2, PlaceholderFactory.makeBlock());
        tokens.add(display.getRealCursorIndex() + 3, closeBracket);

        exponent.addDependency(openBracket);
        exponent.addDependency(closeBracket);

        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    public void clickVarRoot(View v) {
        Token root = OperatorFactory.makeVariableRoot();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();

        tokens.add(display.getRealCursorIndex(), openBracket);
        tokens.add(display.getRealCursorIndex() + 1, PlaceholderFactory.makeBlock());
        tokens.add(display.getRealCursorIndex() + 2, closeBracket);
        tokens.add(display.getRealCursorIndex() + 3, root);

        root.addDependency(openBracket);
        root.addDependency(closeBracket);

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
        Token exponent = OperatorFactory.makeExponent();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();

        tokens.add(display.getRealCursorIndex(), exponent);
        tokens.add(display.getRealCursorIndex() + 1, openBracket);
        tokens.add(display.getRealCursorIndex() + 2, DigitFactory.makeTwo());
        tokens.add(display.getRealCursorIndex() + 3, closeBracket);

        exponent.addDependency(openBracket);
        exponent.addDependency(closeBracket);

        display.setCursorIndex(display.getCursorIndex() + 3);
        updateInput();
    }

    /**
     * When the user presses the x^3 Button.
     *
     * @param v Not Used
     */
    public void clickCube(View v) {
        Token exponent = OperatorFactory.makeExponent();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();

        tokens.add(display.getRealCursorIndex(), exponent);
        tokens.add(display.getRealCursorIndex() + 1, openBracket);
        tokens.add(display.getRealCursorIndex() + 2, DigitFactory.makeThree());
        tokens.add(display.getRealCursorIndex() + 3, closeBracket);

        exponent.addDependency(openBracket);
        exponent.addDependency(closeBracket);

        display.setCursorIndex(display.getCursorIndex() + 3);
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
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeCbrt());
        display.setCursorIndex(display.getCursorIndex() + 1);
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
     * When the user presses the FRAC Button.
     *
     * @param v Not Used
     */
    public void clickFrac(View v) {
        Token frac = OperatorFactory.makeFraction();
        Token numOpenBracket = BracketFactory.makeNumOpen();
        Token numCloseBracket = BracketFactory.makeNumClose();
        Token denomOpenBracket = BracketFactory.makeDenomOpen();
        Token denomCloseBracket = BracketFactory.makeDenomClose();

        frac.addDependency(numOpenBracket);
        frac.addDependency(numCloseBracket);
        frac.addDependency(denomOpenBracket);
        frac.addDependency(denomCloseBracket);


        if (display.getRealCursorIndex() == 0) {
            tokens.add(display.getRealCursorIndex(), numOpenBracket);
            tokens.add(display.getRealCursorIndex() + 1, PlaceholderFactory.makeBlock());
            display.setCursorIndex(display.getCursorIndex() + 1);
        } else {
            //Whats on the numerator depends on the token before
            Token tokenBefore = tokens.get(display.getRealCursorIndex() - 1);
            if (tokenBefore instanceof Digit) {
                LinkedList<Digit> digits = new LinkedList<Digit>();
                int i = display.getRealCursorIndex() - 1;
                while (i >= 0 && tokens.get(i) instanceof Digit) {
                    Token t = tokens.get(i);
                    digits.addFirst((Digit) t);
                    tokens.remove(t);
                    i--;
                }
                tokens.add(display.getRealCursorIndex() - digits.size(), numOpenBracket);
                tokens.addAll(display.getRealCursorIndex() - digits.size() + 1, digits);

                tokens.add(display.getRealCursorIndex() + 1, numCloseBracket);
                tokens.add(display.getRealCursorIndex() + 2, frac);
                tokens.add(display.getRealCursorIndex() + 3, denomOpenBracket);
                Placeholder p = PlaceholderFactory.makeBlock();
                tokens.add(display.getRealCursorIndex() + 4, p);
                tokens.add(display.getRealCursorIndex() + 5, denomCloseBracket);
                frac.addDependency(p);
                display.setCursorIndex(display.getCursorIndex() + 2);
                return;
            } else if (tokenBefore instanceof Bracket && ((Bracket) tokenBefore).getType() == Bracket.CLOSE) {
                LinkedList<Token> expression = new LinkedList<Token>();
                int i = display.getRealCursorIndex() - 2;
                int bracketCount = 1;
                expression.add(tokens.remove(display.getRealCursorIndex() - 1));
                while (i >= 0 && bracketCount != 0) {
                    Token t = tokens.remove(i);
                    if (t instanceof Bracket) {
                        Bracket b = (Bracket) t;
                        if (b.getType() == Bracket.OPEN) {
                            bracketCount--;
                        } else if (b.getType() == Bracket.CLOSE) {
                            bracketCount++;
                        }
                    }
                    expression.addFirst(t);
                    i--;
                }
                tokens.add(i + 1, numOpenBracket);
                tokens.addAll(i + 2, expression);

                tokens.add(display.getRealCursorIndex() + 1, numCloseBracket);
                tokens.add(display.getRealCursorIndex() + 2, frac);
                tokens.add(display.getRealCursorIndex() + 3, denomOpenBracket);
                Placeholder p = PlaceholderFactory.makeBlock();
                tokens.add(display.getRealCursorIndex() + 4, p);
                tokens.add(display.getRealCursorIndex() + 5, denomCloseBracket);
                frac.addDependency(p);
                display.setCursorIndex(display.getCursorIndex() + 2);
                return;

            } else {
                tokens.add(display.getRealCursorIndex(), numOpenBracket);
                Placeholder p = PlaceholderFactory.makeBlock();
                tokens.add(display.getRealCursorIndex() + 1, p);
                frac.addDependency(p);
            }
        }
        tokens.add(display.getRealCursorIndex() + 2, numCloseBracket);
        tokens.add(display.getRealCursorIndex() + 3, frac);
        tokens.add(display.getRealCursorIndex() + 4, denomOpenBracket);
        Placeholder p = PlaceholderFactory.makeBlock();
        tokens.add(display.getRealCursorIndex() + 5, p);
        tokens.add(display.getRealCursorIndex() + 6, denomCloseBracket);
        display.setCursorIndex(display.getCursorIndex() + 1);
        frac.addDependency(p);
        updateInput();
    }

    /**
     * When the user presses the ceiling(x) Button.
     *
     * @param v Not Used
     */
    public void clickCeiling(View v) {
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeCeiling());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the floor(x) Button.
     *
     * @param v Not Used
     */
    public void clickFloor(View v) {
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeFloor());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the |x| or abs(x) Button.
     *
     * @param v Not Used
     */
    public void clickAbs(View v) {
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeAbs());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the x^-1 button.
     *
     * @param v Not Used
     */
    public void clickReciprocal(View v) {
        Token exponent = OperatorFactory.makeExponent();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();

        tokens.add(display.getRealCursorIndex(), exponent);
        tokens.add(display.getRealCursorIndex() + 1, openBracket);
        tokens.add(display.getRealCursorIndex() + 2, DigitFactory.makeNegative());
        tokens.add(display.getRealCursorIndex() + 3, DigitFactory.makeOne());
        tokens.add(display.getRealCursorIndex() + 4, closeBracket);

        exponent.addDependency(openBracket);
        exponent.addDependency(closeBracket);

        display.setCursorIndex(display.getCursorIndex() + 4);
        updateInput();
        updateInput();
    }

    /**
     * When the user presses the n! Button.
     *
     * @param v Not Used
     */
    public void clickFactorial(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeFactorial());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the nCk Button.
     *
     * @param v Not Used
     */
    public void clickCombination(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeCombination());
        display.setCursorIndex(display.getCursorIndex() + 1);

        updateInput();
    }

    /**
     * When the user presses the nPk Button.
     *
     * @param v Not Used
     */
    public void clickPermutation(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makePermutation());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the e Button.
     *
     * @param v Not Used
     */
    public void clickE(View v) {
        tokens.add(display.getRealCursorIndex(), VariableFactory.makeE());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the pi Button.
     *
     * @param v Not Used
     */
    public void clickPi(View v) {
        tokens.add(display.getRealCursorIndex(), VariableFactory.makePI());
        display.setCursorIndex(display.getCursorIndex() + 1);
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
        if (hyperbolic && shift) {
            clickASinh(v);
        } else if (hyperbolic) {
            clickSinh(v);
        } else if (shift) {
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
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeSinD());
        } else if (angleMode == RADIAN) {
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeSinR());
        } else if (angleMode == GRADIAN) {
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeSinG());
        }
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the arcsin(x) or sin^-1(x) Button.
     *
     * @param v Not Used
     */
    public void clickASin(View v) {
        if (angleMode == DEGREE) {
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeASinD());
        } else if (angleMode == RADIAN) {
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeASinR());
        } else if (angleMode == GRADIAN) {
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeASinG());
        }
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the cos(x) button.
     * Picks the correct function based
     * on the modifiers which are currently enabled
     *
     * @param v Not Used
     */
    public void clickCos(View v) {
        if (hyperbolic && shift) {
            clickACosh(v);
        } else if (hyperbolic) {
            clickCosh(v);
        } else if (shift) {
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
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeCosD());
        } else if (angleMode == RADIAN) {
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeCosR());
        } else if (angleMode == GRADIAN) {
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeCosG());
        }
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the arccos(x) or cos^-1(x) Button.
     *
     * @param v Not Used
     */
    public void clickACos(View v) {
        if (angleMode == DEGREE) {
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeACosD());
        } else if (angleMode == RADIAN) {
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeACosR());
        } else if (angleMode == GRADIAN) {
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeACosG());
        }
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the cos(x) button.
     * Picks the correct function based
     * on the modifiers which are currently enabled
     *
     * @param v Not Used
     */
    public void clickTan(View v) {
        if (hyperbolic && shift) {
            clickATanh(v);
        } else if (hyperbolic) {
            clickTanh(v);
        } else if (shift) {
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
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeTanD());
        } else if (angleMode == RADIAN) {
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeTanR());
        } else if (angleMode == GRADIAN) {
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeTanG());
        }
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the arctan(x) or tan^-1(x) Button.
     *
     * @param v Not Used
     */
    public void clickATan(View v) {
        if (angleMode == DEGREE) {
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeATanD());
        } else if (angleMode == RADIAN) {
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeATanR());
        } else if (angleMode == GRADIAN) {
            tokens.add(display.getRealCursorIndex(), FunctionFactory.makeATanG());
        }
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the sinh(x) Button.
     *
     * @param v Not Used
     */
    public void clickSinh(View v) {
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeSinh());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the arsinh(x) Button.
     *
     * @param v Not Used
     */
    public void clickASinh(View v) {
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeASinh());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the cosh(x) Button.
     *
     * @param v Not Used
     */
    public void clickCosh(View v) {
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeCosh());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the arcosh(x) Button.
     *
     * @param v Not Used
     */
    public void clickACosh(View v) {
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeACosh());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the tanh(x) Button.
     *
     * @param v Not Used
     */
    public void clickTanh(View v) {
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeTanh());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the artanh(x) Button.
     *
     * @param v Not Used
     */
    public void clickATanh(View v) {
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeATanh());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }
}