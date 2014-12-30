package com.trutech.calculall;

import java.util.ArrayList;
import java.util.Arrays;

import static com.trutech.calculall.Matrix.AugmentedMatrix;

/**
 * Contains the Row Reduction algorithm
 *
 * Created by Ejaaz on 24/12/2014.
 */
public class RowReducer {
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
    public static double[][] getREFSteps(Matrix m) {
        ArrayList<Double[]> steps = new ArrayList<>();
        Matrix temp = Utility.evaluateMatrix(m);
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
    public static double[][] getRREFSteps(Matrix m) {
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
     * @param m The original Matrix
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
     *
     * @param m     The Matrix to apply the given Row Operations to
     * @param steps The Row Operations to be applied
     * @return A Matrix with the given steps applied to the original Matrix
     */
    public static Matrix applySteps(Matrix m, double[][] steps) {
        return null;
    }


    /**
     * Row Reduces an AugmentedMatrix(aug)
     *
     * @param aug the Augmented Matrix to be Row Reduced
     * @return An Augmented Matrix whose primary Matrix(Matrix at index 0) is
     * in RREF
     */
    public static AugmentedMatrix rowReduce(AugmentedMatrix aug) {
        double[][] steps = getRREFSteps(aug.getMatrices()[0]);
        Matrix[] matrices = new Matrix[aug.getMatrices().length];
        for (int k = 0; k < matrices.length; k++) {
            matrices[k] = applySteps(aug.getMatrices()[k], steps);
        }
        return null;
    }
}
