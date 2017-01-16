/*
 * Copyright (c) 2016 TruTech Innovations Inc - All Rights Reserved
 */

package com.trutechinnovations.calculall;

/**
 * Contains static factory methods for Operators in Matrix Mode.
 *
 * @author Ejaaz Merali, Keith Wong
 * @version 3.0
 */

public class MatrixOperatorFactory {

    public static MatrixOperator makeMatrixAdd() {
        return new MatrixOperator("+", MatrixOperator.ADD, 2, true, 1, true) {
            @Override
            public Object operate(Object left, Object right) {
                if (left instanceof double[][] && right instanceof double[][]) {
                    double[][] leftD = (double[][]) left;
                    double[][] rightD = (double[][]) right;
                    if (leftD.length == rightD.length && leftD[0].length == rightD[0].length) {
                        return MatrixUtils.add(leftD, rightD);
                    } else {
                        throw new IllegalArgumentException("Matrices are not the same size");
                    }
                } else if (left instanceof Number && right instanceof Number) {
                    return new Number(OperatorFactory.makeAdd().operate(((Number) left).getValue(), ((Number) right).getValue()));
                } else {
                    throw new IllegalArgumentException("Both arguments must be Matrices or Numbers");
                }
            }
        };
    }

    public static MatrixOperator makeMatrixSubtract() {
        return new MatrixOperator("-", MatrixOperator.SUBTRACT, 2, true, 1, true) {
            @Override
            public Object operate(Object left, Object right) {
                if (left instanceof double[][] && right instanceof double[][]) {
                    double[][] leftD = (double[][]) left;
                    double[][] rightD = (double[][]) right;
                    if (leftD.length == rightD.length && leftD[0].length == rightD[0].length) {
                        return MatrixUtils.subtract(leftD, rightD);
                    } else {
                        throw new IllegalArgumentException("Matrices are not the same size");
                    }
                } else if (left instanceof Number && right instanceof Number) {
                    return new Number(OperatorFactory.makeSubtract().operate(((Number) left).getValue(), ((Number) right).getValue()));
                } else {
                    throw new IllegalArgumentException("Both arguments must be Matrices or Numbers");
                }
            }
        };
    }

    public static MatrixOperator makeMatrixMultiply() {
        return new MatrixOperator("·", MatrixOperator.MULTIPLY, 2, true, 1, true) {
            @Override
            public Object operate(Object left, Object right) {
                if (left instanceof double[][] && right instanceof double[][]) {//matrix-matrix multiplication
                    try {
                        return MatrixUtils.multiply((double[][]) left, (double[][]) right);
                    } catch (Exception e) {
                        throw e;
                    }
                } else if (left instanceof Number && right instanceof double[][]) {//scalar multiplication
                    return MatrixUtils.scalarMultiply((double[][]) right, ((Number) left).getValue());
                } else if (right instanceof Number && left instanceof double[][]) {//scalar multiplication
                    return MatrixUtils.scalarMultiply((double[][]) left, ((Number) right).getValue());
                } else if (left instanceof Number && right instanceof Number) {
                    return new Number(OperatorFactory.makeMultiply().operate(((Number) left).getValue(), ((Number) right).getValue()));
                } else {
                    throw new IllegalArgumentException("Invalid Input");
                }
            }
        };
    }

    public static MatrixOperator makeMatrixDivide() {
        return new MatrixOperator("/", MatrixOperator.DIVIDE, 2, true, 1, true) {
            @Override
            public Object operate(Object left, Object right) {
                if (left instanceof double[][] && right instanceof double[][]) {//matrix-matrix multiplication
                    try {
                        return MatrixUtils.multiply((double[][]) left, MatrixUtils.findInverse((double[][]) right));
                    } catch (Exception e) {
                        throw e;
                    }
                } else if (left instanceof Number && right instanceof double[][]) {//scalar multiplication + inversion
                    return MatrixUtils.scalarMultiply(MatrixUtils.findInverse((double[][]) right), ((Number) left).getValue());
                } else if (right instanceof Number && left instanceof double[][]) {//scalar multiplication
                    return MatrixUtils.scalarMultiply((double[][]) left, 1 / (((Number) right).getValue()));
                } else if (left instanceof Number && right instanceof Number) {
                    return new Number(OperatorFactory.makeDivide().operate(((Number) left).getValue(), ((Number) right).getValue()));
                } else {
                    throw new IllegalArgumentException("Invalid Input");
                }
            }
        };
    }

    public static MatrixOperator makeMatrixExponent() {
        return new MatrixOperator("^", MatrixOperator.EXPONENT, 2, true, 1, true) {
            @Override
            public Object operate(Object left, Object right) {
                if (left instanceof double[][] && right instanceof Number) {
                    try {
                        return MatrixUtils.exponentiate((double[][]) left, ((Number) right).getValue());
                    } catch (Exception e) {
                        throw e;
                    }
                } else {
                    throw new IllegalArgumentException("Invalid Input");
                }
            }
        };
    }

}
