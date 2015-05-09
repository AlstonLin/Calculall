package com.trutechinnovations.calculall;

import java.util.ArrayList;

/**
 * The Object representation of a mathematical Matrix.
 *
 * @author Ejaaz Merali
 * @version Alpha 2.0
 */

@SuppressWarnings("unused")
public class Matrix extends Token {

    private ArrayList<Token>[][] entries;

    public Matrix(ArrayList<Token>[][] entries) {
        super(null);
        this.entries = entries;
    }


    /**
     * Changes the size of the Matrix while keeping the values
     *
     * @param numOfRows The new number of rows of the Matrix
     * @param numOfCols The new number of columns of the Matrix
     */
    public void changeSize(int numOfRows, int numOfCols) {
        ArrayList<Token>[][] temp = entries;
        entries = new ArrayList[numOfRows][numOfCols];
        //Populates the entries
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfCols; j++) {
                if (i < temp.length && j < temp[0].length) { //Existing entry exists
                    entries[i][j] = temp[i][j];
                } else { //Creates a new entry
                    ArrayList<Token> entry = new ArrayList<>();
                    entry.add(new Number(0));
                    entries[i][j] = entry;
                }
            }
        }
    }

    /**
     * Sets the entry at the given row and column number to the given.
     *
     * @param x     Row number
     * @param y     Column number
     * @param entry The entry to set
     */
    public void setEntry(int x, int y, ArrayList<Token> entry) {
        entries[x][y] = entry;
    }

    public ArrayList<Token>[][] getEntries() {
        return entries;
    }

    public ArrayList<Token> getEntry(int i, int j) {
        if (i >= 0 && i < entries.length && j >= 0 && j < entries[0].length) {
            return entries[i][j];
        } else {
            throw new IllegalArgumentException("Entry does not exist");
        }
    }

    public ArrayList<Token>[] getRow(int i) {
        if (i >= 0 && i < entries.length) {
            return entries[i];
        } else {
            throw new IllegalArgumentException("Not enough rows");
        }
    }

    public ArrayList<Token>[] getColumn(int j) {
        if (j >= 0 && j < entries[0].length) {
            ArrayList[] col = new ArrayList[entries.length];
            for (int i = 0; i < entries.length; i++) {
                col[i] = getEntry(i, j);
            }
            return col;
        } else {
            throw new IllegalArgumentException("Not enough columns");
        }
    }


    public int getNumOfRows() {
        return entries.length;
    }

    public int getNumOfCols() {
        return entries[0].length;
    }

    /**
     * Determines whats gets displayed onto the Screen.
     *
     * @return The visual representation of the Matrix
     */
    public String getSymbol() {
        String s = "";
        for (int i = 0; i < entries.length; i++) {
            String temp = "";
            for (int j = 0; j < entries[i].length; j++) {
                if (j != 0) {
                    s += ",";
                }
                s += Utility.machinePrintExpression(entries[i][j]);
            }
            s += "/";
        }
        return s;
    }
}