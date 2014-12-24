package com.trutech.calculall;

import java.util.ArrayList;

/**
 * Contains the Row Reduction algorithm
 * <p/>
 * Created by Ejaaz on 24/12/2014.
 */
public class RowReducer {


    /**
     * Returns the Row Operations required to reduce the given Matrix(m) to
     * Row Echelon Form(REF)
     *
     * @param m The Matrix which will be Row Reduced to REF
     * @return the row reduction steps
     */
    public static String[] getREFSteps(Matrix m) {
        ArrayList<String> steps = new ArrayList<>();

        return (String[]) steps.toArray();
    }

    /**
     * Returns the Row Operations required to reduce the given Matrix(m) to
     * Reduced Row Echelon Form(RREF)
     *
     * @param m The Matrix which will be Row Reduced to RREF
     * @return the row reduction steps
     */
    public static String[] getRREFSteps(Matrix m) {
        ArrayList<String> steps = new ArrayList<>();

        return (String[]) steps.toArray();
    }


    /**
     * Applies the given Row Operations(steps) to the given Matrix(m)
     *
     * @param m     The Matrix to apply the given Row Operations to
     * @param steps The Row Operations to be applied
     * @return
     */
    public Matrix applySteps(Matrix m, String[] steps) {
        return null;
    }


    /**
     * Row Reduces an AugmentedMatrix(aug)
     *
     * @param aug the Augmented Matrix to be Row Reduced
     * @return An Augmented Matrix whose primary Matrix(Matrix at index 0) is
     * in RREF
     */
    public Matrix.AugmentedMatrix rowReduce(Matrix.AugmentedMatrix aug) {
        String[] steps = getRREFSteps(aug.getMatrices()[0]);
        Matrix[] matrices = new Matrix[aug.getMatrices().length];
        for (int k = 0; k < matrices.length; k++) {
            matrices[k] = applySteps(aug.getMatrices()[k], steps);
        }
        return null;
    }
}
