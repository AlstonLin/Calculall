package com.trutech.calculall;

/**
 * Contains static factory methods of Functions used by Vectors.
 *
 * @author Alston Lin
 * @version Alpha 2.0
 */
public class VectorOperatorFactory {

    public static VectorOperator makeAdd() {
        return new VectorOperator("+", VectorOperator.ADD, 1, true) {
            @Override
            public Token operate(Token left, Token right) {
                if (left instanceof Vector && right instanceof Vector) {
                    Vector leftVector = (Vector) left;
                    Vector rightVector = (Vector) right;
                    if (leftVector.getDimensions() == rightVector.getDimensions()) {
                        double[] values = new double[leftVector.getDimensions()];
                        for (int i = 0; i < leftVector.getDimensions(); i++) {
                            values[i] += leftVector.getValues()[i] + rightVector.getValues()[i];
                        }
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
        return new VectorOperator("-", VectorOperator.SUBTRACT, 1, true) {
            @Override
            public Token operate(Token left, Token right) {
                if (left instanceof Vector && right instanceof Vector) {
                    Vector leftVector = (Vector) left;
                    Vector rightVector = (Vector) right;
                    if (leftVector.getDimensions() == rightVector.getDimensions()) {
                        double[] values = new double[leftVector.getDimensions()];
                        for (int i = 0; i < leftVector.getDimensions(); i++) {
                            values[i] += leftVector.getValues()[i] - rightVector.getValues()[i];
                        }
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
        return new VectorOperator("•", VectorOperator.DOT, 2, true) {
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
        return new VectorOperator("×", VectorOperator.CROSS, 3, true) {
            @Override
            public Token operate(Token left, Token right) {
                if (left instanceof Vector && right instanceof Vector) {
                    return VectorUtilities.findCrossProduct((Vector) left, (Vector) right);
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
        return new VectorOperator("∠", VectorOperator.ANGLE, 3, true) {
            @Override
            public Token operate(Token left, Token right) {
                if (left instanceof Vector && right instanceof Vector) {
                    Vector leftVector = (Vector) left;
                    Vector rightVector = (Vector) right;
                    if (leftVector.getDimensions() == rightVector.getDimensions()) {
                        double result;
                        result = Math.acos((VectorUtilities.findDotProduct(leftVector, rightVector))
                                / (VectorUtilities.calculateMagnitude(leftVector) * VectorUtilities.calculateMagnitude(rightVector)));
                        //Switched depending on angle mode
                        if (Double.isNaN(result)) {
                            result = 0d;
                        }
                        switch (((VectorMode) VectorMode.getInstance()).getAngleMode()) {
                            case VectorMode.DEGREE:
                                result *= (180 / Math.PI);
                                break;
                            case VectorMode.RADIAN:
                                //Deos nothing
                                break;
                            case VectorMode.GRADIAN:
                                result *= (200 / Math.PI);
                                break;
                        }
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