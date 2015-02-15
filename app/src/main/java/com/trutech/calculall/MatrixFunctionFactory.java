package com.trutech.calculall;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;

/**
 * Contains a static Factory to create Functions for Matrix Mode.
 *
 * @author Ejaaz Merali, Keith Wong
 * @version Alpha 2.0
 */
public class MatrixFunctionFactory {

    public static MatrixFunction makeTranspose() {
        return new MatrixFunction("trans", MatrixFunction.TRANSPOSE) {
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

    public static MatrixFunction makeSqrt() {
        return new MatrixFunction("sqrt", MatrixFunction.SQRT) {
            @Override
            public Matrix perform(Matrix input) {
                Matrix m = MatrixUtils.evaluateMatrixEntries(input);
                return MatrixUtils.getSqrt(m);
            }
        };
    }

    public static MatrixFunction makeREF() {
        return new MatrixFunction("ref", MatrixFunction.REF) {
            @Override
            public Matrix perform(Matrix input) {
                if (input instanceof Matrix.AugmentedMatrix) {
                    return MatrixUtils.evaluateMatrixEntries(MatrixUtils.rowReduceREF((Matrix.AugmentedMatrix) input));
                } else {
                    return MatrixUtils.evaluateMatrixEntries(MatrixUtils.ref(input));
                }
            }
        };
    }

    public static MatrixFunction makeRREF() {
        return new MatrixFunction("rref", MatrixFunction.RREF) {
            @Override
            public Matrix perform(Matrix input) {
                if (input instanceof Matrix.AugmentedMatrix) {
                    return MatrixUtils.evaluateMatrixEntries(MatrixUtils.rowReduceRREF((Matrix.AugmentedMatrix) input));
                } else {
                    return MatrixUtils.evaluateMatrixEntries(MatrixUtils.rref(input));
                }
            }
        };
    }

    public static MatrixFunction makeDeterminant() {
        return new MatrixFunction("det", MatrixFunction.DET) {
            @Override
            public Number perform(Matrix input) {
                if (input.getNumOfCols() != input.getNumOfRows()) {
                    throw new IllegalArgumentException("Determinant can only be applied to square matrices.");
                }
                Matrix a = MatrixUtils.evaluateMatrixEntries(input);
                RealMatrix m = new Array2DRowRealMatrix(MatrixUtils.convMatrixEntriesToDbl(a.getEntries()));
                LUDecomposition lu = new LUDecomposition(m);
                return new Number(lu.getDeterminant());
            }
        };
    }

    public static MatrixFunction makeTrace() {
        return new MatrixFunction("tr", MatrixFunction.TRACE) {
            @Override
            public Number perform(Matrix input) {
                Matrix m = MatrixUtils.evaluateMatrixEntries(input);
                if (m.getNumOfCols() == m.getNumOfRows()) {
                    ArrayList<Token> trace = new ArrayList<>();
                    for (int i = 0; i < m.getNumOfCols() - 1; i++) {
                        trace.addAll(m.getEntry(i, i));
                        trace.add(OperatorFactory.makeAdd());
                    }
                    trace.addAll(m.getEntry(m.getNumOfCols() - 1, m.getNumOfCols() - 1));
                    return new Number(Utility.evaluateExpression(Utility.convertToReversePolish(Utility.setupExpression(Utility.condenseDigits(Utility.addMissingBrackets(trace))))));
                } else {
                    throw new IllegalArgumentException("Trace can only be applied to square matrices.");
                }
            }
        };
    }

    public static MatrixFunction makeRank() {
        return new MatrixFunction("rank", MatrixFunction.RANK) {
            @Override
            public Number perform(Matrix input) {
//                ArrayList<Token>[][] rank = new ArrayList[1][1];
//                rank[0][0].add(new Number(MatrixUtils.findRank(input)));
//                return new Matrix(rank);

                return new Number(MatrixUtils.findRank(input));
            }
        };
    }

    public static MatrixFunction makeInverse() {
        return new MatrixFunction("inv", MatrixFunction.INVERSE) {
            @Override
            public Matrix perform(Matrix input) {
                return MatrixUtils.findInverse(input);
            }
        };
    }

    /**
     * @return a Matrix containing the eigenvalues of the input Matrix
     * @deprecated Kept just in case
     */
    private static MatrixFunction makeEigenVal() {
        return new MatrixFunction("λ", MatrixFunction.EIGENVAL) {
            @Override
            public Matrix perform(Matrix input) {
                double[] output = MatrixUtils.getRealEigenValues(input);

                double[][] temp = new double[1][output.length];
                temp[0] = output;

                return new Matrix(temp);
            }
        };
    }

    /**
     * @return an Augmented Matrix containing the Eigenvectors of the input Matrix
     * @deprecated Kept just in case
     */
    private static MatrixFunction makeEigenVectors() {
        return new MatrixFunction("eigenvect", MatrixFunction.EIGENVECT) {
            @Override
            public Matrix.AugmentedMatrix perform(Matrix input) {
                return new Matrix.AugmentedMatrix(MatrixUtils.getEigenVectors(input));
            }
        };
    }

    /**
     * @return an Augmented Matrix containing the eigenvector decomposition of the input Matrix
     * @deprecated Kept just in case
     */
    private static MatrixFunction makeDiagonalize() {
        return new MatrixFunction("diag", MatrixFunction.DIAG) {
            @Override
            public Matrix.AugmentedMatrix perform(Matrix input) {
                Matrix[] matrices = new Matrix[3];
                matrices[0] = MatrixUtils.getDiagonalizingMatrix(input);
                matrices[1] = MatrixUtils.getDiagonalMatrix(input);
                matrices[2] = MatrixUtils.findInverse(matrices[0]);
                return new Matrix.AugmentedMatrix(matrices);
            }
        };
    }

    /**
     * @return an Augmented Matrix containing the LUP factorization of the input Matrix
     * @deprecated Kept just in case
     */
    private static MatrixFunction makeLUP() {
        return new MatrixFunction("LUP", MatrixFunction.LU) {
            @Override
            public Matrix.AugmentedMatrix perform(Matrix input) {
                Matrix[] matrices = new Matrix[3];
                matrices[0] = (Matrix) makeTranspose().perform(MatrixUtils.getPermutationMatrix(input));
                matrices[1] = MatrixUtils.getLowerTriangularMatrix(input);
                matrices[2] = MatrixUtils.getUpperTriangularMatrix(input);
                return new Matrix.AugmentedMatrix(matrices);
            }
        };
    }

    private static Matrix minorMatrix(Matrix input, int row, int column) {
        ArrayList[][] minor = new ArrayList[input.getNumOfRows() - 1][input.getNumOfCols() - 1];

        for (int i = 0; i < minor.length; i++) {
            for (int j = 0; j < minor[0].length; j++) {
                if (i != row || j != column) {
                    minor[i][j] = input.getEntry(i + (i > row ? 1 : 0), j + (j > column ? 1 : 0));
                }
            }
        }
        return new Matrix(minor);
    }


}
