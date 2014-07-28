package com.trutech.calculall;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;


/**
 * The activity for the advanced calculator mode. The advanced mode will be able to
 * perform the most of the operations of a standard scientific calculator.
 *
 * @version 0.3.0
 */
public class Advanced extends Basic{
	private int angleMode = 1;
	public static final int DEGREE = 1, RADIAN = 2, GRADIAN = 3; //angleMode options
	private int base = 1;
	public static final int DEC = 1, BIN = 2, OCT = 3, HEX = 4;//number bases
	private int fracMode = DEC;
	public static final int MIXED = 2, IMP = 3;
	private boolean hyperbolic = false;
	private boolean shift = false;
	private boolean mem = false;
	private Button sinButton, cosButton, tanButton, powButton, logButton, rootButton, squareButton, expButton, angleModeButton, baseButton, fracModeButton;
	private ToggleButton hypButton, shiftButton, memButton;
	private TextView output;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.activity_advanced);
		sinButton = (Button)findViewById(R.id.sinButton);
		cosButton = (Button)findViewById(R.id.cosButton);
		tanButton = (Button)findViewById(R.id.tanButton);
		powButton = (Button)findViewById(R.id.powButton);
		expButton = (Button)findViewById(R.id.powerButton);
		logButton = (Button)findViewById(R.id.logButton);
		rootButton = (Button)findViewById(R.id.rootButton);
		squareButton = (Button)findViewById(R.id.squareButton);
		angleModeButton = (Button)findViewById(R.id.angleMode);
		baseButton = (Button)findViewById(R.id.baseButton);
		fracModeButton = (Button)findViewById(R.id.fracMode);
		hypButton = (ToggleButton)findViewById(R.id.hypButton);
		shiftButton = (ToggleButton)findViewById(R.id.shiftButton);
		memButton = (ToggleButton)findViewById(R.id.memButton);
		powButton.setText(Html.fromHtml(getString(R.string.powOfTen)));
		expButton.setText(Html.fromHtml(getString(R.string.exponent)));
		output = (TextView) findViewById(R.id.txtStack);
	}

	/**
	 * @return the angleMode
	 */
	public int getAngleMode() {
		return angleMode;
	}
	
	public void clickAngleMode(View v){
		if(angleMode==GRADIAN){
			convGtoD();
			angleMode=DEGREE;
			angleModeButton.setText(getString(R.string.deg));
		}else if(angleMode==RADIAN){
			convRtoG();
			angleMode=GRADIAN;
			angleModeButton.setText(getString(R.string.grad));
		}else if(angleMode==DEGREE){
			convDtoR();
			angleMode=RADIAN;
			angleModeButton.setText(getString(R.string.rad));
		}
		updateInput();
	}
	
	public void convGtoD(){
		//Converts the number displayed from gradians into degrees ie multiplies the number by 9/10
	}
	
	public void convRtoG(){
		//Converts the number displayed from radians into gradians ie multiplies the number by 100/pi
	}
	
	public void convDtoR(){
		//Converts the number displayed from degrees into radians ie multiplies the number by pi/180
	}
	
	/**
	 * @return the fracMode
	 */
	public int getFracMode() {
		return fracMode;
	}
	
	public void clickFracMode(View v){
		if(fracMode==DEC){
			convDtoM();
			fracMode=MIXED;
			fracModeButton.setText(getString(R.string.mixedFrac));
		}else if(fracMode==IMP){
			convItoD();
			fracMode=DEC;
			fracModeButton.setText(getString(R.string.radix));
		}else if(fracMode==MIXED){
			convMtoI();
			fracMode=IMP;
			fracModeButton.setText(getString(R.string.impFrac));
		}
		updateInput();
	}
	
	public void convMtoI(){
		//Converts the number displayed from a mixed fraction into an improper fraction
	}
	
	public void convItoD(){
		//Converts the number displayed from an improper fraction into a decimal number
	}
	
	public void convDtoM(){
		//Converts the number displayed from a decimal number into a mixed fraction
	}
	
	/**
	 * @return the number base
	 */
	public int getBase() {
		return base;
	}
	
	public void clickBase(View v){
		if(base==DEC){
			convDecToBin();
			base=BIN;
			baseButton.setText(getString(R.string.base2));
		}else if(base==BIN){
			convBinToOct();
			base=OCT;
			baseButton.setText(getString(R.string.base8));
		}else if(base==OCT){
			convOctToHex();
			base=HEX;
			baseButton.setText(getString(R.string.base16));
		}else if(base==HEX){
			convHexToDec();
			base=DEC;
			baseButton.setText(getString(R.string.base10));
		}
		updateInput();
	}
	
	public void convDecToBin(){
		//Converts the number displayed from base10 to base2
	}
	
	public void convBinToOct(){
		//Converts the number displayed from base2 to base8
	}
	
	public void convOctToHex(){
		//Converts the number displayed from base8 to base16
	}
	
	public void convHexToDec(){
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
	public void clickHyp(View v){
		if(hyperbolic){
			hyperbolic = false;
			if(shift){
				sinButton.setText("arcsin");
				cosButton.setText("arccos");
				tanButton.setText("arctan");
			}else{
				sinButton.setText("sin");
				cosButton.setText("cos");
				tanButton.setText("tan");
			}
		}else{
			hyperbolic = true;
			if(shift){
				sinButton.setText("arsinh");
				cosButton.setText("arcosh");
				tanButton.setText("artanh");
			}else{
				sinButton.setText("sinh");
				cosButton.setText("cosh");
				tanButton.setText("tanh");
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
	public void clickShift(View v){
		if(shift){
			shift = false;
			powButton.setText(Html.fromHtml(getString(R.string.powOfTen)));
			logButton.setText(getString(R.string.log10));
			rootButton.setText(getString(R.string.sqrt));
			squareButton.setText(getString(R.string.square));
			expButton.setText(Html.fromHtml(getString(R.string.exponent)));
			
			if(hyperbolic){
				sinButton.setText("sinh");
				cosButton.setText("cosh");
				tanButton.setText("tanh");
			}else{
				sinButton.setText("sin");
				cosButton.setText("cos");
				tanButton.setText("tan");
			}
		}else{
			shift = true;
			powButton.setText(Html.fromHtml(getString(R.string.powOfE)));
			logButton.setText(getString(R.string.ln));
			rootButton.setText(getString(R.string.cbrt));
			squareButton.setText(getString(R.string.cube));
			expButton.setText(Html.fromHtml(getString(R.string.varRoot)));
			
			if(hyperbolic){
				sinButton.setText("arsinh");
				cosButton.setText("arcosh");
				tanButton.setText("artanh");
			}else{
				sinButton.setText("arcsin");
				cosButton.setText("arccos");
				tanButton.setText("arctan");
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
     * When the user presses the ( Button.
     * 
     * @param v Not Used
     */
	public void clickOpenBracket(View v){
		tokens.add(BracketFactory.createOpenBracket());
		updateInput();
	}

	/**
     * When the user presses the ) Button.
     * 
     * @param v Not Used
     */
	public void clickCloseBracket(View v){
		tokens.add(BracketFactory.createCloseBracket());
		updateInput();
	}

	/**
     * When the user presses the 10^x Button.
     * 
     * @param v Not Used
     */
	public void clickPowerOfTen(View v){
		tokens.add(FunctionFactory.makePowOfTen());
		updateInput();
	}


	/**
     * When the user presses the e^x Button.
     * 
     * @param v Not Used
     */
	public void clickExp(View v){
		tokens.add(FunctionFactory.makeExp());
		updateInput();
	}
	
	/**
     * When the user presses the C^x button. Where C is either 10 or e
     * 
     * @param v Not Used
     */
	public void clickPower(View v){
		if(!shift){
			clickPowerOfTen(v);
		}else{
			clickExp(v);
		}
		updateInput();
	}

	/**
     * When the user presses the ln(x) Button.
     * 
     * @param v Not Used
     */
	public void clickLn(View v){
		tokens.add(FunctionFactory.makeLn());
		updateInput();
	}

	/**
     * When the user presses the log(x) or log_10(x)Button.
     * 
     * @param v Not Used
     */
	public void clickLog_10(View v){
		tokens.add(FunctionFactory.makeLog_10());
		updateInput();
	}

	
	/**
     * When the user presses the log/ln button.
     * 
     * @param v Not Used
     */
	public void clickLog(View v){
		if(!shift){
			clickLog_10(v);
		}else{
			clickLn(v);
		}
		updateInput();
	}
	
	public void clickExponent(View v){
		tokens.add(OperatorFactory.makeExponent());
		updateInput();
	}
	
	public void clickVarRoot(View v){
		tokens.add(OperatorFactory.makeVariableRoot());
		updateInput();
	}
	
	public void clickPow(View v){
		if(!shift){
			clickExponent(v);
		}else{
			clickVarRoot(v);
		}
		updateInput();
	}

	/**
     * When the user presses the x^2 Button.
     * 
     * @param v Not Used
     */
	public void clickSquare(View v){
		tokens.add(FunctionFactory.makeSquare());
		updateInput();
	}

	/**
     * When the user presses the x^3 Button.
     * 
     * @param v Not Used
     */
	public void clickCube(View v){
		tokens.add(FunctionFactory.makeCube());
		updateInput();
	}
	
	/**
     * When the user presses the square or cube button.
     * 
     * @param v Not Used
     */
	public void clickSmallExp(View v){
		if(!shift){
			clickSquare(v);
		}else{
			clickCube(v);
		}
		updateInput();
	}

	/**
     * When the user presses the cuberoot Button.
     * 
     * @param v Not Used
     */
	public void clickCbrt(View v){
		tokens.add(FunctionFactory.makeCbrt());
		updateInput();
	}
	
	/**
     * When the user presses the root button.
     * 
     * @param v Not Used
     */
	public void clickRoot(View v){
		if(!shift){
			clickSqrt(v);
		}else{
			clickCbrt(v);
		}
		updateInput();
	}

	/**
     * When the user presses the ceiling(x) Button.
     * 
     * @param v Not Used
     */
	public void clickCeiling(View v){
		tokens.add(FunctionFactory.makeCeiling());
		updateInput();
	}

	/**
     * When the user presses the floor(x) Button.
     * 
     * @param v Not Used
     */
	public void clickFloor(View v){
		tokens.add(FunctionFactory.makeFloor());
		updateInput();
	}

	/**
     * When the user presses the |x| or abs(x) Button.
     * 
     * @param v Not Used
     */
	public void clickAbs(View v){
		tokens.add(FunctionFactory.makeAbs());
		updateInput();
	}
	
	/**
     * When the user presses the x^-1 button.
     * 
     * @param v Not Used
     */
	public void clickReciprocal(View v){
		tokens.add(FunctionFactory.makeReciprocal());
		updateInput();
	}

	/**
     * When the user presses the n! Button.
     * 
     * @param v Not Used
     */
	public void clickFactorial(View v){
		tokens.add(OperatorFactory.makeFactorial());
		updateInput();
	}
	
	/**
     * When the user presses the nCk Button.
     * 
     * @param v Not Used
     */
	public void clickCombination(View v){
		tokens.add(OperatorFactory.makeCombination());
		updateInput();
	}
	
	/**
     * When the user presses the nPk Button.
     * 
     * @param v Not Used
     */
	public void clickPermutation(View v){
		tokens.add(OperatorFactory.makePermutation());
		updateInput();
	}

	/**
     * When the user presses the e Button.
     * 
     * @param v Not Used
     */
	public void clickE(View v){
		tokens.add(VariableFactory.makeE());
		updateInput();
	}

	/**
     * When the user presses the pi Button.
     * 
     * @param v Not Used
     */
	public void clickPi(View v){
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
	public void clickSin(View v){
		if(hyperbolic&&shift){
			clickASinh(v);
		}else if(hyperbolic){
			clickSinh(v);
		}else if(shift){
			clickASin(v);
		}else{
			clickSin1(v);
		}
		updateInput();
	}
	
	/**
     * When the user presses the sin(x) Button.
     * 
     * @param v Not Used
     */
	public void clickSin1(View v){
		if (angleMode == DEGREE){
			tokens.add(FunctionFactory.makeSinD());
		}else if (angleMode == RADIAN){
			tokens.add(FunctionFactory.makeSinR());
		}else if (angleMode == GRADIAN){
			tokens.add(FunctionFactory.makeSinG());
		}
	}

	/**
     * When the user presses the arcsin(x) or sin^-1(x) Button.
     * 
     * @param v Not Used
     */
	public void clickASin(View v){
		if (angleMode == DEGREE){
			tokens.add(FunctionFactory.makeASinD());
		}else if (angleMode == RADIAN){
			tokens.add(FunctionFactory.makeASinR());
		}else if (angleMode == GRADIAN){
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
	public void clickCos(View v){
		if(hyperbolic&&shift){
			clickACosh(v);
		}else if(hyperbolic){
			clickCosh(v);
		}else if(shift){
			clickACos(v);
		}else{
			clickCos1(v);
		}
		updateInput();
	}

	/**
     * When the user presses the cos(x) Button.
     * 
     * @param v Not Used
     */
	public void clickCos1(View v){
		if (angleMode == DEGREE){
			tokens.add(FunctionFactory.makeCosD());
		}else if (angleMode == RADIAN){
			tokens.add(FunctionFactory.makeCosR());
		}else if (angleMode == GRADIAN){
			tokens.add(FunctionFactory.makeCosG());
		}
	}

	/**
     * When the user presses the arccos(x) or cos^-1(x) Button.
     * 
     * @param v Not Used
     */
	public void clickACos(View v){
		if (angleMode == DEGREE){
			tokens.add(FunctionFactory.makeACosD());
		}else if (angleMode == RADIAN){
			tokens.add(FunctionFactory.makeACosR());
		}else if (angleMode == GRADIAN){
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
	public void clickTan(View v){
		if(hyperbolic&&shift){
			clickATanh(v);
		}else if(hyperbolic){
			clickTanh(v);
		}else if(shift){
			clickATan(v);
		}else{
			clickTan1(v);
		}
		updateInput();
	}
	
	/**
     * When the user presses the tan(x) Button.
     * 
     * @param v Not Used
     */
	public void clickTan1(View v){
		if (angleMode == DEGREE){
			tokens.add(FunctionFactory.makeTanD());
		}else if (angleMode == RADIAN){
			tokens.add(FunctionFactory.makeTanR());
		}else if (angleMode == GRADIAN){
			tokens.add(FunctionFactory.makeTanG());
		}
	}

	/**
     * When the user presses the arctan(x) or tan^-1(x) Button.
     * 
     * @param v Not Used
     */
	public void clickATan(View v){
		if (angleMode == DEGREE){
			tokens.add(FunctionFactory.makeATanD());
		}else if (angleMode == RADIAN){
			tokens.add(FunctionFactory.makeATanR());
		}else if (angleMode == GRADIAN){
			tokens.add(FunctionFactory.makeATanG());
		}
	}

	/**
     * When the user presses the sinh(x) Button.
     * 
     * @param v Not Used
     */
	public void clickSinh(View v){
		tokens.add(FunctionFactory.makeSinh());
		updateInput();
	}
	
	/**
     * When the user presses the arsinh(x) Button.
     * 
     * @param v Not Used
     */
	public void clickASinh(View v){
		tokens.add(FunctionFactory.makeASinh());
		updateInput();
	}

	/**
     * When the user presses the cosh(x) Button.
     * 
     * @param v Not Used
     */
	public void clickCosh(View v){
		tokens.add(FunctionFactory.makeCosh());
		updateInput();
	}
	
	/**
     * When the user presses the arcosh(x) Button.
     * 
     * @param v Not Used
     */
	public void clickACosh(View v){
		tokens.add(FunctionFactory.makeACosh());
		updateInput();
	}

	/**
     * When the user presses the tanh(x) Button.
     * 
     * @param v Not Used
     */
	public void clickTanh(View v){
		tokens.add(FunctionFactory.makeTanh());
		updateInput();
	}
	
	/**
     * When the user presses the artanh(x) Button.
     * 
     * @param v Not Used
     */
	public void clickATanh(View v){
		tokens.add(FunctionFactory.makeATanh());
		updateInput();
	}
}