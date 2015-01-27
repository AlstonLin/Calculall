package com.trutech.calculall;

import java.io.Serializable;

/**
 * Created by Alston on 1/13/2015.
 */
public class Vector extends Token implements Serializable{
    private double[] values;

    public Vector(double[] values){
        super(null);
        this.values = values;
    }

    public double[] getValues(){
        return values;
    }

    public int getDimensions(){
        return values.length;
    }
}
