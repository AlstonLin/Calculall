package com.trutechinnovations.calculall;

import java.util.Random;

/**
 * Contains a static Factory to create Functions for Matrix Mode.
 *
 * @author Ejaaz Merali, Keith Wong, Alston Lin
 * @version 3.0
 */
public class MatrixFunctionFactory {

    public static MatrixFunction makeTranspose() {
        return new MatrixFunction("trans", MatrixFunction.TRANSPOSE) {
            @Override
            public double[][] perform(double[][] input) {
                return MatrixUtils.transpose(input);
            }
        };
    }

    public static MatrixFunction makeREF() {
        return new MatrixFunction("REF", MatrixFunction.REF) {
            @Override
            public double[][] perform(double[][] input) {
                return MatrixUtils.toREF(input);
            }
        };
    }

    public static MatrixFunction makeRREF() {
        return new MatrixFunction("RREF", MatrixFunction.RREF) {
            @Override
            public double[][] perform(double[][] input) {
                return MatrixUtils.toRREF(input);
            }
        };
    }

    public static MatrixFunction makeDeterminant() {
        return new MatrixFunction("det", MatrixFunction.DET) {
            @Override
            public double[][] perform(double[][] input) {
                if (input.length == input[0].length) {
                    double[][] output = new double[1][1];
                    output[0][0] = MatrixUtils.findDeterminant(input);
                    return output;
                } else {
                    throw new IllegalArgumentException("Determinant is only defined for square matrices");
                }
            }
        };
    }

    public static MatrixFunction makeTrace() {
        return new MatrixFunction("tr", MatrixFunction.TRACE) {
            @Override
            public double[][] perform(double[][] input) {
                if (input.length != input[0].length) {
                    throw new IllegalArgumentException("Trace is only defined for square matrices");
                } else {
                    double[][] trace = new double[1][1];
                    trace[0][0] = MatrixUtils.trace(input);
                    return trace;
                }
            }
        };
    }

    public static MatrixFunction makeRank() {
        return new MatrixFunction("rank", MatrixFunction.RANK) {
            @Override
            public double[][] perform(double[][] input) {
                double[][] output = new double[1][1];
                output[0][0] = MatrixUtils.rank(input);
                return output;
            }
        };
    }

    public static MatrixFunction makeInverse() {
        return new MatrixFunction("inv", MatrixFunction.INVERSE) {
            @Override
            public double[][] perform(double[][] input) {
                if (input.length != input[0].length) {
                    throw new IllegalArgumentException("Matrix must be n*n to be invertible");
                }
                return MatrixUtils.findInverse(input);
            }
        };
    }

    public static MatrixFunction makeLU() {
        return new MatrixFunction("LU", MatrixFunction.LU) {
            @Override
            public double[][] perform(double[][] input) {
                if (input.length != input[0].length) {
                    throw new IllegalArgumentException("Matrix must be n*n to be LU factorizable");
                }
                Random r = new Random();
                double[][][] m = MatrixUtils.getLUDecomposition(input);
                return m[r.nextInt(m.length)];
            }
        };
    }

    public static MatrixFunction makeDiag() {
        return new MatrixFunction("diag", MatrixFunction.DIAG) {
            @Override
            public double[][] perform(double[][] input) {
                if (input.length != input[0].length) {
                    throw new IllegalArgumentException("Matrix must be n*n to be diagonalizable");
                }
                Random r = new Random();
                double[][][] m = MatrixUtils.getEigenDecomposition(input);
                return m[r.nextInt(m.length)];
            }
        };
    }
}
