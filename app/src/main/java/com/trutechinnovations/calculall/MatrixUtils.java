package com.trutechinnovations.calculall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;


/**
 * Contains the Matrix Utilities (row reduction algorithms, matrix entry simplifier, etc.)
 *
 * @author Alston Lin, Ejaaz Merali
 * @version 3.0
 */
public class MatrixUtils {

    private static Command<Double, double[]> addCommand = new Command<Double, double[]>() {
        @Override
        public Double execute(double[] o) {
            return o[0] + o[1];
        }
    };


    private static Command<Double, double[]> subtractCommand = new Command<Double, double[]>() {
        @Override
        public Double execute(double[] o) {
            return o[0] - o[1];
        }
    };

    /**
     * Applies the given Command the two given Matrices and returns the resultant, or null if they are no the same dimensions.
     *
     * @param a       The first Matrix as an array of doubles
     * @param b       The second Matrix as an array of doubles
     * @param command The command to apply to the operation
     * @return The resultant Matrix, or null if the command is not possible.
     */
    public static double[][] applyCommand(double[][] a, double[][] b, Command<Double, double[]> command) {
        if (a.length == b.length && a[0].length == b[0].length) {
            double[][] result = new double[a.length][b.length];
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[i].length; j++) {
                    double[] data = new double[2];
                    data[0] = a[i][j];
                    data[1] = b[i][j];
                    result[i][j] = command.execute(data);
                }
            }
            return result;
        } else {
            return null;
        }
    }

    public static double[][] add(double[][] a, double[][] b) {
        return applyCommand(a, b, addCommand);
    }

    public static double[][] subtract(double[][] a, double[][] b) {
        return applyCommand(a, b, subtractCommand);
    }

    public static double[][] transpose(double[][] a) {
        double[][] result = new double[a[0].length][a.length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                result[j][i] = a[i][j];
            }
        }
        return result;
    }

    public static double[][] multiply(double[][] a, double[][] b) {
        if (a[0].length != b.length) {
            throw new IllegalArgumentException("Number of columns of left matrix is not " +
                    "equal to the number of rows of right matrix");
        } else {
            double[][] matrix = new double[a.length][b[0].length];
            double tempEntry = 0;
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < b[0].length; j++) {
                    tempEntry = dotProduct(getRow(a, i), getColumn(b, j));
                    matrix[i][j] = tempEntry;
                }
            }
            return matrix;
        }
    }

    public static double[][] scalarMultiply(double[][] a, double b) {
        double[][] matrix = new double[a.length][a[0].length];
        double tempEntry = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                matrix[i][j] = b * (a[i][j]);
            }
        }
        return matrix;
    }

    public static double[][] exponentiate(double[][] a, double b) {
        if (b % 1 != 0) {
            throw new IllegalArgumentException("Matrices can only be raised to integer powers");
        }
        if (a.length == a[0].length) {
            double[][] c = a.clone();
            for (int i = 1; i < b; i++) {
                c = multiply(c, a);
            }
            return c;
        } else {
            throw new IllegalArgumentException("Only square matrices can be raised to a power");
        }
    }

    /**
     * @param left
     * @param right
     * @return The dot product of two column vectors
     */
    private static double dotProduct(double[] left, double[] right) {
        if (left.length == right.length) {
            double output = 0;
            for (int i = 0; i < left.length; i++) {
                output += (left[i]) * (right[i]);
            }
            return output;
        } else {
            throw new IllegalArgumentException("Vectors are not the same length");
        }
    }

    private static double[] getRow(double[][] a, int row) {
        return a[row];
    }

    private static double[] getColumn(double[][] a, int col) {
        double[] output = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            output[i] = a[i][col];
        }
        return output;
    }

    /**
     * Checks if the given array contains only zeroes, assumes
     * the all the elements of the array have been fully simplified
     *
     * @param a An array
     * @return true if the array only contains zeroes, false otherwise
     */
    private static boolean onlyZeroes(double[] a) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] != 0) {
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
    private static int getFirstNonZero(double[] a) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] != 0) {
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
    private static int getLastNonZero(double[] a) {
        for (int i = a.length - 1; i >= 0; i--) {
            if (a[i] != 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param a      The original Matrix
     * @param row1   The index of the Row being added to
     * @param row2   The index of the Row being added
     * @param scalar The scalar to multiply the second row by
     * @return A Matrix which is similar to the given Matrix(m) but with the Row at index row2,
     * multiplied by a scalar, being added to the row at index row1
     */
    private static double[][] addRows(double[][] a, int row1, int row2, double scalar) {
        double[][] newMatrix = a;
        for (int j = 0; j < a[0].length; j++) {
            newMatrix[row1][j] += scalar * (a[row2][j]);
        }
        return newMatrix;
    }

    /**
     * @param a    The original Matrix
     * @param row1 The index of the first Row
     * @param row2 The index of the second Row
     * @return A Matrix which is similar to the given Matrix but with two Rows having been swapped
     */
    private static double[][] swapRows(double[][] a, int row1, int row2) {
        double[][] newMatrix = a;
        double[] temp = newMatrix[row1];
        newMatrix[row1] = newMatrix[row2];
        newMatrix[row2] = temp;
        return newMatrix;
    }

    /**
     * @param a      The original Matrix
     * @param row    The index of the Row to be scaled
     * @param scalar The scaling factor
     * @return The Matrix m with all the entries, of the specified row, multiplied by a scalar
     */
    private static double[][] scaleRow(double[][] a, int row, double scalar) {
        double[][] newMatrix = a;
        for (int j = 0; j < a[0].length; j++) {
            newMatrix[row][j] *= scalar;
        }
        return newMatrix;
    }

    /**
     * Applies the given Row Operations(steps) to the given Matrix(m)
     * Swap step: 1, Add step: 2, Scale step: 3
     *
     * @param a     The Matrix to apply the given Row Operations to
     * @param steps The Row Operations to be applied
     * @return A Matrix with the given steps applied to the original Matrix
     */
    private static double[][] applySteps(double[][] a, double[][] steps) {
        if (steps.length == 0) {
            return a;
        } else if (steps.length == 1) {
            if (steps[0][0] == 1) {
                return swapRows(a, (int) steps[0][1], (int) steps[0][2]);
            } else if (steps[0][0] == 2) {
                return addRows(a, (int) steps[0][1], (int) steps[0][2], steps[0][3]);
            } else if (steps[0][0] == 3) {
                return scaleRow(a, (int) steps[0][1], steps[0][2]);
            } else {
                throw new IllegalArgumentException("Invalid steps");
            }
        } else if (steps.length > 1) {
            if (steps[0][0] == 1) {
                return applySteps(swapRows(a, (int) steps[0][1], (int) steps[0][2]), Arrays.copyOfRange(steps, 1, steps.length));
            } else if (steps[0][0] == 2) {
                return applySteps(addRows(a, (int) steps[0][1], (int) steps[0][2], steps[0][3]), Arrays.copyOfRange(steps, 1, steps.length));
            } else if (steps[0][0] == 3) {
                return applySteps(scaleRow(a, (int) steps[0][1], steps[0][2]), Arrays.copyOfRange(steps, 1, steps.length));
            } else {
                throw new IllegalArgumentException("Invalid steps");
            }
        } else if (steps[0][0] == 0) {
            return applySteps(a, Arrays.copyOfRange(steps, 1, steps.length));
        } else {
            throw new IllegalArgumentException("Invalid steps");
        }
    }

    private static double[][] deepCopyDblMatrix(double[][] input) {
        if (input == null) {
            return null;
        }
        double[][] output = new double[input.length][];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i].clone();
        }
        return output;
    }

    private static double[][] trimMatrix(double[][] input, int lastRow, int lastCol) {
        double[][] output = Arrays.copyOfRange(input, 0, lastRow);
        for (int i = 0; i < output.length; i++) {
            output[i] = Arrays.copyOfRange(input[i], 0, lastCol);
        }
        return output;
    }

    /**
     * Returns the Row Operations required to reduce the given Matrix(a) to
     * Row Echelon Form(REF)
     * Swap step: 1, Add step: 2, Scale step: 3
     *
     * @param a         The Matrix which will be Row Reduced to REF
     * @param minorNess Total number of rows/columns that have been stripped away from the original matrix
     * @return the row reduction steps
     */
    private static double[][] getREFSteps(double[][] a, int minorNess) {
        ArrayList<Double[]> steps = new ArrayList<>();
        if (a.length > 1) {
            double[][] temp = deepCopyDblMatrix(a);
            if (!onlyZeroes(getColumn(temp, 0))) {
                int pivot = getFirstNonZero(getColumn(temp, 0));
                if (pivot != 0) {
                    temp = swapRows(temp, 0, pivot);
                    Double[] swapStep = {1d, 0d, (double) pivot};
                    steps.add(swapStep);
                    pivot = 0;
                }
                double[] restOfCol = Arrays.copyOfRange(getColumn(temp, 0), 1, temp.length);
                for (int i = 1; !onlyZeroes(restOfCol) && i < temp.length; i++) {
                    if (temp[i][0] != 0) {
                        double scalar = -1 * temp[i][0] / temp[0][0];
                        temp = addRows(temp, i, pivot, scalar);
                        Double[] addStep = {2d, (double) i, (double) pivot, scalar};
                        steps.add(addStep);
                    }
                }
            }
            double[][] minorSteps = getREFSteps(minorMatrix(temp, 0, 0), minorNess++);
            Double[] tempStep;
            for (int i = 0; i < minorSteps.length; i++) {
                if (minorSteps[i][0] == 1) {
                    minorSteps[i][1] += minorNess;
                    minorSteps[i][2] += minorNess;
                } else if (minorSteps[i][0] == 2) {
                    minorSteps[i][1] += minorNess;
                    minorSteps[i][2] += minorNess;
                } else if (minorSteps[i][0] == 3) {
                    minorSteps[i][1] += minorNess;
                } else {
                    throw new IllegalArgumentException("Invalid steps");
                }
            }
            for (int i = 0; i < minorSteps.length; i++) {
                tempStep = new Double[minorSteps[i].length];
                for (int j = 0; j < minorSteps[i].length; j++) {
                    tempStep[j] = minorSteps[i][j];
                }
                steps.add(tempStep);
            }
        }
        double[][] stepsArray = new double[steps.size()][3];
        for (int i = 0; i < stepsArray.length; i++) {
            stepsArray[i] = new double[steps.get(i).length];
            for (int j = 0; j < stepsArray[i].length; j++) {
                stepsArray[i][j] = steps.get(i)[j];
            }
        }
        return stepsArray;
    }

    public static double[][] toREF(double[][] a) {
        return applySteps(a, getREFSteps(a, 0));
    }

    /**
     * Returns the Row Operations required to reduce the given Matrix(m) to
     * Reduced Row Echelon Form(RREF)
     *
     * @param a The Matrix which will be Row Reduced to RREF
     * @return the row reduction steps
     */
    private static double[][] getRREFSteps(double[][] a) {
        ArrayList<Double[]> steps = new ArrayList<>();
        double[][] dummyArray = {{0d, 0d, 0d}};
        if (a.length > 1) {
            double[][] refSteps = getREFSteps(a, 0);
            Double[] tempStep;
            double[][] temp;
            for (int i = 0; i < refSteps.length; i++) {
                tempStep = new Double[refSteps[i].length];
                for (int j = 0; j < refSteps[i].length; j++) {
                    tempStep[j] = refSteps[i][j];
                }
                steps.add(tempStep);
            }
            temp = applySteps(deepCopyDblMatrix(a), refSteps);

            int pivotRowIndex = -1;
            for (int i = 1; pivotRowIndex == -1 && i < temp[0].length; i++) {
                pivotRowIndex = getLastNonZero(getColumn(temp, temp[0].length - i));
            }
            int pivotColIndex = getFirstNonZero(getRow(temp, pivotRowIndex));
            if (!onlyZeroes(getColumn(temp, pivotRowIndex))) {
                double[] restOfCol = Arrays.copyOfRange(getColumn(temp, pivotColIndex), 0, pivotRowIndex);
                for (int i = 0; !onlyZeroes(restOfCol) && i < pivotRowIndex; i++) {
                    if (temp[i][pivotColIndex] != 0) {
                        double scalar = -1 * temp[i][pivotColIndex] / temp[pivotRowIndex][pivotColIndex];
                        temp = addRows(temp, i, pivotRowIndex, scalar);
                        Double[] addStep = {2d, (double) i, (double) pivotRowIndex, scalar};
                        steps.add(addStep);
                    }
                }
            }

            for (int i = 0; i < temp.length; i++) {
                int pivot = getFirstNonZero(getRow(temp, i));
                if (pivot != -1 && temp[i][pivot] != 1) {
                    double scalar = 1 / (temp[i][pivot]);
                    scaleRow(temp, i, scalar);
                    Double[] scaleStep = {3d, (double) i, scalar};
                    steps.add(scaleStep);
                }
            }

            if (a.length > 2) {
                double[][] minorSteps = getRREFSteps(trimMatrix(temp, temp.length - 1, temp[0].length - 1));
                if (minorSteps.length == 1 && !Arrays.equals(minorSteps[0], dummyArray[0])) {
                    for (int i = 0; i < minorSteps.length; i++) {
                        tempStep = new Double[minorSteps[i].length];
                        for (int j = 0; j < minorSteps[i].length; j++) {
                            tempStep[j] = minorSteps[i][j];
                        }
                        steps.add(tempStep);
                    }
                }
            }

            double[][] stepsArray = new double[steps.size()][3];
            for (int i = 0; i < stepsArray.length; i++) {
                stepsArray[i] = new double[steps.get(i).length];
                for (int j = 0; j < stepsArray[i].length; j++) {
                    stepsArray[i][j] = steps.get(i)[j];
                }
            }
            return stepsArray;
        } else {
            return dummyArray;
        }
    }

    public static double[][] toRREF(double[][] a) {
        return applySteps(a, getRREFSteps(a));
    }

    public static int rank(double[][] a) {
        double[][] rowReduced = toREF(a);
        int rank = 0;
        for (int i = 0; i < a.length; i++) {
            if (!onlyZeroes(getRow(a, i))) {
                rank++;
            }
        }
        return rank;
    }

    public static double trace(double[][] a) {
        double tr = 0d;
        for (int i = 0; i < a.length; i++) {
            tr += a[i][i];
        }
        return tr;
    }

    /**
     * Finds the determinant of the given matrix using a cofactor expansion of row 0.
     *
     * @param a The matrix
     * @return The determinant of the matrix
     */
    public static double findDeterminant(double[][] a) {
        final int ROW = 0;
        if (a.length > 2) {
            double det = 0;
            for (int j = 0; j < a[ROW].length; j++) {
                double entry = a[ROW][j];
                double cofactor = findCofactor(a, ROW, j);
                det += entry * cofactor;
            }
            return det;
        } else if (a.length == 2) {
            return ((a[0][0]) * (a[1][1])) - ((a[1][0]) * (a[0][1]));
        } else if (a.length == 1) {
            return a[0][0];
        } else {
            throw new IllegalArgumentException("Invalid matrix size");
        }

    }

    /**
     * Finds the cofactor of a specific entry of the given matrix.
     *
     * @param matrix The matrix
     * @param i      The row number
     * @param j      The column number
     * @return The cofactor value
     */
    public static double findCofactor(double[][] matrix, int i, int j) {
        return Math.pow(-1, i + j) * findDeterminant(minorMatrix(matrix, i, j));
    }

    /**
     * Finds the cofactor matrix of the given matrix.
     *
     * @param matrix The matrix to find the cofactor matrix
     * @return The cofactor matrix of the matrix
     */
    public static double[][] getCofactorMatrix(double[][] matrix) {
        double[][] cofactorMatrix = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                double cofactor = findCofactor(matrix, i, j);
                cofactorMatrix[i][j] = cofactor;
            }
        }
        return cofactorMatrix;
    }

    /**
     * Finds the adjoint (transpose of cofactor) matrix of the given matrix
     *
     * @param matrix The matrix to find the adjoint
     * @return The adjoint matrix
     */
    public static double[][] getAdjointMatrix(double[][] matrix) {
        return transpose(getCofactorMatrix(matrix));
    }


    /**
     * Finds the inverse matrix by using the adjoint method.
     *
     * @param matrix The matrix to find the inverse
     * @return The inverse matrix
     */
    public static double[][] findInverse(double[][] matrix) {
        double[][] adjoint = getAdjointMatrix(matrix);
        double determinant = findDeterminant(matrix);
        if (determinant == 0) { //Uninvertible Matrix
            throw new IllegalArgumentException("The matrix is non-invertible!");
        }
        for (int i = 0; i < adjoint.length; i++) {
            for (int j = 0; j < adjoint[i].length; j++) {
                adjoint[i][j] *= 1 / determinant;
            }
        }
        return adjoint;
    }

    private static double[][] minorMatrix(double[][] input, int row, int column) {
        int rowIndex = 0;
        int colIndex = 0;
        double[][] minor = new double[input.length - 1][input.length - 1];

        for (int i = 0; i < input.length; i++) {
            if (i != row) {
                for (int j = 0; j < input.length; j++) {
                    if (j != column) {
                        minor[rowIndex][colIndex] = input[i][j];
                        colIndex++;
                    }
                }
                rowIndex++;
                colIndex = 0;
            }
        }
        return minor;
    }

    /**
     * Evaluates every entry of the given matrix.
     *
     * @param matrix The unsimplified matrix
     * @return A array of doubles containing the value of each entry
     */
    public static double[][] evaluateMatrixEntries(Matrix matrix) {
        double[][] result = new double[matrix.getNumOfRows()][matrix.getNumOfCols()];
        for (int i = 0; i < matrix.getNumOfRows(); i++) {
            for (int j = 0; j < matrix.getNumOfCols(); j++) {
                ArrayList<Token> entry = Utility.condenseDigits(Utility.addMissingBrackets(Utility.subVariables(matrix.getEntry(i, j))));
                if (entry.size() == 0) {
                    throw new IllegalArgumentException("Parsing failed, entry is empty");
                } else {
                    entry = Utility.setupExpression(entry);
                    entry = Utility.convertToReversePolish(entry);
                    result[i][j] = Utility.evaluateExpression(entry);
                }
            }
        }
        return result;
    }


    /**
     * Substitutes all the variables on the tokens list with the defined values
     *
     * @param tokens The tokens to sub variables
     * @return The list of tokens with the variables substituted
     */
    private static ArrayList<Token> subVariables(ArrayList<Token> tokens) {
        ArrayList<Token> newTokens = new ArrayList<>();
        for (Token token : tokens) {
            if (token instanceof Variable) {
                int index = tokens.indexOf(token);
                Variable v = (Variable) token;
                newTokens.addAll(index, v.getValue());
            } else {
                newTokens.add(token);
            }
        }
        return newTokens;
    }


    public static ArrayList<Token> setupExpression(ArrayList<Token> toSetup) {
        ArrayList<Token> newExpression = new ArrayList<>();
        for (Token t : toSetup) {
            boolean negative = false;
            Token last = newExpression.isEmpty() ? null : newExpression.get(newExpression.size() - 1); //Last token in the new expression
            Token beforeLast = newExpression.size() > 1 ? newExpression.get(newExpression.size() - 2) : null;
            boolean lastIsSubtract = last instanceof MatrixOperator && last.getType() == MatrixOperator.SUBTRACT;
            boolean beforeLastIsOperator = beforeLast != null && beforeLast instanceof MatrixOperator;
            boolean beforeLastIsOpenBracket = beforeLast != null && beforeLast instanceof Bracket && (beforeLast.getType() == Bracket.OPEN
                    || beforeLast.getType() == Bracket.NUM_OPEN || beforeLast.getType() == Bracket.DENOM_OPEN || beforeLast.getType() == Bracket.SUPERSCRIPT_OPEN);

            if (t instanceof Bracket) {
                Bracket b = (Bracket) t;
                if (b.getType() == Bracket.OPEN &&
                        last instanceof Bracket &&
                        (last.getType() == Bracket.CLOSE ||
                                last.getType() == Bracket.SUPERSCRIPT_CLOSE ||
                                last.getType() == Bracket.DENOM_CLOSE)) { //Ex. (2 + 1)(3 + 4), (2)/(5)(x + 1) or x^(2)(x+1)
                    newExpression.add(MatrixOperatorFactory.makeMatrixMultiply()); //Implies multiplication between the two expressions in the brackets
                } else if ((last instanceof Number || last instanceof Matrix || last instanceof Variable) &&
                        b.getType() == Bracket.OPEN) { //Ex. 3(2 + 1) or X(1+X)
                    newExpression.add(MatrixOperatorFactory.makeMatrixMultiply());
                } else if (last instanceof MatrixOperator && last.getType() == Operator.SUBTRACT && beforeLastIsOperator) { //Ex. E + -(X + 1) -> E + -1 * (X + 1)
                    newExpression.remove(last);
                    newExpression.add(new Number(-1));
                    newExpression.add(MatrixOperatorFactory.makeMatrixMultiply());
                }
            } else if (t instanceof Number || t instanceof Variable || t instanceof Matrix || t instanceof MatrixFunction) { //So it works with Function mode too
                if (last instanceof Number) { //Ex. 5A , 5f(x)
                    newExpression.add(MatrixOperatorFactory.makeMatrixMultiply());
                } else if (last instanceof Matrix) {
                    newExpression.add(MatrixOperatorFactory.makeMatrixMultiply());
                } else if (last instanceof Bracket &&
                        (last.getType() == Bracket.CLOSE ||
                                last.getType() == Bracket.SUPERSCRIPT_CLOSE ||
                                last.getType() == Bracket.DENOM_CLOSE)) { //Ex. x^2(x + 1) or 2/5x
                    newExpression.add(MatrixOperatorFactory.makeMatrixMultiply());
                } else if (lastIsSubtract &&
                        (beforeLastIsOperator || beforeLastIsOpenBracket || newExpression.size() <= 1)) { //Ex. E * -X -> E * -1 * X
                    newExpression.remove(last);
                    if (t instanceof Number || t instanceof Matrix) {
                        negative = true;
                    } else {
                        newExpression.add(new Number(-1));
                        newExpression.add(MatrixOperatorFactory.makeMatrixMultiply());
                    }
                } else if (t instanceof MatrixFunction &&
                        (last instanceof MatrixFunction ||
                                (last instanceof Bracket &&
                                        (last.getType() == Bracket.CLOSE ||
                                                last.getType() == Bracket.SUPERSCRIPT_CLOSE ||
                                                last.getType() == Bracket.DENOM_CLOSE)) ||
                                last instanceof Variable)) { //Ex. f(x)g(x) or (1 + 2)f(x) or xf(x)
                    newExpression.add(MatrixOperatorFactory.makeMatrixMultiply());
                }

                if (t instanceof Variable && last instanceof Variable) { //Ex. pi x
                    newExpression.add(MatrixOperatorFactory.makeMatrixMultiply());
                }
            }
            if (negative) {
                if (t instanceof Number) {
                    newExpression.add(new Number(((Number) t).getValue() * -1));
                } else if (t instanceof Matrix) {
                    newExpression.add((Token) MatrixOperatorFactory.makeMatrixMultiply().operate(new Number(-1), t));
                } else {
                    newExpression.add(t);
                }
            } else {
                newExpression.add(t);
            }
        }
        return newExpression;
    }

    /**
     * Uses the shunting yard algorithm to change the matrix expression from infix to reverse polish.
     *
     * @param infix The infix expression
     * @return The expression in reverse polish
     * @throws java.lang.IllegalArgumentException The infix notation is invalid
     */
    public static ArrayList<Token> convertToReversePolish(ArrayList<Token> infix) {
        ArrayList<Token> reversePolish = new ArrayList<>();
        Stack<Token> stack = new Stack<>();
        for (Token token : infix) {
            if (token instanceof Number || token instanceof Variable || token instanceof Matrix) { //Adds directly to the queue if it's a token
                reversePolish.add(token);
            } else if (token instanceof MatrixFunction) { //Adds to the stack if it's a function
                stack.push(token);
            } else if (token instanceof MatrixOperator) {
                if (!stack.empty()) { //Make sure it's not empty to prevent bugs
                    Token top = stack.lastElement();
                    while (top != null
                            && ((top instanceof MatrixOperator && ((MatrixOperator) token).isLeftAssociative()
                            && ((MatrixOperator) top).getPrecedence() >= ((MatrixOperator) token).getPrecedence())
                            || top instanceof MatrixFunction)) { //Operator is left associative and has higher precedence / is a function
                        reversePolish.add(stack.pop()); //Pops top element to the queue
                        top = stack.isEmpty() ? null : stack.lastElement(); //Assigns the top element of the stack if it exists
                    }
                }
                stack.push(token);
            } else if (token instanceof Bracket) {
                Bracket bracket = (Bracket) token;
                if (bracket.getType() == Bracket.OPEN || bracket.getType() == Bracket.SUPERSCRIPT_OPEN
                        || bracket.getType() == Bracket.NUM_OPEN || bracket.getType() == Bracket.DENOM_OPEN) { //Pushes the bracket to the stack if it's open
                    stack.push(bracket);
                } else if (bracket.getType() == Bracket.CLOSE || bracket.getType() == Bracket.SUPERSCRIPT_CLOSE
                        || bracket.getType() == Bracket.NUM_CLOSE || bracket.getType() == Bracket.DENOM_CLOSE) { //For close brackets, pop operators onto the list until a open bracket is found
                    Token top = stack.lastElement();
                    while (!(top instanceof Bracket)) { //While it has not found an open bracket
                        reversePolish.add(stack.pop()); //Pops the top element
                        if (stack.isEmpty()) { //Mismatched brackets
                            throw new IllegalArgumentException();
                        }
                        top = stack.lastElement();
                    }
                    stack.pop(); //Removes the bracket
                }
            }
        }
        //All tokens read at this point
        while (!stack.isEmpty()) { //Puts the remaining tokens in the stack to the queue
            reversePolish.add(stack.pop());
        }
        return reversePolish;
    }

    /**
     * Takes a given Matrix expression in reverse polish form and returns the resulting value.
     *
     * @param tokens The matrix expression in reverse polish
     * @return The value of the expression
     * @throws java.lang.IllegalArgumentException The user entered an invalid expression
     */
    public static Token evaluateExpression(ArrayList<Token> tokens) {
        Stack<Object> stack = new Stack();
        for (Token token : tokens) {
            if (token instanceof Matrix || token instanceof Number) { //Adds all Matrices directly to the stack
                if (token instanceof Matrix) {
                    stack.push(evaluateMatrixEntries((Matrix) token));
                } else {
                    stack.push(token);
                }
            } else if (token instanceof MatrixOperator) {
                //Operates the first and second top operators
                Object right = stack.pop();
                Object left = stack.pop();
                stack.push(((MatrixOperator) token).operate(left, right)); //Adds the result back to the stack
            } else if (token instanceof MatrixFunction) { //Function uses the top number on the stack
                if (stack.peek() instanceof Number) {
                    throw new IllegalArgumentException(token.getSymbol() + " can only be applied to Matrices");
                }
                double[][] top = (double[][]) stack.pop(); //Function performs on the first matrix
                stack.push(((MatrixFunction) token).perform(top)); //Adds the result back to the stack
            } else { //This should never be reached
                throw new IllegalArgumentException();
            }
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Stack size is empty"); //There should only be 1 token left on the stack
        } else {
            Object o = stack.pop();
            if (o instanceof Token) {
                return (Token) o;
            } else if (o instanceof double[][]) {
                double[][] numbers = (double[][]) o;
                ArrayList<Token>[][] matrix = new ArrayList[numbers.length][numbers[0].length];
                for (int i = 0; i < numbers.length; i++) {
                    for (int j = 0; j < numbers[i].length; j++) {
                        matrix[i][j] = new ArrayList<>();
                        matrix[i][j].add(new Number(numbers[i][j]));
                    }
                }
                return new Matrix(matrix);
            } else {
                throw new IllegalStateException("Object that is not a Token nor a double[][] popped from Stack!");
            }
        }
    }

    /**
     * Makes an Identity Matrix of the given size
     *
     * @param dim the number of rows/columns of the Identity Matrix
     * @return The Identity Matrix of the specified size
     */
    public static double[][] makeIdentity(int dim) {
        double[][] newMatrix = new double[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                newMatrix[i][j] = 0;
            }
            newMatrix[i][i] = 1;
        }
        return newMatrix;
    }
