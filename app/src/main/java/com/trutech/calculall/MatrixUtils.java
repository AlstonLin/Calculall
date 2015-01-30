package com.trutech.calculall;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.Arrays;

import static com.trutech.calculall.Matrix.AugmentedMatrix;

/**
 * Contains the Row Reduction algorithm.
 *
 * @author Ejaaz Merali
 * @version Alpha 2.0
 */
public class MatrixUtils {
    /**
     * Evaluates every entry of the given matrix
     *
     * @param matrix The unsimplified matrix
     * @return The simplified matrix
     */
    public static Matrix evaluateMatrixEntries(Matrix matrix) {
        ArrayList[][] newMatrix = new ArrayList[matrix.getNumOfRows()][matrix.getNumOfCols()];
        ArrayList<Token> temp = new ArrayList<>();
        for (int i = 0; i < matrix.getNumOfRows(); i++) {
            for (int j = 0; j < matrix.getNumOfCols(); j++) {
                temp.add(new Number(Utility.evaluateExpression(Utility.convertToReversePolish(matrix.getEntry(i, j)))));
                newMatrix[i][j] = temp;
                temp.clear();
            }
        }
        return new Matrix(newMatrix);
    }

    /**
     * Makes an Identity Matrix of the given size
     *
     * @param dim the number of rows/columns of the Identity Matrix
     * @return The Identity Matrix of the specified size
     */
    public static Matrix makeIdentity(int dim) {
        ArrayList<Token>[][] newMatrix = new ArrayList[dim][dim];
        for (int i = 0; i < dim; i++) {
            newMatrix[i][i].add(new Number(1));
        }
        return new Matrix(newMatrix);
    }

    /**
     * Checks if the given array contains only zeroes, assumes
     * the all the elements of the array have been fully simplified
     *
     * @param a An array
     * @return true if the array only contains zeroes, false otherwise
     */
    private static boolean onlyZeroes(ArrayList<Token>[] a) {
        for (int i = 0; i < a.length; i++) {
            if (((Number) a[i].get(0)).getValue() != 0) {
                return false;
            }
        }
        return true;
    }


