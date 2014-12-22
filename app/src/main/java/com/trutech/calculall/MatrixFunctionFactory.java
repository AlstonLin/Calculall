package com.trutech.calculall;

/**
 * Created by Keith on 2014-12-22.
 */
public class MatrixFunctionFactory {

    public static MatrixFunction transpose() {
        return new MatrixFunction("transpose", MatrixFunction.TRANSPOSE) {
            @Override
            public Matrix perform(Matrix input) {
                return null;
            }
        };
    }

}
