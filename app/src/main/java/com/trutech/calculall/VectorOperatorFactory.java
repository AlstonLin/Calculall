package com.trutech.calculall;

/**
 * Contains static factory methods of Functions used by Vectors.
 *
 * @author Alston Lin
 * @version Alpha 2.0
 */
public class VectorOperatorFactory {

    public static VectorOperator makeAdd() {
        return new VectorOperator("+", VectorOperator.ADD, 1) {
            @Override
            public Token operate(Token left, Token right) {
                if (left instanceof Vector && right instanceof Vector) {
                    Vector leftVector = (Vector) left;
                    Vector rightVector = (Vector) right;
                    if (leftVector.getDimensions() == 2 && rightVector.getDimensions() == 2) {
                        double[] values = new double[2];
                        values[0] = leftVector.getValues()[0] + rightVector.getValues()[0];
                        values[1] = leftVector.getValues()[1] + rightVector.getValues()[1];
                        return new Vector(values);
                    } else if (leftVector.getDimensions() == 3 && rightVector.getDimensions() == 3) {
                        double[] values = new double[3];
                        values[0] = leftVector.getValues()[0] + rightVector.getValues()[0];
                        values[1] = leftVector.getValues()[1] + rightVector.getValues()[1];
                        values[2] = leftVector.getValues()[2] + rightVector.getValues()[2];
                        return new Vector(values);
                    } else {
                        throw new IllegalArgumentException("Attempted to operate two vector of different dimensions");
                    }
                } else if (left instanceof Number && right instanceof Number) {
                    return new Number(((Number) left).getValue() + ((Number) right).getValue());
                } else {
                    throw new IllegalArgumentException("Illegal Adding.");
                }
            }
        };
    }

    public static VectorOperator makeSubtract() {
        return new VectorOperator("-", VectorOperator.SUBTRACT, 1) {
            @Override
            public Token operate(Token left, Token right) {
                if (left instanceof Vector && right instanceof Vector) {
                    Vector leftVector = (Vector) left;
                    Vector rightVector = (Vector) right;
                    if (leftVector.getDimensions() == 2 && rightVector.getDimensions() == 2) {
                        double[] values = new double[2];
                        values[0] = leftVector.getValues()[0] - rightVector.getValues()[0];
                        values[1] = leftVector.getValues()[1] - rightVector.getValues()[1];
                        return new Vector(values);
                    } else if (leftVector.getDimensions() == 3 && rightVector.getDimensions() == 3) {
                        double[] values = new double[3];
                        values[0] = leftVector.getValues()[0] - rightVector.getValues()[0];
                        values[1] = leftVector.getValues()[1] - rightVector.getValues()[1];
                        values[2] = leftVector.getValues()[2] - rightVector.getValues()[2];
                        return new Vector(values);
                    } else {
                        throw new IllegalArgumentException("Attempted to operate two vector of different dimensions");
                    }
                } else if (left instanceof Number && right instanceof Number) {
                    return new Number(((Number) left).getValue() - ((Number) right).getValue());
                } else {
                    throw new IllegalArgumentException("Illegal Subtracting.");
                }
            }
        };
    }

    public static VectorOperator makeDot() {
        return new VectorOperator("•", VectorOperator.DOT, 2) {
            @Override
            public Token operate(Token left, Token right) {
                if (left instanceof Vector && right instanceof Vector) {
                    return new Number(VectorUtilities.findDotProduct((Vector) left, (Vector) right));
                } else if (left instanceof Number && right instanceof Vector) {
                    return VectorUtilities.findScalarProduct(((Number) left).getValue(), (Vector) right);
                } else if (right instanceof Number && left instanceof Vector) {
                    return VectorUtilities.findScalarProduct(((Number) right).getValue(), (Vector) left);
                } else if (left instanceof Number && right instanceof Number) {
                    return new Number(((Number) left).getValue() * ((Number) right).getValue());
                } else {
                    throw new IllegalArgumentException("Illegal Dot Product,");
                }
            }
        };
    }

    public static VectorOperator makeCross() {
        return new VectorOperator("×", VectorOperator.CROSS, 3) {
            @Override
            public Token operate(Token left, Token right) {
                if (left instanceof Vector && right instanceof Vector) {
                    Vector leftVector = (Vector) left;
                    Vector rightVector = (Vector) right;
                    if (leftVector.getDimensions() == 3 && rightVector.getDimensions() == 3) {
                        double[] values = new double[3];
                        values[0] = (leftVector.getValues()[1] * rightVector.getValues()[2]) - (leftVector.getValues()[2] * rightVector.getValues()[1]); //U2V3 - U3V2
                        values[1] = (leftVector.getValues()[2] * rightVector.getValues()[0]) - (leftVector.getValues()[0] * rightVector.getValues()[2]); //U3V1 - U1V3
                        values[2] = (leftVector.getValues()[0] * rightVector.getValues()[1]) - (leftVector.getValues()[1] * rightVector.getValues()[0]); //U1V2 - U2V1
                        return new Vector(values);
                    } else {
                        throw new IllegalArgumentException("Can only have a cross product of two 3D vectors.");
                    }
                } else if (left instanceof Number && right instanceof Vector) {
                    return VectorUtilities.findScalarProduct(((Number) left).getValue(), (Vector) right);
                } else if (right instanceof Number && left instanceof Vector) {
                    return VectorUtilities.findScalarProduct(((Number) right).getValue(), (Vector) left);
                } else if (left instanceof Number && right instanceof Number) {
                    return new Number(((Number) left).getValue() * ((Number) right).getValue());
                } else {
                    throw new IllegalArgumentException("Illegal Dot Product,");
                }
            }
        };
    }

    public static VectorOperator makeAngle() {
        return new VectorOperator("∠", VectorOperator.ANGLE, 3) {
            @Override
            public Token operate(Token left, Token right) {
                if (left instanceof Vector && right instanceof Vector) {
                    Vector leftVector = (Vector) left;
                    Vector rightVector = (Vector) right;
                    if (leftVector.getDimensions() == 3 && rightVector.getDimensions() == 3) {
                        double result;
                        result = Math.acos((VectorUtilities.findDotProduct(leftVector, rightVector))
                                / (VectorUtilities.calculateMagnitude(leftVector) * VectorUtilities.calculateMagnitude(rightVector))) * (180 / Math.PI);
                        return new Number(result);
                    } else if (leftVector.getDimensions() == 2 && rightVector.getDimensions() == 2) {
                        double result;
                        result = Math.acos((VectorUtilities.findDotProduct(leftVector, rightVector))
                                / (VectorUtilities.calculateMagnitude(leftVector) * VectorUtilities.calculateMagnitude(rightVector))) * (180 / Math.PI);
                        return new Number(result);
                    } else {
                        throw new IllegalArgumentException("Attempted to operate two vector of different dimensions");
                    }
                } else {
                    throw new IllegalArgumentException("Can only find an angle between two Vectors");
                }
            }
        };
    }
}
