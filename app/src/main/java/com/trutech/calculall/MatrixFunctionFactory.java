package com.trutech.calculall;

import java.util.ArrayList;

/**
 * Created by Keith on 2014-12-22.
 */
public class MatrixFunctionFactory {

    public static MatrixFunction makeTranspose() {
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

    public static MatrixFunction makeSqrt() {
        return new MatrixFunction("sqrt", MatrixFunction.SQRT) {
            @Override
            public Matrix perform(Matrix input) {
                return MatrixUtils.getSqrt(input);
            }
        };
    }

    public static MatrixFunction makeREF() {
        return new MatrixFunction("ref", MatrixFunction.REF) {
            @Override
            public Matrix perform(Matrix input) {
                if (input instanceof Matrix.AugmentedMatrix) {
                    return MatrixUtils.rowReduceREF((Matrix.AugmentedMatrix) input);
                } else {
                    return MatrixUtils.ref(input);
                }
            }
        };
    }

    public static MatrixFunction makeRREF() {
        return new MatrixFunction("rref", MatrixFunction.RREF) {
            @Override
            public Matrix perform(Matrix input) {
                if (input instanceof Matrix.AugmentedMatrix) {
                    return MatrixUtils.rowReduceRREF((Matrix.AugmentedMatrix) input);
                } else {
                    return MatrixUtils.rref(input);
                }
            }
        };
    }

    //TODO: make a wrapper function to convert this into a double
    public static MatrixFunction makeDeterminant() {
        return new MatrixFunction("det", MatrixFunction.DET) {
            @Override
            public Matrix perform(Matrix input) {
                if (input.getNumOfCols() != input.getNumOfRows()) {
                    throw new IllegalArgumentException("Determinant can only be applied to square matrices.");
                }

                if (input.getNumOfCols() == 2) {
                    double ele11 = ((Number) (input.getEntry(0, 0).get(0))).getValue();
                    double ele12 = ((Number) (input.getEntry(0, 1).get(0))).getValue();
                    double ele21 = ((Number) (input.getEntry(1, 0).get(0))).getValue();
                    double ele22 = ((Number) (input.getEntry(1, 1).get(0))).getValue();

                    ArrayList<Token> det_2by2 = new ArrayList<Token>();
                    det_2by2.add(new Number(ele11 * ele22 - ele12 * ele21));

                    ArrayList[][] temp = new ArrayList[1][1];
                    temp[0][0] = det_2by2;

                    return new Matrix(temp);
                } else {
                    double output = 0;
                    for (int i = 0; i < input.getNumOfRows(); i++) {
                        for (int j = 0; j < input.getNumOfCols(); j++) {
                            output += ((Number) input.getEntry(i, j).get(0)).getValue() * (Math.pow(-1, i + j) * (((Number) (perform(minorMatrix(input, i, j)).getEntry(0, 0)).get(0)).getValue()));
                        }
                    }

                    ArrayList<Token> det = new ArrayList<>();
                    det.add(new Number(output));

                    ArrayList[][] temp = new ArrayList[1][1];
                    temp[0][0].add(new Number(Utility.evaluateExpression(det)));

                    return new Matrix(temp);
                }
            }
        };
    }

    //TODO: make a wrapper function to convert output into a double (might be able to do this in the matrix mode class)
    public static MatrixFunction makeTrace() {
        return new MatrixFunction("tr", MatrixFunction.TRACE) {
            @Override
            public Matrix perform(Matrix input) {
                if (input.getNumOfCols() == input.getNumOfRows()) {
                    ArrayList<Token> trace = new ArrayList<>();
                    for (int i = 0; i < input.getNumOfCols() - 1; i++) {
                        trace.addAll(input.getEntry(i, i));
                        trace.add(OperatorFactory.makeAdd());
                    }
                    trace.addAll(input.getEntry(input.getNumOfCols() - 1, input.getNumOfCols() - 1));
                    ArrayList[][] temp = new ArrayList[1][1];
                    temp[0][0].add(new Number(Utility.evaluateExpression(trace)));

                    return new Matrix(temp);
                } else {
                    throw new IllegalArgumentException("Trace can only be applied to square matrices.");
                }
            }
        };
    }

    public static MatrixFunction makeRank() {
        return new MatrixFunction("rank", MatrixFunction.RANK) {
            @Override
            public Matrix perform(Matrix input) {
                ArrayList<Token>[][] rank = new ArrayList[1][1];
                rank[0][0].add(new Number(MatrixUtils.findRank(input)));
                return new Matrix(rank);
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

    //NOTES:
    //Outputs a row matrix, make it show up on screen as a set of numbers for ex. λ = {1,2,3,4}
    //get the entries using m.getRow(0) which will output an ArrayList<Token>[]
    //alternatively you could output the actual matrix and just display λ = [...]
    //if the matrix that is returned is empty, output "No real eigenvalues" to screen
    public static MatrixFunction makeEigenVal() {
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

    //Notes:
    //Outputs as an Augmented Matrix, extract the matrices contained in it with AM.getMatrices() which will output
    // an array of Matrices, then show all of those matrices on the screen
    public static MatrixFunction makeEigenVectors() {
        return new MatrixFunction("eigenvect", MatrixFunction.EIGENVECT) {
            @Override
            public Matrix.AugmentedMatrix perform(Matrix input) {
                return new Matrix.AugmentedMatrix(MatrixUtils.getEigenVectors(input));
            }
        };
    }

    public static MatrixFunction makeDiagonalize() {
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

    public static MatrixFunction makeLUP() {
        return new MatrixFunction("LUP", MatrixFunction.LU) {
            @Override
            public Matrix.AugmentedMatrix perform(Matrix input) {
                Matrix[] matrices = new Matrix[3];
                matrices[0] = makeTranspose().perform(MatrixUtils.getPermutationMatrix(input));
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
