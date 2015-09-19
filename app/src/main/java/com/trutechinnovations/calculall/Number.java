package com.trutechinnovations.calculall;

import java.io.Serializable;

/**
 * This is the class for the main activity (entry-point to the app). It will simply configure
 * the setting then go to the basic activity.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class Number extends Token implements Serializable {

    private static final long serialVersionUID = 752647223;
    public static int roundTo = 9;
    private double value;

    /**
     * Only to be used during expression simplification for the purpose of efficiency. Stores
     * multiple digits into a single piece with the total value of the digits stored.
     */
    protected Number(double value) {
        super(null); //No image for this; it should never be shown on screen
        this.value = value;
        this.type = -1;
    }

    /**
     * @return The value of the number
     */
    public double getValue() {
        return value;
    }

    public String getSymbol() {
        if (Double.isInfinite(value) || Double.isNaN(value)){
            return ("A Really Big Number");
        }
        //Rounds
        value = Utility.round(value, roundTo);
        String s = Double.toString(value);
        s = !s.contains(".") ? s : (s.indexOf("E") > 0 ? s.substring(0, s.indexOf("E")).replaceAll("0*$", "")
                .replaceAll("\\.$", "").concat(s.substring(s.indexOf("E"))) : s.replaceAll("0*$", "")
                .replaceAll("\\.$", "")); //Removes trailing zeroes
        return s;
    }

    public String toLaTeX() {
        return "$" + getSymbol() + "$";
    }
}