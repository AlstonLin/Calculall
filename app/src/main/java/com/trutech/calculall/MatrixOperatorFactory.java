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
            public Matrix operate(Object left, Object right) {
                if (left instanceof Matrix.AugmentedMatrix || right instanceof Matrix.AugmentedMatrix) {
                    throw new IllegalArgumentException("Addition is not defined for Augmented Matrices");
                } else if (left instanceof Matrix && right instanceof Matrix) {
                    if (((Matrix) left).getNumOfCols() == ((Matrix) right).getNumOfCols()
                            && ((Matrix) left).getNumOfRows() == ((Matrix) right).getNumOfRows()) {
                        ArrayList[][] newMatrix = new ArrayList[((Matrix) left).getNumOfRows()][((Matrix) left).getNumOfCols()];
                        for (int i = 0; i < newMatrix.length; i++) {
                            for (int j = 0; j < newMatrix.length; j++) {
                                newMatrix[i][j] = new ArrayList<Token>();
                                newMatrix[i][j].addAll(((Matrix) left).getEntry(i, j));
                                newMatrix[i][j].add(OperatorFactory.makeAdd());
                                newMatrix[i][j].addAll(((Matrix) right).getEntry(i, j));
                            }
                        }
                        return MatrixUtils.evaluateMatrixEntries(new Matrix(newMatrix));
                    } else {
                        throw new IllegalArgumentException("Matrices are not the same size");
                    }
                } else {
                    throw new IllegalArgumentException("Both arguments must be Matrices");
                }
            }
        };
    }

    public static MatrixOperator makeMatrixSubtract() {
        return new MatrixOperator("−", MatrixOperator.SUBTRACT, 2, true, -1, false) {
            @Override
            public Matrix operate(Object left, Object right) {
                if (left instanceof Matrix.AugmentedMatrix || right instanceof Matrix.AugmentedMatrix) {
                    throw new IllegalArgumentException("Subtraction is not defined for Augmented Matrices");
                } else if (left instanceof Matrix && right instanceof Matrix) {
                    if (((Matrix) left).getNumOfCols() == ((Matrix) right).getNumOfCols()
                            && ((Matrix) left).getNumOfRows() == ((Matrix) right).getNumOfRows()) {
                        ArrayList[][] newMatrix = new ArrayList[((Matrix) left).getNumOfRows()][((Matrix) left).getNumOfCols()];
                        for (int i = 0; i < newMatrix.length; i++) {
                            for (int j = 0; j < newMatrix.length; j++) {
                                newMatrix[i][j] = new ArrayList<Token>();
                                newMatrix[i][j].addAll(((Matrix) left).getEntry(i, j));
                                newMatrix[i][j].add(OperatorFactory.makeSubtract());
                                newMatrix[i][j].addAll(((Matrix) right).getEntry(i, j));
                            }
                        }
                        return MatrixUtils.evaluateMatrixEntries(new Matrix(newMatrix));
                    } else {
                        throw new IllegalArgumentException("Matrices are not the same size");
                    }
                } else {
                    throw new IllegalArgumentException("Both arguments must be Matrices");
                }
            }
        };
    }

    public static MatrixOperator makeMatrixMultiply() {
        return new MatrixOperator("", MatrixOperator.MULTIPLY, 3, true, 0, true) {
            @Override
            public Matrix operate(Object left, Object right) {
                if (left instanceof Matrix.AugmentedMatrix || right instanceof Matrix.AugmentedMatrix) {
                    throw new IllegalArgumentException("Multiplication is not defined for Augmented Matrices");
                } else if (left instanceof Matrix && right instanceof Matrix) {
                    if (((Matrix) left).getNumOfCols() == ((Matrix) right).getNumOfRows()) {
                        ArrayList[][] newMatrix = new ArrayList[((Matrix) left).getNumOfRows()][((Matrix) right).getNumOfCols()];
                        for (int i = 0; i < ((Matrix) left).getNumOfRows(); i++) {
                            for (int j = 0; j < ((Matrix) right).getNumOfCols(); j++) {
                                newMatrix[i][j].add(new Number(dotProduct(((Matrix) left).getRow(i), ((Matrix) right).getColumn(j))));
                            }
                        }
                        return MatrixUtils.evaluateMatrixEntries(new Matrix(newMatrix));
                    } else {
                        throw new IllegalArgumentException("Number of columns of left matrix is not " +
                                "equal to the number of rows of the right matrix");
                    }
                } else if (left instanceof Number && right instanceof Matrix) {
                    ArrayList[][] newMatrix = new ArrayList[((Matrix) right).getNumOfRows()][((Matrix) right).getNumOfCols()];
                    for (int i = 0; i < ((Matrix) right).getNumOfRows(); i++) {
                        for (int j = 0; j < ((Matrix) right).getNumOfCols(); j++) {
                            newMatrix[i][j].add(left);
                            newMatrix[i][j].add(OperatorFactory.makeMultiply());
                            newMatrix[i][j].add(BracketFactory.makeOpenBracket());
                            newMatrix[i][j].addAll(((Matrix) right).getEntry(i, j));
                            newMatrix[i][j].add(BracketFactory.makeCloseBracket());
                        }
                    }
                    return new Matrix(newMatrix);
                } else if (right instanceof Number && left instanceof Matrix) {
                    ArrayList[][] newMatrix = new ArrayList[((Matrix) left).getNumOfRows()][((Matrix) left).getNumOfCols()];
                    for (int i = 0; i < ((Matrix) left).getNumOfRows(); i++) {
                        for (int j = 0; j < ((Matrix) left).getNumOfCols(); j++) {
                            newMatrix[i][j].add(right);
                            newMatrix[i][j].add(OperatorFactory.makeMultiply());
                            newMatrix[i][j].add(BracketFactory.makeOpenBracket());
                            newMatrix[i][j].addAll(((Matrix) left).getEntry(i, j));
                            newMatrix[i][j].add(BracketFactory.makeCloseBracket());
                        }
                    }
                    return new Matrix(newMatrix);
                } else {
                    throw new IllegalArgumentException("None of the arguments are Matrices");
                }
            }
        };
    }

    public static MatrixOperator makeMatrixExponent() {
        return new MatrixOperator("^", MatrixOperator.EXPONENT, 5, false, 0, false) {
            @Override
            public Matrix operate(Object left, Object right) {
                if (left instanceof Matrix.AugmentedMatrix || right instanceof Matrix.AugmentedMatrix) {
                    throw new IllegalArgumentException("Exponentiation is not defined for Augmented Matrices");
                } else if (left instanceof Matrix && right instanceof Number && ((Number) right).getValue() % 1 == 0) {
                    if (((Matrix) left).getNumOfCols() == ((Matrix) left).getNumOfRows()) {
                        return MatrixUtils.evaluateMatrixEntries(matrixExponent((Matrix) left, (int) ((Number) right).getValue()));
                    } else {
                        throw new IllegalArgumentException("Cannot raise a non-square Matrix to a power");
                    }
                } else {
                    if (!(left instanceof Matrix)) {
                        throw new IllegalArgumentException("Invalid input: base is not a matrix");
                    } else if (!(right instanceof Number) || ((Number) right).getValue() % 1 != 0) {
                        throw new IllegalArgumentException("Invalid input: exponent must be an integer");
                    }
                }
                return null;
            }
        };
    }

    private static Matrix matrixExponent(Matrix m, int power) {
        if (power > 0) {
            return matrixExponent(MatrixUtils.evaluateMatrixEntries(makeMatrixMultiply().operate(m, m)), power - 1);
        } else if (power == 0) {
            return MatrixUtils.makeIdentity(m.getNumOfCols());
        } else if (power <= 0) {
            Matrix inverse;
            try {
                inverse = MatrixFunctionFactory.makeInverse().perform(m);
                return matrixExponent(inverse, -1 * power);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot raise a non-invertible matrix to a negative power");
            }
        } else {
            return null;
        }
    }

    /**
     * @param left
     * @param right
     * @return The dot product of two column vectors
     */
    private static double dotProduct(ArrayList<Token>[] left, ArrayList<Token>[] right) {
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
            return Utility.evaluateExpression(exp);
        } else {
            throw new IllegalArgumentException("Vectors are not the same length");
        }
    }

    public static MatrixOperator makeAugment() {
        return new MatrixOperator("", MatrixOperator.AUGMENT, 3, true, 0, true) {
            @Override
            public Matrix.AugmentedMatrix operate(Object left, Object right) {
                if (left instanceof Matrix && right instanceof Matrix) {
                    if (((Matrix) left).getNumOfRows() == ((Matrix) right).getNumOfRows()) {
                        ArrayList[][] newMatrix = new ArrayList[((Matrix) left).getNumOfRows()][((Matrix) left).getNumOfCols() + ((Matrix) right).getNumOfCols()];
                        if (left instanceof Matrix.AugmentedMatrix && right instanceof Matrix.AugmentedMatrix) {
                            Matrix[] matrices = new Matrix[((Matrix.AugmentedMatrix) left).getMatrices().length + ((Matrix.AugmentedMatrix) right).getMatrices().length];
                            for (int k = 0; k < ((Matrix.AugmentedMatrix) left).getMatrices().length; k++) {
                                matrices[k] = ((Matrix.AugmentedMatrix) left).getMatrices()[k];
                            }
                            for (int k = 0; k < ((Matrix.AugmentedMatrix) right).getMatrices().length; k++) {
                                matrices[k] = ((Matrix.AugmentedMatrix) right).getMatrices()[k];
                            }
                            Matrix.AugmentedMatrix finalMatrix = new Matrix.AugmentedMatrix(matrices);
                            return finalMatrix;
                        } else if (left instanceof Matrix.AugmentedMatrix) {
                            Matrix[] matrices = new Matrix[((Matrix.AugmentedMatrix) left).getMatrices().length + 1];
                            for (int k = 0; k < matrices.length - 1; k++) {
                                matrices[k] = ((Matrix.AugmentedMatrix) left).getMatrices()[k];
                            }
                            matrices[matrices.length - 1] = (Matrix) right;
                            Matrix.AugmentedMatrix finalMatrix = new Matrix.AugmentedMatrix(matrices);
                            return finalMatrix;
                        } else if (right instanceof Matrix.AugmentedMatrix) {
                            Matrix[] matrices = new Matrix[((Matrix.AugmentedMatrix) right).getMatrices().length + 1];
                            for (int k = 1; k < matrices.length - 1; k++) {
                                matrices[k] = ((Matrix.AugmentedMatrix) right).getMatrices()[k];
                            }
                            matrices[0] = (Matrix) left;
                            Matrix.AugmentedMatrix finalMatrix = new Matrix.AugmentedMatrix(matrices);
                            return finalMatrix;
                        } else {
                            Matrix[] matrices = {(Matrix) left, (Matrix) right};
                            return new Matrix.AugmentedMatrix(matrices);
                        }
                    } else {
                        throw new IllegalArgumentException("Inputs do not have the same number of rows");
                    }
                } else {
                    throw new IllegalArgumentException("Both arguments must be Matrices");
                }
            }
        };
    }


}
