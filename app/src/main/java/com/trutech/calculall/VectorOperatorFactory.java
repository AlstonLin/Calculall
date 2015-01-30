package com.trutech.calculall;

/**
 * Contains static factory methods of Functions used by Vectors.
 *
 * @version Alpha 2.0
 * @author Alston Lin
 */
public class VectorOperatorFactory {

    public static VectorOperator makeAdd(){
        return new VectorOperator("+", VectorOperator.ADD, 1) {
            @Override
            public Vector operate(Vector left, Vector right) {
                if (left.getDimensions() == 2 && right.getDimensions() == 2){
                    double[] values = new double[2];
                    values[0] = left.getValues()[0] + right.getValues()[0];
                    values[1] = left.getValues()[1] + right.getValues()[1];
                    return new Vector(values);
                }else if (left.getDimensions() == 3 && right.getDimensions() == 3){
                    double[] values = new double[3];
                    values[0] = left.getValues()[0] + right.getValues()[0];
                    values[1] = left.getValues()[1] + right.getValues()[1];
                    values[2] = left.getValues()[2] + right.getValues()[2];
                    return new Vector(values);
                }else{
                    throw new IllegalArgumentException("Attempted to operate two vector of different dimensions");
                }
            }
        };
    }
}
