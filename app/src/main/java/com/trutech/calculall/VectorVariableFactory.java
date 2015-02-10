package com.trutech.calculall;

/**
 * The Factory that creates instances of Vector Variables.
 *
 * @version Alpha 2.2
 * @author Alston Lin
 */
public class VectorVariableFactory {

    public static VectorVariable makeU() {
        return new VectorVariable(VectorVariable.U, "U");
    }

    public static VectorVariable makeV() {
        return new VectorVariable(VectorVariable.V, "V");
    }

    public static VectorVariable makeT() {
        return new VectorVariable(VectorVariable.T, "t");
    }
}
