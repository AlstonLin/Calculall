package com.trutech.calculall;

/**
 * Contains static methods that will create Operator pieces.
 *
 * @version 0.4.0
 */
public class OperatorFactory {

    public static Operator makeAdd() {
        return new Operator("+", Operator.ADD, 2, true) {
            @Override
            public double operate(double left, double right) {
                return left + right;
            }
        };
    }

    public static Operator makeSubtract() {
        return new Operator("−", Operator.SUBTRACT, 2, true) {
            @Override
            public double operate(double left, double right) {
                return left - right;
            }
        };
    }

    public static Operator makeMultiply() {
        return new Operator("*", Operator.MULTIPLY, 3, true) {
            @Override
            public double operate(double left, double right) {
                return left * right;
            }
        };
    }

    public static Operator makeDivide() {
        return new Operator("/", Operator.DIVIDE, 3, true) {
            @Override
            public double operate(double left, double right) {
                if (right == 0) {
                    throw new IllegalArgumentException();
                } else {
                    return left / right;
                }
            }
        };
    }

    public static Operator makeDot() {
        return new Operator("•", Operator.DOT, 6, true) {
            @Override
            public double operate(double left, double right) {
                return 1;
            }
        };
    }

    public static Operator makeCross() {
        return new Operator("×", Operator.CROSS, 6, true) {
            @Override
            public double operate(double left, double right) {
                return 1;
            }
        };
    }

    public static Operator makeAngle() {
        return new Operator("a", Operator.ANGLE, 6, true) {
            @Override
            public double operate(double left, double right) {
                return 1;
            }
        };
    }

    public static Operator makeExponent() {
        return new Operator("^", Operator.EXPONENT, 5, false) { //TODO: Make it so the next number is a superscript
            @Override
            public double operate(double left, double right) {
                return Math.pow(left, right);
            }
        };
    }

    public static Operator makeFactorial() {
        return new Operator("!", Operator.FACTORIAL, 5, false) {
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
        return new Operator("^√", Operator.VARROOT, 5, false) {
            @Override
            public double operate(double left, double right) {
                return Math.pow(right, 1 / left);
            }
        };
    }

    public static Operator makePermutation() {
        return new Operator("P", Operator.PERMUTATION, 5, false) {  //TODO: make it so that the previous and next numbers are subscript
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
        return new Operator("C", Operator.COMBINATION, 5, false) { //TODO: make it so that the previous and next numbers are subscript
            @Override
            public double operate(double left, double right) {
                if (left % 1 != 0 || right % 1 != 0) {
                    throw new IllegalArgumentException();
                } else {
                    if (right > left) {
                        return 0;
                    } else {
                        return Utility.factorial((int) left) / (Utility.factorial((int) right) * Utility.factorial((int) (left - right)));
                    }
                }
            }
        };
    }


}
