package com.trutech.calculall;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ejaaz on 22/12/2014.
 */

@SuppressWarnings("unused")
public class Matrix extends Token {

    protected ArrayList<Token>[][] entries;
    private int numOfCols, numOfRows;

    protected Matrix(ArrayList<Token>[][] entries) {
        super(null);
        this.entries = entries;
        this.numOfRows = entries.length;
        this.numOfCols = entries[0].length;
    }

    protected Matrix(double[][] entries) {
        super(null);
        this.numOfRows = entries.length;
        this.numOfCols = entries[0].length;
        ArrayList<Token>[][] newMatrix = new ArrayList[numOfRows][numOfCols];
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfCols; j++) {
                newMatrix[i][j] = new ArrayList<>();
                newMatrix[i][j].add(new Number(entries[i][j]));
            }
        }
        this.entries = newMatrix;
    }

    public AugmentedMatrix makeNewAM(Matrix[] matrices){ return new AugmentedMatrix(matrices); }

    public int getNumOfCols() {
        return numOfCols;
    }

    public int getNumOfRows() {
        return numOfRows;
    }

    public ArrayList<Token>[][] getEntries(){ return entries; }

    public ArrayList<Token> getEntry(int i, int j) {
        if (i >= 0 && i < numOfRows && j >= 0 && j < numOfCols) {
            return entries[i][j];
        } else {
            throw new IllegalArgumentException("Entry does not exist");
        }
    }

    public ArrayList<Token>[] getRow(int i) {
        if (i >= 0 && i < numOfRows) {
            return entries[i];
        } else {
            throw new IllegalArgumentException("Not enough rows");
        }
    }

    public ArrayList<Token>[] getColumn(int j) {
        if (j >= 0 && j < numOfCols) {
            ArrayList[] col = new ArrayList[numOfRows];
            for (int i = 0; i < numOfRows; i++) {
                col[i] = getEntry(i, j);
            }
            return col;
        } else {
            throw new IllegalArgumentException("Not enough columns");
        }
    }

    public static class AugmentedMatrix extends Matrix {
        private int augmentation;
        private int[] augmentBars;
        private Matrix[] matrices;
        //private ArrayList<Token>[][] entries;
        private int numOfCols, numOfRows;

        protected AugmentedMatrix(Matrix[] matrices) {
            super((double[][]) null);
            this.augmentation = matrices.length;
            for (int i = 0; i < this.augmentation; i++) {
                if (i == 0) {
                    this.augmentBars[i] = matrices[i].getNumOfCols();
                } else {
                    this.augmentBars[i] = this.augmentBars[i - 1] + matrices[i].getNumOfCols() - 1;
                }
                this.numOfCols += matrices[i].getNumOfCols();
            }
            this.numOfRows = matrices[0].getNumOfRows();
            this.entries = new ArrayList[numOfRows][numOfCols];
            for (int i = 0; i < this.numOfRows; i++) {
                this.entries[i] = this.getRow(i);
            }
        }

        public int getNumOfCols() {
            return numOfCols;
        }

        public int getNumOfRows() {
            return numOfRows;
        }

        public int getAugmentation() {
            return augmentation;
        }

        public int[] getAugmentBars() {
            return augmentBars;
        }

        public Matrix[] getMatrices() {
            return matrices;
        }

        public ArrayList<Token>[] getRow(int i) {
            if (i >= 0 && i < numOfRows) {
                ArrayList<Token>[] row = new ArrayList[numOfCols];
                for (int k = 0; k < augmentation; k++) {
                    for (int j = 0; j < numOfCols; j++) {
                        row[j] = (matrices[k].getRow(i))[j];
                    }
                }
                return row;
            } else {
                throw new IllegalArgumentException("Invalid row index");
            }
        }

        private ArrayList<Token>[] getMatrixCol(AugmentedMatrix aug, int k) {
            if (k >= 0 && k < aug.getMatrices()[0].getNumOfCols()) {
                return aug.getMatrices()[0].getColumn(k);
            } else {
                return getMatrixCol(new AugmentedMatrix(Arrays.copyOfRange(aug.getMatrices(), 1, aug.getMatrices().length)),
                        k - (aug.getMatrices()[0].getNumOfCols() - 1));
            }
        }

        public ArrayList<Token>[] getColumn(int j) {
            if (j >= 0 && j < numOfCols) {
                ArrayList<Token>[] col = new ArrayList[numOfRows];
                return getMatrixCol(this, j);
            } else {
                throw new IllegalArgumentException("Invalid column index");
            }
        }
    }
}
