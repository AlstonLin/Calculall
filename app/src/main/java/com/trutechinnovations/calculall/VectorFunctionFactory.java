package com.trutechinnovations.calculall;

/**
 * Contains static factory methods for Functions used by Vector Mode.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class VectorFunctionFactory {
    public static VectorFunction makeMagnitude() {
        return new VectorFunction("MAGNITUDE", VectorFunction.MAGNITUDE) {
            @Override
            public Number perform(Vector v) {
                return new Number(VectorUtilities.calculateMagnitude(v));
            }
        };
    }

    public static VectorFunction makeUnit() {
        return new VectorFunction("Û", VectorFunction.UNIT) {
            @Override
            public Token perform(Vector v) {
                double magnitude = VectorUtilities.calculateMagnitude(v);
                double[] unitVector = new double[v.getDimensions()];
                for (int i = 0; i < v.getDimensions(); i++) {
                    unitVector[i] = v.getValues()[i] / magnitude;
                }
                return new Vector(unitVector);
            }

        };
    }
}