//
//
//
//
//
//    private static Matrix minorMatrix(Matrix input, int row, int column) {
//        ArrayList[][] minor = new ArrayList[input.getNumOfRows() - 1][input.getNumOfCols() - 1];
//
//        for (int i = 0; i < input.getNumOfRows(); i++) {
//            for (int j = 0; j < input.getNumOfCols(); j++) {
//                if (i != row && j != column) {
//                    int x = 0;
//                    int y = 0;
//                    if (i > row && i + 1 < input.getNumOfRows()) {
//                        x = i + 1;
//                    } else if (i + 1 == input.getNumOfRows()) {
//                        continue;
//                    } else {
//                        x = i;
//                    }
//                    if (j > column && j + 1 < input.getNumOfCols()) {
//                        y = j + 1;
//                    } else if (j + 1 == input.getNumOfCols()) {
//                        continue;
//                    } else {
//                        y = j;
//                    }
//                    minor[i][j] = input.getEntry(x, y);
//                }
//            }
//        }
//        return new Matrix(minor);
//    }
//
//
//
//

//    /**
//     * Row Reduces an AugmentedMatrix(aug) to RREF
//     *
//     * @param aug the Augmented Matrix to be Row Reduced
//     * @return An Augmented Matrix whose primary Matrix(Matrix at index 0) is
//     * in RREF
//     */
//    public static AugmentedMatrix rowReduceRREF(AugmentedMatrix aug) {
//        double[][] steps = getRREFSteps(aug.getMatrices()[0]);
//        Matrix[] matrices = new Matrix[aug.getMatrices().length];
//        for (int k = 0; k < matrices.length; k++) {
//            matrices[k] = applySteps(aug.getMatrices()[k], steps);
//        }
//        return new AugmentedMatrix(matrices);
//    }
//
//    /**
//     * Row Reduces an AugmentedMatrix(aug) to REF
//     *
//     * @param aug the Augmented Matrix to be Row Reduced
//     * @return An Augmented Matrix whose primary Matrix(Matrix at index 0) is
//     * in REF
//     */
//    public static AugmentedMatrix rowReduceREF(AugmentedMatrix aug) {
//        double[][] steps = getREFSteps(aug.getMatrices()[0]);
//        Matrix[] matrices = new Matrix[aug.getMatrices().length];
//        for (int k = 0; k < matrices.length; k++) {
//            matrices[k] = applySteps(aug.getMatrices()[k], steps);
//        }
//        return new AugmentedMatrix(matrices);
//    }
//
//    public static Matrix ref(Matrix m) {
//        return applySteps(m, getREFSteps(m));
//    }
//
//    public static Matrix rref(Matrix m) {
//        return applySteps(m, getRREFSteps(m));
//    }
//
//    public static double[][] convMatrixEntriesToDbl(ArrayList<Token>[][] entries) {
//        double[][] tempDbls = new double[entries.length][entries[0].length];
//        for (int i = 0; i < tempDbls.length; i++) {
//            for (int j = 0; j < tempDbls[i].length; j++) {
//                tempDbls[i][j] = ((Number) entries[i][j].get(0)).getValue();
//            }
//        }
//        return tempDbls;
//    }
//
//    public static Matrix getSqrt(Matrix a) {
//        if (a instanceof AugmentedMatrix) {
//            throw new IllegalArgumentException("Squareroots of Augmented Matrices cannot be computed");
//        } else {
//            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
//            if (!m.isSquare()) {
//                throw new IllegalArgumentException("Matrix must be square in order to compute its squareroot");
//            } else {
//                EigenDecomposition ed = new EigenDecomposition(m);
//                try {
//                    return new Matrix(ed.getSquareRoot().getData());
//                } catch (Exception e) {
//                    throw new IllegalArgumentException("Matrix must be symmetric and positive definite in order to compute its squareroot");
//                }
//            }
//        }
//    }
//
//    public static Matrix solve(Matrix a, Matrix b) {
//        if (b.getNumOfCols() > 1) {
//            throw new IllegalArgumentException("Second argument must be a column vector");
//        }
//        Matrix tempA = evaluateMatrixEntries(a);
//        Matrix tempB = evaluateMatrixEntries(b);
//        RealMatrix matrix = new Array2DRowRealMatrix(convMatrixEntriesToDbl(tempA.getEntries()));
//        RealVector vector = new ArrayRealVector(convMatrixEntriesToDbl(tempB.getEntries())[0]);
//        return solve(matrix, vector);
//    }
//
//    /**
//     * Solves the system [A|b]
//     *
//     * @param a The coefficient Matrix
//     * @param b The constant vector
//     * @return The solution vector
//     */
//    private static Matrix solve(RealMatrix a, RealVector b) {
//        DecompositionSolver solver = new LUDecomposition(a).getSolver();
//        RealVector solution = solver.solve(b);
//        double[][] entries = new double[1][solution.getDimension()];
//        entries[0] = solution.toArray();
//        return new Matrix(entries);
//    }
//
//    /**
//     * Finds the Inverse (if it exists) of a Matrix
//     *
//     * @param m A Matrix
//     * @return The Inverse of the given Matrix
//     */
//    public static Matrix findInverse(Matrix m) {
//        if (m.getNumOfRows() != m.getNumOfCols()) {
//            throw new IllegalArgumentException("Non-square matrices are not invertible");
//        } else if (((Number) MatrixFunctionFactory.makeDeterminant().perform(m)).getValue() != 0) {
//            RealMatrix a = new Array2DRowRealMatrix(convMatrixEntriesToDbl(evaluateMatrixEntries(m).getEntries()));
//            RealMatrix inverse = new LUDecomposition(a).getSolver().getInverse();
//            return new Matrix(inverse.getData());
//        } else {
//            throw new IllegalArgumentException("Matrix is not invertible");
//        }
//    }
//
//
//    /**
//     * Finds the rank of a Matrix
//     *
//     * @param m A Matrix
//     * @return The rank of the given Matrix
//     */
//    public static int findRank(Matrix m) {
//        Matrix rowEquiv = applySteps(m, getRREFSteps(m));
//        int rank = 0;
//        for (int i = 0; i < m.getNumOfRows(); i++) {
//            if (onlyZeroes(rowEquiv.getRow(i))) {
//                rank++;
//            }
//        }
//        return rank;
//    }
//
//
//    /**
//     * ******************************************************
//     * ************ EIGENVALUES AND EIGENVECTORS **************
//     * *******************************************************
//     */
//
//    public static double[] getRealEigenValues(Matrix a) {
//        if (a instanceof AugmentedMatrix) {
//            throw new IllegalArgumentException("Augmented Matrices do not have eigenvalues");
//        } else {
//            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
//            return realEigenValues(m);
//        }
//    }
//
//    private static double[] realEigenValues(RealMatrix a) {
//        if (!a.isSquare()) {
//            throw new IllegalArgumentException("Matrix must be square in order to find eigenvalues");
//        } else {
//            Complex[] eigenVals = complexEigenValues(a);
//            ArrayList<Double> realEigenVals = new ArrayList<>();
//            for (int i = 0; i < eigenVals.length; i++) {
//                if (eigenVals[i].isReal()) {
//                    realEigenVals.add(new Double(eigenVals[i].getReal()));
//                }
//            }
//            double[] temp = new double[realEigenVals.toArray().length];
//            for (int i = 0; i < temp.length; i++) {
//                temp[i] = (double) realEigenVals.get(i);
//            }
//            return temp;
//        }
//    }
//
//    public static Complex[] getComplexEigenValues(Matrix a) {
//        if (a instanceof AugmentedMatrix) {
//            throw new IllegalArgumentException("Augmented Matrices do not have eigenvalues");
//        } else {
//            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
//            return complexEigenValues(m);
//        }
//    }
//
//    private static Complex[] complexEigenValues(RealMatrix a) {
//        if (!a.isSquare()) {
//            throw new IllegalArgumentException("Matrix must be square in order to find eigenvalues");
//        } else {
//            Complex[] eigenVals = new Complex[a.getColumnDimension()];
//            EigenDecomposition ed = new EigenDecomposition(a);
//            for (int i = 0; i < eigenVals.length; i++) {
//                eigenVals[i] = new Complex(ed.getRealEigenvalue(i), ed.getImagEigenvalue(i));
//            }
//            return eigenVals;
//        }
//    }
//
//    public static Matrix[] getEigenVectors(Matrix a) {
//        if (a instanceof AugmentedMatrix) {
//            throw new IllegalArgumentException("Augmented Matrices do not have eigenvalues");
//        } else {
//            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
//            return eigenVectors(m);
//        }
//    }
//
//    private static Matrix[] eigenVectors(RealMatrix a) {
//        if (!a.isSquare()) {
//            throw new IllegalArgumentException("Matrix must be square in order to find eigenvalues");
//        } else {
//            RealVector ev;
//            Matrix[] vectors = new Matrix[a.getColumnDimension()];
//            double[][] temp = new double[1][a.getColumnDimension()];
//            EigenDecomposition ed = new EigenDecomposition(a);
//            for (int i = 0; i < a.getColumnDimension(); i++) {
//                ev = ed.getEigenvector(i);
//                temp[0] = ev.toArray();
//                vectors[i] = new Matrix(temp);
//            }
//            return vectors;
//        }
//    }
//
//    public static Matrix getDiagonalMatrix(Matrix a) {
//        if (a instanceof AugmentedMatrix) {
//            throw new IllegalArgumentException("Augmented Matrices can not be diagonalized");
//        } else {
//            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
//            if (!m.isSquare()) {
//                throw new IllegalArgumentException("Matrix must be square in order to be diagonalizable");
//            } else {
//                EigenDecomposition ed = new EigenDecomposition(m);
//                return new Matrix(ed.getD().getData());
//            }
//        }
//    }
//
//    public static Matrix getDiagonalizingMatrix(Matrix a) {
//        if (a instanceof AugmentedMatrix) {
//            throw new IllegalArgumentException("Augmented Matrices can not be diagonalized");
//        } else {
//            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
//            if (!m.isSquare()) {
//                throw new IllegalArgumentException("Matrix must be square in order to be diagonalizable");
//            } else {
//                EigenDecomposition ed = new EigenDecomposition(m);
//                return new Matrix(ed.getV().getData());
//            }
//        }
//    }
//
//
//    /**
//     * ******************************************************
//     * ****************** LUP DECOMPOSITION *******************
//     * *******************************************************
//     */
//
//    public static Matrix getLowerTriangularMatrix(Matrix a) {
//        if (a instanceof AugmentedMatrix) {
//            throw new IllegalArgumentException("Augmented Matrices can not be decomposed");
//        } else {
//            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
//            if (!m.isSquare()) {
//                throw new IllegalArgumentException("Matrix must be square in order to be LUP factorizable");
//            } else {
//                LUDecomposition lu = new LUDecomposition(m);
//                return new Matrix(lu.getL().getData());
//            }
//        }
//    }
//
//    public static Matrix getUpperTriangularMatrix(Matrix a) {
//        if (a instanceof AugmentedMatrix) {
//            throw new IllegalArgumentException("Augmented Matrices can not be decomposed");
//        } else {
//            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
//            if (!m.isSquare()) {
//                throw new IllegalArgumentException("Matrix must be square in order to be LUP factorizable");
//            } else {
//                LUDecomposition lu = new LUDecomposition(m);
//                return new Matrix(lu.getU().getData());
//            }
//        }
//    }
//
//    public static Matrix getPermutationMatrix(Matrix a) {
//        if (a instanceof AugmentedMatrix) {
//            throw new IllegalArgumentException("Augmented Matrices can not be decomposed");
//        } else {
//            RealMatrix m = new Array2DRowRealMatrix(convMatrixEntriesToDbl(a.getEntries()));
//            if (!m.isSquare()) {
//                throw new IllegalArgumentException("Matrix must be square in order to be LUP factorizable");
//            } else {
//                LUDecomposition lu = new LUDecomposition(m);
//                return new Matrix(lu.getP().getData());
//            }
//        }
//    }
}
