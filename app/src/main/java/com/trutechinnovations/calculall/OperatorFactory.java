package com.trutechinnovations.calculall;

/**
 * Contains static methods that will create Operator pieces.
 *
 * @author Alston Lin, Ejaaz Merali
 * @version Alpha 2.0
 */
public class OperatorFactory {

    public static Operator makeAdd() {
        return new Operator("+", Operator.ADD, 2, true, 1, true) {
            @Override
            public double operate(double left, double right) {
                return left + right;
            }
        };
    }

    public static Operator makeSubtract() {
        return new Operator("−", Operator.SUBTRACT, 2, true, -1, false) {
            @Override
            public double operate(double left, double right) {
                return left - right;
            }
        };
    }

    public static Operator makeMultiply() {
        return new Operator("×", Operator.MULTIPLY, 3, true, 1, true) {
            @Override
            public double operate(double left, double right) {
                return left * right;
            }
        };
    }

    public static Operator makeDivide() {
        return new Operator("/", Operator.DIVIDE, 3, true, 0, false) {
            @Override
            public double operate(double left, double right) {
                if (right == 0) {
                    throw new ArithmeticException("Division by zero");
                } else {
                    return left / right;
                }
            }
        };
    }

    public static Operator makeFraction() {
        return new Operator("", Operator.FRACTION, 3, true, 0, false) {
            @Override
            public double operate(double left, double right) {
                if (right == 0) {
                    throw new ArithmeticException("Division by zero");
                } else {
                    return left / right;
                }
            }
        };
    }

    public static Operator makeExponent() {
        return new Operator("", Operator.EXPONENT, 5, false, 0, false) {
            @Override
            public double operate(double left, double right) {
                double result = Math.pow(left, right);
                if (result == Double.NaN)
                    throw new IllegalArgumentException("The answer involves Imaginary numbers (currently not supported)");
                return result;
            }
        };
    }

    public static Operator makeFactorial() {
        return new Operator("!", Operator.FACTORIAL, 5, false, 0, false) {
            @Override
            public double operate(double left, double right) {
                if (left % 1 != 0) { // Makes sure this is an integer
                    throw new IllegalArgumentException();
                }
                return Utility.factorial((int) left);
            }
        };
    }

    public static Operator makeVariableRoot() {
        return new Operator("√", Operator.VARROOT, 5, false, 0, false) {
            @Override
            public double operate(double left, double right) {
                if (left == 0) {
                    throw new IllegalArgumentException("Cannot have 0 as the base for a variable root!");
                }
                return Math.pow(right, 1 / left);
            }
        };
    }

    public static Operator makePermutation() {
        return new Operator("P", Operator.PERMUTATION, 5, false, 0, false) {  //TODO: make it so that the previous and next numbers are subscript
            @Override
            public double operate(double left, double right) {
                if (left % 1 != 0 || right % 1 != 0 || right > left) {
                    throw new IllegalArgumentException();
                } else {
                    try {
                        return Utility.factorial((int) left) / Utility.factorial((int) (left - right));
                    } catch (StackOverflowError e) {
                        throw new IllegalArgumentException("The calculation is to large to compute.");
                    }
                }
            }
        };
    }

    public static Operator makeCombination() {
        return new Operator("C", Operator.COMBINATION, 5, false, 0, false) { //TODO: make it so that the previous and next numbers are subscript
            @Override
            public double operate(double left, double right) throws NumberTooLargeException {
                if (left % 1 != 0 || right % 1 != 0) {
                    throw new IllegalArgumentException();
                } else {
                    if (right > left) {
                        return 0;
                    } else if (right == left) {
                        return 1;
                    } else {
                        try {
                            return Utility.factorial((int) left) / (Utility.factorial((int) right) * Utility.factorial((int) (left - right)));
                        } catch (StackOverflowError e) {
                            throw new IllegalArgumentException("The calculation is to large to compute.");
                        }
                    }
                }
            }
        };
    }
}
