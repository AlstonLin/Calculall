/*
 * Copyright (c) 2016 TruTech Innovations Inc - All Rights Reserved
 */

package com.trutechinnovations.calculall;

import org.apache.commons.math3.special.Erf;

/**
 * Contains static methods that will create function pieces.
 *
 * @author Ejaaz Merali
 * @version 3.0
 */

public class FunctionFactory {
    private static double sin(double a) {
        if (a % Math.PI == 0) {
            return 0;
        } else {
            return Math.sin(a);
        }
    }

    private static double cos(double a) {
        if ((a % Math.PI / 2 == 0) && (a % Math.PI != 0)) {
            return 0;
        } else {
            return Math.cos(a);
        }
    }

    private static double tan(double a) {
        if (sin(a) == 0) {
            return 0;
        } else if (cos(a) == 0) {
            throw new IllegalArgumentException("tan is not defined at this value!");
        } else {
            return Math.tan(a);
        }
    }

    //Secondary trig functions (perform() should never be called for these)
    public static Function makeCsc() {
        return new Function("csc", Function.CSC) {
            @Override
            public double perform(double input) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static Function makeSec() {
        return new Function("sec", Function.SEC) {
            @Override
            public double perform(double input) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static Function makeCot() {
        return new Function("cot", Function.COT) {
            @Override
            public double perform(double input) {
                throw new UnsupportedOperationException();
            }
        };
    }

    //Special functions
    public static Function makeErf() {
        return new Function("erf", Function.ERF) {
            @Override
            public double perform(double input) {
                return Erf.erf(input);
            }
        };
    }

    public static Function makeErfi() { //Do not call perform()
        return new Function("erfi", Function.ERFI) {
            @Override
            public double perform(double input) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static Function makeGamma() {
        return new Function("Γ", Function.GAMMA) {
            @Override
            public double perform(double input) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static Function makeAppellF1() { //Do not call perform()
        return new Function("AppellF1", Function.APPELLF1) {
            @Override
            public double perform(double input) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static Function makeSin() { //In degrees
        return new Function("sin", Function.SIN) {
            @Override
            public double perform(double input) {
                switch (angleMode) {
                    case DEGREE:
                        return sin(Math.toRadians(input));
                    case RADIAN:
                        return sin(input);
                    case GRADIAN:
                        return sin(input * (Math.PI / 200));
                    default:
                        throw new IllegalArgumentException("Illegal Angle Mode!");
                }
            }
        };
    }

    public static Function makeASin() { //Result will be in degrees
        //TODO: MAKE IT SIN-1
        return new Function("arcsin", Function.ARCSIN) {
            @Override
            public double perform(double input) {
                if (Math.abs(input) > 1) {//Makes sure the input is within the range of sin
                    throw new IllegalArgumentException("The answer involves Imaginary numbers (currently not supported)");
                } else {
                    switch (angleMode) {
                        case DEGREE:
                            return Math.toDegrees(Math.asin(input));
                        case RADIAN:
                            return Math.asin(input);
                        case GRADIAN:
                            return ((Math.asin(input)) * (200 / Math.PI));
                        default:
                            throw new IllegalArgumentException("Illegal Angle Mode!");
                    }
                }
            }
        };
    }

    public static Function makeCos() { //In degrees
        return new Function("cos", Function.COS) {
            @Override
            public double perform(double input) {
                switch (angleMode) {
                    case DEGREE:
                        if ((input % 90 == 0) && (input % 180 != 0)) {
                            return 0;
                        }
                        return cos(Math.toRadians(input));
                    case RADIAN:
                        return cos(input);
                    case GRADIAN:
                        if ((input % 100 == 0) && (input % 200 != 0)) {
                            return 0;
                        }
                        return cos(input * (Math.PI / 200));
                    default:
                        throw new IllegalArgumentException("Illegal Angle Mode!");
                }
            }
        };
    }

    public static Function makeACos() { //Result will be in degrees
        //TODO: MAKE IT SIN-1
        return new Function("arccos", Function.ARCCOS) {
            @Override
            public double perform(double input) {
                if (Math.abs(input) > 1) {//Makes sure the input is within the range of sin
                    throw new IllegalArgumentException("The answer involves Imaginary numbers (currently not supported)");
                } else {
                    switch (angleMode) {
                        case DEGREE:
                            return Math.toDegrees(Math.acos(input));
                        case RADIAN:
                            return Math.acos(input);
                        case GRADIAN:
                            return ((Math.acos(input)) * (200 / Math.PI));
                        default:
                            throw new IllegalArgumentException("Illegal Angle Mode!");
                    }
                }
            }
        };
    }

    public static Function makeTan() { //In degrees
        return new Function("tan", Function.TAN) {
            @Override
            public double perform(double input) {
                switch (angleMode) {
                    case DEGREE:
                        if ((input % 90 == 0) && (input % 180 != 0)) {
                            throw new IllegalArgumentException("tan is not defined at this value!");
                        }
                        return tan(Math.toRadians(input));
                    case RADIAN:
                        if ((input % (Math.PI / 2) == 0) && (input % Math.PI != 0)) {
                            throw new IllegalArgumentException("tan is not defined at this value!");
                        }
                        return tan(input);
                    case GRADIAN:
                        if ((input % 100 == 0) && (input % 200 != 0)) {
                            throw new IllegalArgumentException("tan is not defined at this value!");
                        }
                        return tan(input * (Math.PI / 200));
                    default:
                        throw new IllegalArgumentException("Illegal Angle Mode!");
                }
            }
        };
    }

    public static Function makeATan() { //Result will be in degrees
        return new Function("arctan", Function.ARCTAN) {
            @Override
            public double perform(double input) {
                switch (angleMode) {
                    case DEGREE:
                        return Math.toDegrees(Math.atan(input));
                    case RADIAN:
                        return Math.atan(input);
                    case GRADIAN:
                        return ((Math.atan(input)) * (200 / Math.PI));
                    default:
                        throw new IllegalArgumentException("Illegal Angle Mode!");
                }
            }
        };
    }

    //NOTE: Hyperbolic functions do not use angles
    public static Function makeSinh() {
        return new Function("sinh", Function.SINH) {
            @Override
            public double perform(double input) {
                return Math.sinh(input);
            }
        };
    }

    public static Function makeASinh() {
        return new Function("arsinh", Function.ARCSINH) {
            @Override
            public double perform(double input) {
                return Math.log(input + Math.sqrt(Math.pow(input, 2) + 1));
            }
        };
    }

    public static Function makeCosh() {
        return new Function("cosh", Function.COSH) {
            @Override
            public double perform(double input) {
                return Math.cosh(input);
            }
        };
    }

    public static Function makeACosh() {
        return new Function("arcosh", Function.ARCCOSH) {
            @Override
            public double perform(double input) {
                if (input >= 1) {
                    return Math.log(input + Math.sqrt(Math.pow(input, 2) - 1));
                } else {
                    throw new IllegalArgumentException("The answer involves Imaginary numbers (currently not supported)");
                }
            }
        };
    }

    public static Function makeTanh() {
        return new Function("tanh", Function.TANH) {
            @Override
            public double perform(double input) {
                return Math.tanh(input);
            }
        };
    }

    public static Function makeATanh() {
        return new Function("arctanh", Function.ARCTANH) {
            @Override
            public double perform(double input) {
                if (Math.abs(input) < 1) {
                    return 0.5 * Math.log((input + 1) / (1 - input));
                } else {
                    throw new IllegalArgumentException("The answer involves Imaginary numbers (currently not supported)");
                }
            }
        };
    }

    public static Function makeLog_10() {
        return new Function("log", Function.LOG10) {
            @Override
            public double perform(double input) {
                return Math.log10(input);
            }
        };
    }

    public static Function makeLn() {
        return new Function("ln", Function.LN) {
            @Override
            public double perform(double input) {
                return Math.log(input);
            }
        };
    }

    public static Function makeSqrt() {
        return new Function("√", Function.SQRT) {
            @Override
            public double perform(double input) {
                if (input < 0) {
                    throw new IllegalArgumentException("The answer involves Imaginary numbers (currently not supported)");
                } else {
                    return Math.sqrt(input);
                }
            }
        };
    }

}