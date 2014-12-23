package com.trutech.calculall;

import java.util.ArrayList;

/**
 * Created by Keith on 2014-12-22.
 */
public class MatrixFunctionFactory {

    public static MatrixFunction transpose() {
        return new MatrixFunction("transpose", MatrixFunction.TRANSPOSE) {
            @Override
            public Matrix perform(Matrix input) {
                ArrayList[][] transposed = new ArrayList[input.getNumOfCols()][input.getNumOfRows()];
                for (int i = 0; i < transposed.length; i++) {
                    for (int j = 0; j < transposed[0].length; j++) {
                        transposed[i][j] = input.getEntry(j, i);
                    }
                }

                return new Matrix(transposed);
            }
        };
    }

    public static MatrixFunction determinant() {
        return new MatrixFunction("det", MatrixFunction.DET) {
            @Override
            public double perform(Matrix input) {
                if (input.getNumOfCols() != input.getNumOfRows()) {
                    throw new IllegalArgumentException("Determinant can only be applied to square matrices.");
                }

                if (input.getNumOfCols() == 2) {
                    double ele11 = ((Number) (input.getEntry(0, 0).get(0))).getValue();
                    double ele12 = ((Number) (input.getEntry(0, 1).get(0))).getValue();
                    double ele21 = ((Number) (input.getEntry(1, 0).get(0))).getValue();
                    double ele22 = ((Number) (input.getEntry(1, 1).get(0))).getValue();

                    return ele11 * ele22 - ele12 * ele21;
                } else {
                    double output = 0;
                    for (int i = 0; i < input.getNumOfRows(); i++) {
                        for (int j = 0; j < input.getNumOfCols(); j++) {
                            output += ((Number) input.getEntry(i, j).get(0)).getValue() * (Math.pow(-1, i + j) * perform(minorMatrix(input, i, j)));
                        }
                    }
                    return output;
                }
            }
        };
    }

    private static Matrix minorMatrix(Matrix input, int row, int column) {
        ArrayList[][] minor = new ArrayList[input.getNumOfRows() - 1][input.getNumOfCols() - 1];

        for (int i = 0; i < minor.length; i++) {
            for (int j = 0; j < minor[0].length; j++) {
                if (i != row && j != column) {
                    minor[i][j] = input.getEntry(i + (i > row ? 1 : 0), j + (j > column ? 1 : 0));
                }
            }
        }
        return new Matrix(minor);
    }

}
