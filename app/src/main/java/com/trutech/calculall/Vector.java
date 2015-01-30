package com.trutech.calculall;

import java.io.Serializable;

/**
 * Represents a Vector for Vector Mode.
 *
 * @author Alston Lin
 * @version Alpha 2.0
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
}