    /**
     * Finds the index of the first element which is not zero
     *
     * @param a An array
     * @return The index of the first non-zero element
     */
    private static int getFirstNonZero(ArrayList<Token>[] a) {
        for (int i = 0; i < a.length; i++) {
            if (((Number) a[i].get(0)).getValue() != 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds the index of the last element which is not zero
     *
     * @param a An array
     * @return The index of the last non-zero element
     */
    private static int getLastNonZero(ArrayList<Token>[] a) {
        for (int i = a.length - 1; i >= 0; i--) {
            if (((Number) a[i].get(0)).getValue() != 0) {
                return i;
            }
        }
        return -1;
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

    /**
     * Returns the Row Operations required to reduce the given Matrix(m) to
     * Row Echelon Form(REF)
     * Swap step: 1, Add step: 2, Scale step: 3
     *
     * @param m The Matrix which will be Row Reduced to REF
     * @return the row reduction steps
     */
    private static double[][] getREFSteps(Matrix m) {
        ArrayList<Double[]> steps = new ArrayList<>();
        Matrix temp = evaluateMatrixEntries(m);
        if (!onlyZeroes(temp.getColumn(0))) {
            for (int j = 0; j < m.getNumOfCols(); j++) {
                int pivot = getFirstNonZero(temp.getColumn(j));
                if (pivot != 0) {
                    temp = swapRows(temp, 0, pivot);
                    Double[] swapStep = {1d, 0d, (double) pivot};
                    steps.add(swapStep);
                    pivot = 0;
                }
                ArrayList<Token>[] restOfCol = Arrays.copyOfRange(temp.getColumn(j), 1, m.getNumOfRows());
                for (int i = 1; !onlyZeroes(restOfCol); i++) {
                    if (((Number) temp.getEntry(i, j).get(0)).getValue() != 0) {
                        double scalar = -1 * ((Number) temp.getEntry(i, j).get(0)).getValue() / ((Number) temp.getEntry(0, j).get(0)).getValue();
                        temp = addRows(temp, i, pivot, scalar);
                        Double[] addStep = {2d, (double) i, (double) pivot, scalar};
                        steps.add(addStep);
                    }
                }
                //ArrayList<Token>[] tempRow = temp.getRow(0);
                //ArrayList<Token>[] tempCol = temp.getColumn(0);
            }
        }
        double[][] minorSteps = getREFSteps(minorMatrix(temp, 0, 0));
        Double[] tempStep;
        for (int i = 0; i < minorSteps.length; i++) {
            tempStep = new Double[minorSteps[i].length];
            for (int j = 0; j < minorSteps[i].length; j++) {
                tempStep[j] = minorSteps[i][j];
            }
            steps.add(tempStep);
        }
        return (double[][]) steps.toArray();
    }

    /**
     * Returns the Row Operations required to reduce the given Matrix(m) to
     * Reduced Row Echelon Form(RREF)
     *
     * @param m The Matrix which will be Row Reduced to RREF
     * @return the row reduction steps
     */
    private static double[][] getRREFSteps(Matrix m) {
        ArrayList<Double[]> steps = new ArrayList<>();
        double[][] refSteps = getREFSteps(m);
        Double[] tempStep;
        for (int i = 0; i < refSteps.length; i++) {
            tempStep = new Double[refSteps[i].length];
            for (int j = 0; j < refSteps.length; j++) {
                tempStep[j] = refSteps[i][j];
            }
            steps.add(tempStep);
        }
        Matrix temp = applySteps(m, refSteps);
        if (!onlyZeroes(temp.getColumn(temp.getNumOfCols() - 1))) {
            for (int j = temp.getNumOfCols() - 1; j >= 0; j--) {
                int pivot = getLastNonZero(temp.getColumn(j));
                if (pivot != 0) {
                    temp = swapRows(temp, 0, pivot);
                    Double[] swapStep = {1d, 0d, (double) pivot};
                    steps.add(swapStep);
                    pivot = 0;
                }
                ArrayList<Token>[] restOfCol = Arrays.copyOfRange(temp.getColumn(j), 0, temp.getNumOfRows() - 2);
                for (int i = 1; !onlyZeroes(restOfCol); i++) {
                    if (((Number) temp.getEntry(i, j).get(0)).getValue() != 0) {
                        double scalar = -1 * ((Number) temp.getEntry(i, j).get(0)).getValue() / ((Number) temp.getEntry(0, j).get(0)).getValue();
                        temp = addRows(temp, i, pivot, scalar);
                        Double[] addStep = {2d, (double) i, (double) pivot, scalar};
                        steps.add(addStep);
                    }
                }
            }
        }

        for (int i = 0; i < temp.getNumOfRows(); i++) {
            if (temp.getEntry(i, getFirstNonZero(temp.getRow(i))).get(0) instanceof Number
                    && ((Number) temp.getEntry(i, getFirstNonZero(temp.getRow(i))).get(0)).getValue() != 1) {
                double scalar = ((Number) temp.getEntry(i, getFirstNonZero(temp.getRow(i))).get(0)).getValue();
                scaleRow(temp, i, scalar);
                Double[] scaleStep = {3d, (double) i, scalar};
                steps.add(scaleStep);
            }
        }

        double[][] minorSteps = getRREFSteps(minorMatrix(temp, temp.getNumOfRows() - 1, temp.getNumOfCols() - 1));
        for (int i = 0; i < minorSteps.length; i++) {
            tempStep = new Double[minorSteps[i].length];
            for (int j = 0; j < minorSteps[i].length; j++) {
                tempStep[j] = minorSteps[i][j];
            }
            steps.add(tempStep);
        }

        return (double[][]) steps.toArray();
    }

    /**
     * @param m      The original Matrix
     * @param row1   The index of the Row being added to
     * @param row2   The index of the Row being added
     * @param scalar The scalar to multiply the second row by
     * @return A Matrix which is similar to the given Matrix(m) but with the Row at index row2,
     * multiplied by a scalar, being added to the row at index row1
     */
    private static Matrix addRows(Matrix m, int row1, int row2, double scalar) {
        ArrayList<Token>[][] entries = m.getEntries();
        for (int j = 0; j < m.getNumOfCols(); j++) {
            entries[row1][j].add(OperatorFactory.makeAdd());
            entries[row1][j].add(new Number(scalar));
            entries[row1][j].add(BracketFactory.makeOpenBracket());
            entries[row1][j].addAll(entries[row2][j]);
            entries[row1][j].add(BracketFactory.makeCloseBracket());
        }
        return new Matrix(entries);
    }

    /**
     * @param m    The original Matrix
     * @param row1 The index of the first Row
     * @param row2 The index of the second Row
     * @return A Matrix which is similar to the given Matrix but with two Rows having been swapped
     */
    private static Matrix swapRows(Matrix m, int row1, int row2) {
        ArrayList<Token>[][] entries = m.getEntries();
        ArrayList<Token>[] temp = entries[row1];
        entries[row1] = entries[row2];
        entries[row2] = temp;
        return new Matrix(entries);
    }


    /**
     * @param m      The original Matrix
     * @param row    The index of the Row to be scaled
     * @param scalar The scaling factor
     * @return The Matrix m with all the entries, of the specified row, multiplied by a scalar
     */
    private static Matrix scaleRow(Matrix m, int row, double scalar) {
        ArrayList<Token>[][] entries = m.getEntries();
        ArrayList<Token>[] tempRow = entries[row];
        ArrayList<Token> tempEntry = new ArrayList<>();
        for (int j = 0; j < m.getNumOfCols(); j++) {
            tempEntry.add(new Number(scalar));
            tempEntry.add(BracketFactory.makeOpenBracket());
            tempEntry.addAll(tempRow[j]);
            tempEntry.add(BracketFactory.makeCloseBracket());
            entries[row][j] = tempEntry;
            tempEntry.clear();
        }
        return new Matrix(entries);
    }

    /**
     * Applies the given Row Operations(steps) to the given Matrix(m)
     * Swap step: 1, Add step: 2, Scale step: 3
     *
     * @param m     The Matrix to apply the given Row Operations to
     * @param steps The Row Operations to be applied
     * @return A Matrix with the given steps applied to the original Matrix
     */
    private static Matrix applySteps(Matrix m, double[][] steps) {
        if (steps.length == 0) {
            return m;
        } else if (steps[0][0] == 1) {
            return applySteps(swapRows(m, (int) steps[0][1], (int) steps[0][2]), Arrays.copyOfRange(steps, 1, steps.length - 1));
        } else if (steps[0][0] == 2) {
            return applySteps(addRows(m, (int) steps[0][1], (int) steps[0][2], (int) steps[0][3]), Arrays.copyOfRange(steps, 1, steps.length - 1));
        } else if (steps[0][0] == 3) {
            return applySteps(scaleRow(m, (int) steps[0][1], steps[0][2]), Arrays.copyOfRange(steps, 1, steps.length - 1));
        } else {
            throw new IllegalArgumentException("Invalid steps");
        }
    }


    /**
     * Row Reduces an AugmentedMatrix(aug) to RREF
     *
     * @param aug the Augmented Matrix to be Row Reduced
     * @return An Augmented Matrix whose primary Matrix(Matrix at index 0) is
     * in RREF
     */
    public static AugmentedMatrix rowReduceRREF(AugmentedMatrix aug) {
        double[][] steps = getRREFSteps(aug.getMatrices()[0]);
        Matrix[] matrices = new Matrix[aug.getMatrices().length];
        for (int k = 0; k < matrices.length; k++) {
            matrices[k] = applySteps(aug.getMatrices()[k], steps);
        }
        return new AugmentedMatrix(matrices);
    }

    /**
     * Row Reduces an AugmentedMatrix(aug) to REF
     *
     * @param aug the Augmented Matrix to be Row Reduced
     * @return An Augmented Matrix whose primary Matrix(Matrix at index 0) is
     * in REF
     */
    public static AugmentedMatrix rowReduceREF(AugmentedMatrix aug) {
        double[][] steps = getREFSteps(aug.getMatrices()[0]);
        Matrix[] matrices = new Matrix[aug.getMatrices().length];
        for (int k = 0; k < matrices.length; k++) {
            matrices[k] = applySteps(aug.getMatrices()[k], steps);
        }
        return new AugmentedMatrix(matrices);
    }

    public static Matrix ref(Matrix m) {
        return applySteps(m, getREFSteps(m));
    }

    public static Matrix rref(Matrix m) {
        return applySteps(m, getRREFSteps(m));
    }

    private static double[][] convMatrixEntriesToDbl(ArrayList<Token>[][] entries) {
        double[][] tempDbls = new double[entries.length][entries[0].length];
        for (int i = 0; i < tempDbls.length; i++) {
            for (int j = 0; j < tempDbls[i].length; j++) {
                tempDbls[i][j] = ((Number) entries[i][j].get(0)).getValue();
            }
        }
        return tempDbls;
    }

    public static Matrix getSqrt(Matrix a) {
        if (a instanceof AugmentedMatrix) {
            throw new IllegalArgumentException("Squareroots of Augmented Matrices cannot be computed");
        } else {
            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
            if (!m.isSquare()) {
                throw new IllegalArgumentException("Matrix must be square in order to compute its squareroot");
            } else {
                EigenDecomposition ed = new EigenDecomposition(m);
                try {
                    return new Matrix(ed.getSquareRoot().getData());
                } catch (Exception e) {
                    throw new IllegalArgumentException("Matrix must be symmetric and positive definite in order to compute its squareroot");
                }
            }
        }
    }

    public static Matrix solve(Matrix a, Matrix b) {
        Matrix tempA = evaluateMatrixEntries(a);
        Matrix tempB = evaluateMatrixEntries(b);
        RealMatrix matrix = new Array2DRowRealMatrix(convMatrixEntriesToDbl(tempA.getEntries()));
        RealVector vector = new ArrayRealVector(convMatrixEntriesToDbl(tempB.getEntries())[0]);
        return solve(matrix, vector);
    }

    /**
     * Solves the system [A|b]
     *
     * @param a The coefficient Matrix
     * @param b The constant vector
     * @return The solution vector
     */
    public static Matrix solve(RealMatrix a, RealVector b) {
        DecompositionSolver solver = new LUDecomposition(a).getSolver();
        RealVector solution = solver.solve(b);
        double[][] entries = new double[1][solution.getDimension()];
        entries[0] = solution.toArray();
        return new Matrix(entries);
    }

    /**
     * Finds the Inverse (if it exists) of a Matrix
     *
     * @param m A Matrix
     * @return The Inverse of the given Matrix
     */
    public static Matrix findInverse(Matrix m) {
        if (m.getNumOfRows() != m.getNumOfCols()) {
            throw new IllegalArgumentException("Non-square matrices are not invertible");
        } else if (((Number) (MatrixFunctionFactory.makeDeterminant().perform(m)).getEntry(0, 0).get(0)).getValue() != 0) {
            RealMatrix a = new Array2DRowRealMatrix(convMatrixEntriesToDbl(evaluateMatrixEntries(m).getEntries()));
            RealMatrix inverse = new LUDecomposition(a).getSolver().getInverse();
            return new Matrix(inverse.getData());
        } else {
            throw new IllegalArgumentException("Matrix is not invertible");
        }
    }


    /**
     * Finds the rank of a Matrix
     *
     * @param m A Matrix
     * @return The rank of the given Matrix
     */
    public static int findRank(Matrix m) {
        Matrix rowEquiv = applySteps(m, getRREFSteps(m));
        int rank = 0;
        for (int i = 0; i < m.getNumOfRows(); i++) {
            if (onlyZeroes(rowEquiv.getRow(i))) {
                rank++;
            }
        }
        return rank;
    }

    /**
     * ******************************************************
     * ************ EIGENVALUES AND EIGENVECTORS **************
     * *******************************************************
     */

    public static double[] getRealEigenValues(Matrix a) {
        if (a instanceof AugmentedMatrix) {
            throw new IllegalArgumentException("Augmented Matrices do not have eigenvalues");
        } else {
            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
            return realEigenValues(m);
        }
    }

    private static double[] realEigenValues(RealMatrix a) {
        if (!a.isSquare()) {
            throw new IllegalArgumentException("Matrix must be square in order to find eigenvalues");
        } else {
            Complex[] eigenVals = complexEigenValues(a);
            ArrayList<Double> realEigenVals = new ArrayList<>();
            for (int i = 0; i < eigenVals.length; i++) {
                if (eigenVals[i].isReal()) {
                    realEigenVals.add(new Double(eigenVals[i].getReal()));
                }
            }
            double[] temp = new double[realEigenVals.toArray().length];
            for (int i = 0; i < temp.length; i++) {
                temp[i] = (double) realEigenVals.get(i);
            }
            return temp;
        }
    }

    public static Complex[] getComplexEigenValues(Matrix a) {
        if (a instanceof AugmentedMatrix) {
            throw new IllegalArgumentException("Augmented Matrices do not have eigenvalues");
        } else {
            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
            return complexEigenValues(m);
        }
    }

    private static Complex[] complexEigenValues(RealMatrix a) {
        if (!a.isSquare()) {
            throw new IllegalArgumentException("Matrix must be square in order to find eigenvalues");
        } else {
            Complex[] eigenVals = new Complex[a.getColumnDimension()];
            EigenDecomposition ed = new EigenDecomposition(a);
            for (int i = 0; i < eigenVals.length; i++) {
                eigenVals[i] = new Complex(ed.getRealEigenvalue(i), ed.getImagEigenvalue(i));
            }
            return eigenVals;
        }
    }

    public static Matrix[] getEigenVectors(Matrix a) {
        if (a instanceof AugmentedMatrix) {
            throw new IllegalArgumentException("Augmented Matrices do not have eigenvalues");
        } else {
            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
            return eigenVectors(m);
        }
    }

    private static Matrix[] eigenVectors(RealMatrix a) {
        if (!a.isSquare()) {
            throw new IllegalArgumentException("Matrix must be square in order to find eigenvalues");
        } else {
            RealVector ev;
            Matrix[] vectors = new Matrix[a.getColumnDimension()];
            double[][] temp = new double[1][a.getColumnDimension()];
            EigenDecomposition ed = new EigenDecomposition(a);
            for (int i = 0; i < a.getColumnDimension(); i++) {
                ev = ed.getEigenvector(i);
                temp[0] = ev.toArray();
                vectors[i] = new Matrix(temp);
            }
            return vectors;
        }
    }

    public static Matrix getDiagonalMatrix(Matrix a) {
        if (a instanceof AugmentedMatrix) {
            throw new IllegalArgumentException("Augmented Matrices can not be diagonalized");
        } else {
            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
            if (!m.isSquare()) {
                throw new IllegalArgumentException("Matrix must be square in order to be diagonalizable");
            } else {
                EigenDecomposition ed = new EigenDecomposition(m);
                return new Matrix(ed.getD().getData());
            }
        }
    }

    public static Matrix getDiagonalizingMatrix(Matrix a) {
        if (a instanceof AugmentedMatrix) {
            throw new IllegalArgumentException("Augmented Matrices can not be diagonalized");
        } else {
            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
            if (!m.isSquare()) {
                throw new IllegalArgumentException("Matrix must be square in order to be diagonalizable");
            } else {
                EigenDecomposition ed = new EigenDecomposition(m);
                return new Matrix(ed.getV().getData());
            }
        }
    }

    /**
     * ******************************************************
     * ****************** LUP DECOMPOSITION *******************
     * *******************************************************
     */

    public static Matrix getLowerTriangularMatrix(Matrix a) {
        if (a instanceof AugmentedMatrix) {
            throw new IllegalArgumentException("Augmented Matrices can not be decomposed");
        } else {
            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
            if (!m.isSquare()) {
                throw new IllegalArgumentException("Matrix must be square in order to be LUP factorizable");
            } else {
                LUDecomposition lu = new LUDecomposition(m);
                return new Matrix(lu.getL().getData());
            }
        }
    }

    public static Matrix getUpperTriangularMatrix(Matrix a) {
        if (a instanceof AugmentedMatrix) {
            throw new IllegalArgumentException("Augmented Matrices can not be decomposed");
        } else {
            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
            if (!m.isSquare()) {
                throw new IllegalArgumentException("Matrix must be square in order to be LUP factorizable");
            } else {
                LUDecomposition lu = new LUDecomposition(m);
                return new Matrix(lu.getU().getData());
            }
        }
    }

    public static Matrix getPermutationMatrix(Matrix a) {
        if (a instanceof AugmentedMatrix) {
            throw new IllegalArgumentException("Augmented Matrices can not be decomposed");
        } else {
            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
            if (!m.isSquare()) {
                throw new IllegalArgumentException("Matrix must be square in order to be LUP factorizable");
            } else {
                LUDecomposition lu = new LUDecomposition(m);
                return new Matrix(lu.getP().getData());
            }
        }
    }
}
