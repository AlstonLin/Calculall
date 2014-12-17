package com.trutech.calculall;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.AllowedSolution;
import org.apache.commons.math3.analysis.solvers.BracketingNthOrderBrentSolver;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;

import java.util.ArrayList;

/**
 * Static class that contains Utility methods using the Apache Library.
 */
public class ApacheUtility {
    public static final int MAX_EVALS = 10000;
    public static final double MIN_INTERVAL_BETWEEN_ROOTS = Math.pow(1, -(FunctionMode.ROUND_TO + 1));

    public static double[] findRoots(final ArrayList<Token> tokens) {
        UnivariateFunction f = new UnivariateFunction() {
            @Override
            public double value(double v) {
                double value = Utility.valueAt(tokens, v);
                return value;
            }
        };
        return findRoots(f, -3.14e9, 3.14e9);
    }

    private static double[] findRoots(UnivariateFunction f, double min, double max) {
        ArrayList<Double> roots = new ArrayList<Double>();
        BracketingNthOrderBrentSolver solver = new BracketingNthOrderBrentSolver();
        try {
            double root = solver.solve(MAX_EVALS, f, min, max, 0, AllowedSolution.LEFT_SIDE);
            roots.add(root);
            //Adds any other roots on the left or right side
            double[] rootsLeft = findRoots(f, min, root - MIN_INTERVAL_BETWEEN_ROOTS);
            double[] rootsRight = findRoots(f, root + MIN_INTERVAL_BETWEEN_ROOTS, max);
            for (int i = 0; i < rootsLeft.length; i++) {
                roots.add(rootsLeft[i]);
            }
            for (int i = 0; i < rootsRight.length; i++) {
                roots.add(rootsRight[i]);
            }
            //Dealing with Java's inability to convert to a double[] array easily
            Object[] array = roots.toArray();
            double[] rootsArray = new double[array.length];
            for (int i = 0; i < array.length; i++) {
                rootsArray[i] = (Double) array[i];
            }
            return rootsArray;
        } catch (NoBracketingException e) { //No solution
            return new double[0];
        } catch (NumberIsTooLargeException e) {
            return new double[0];
        }
    }
}
