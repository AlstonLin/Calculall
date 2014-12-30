package com.trutech.calculall;

import java.util.ArrayList;

/**
 * Created by Ejaaz on 22/12/2014.
 */

@SuppressWarnings("unused")
public class MatrixOperatorFactory {

    public static MatrixOperator makeMatrixAdd() {
        return new MatrixOperator("+", MatrixOperator.ADD, 2, true, 1, true) {
            @Override
            public Matrix operate(Matrix left, Matrix right) {
                if (left.getNumOfCols() == right.getNumOfCols()
                        && left.getNumOfRows() == right.getNumOfRows()) {
                    ArrayList[][] newMatrix = new ArrayList[left.getNumOfRows()][left.getNumOfCols()];
                    for (int i = 0; i < newMatrix.length; i++) {
                        for (int j = 0; j < newMatrix.length; j++) {
                            newMatrix[i][j] = new ArrayList<Token>();
                            newMatrix[i][j].addAll(left.getEntry(i, j));
                            newMatrix[i][j].add(OperatorFactory.makeAdd());
                            newMatrix[i][j].addAll(right.getEntry(i, j));
                        }
                    }
                    return Utility.evaluateMatrix(new Matrix(newMatrix));
                } else {
                    throw new IllegalArgumentException("Matrices are not the same size");
                }
            }
        };
    }

    public static MatrixOperator makeMatrixSubtract() {
        return new MatrixOperator("−", MatrixOperator.SUBTRACT, 2, true, -1, false) {
            @Override
            public Matrix operate(Matrix left, Matrix right) {
                if (left.getNumOfCols() == right.getNumOfCols()
                        && left.getNumOfRows() == right.getNumOfRows()) {
                    ArrayList[][] newMatrix = new ArrayList[left.getNumOfRows()][left.getNumOfCols()];
                    for (int i = 0; i < newMatrix.length; i++) {
                        for (int j = 0; j < newMatrix.length; j++) {
                            newMatrix[i][j] = new ArrayList<Token>();
                            newMatrix[i][j].addAll(left.getEntry(i, j));
                            newMatrix[i][j].add(OperatorFactory.makeSubtract());
                            newMatrix[i][j].addAll(right.getEntry(i, j));
                        }
                    }
                    return Utility.evaluateMatrix(new Matrix(newMatrix));
                } else {
                    throw new IllegalArgumentException("Matrices are not the same size");
                }
            }
        };
    }

    public static MatrixOperator makeMatrixMultiply() {
        return new MatrixOperator("", MatrixOperator.MULTIPLY, 3, true, 0, true) {
            @Override
            public Matrix operate(Matrix left, Matrix right) {
                if (left.getNumOfCols() == right.getNumOfRows()) {
                    ArrayList[][] newMatrix = new ArrayList[left.getNumOfRows()][right.getNumOfCols()];
                    for (int i = 0; i < left.getNumOfRows(); i++) {
                        for (int j = 0; j < right.getNumOfCols(); j++) {
                            newMatrix[i][j] = dotProduct(left.getRow(i), right.getColumn(j));
                        }
                    }
                    return Utility.evaluateMatrix(new Matrix(newMatrix));
                } else {
                    throw new IllegalArgumentException("Number of columns of left matrix is not " +
                            "equal to the number of row of the right matrix");
                }
            }
        };
    }

    /**
     * @param left
     * @param right
     * @return The dot product of two column vectors
     */
    private static ArrayList<Token> dotProduct(ArrayList<Token>[] left, ArrayList<Token>[] right) {
        if (left.length == right.length) {
            ArrayList<Token> exp = new ArrayList<Token>();
            for (int i = 0; i < left.length; i++) {
                exp.add(BracketFactory.makeOpenBracket());
                exp.addAll(left[i]);
                exp.add(OperatorFactory.makeMultiply());
                exp.addAll(right[i]);
                exp.add(BracketFactory.makeCloseBracket());
                if (i != left.length - 1) {
                    exp.add(OperatorFactory.makeAdd());
                }
            }
            return exp;
        } else {
            throw new IllegalArgumentException("Vectors are not the same length");
        }
    }

    public static MatrixOperator makeAugment() {
        return new MatrixOperator("", MatrixOperator.AUGMENT, 3, true, 0, true) {
            @Override
            public Matrix.AugmentedMatrix operate(Matrix left, Matrix right) {
                if (left.getNumOfRows() == right.getNumOfRows()) {
                    ArrayList[][] newMatrix = new ArrayList[left.getNumOfRows()][left.getNumOfCols() + right.getNumOfCols()];
                    if (left instanceof Matrix.AugmentedMatrix) {
                        Matrix[] matrices = new Matrix[((Matrix.AugmentedMatrix) left).getMatrices().length + 1];
                        for (int k = 0; k < matrices.length - 1; k++) {
                            matrices[k] = ((Matrix.AugmentedMatrix) left).getMatrices()[k];
                        }
                        matrices[matrices.length - 1] = right;
                        Matrix.AugmentedMatrix finalMatrix = new Matrix.AugmentedMatrix(matrices);
                        return finalMatrix;
                    } else {
                        Matrix[] matrices = {left, right};
                        return new Matrix.AugmentedMatrix(matrices);
                    }
                } else {
                    throw new IllegalArgumentException("Inputs do not have the same number of rows");
                }
            }
        };
    }


}
