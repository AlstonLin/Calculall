package com.trutech.calculall;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * This is the class for the main activity (entry-point to the app). It will simply configure
 * the setting then go to the basic activity.
 *
 * @author Alston Lin
 * @version Alpha 2.0
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
    }

    /**
     * @return The value of the number
     */
    public double getValue() {
        return value;
    }

    public String getSymbol() {
        //Removes trailing zeroes
        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(Locale.CANADA);
        formatter.setMaximumFractionDigits(roundTo);
        return formatter.format(value);
    }
}