package com.trutechinnovations.calculall;

import java.io.Serializable;

/**
 * Represents a Vector for Vector Mode.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class Vector extends Token implements Serializable {
    private double[] values;

    public Vector(double[] values) {
        super(null);
        this.values = values;
    }

    public double[] getValues() {
        return values;
    }

    public int getDimensions() {
        return values.length;
    }

    /**
     * Determines whats gets displayed onto the Screen.
     *
     * @return The visual representation of the Vector
     */
    public String getSymbol() {
        String s = "[";
        for (int i = 0; i < values.length; i++) {
            if (i != 0) {
                s += ", ";
            }
            double value = Utility.round(values[i], Number.roundTo);
            String string = Double.toString(value);
            string = !string.contains(".") ? string : (string.indexOf("E") > 0 ? string.substring(0, string.indexOf("E")).replaceAll("0*$", "")
                    .replaceAll("\\.$", "").concat(string.substring(string.indexOf("E"))) : string.replaceAll("0*$", "")
                    .replaceAll("\\.$", "")); //Removes trailing zeroes
            s += string;
        }
        s += "]";
        return s;
    }

    public String getLaTeX() {
        String output = "$" + "\\begin{bmatrix}";
        for (int i = 0; i < values.length; i++) {
            output += (new Double(values[i])).toString();
            output += "\\\\";
        }
        output += "\\end{bmatrix}" + "$";
        return output;
    }

}