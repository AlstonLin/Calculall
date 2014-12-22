package com.trutech.calculall;

import java.util.ArrayList;

/**
 * Created by Ejaaz on 22/12/2014.
 */

@SuppressWarnings("unused")
public class Matrix extends Token {

    private int numOfCols, numOfRows;
    private int augmentation;
    private int[] augmentBars;
    private ArrayList<Token>[][] entries;

    protected Matrix(ArrayList<Token>[][] entries) {
        super(null);
        this.entries = entries;
        this.numOfRows = entries.length;
        this.numOfCols = entries[0].length;
    }

    protected Matrix(ArrayList<Token>[][] entries, int[] augmentBars) {
        super(null);
        this.entries = entries;
        this.numOfRows = entries.length;
        this.numOfCols = entries[0].length;
        this.augmentBars = augmentBars;
        this.augmentation = augmentBars.length;
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
}
