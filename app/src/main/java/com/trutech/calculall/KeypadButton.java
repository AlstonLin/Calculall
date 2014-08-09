package com.trutech.calculall;

/**
 * For displaying and finding out what the button is
 *
 * Created by david_000 on 7/6/2014.
 */
public enum KeypadButton {
    
    MC(Utility.MC , "MC",KeypadButtonCategory.MEMORYBUFFER)
    , MR(Utility.MR, "MR",KeypadButtonCategory.MEMORYBUFFER)
    , MS(Utility.MS, "MS",KeypadButtonCategory.MEMORYBUFFER)
    , M_ADD(Utility.M_ADD, "M+",KeypadButtonCategory.MEMORYBUFFER)
    , M_REMOVE(Utility.M_REMOVE, "M-",KeypadButtonCategory.MEMORYBUFFER)
    , BACKSPACE(Utility.BACKSPACE, "<-",KeypadButtonCategory.CLEAR)
    , CE(Utility.CE, "CE",KeypadButtonCategory.CLEAR)
    , C(Utility.C, "C",KeypadButtonCategory.CLEAR)
    , ZERO(Utility.ZERO, "0",KeypadButtonCategory.NUMBER)
    , ONE(Utility.ONE, "1",KeypadButtonCategory.NUMBER)
    , TWO(Utility.TWO, "2",KeypadButtonCategory.NUMBER)
    , THREE(Utility.THREE, "3",KeypadButtonCategory.NUMBER)
    , FOUR(Utility.FOUR, "4", KeypadButtonCategory.NUMBER)
    , FIVE(Utility.FIVE, "5",KeypadButtonCategory.NUMBER)
    , SIX(Utility.SIX, "6",KeypadButtonCategory.NUMBER)
    , SEVEN(Utility.SEVEN, "7",KeypadButtonCategory.NUMBER)
    , EIGHT(Utility.EIGHT, "8",KeypadButtonCategory.NUMBER)
    , NINE(Utility.NINE, "9",KeypadButtonCategory.NUMBER)
    , PLUS(Utility.PLUS, " + ",KeypadButtonCategory.OPERATOR)
    , MINUS(Utility.MINUS, " - ",KeypadButtonCategory.OPERATOR)
    , MULTIPLY(Utility.MULTIPLY, " * ",KeypadButtonCategory.OPERATOR)
    , DIV(Utility.DIV, " / ",KeypadButtonCategory.OPERATOR)
    , RECIPROC(Utility.RECIPROC, "1/x",KeypadButtonCategory.OTHER)
    , DECIMAL_SEP(Utility.DECIMAL_SEP, ".",KeypadButtonCategory.OTHER)
    //, SIGN(Utility.SIGN, "�",KeypadButtonCategory.OTHER)
    , SQRT(Utility.SQRT, "SQRT",KeypadButtonCategory.OTHER)
    , PERCENT(Utility.PERCENT, "%",KeypadButtonCategory.OTHER)
    , CALCULATE(Utility.CALCULATE, "=",KeypadButtonCategory.RESULT)
    , DUMMY(Utility.DUMMY, "",KeypadButtonCategory.DUMMY);
    
    private int ID; //ID of the button
    private CharSequence mText; // Display Text
    
    private KeypadButton(int ID, CharSequence text,KeypadButtonCategory category) {
    	this.ID = ID;
        mText = text;
    }

    /**
     *
     * @return the Number/Sign it is representing
     */
    public CharSequence getText() {
        return mText;
    }
    
    /**
     * 
     * @return The ID of the Button
     */
    public int getID() {
		return ID;
	}

	/**
     * Class that defines what type of the Button is representing
     */
    public enum KeypadButtonCategory {
        MEMORYBUFFER
        , NUMBER
        , OPERATOR
        , DUMMY
        , CLEAR
        , RESULT
        , OTHER
    }
}
