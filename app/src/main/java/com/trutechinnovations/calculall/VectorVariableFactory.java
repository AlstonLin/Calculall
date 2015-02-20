package com.trutechinnovations.calculall;

/**
 * The Factory that creates instances of Vector Variables.
 *
 * @author Alston Lin
 * @version Alpha 2.2
 */
public class VectorVariableFactory {

    public static VectorVariable makeU() {
        return new VectorVariable(VectorVariable.U, "U");
    }

    public static VectorVariable makeV() {
        return new VectorVariable(VectorVariable.V, "V");
    }

    public static VectorVariable makeS() {
        return new VectorVariable(VectorVariable.S, "s");
    }

    public static VectorVariable makeT() {
        return new VectorVariable(VectorVariable.T, "t");
    }
}
