package com.trutech.calculall;

/**
 * Contains static factory methods for Functions used by Vector Mode.
 *
 * @author Alston Lin
 * @version Alpha 2.0
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
                if (v.getDimensions() == 2) {
                    double[] unitVector = new double[2];
                    unitVector[0] = v.getValues()[0] / magnitude;
                    unitVector[1] = v.getValues()[1] / magnitude;
                    return new Vector(unitVector);
                } else if (v.getDimensions() == 3) {
                    double[] unitVector = new double[3];
                    unitVector[0] = v.getValues()[0] / magnitude;
                    unitVector[1] = v.getValues()[1] / magnitude;
                    unitVector[2] = v.getValues()[2] / magnitude;
                    return new Vector(unitVector);
                } else {
                    throw new IllegalArgumentException("Error: This calculator only supports 2D and 3D vectors.");
                }
            }
        };
    }

}
