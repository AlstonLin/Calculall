package com.trutech.calculall;

/**
 * Contains static methods that will create Operator pieces.
 *
 * @author Alston Lin, Ejaaz Merali
 * @version Alpha 2.0
 */
public class OperatorFactory {

    public static Operator makeAdd() {
        return new Operator("+", Operator.ADD, Operator.ADD_SUBTRACT, true, 1, true) {
            @Override
            public double operate(double left, double right) {
                return left + right;
            }
        };
    }

    public static Operator makeSubtract() {
        return new Operator("−", Operator.SUBTRACT, Operator.ADD_SUBTRACT, true, -1, false) {
            @Override
            public double operate(double left, double right) {
                return left - right;
            }
        };
    }

    public static Operator makeMultiply() {
        return new Operator("×", Operator.MULTIPLY, Operator.MULTIPLY_DIVIDE, true, 1, true) {
            @Override
            public double operate(double left, double right) {
                return left * right;
            }
        };
    }

    public static Operator makeDivide() {
        return new Operator("/", Operator.DIVIDE, Operator.MULTIPLY_DIVIDE, true, 0, false) {
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
        return new Operator("", Operator.FRACTION, Operator.MULTIPLY_DIVIDE, true, 0, false) {
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
        return new Operator("", Operator.EXPONENT, Operator.EXPONENT_PRECEDENCE, false, 0, false) {
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
        return new Operator("!", Operator.FACTORIAL, Operator.EXPONENT_PRECEDENCE, false, 0, false) {
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
        return new Operator("√", Operator.VARROOT, Operator.EXPONENT_PRECEDENCE, false, 0, false) {
            @Override
            public double operate(double left, double right) {
                return Math.pow(right, 1 / left);
            }
        };
    }

    public static Operator makePermutation() {
        return new Operator("P", Operator.PERMUTATION, Operator.EXPONENT_PRECEDENCE, false, 0, false) {
            @Override
            public double operate(double left, double right) {
                if (left % 1 != 0 || right % 1 != 0 || right > left) {
                    throw new IllegalArgumentException();
                } else {
                    return Utility.factorial((int) left) / Utility.factorial((int) (left - right));
                }
            }
        };
    }

    public static Operator makeCombination() {
        return new Operator("C", Operator.COMBINATION, Operator.EXPONENT_PRECEDENCE, false, 0, false) {
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
                        return Utility.factorial((int) left) / (Utility.factorial((int) right) * Utility.factorial((int) (left - right)));
                    }
                }
            }
        };
    }
}
