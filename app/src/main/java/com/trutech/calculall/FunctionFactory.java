package com.trutech.calculall;

import org.apache.commons.math3.special.Erf;

/**
 * Contains static methods that will create function pieces.
 *
 * @author Ejaaz Merali
 * @version Alpha 2.0
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
    ////////////////////////////////////////////////////
    ////////////////////SIN and ARCSIN//////////////////
    ////////////////////////////////////////////////////

    public static Function makeSinD() { //In degrees
        return new Function("sin", Function.SIN) {
            @Override
            public double perform(double input) {
                return sin(Math.toRadians(input));
            }
        };
    }

    public static Function makeASinD() { //Result will be in degrees
        //TODO: MAKE IT SIN-1
        return new Function("arcsin", Function.ARCSIN) {
            @Override
            public double perform(double input) {
                if (Math.abs(input) > 1) {//Makes sure the input is within the range of sin
                    throw new IllegalArgumentException();
                } else {
                    return Math.toDegrees(Math.asin(input));
                }
            }
        };
    }

    ////////////////////////////////////////////////////
    public static Function makeSinR() { //In radians
        return new Function("sin", Function.SIN) {
            @Override
            public double perform(double input) {
                return sin(input);
            }
        };
    }

    public static Function makeASinR() { //Result will be in radians
        return new Function("arcsin", Function.ARCSIN) {
            @Override
            public double perform(double input) {
                if (Math.abs(input) > 1) {//Makes sure the input is within the range of sin
                    throw new IllegalArgumentException();
                } else {
                    return Math.asin(input);
                }
            }
        };
    }

    ////////////////////////////////////////////////////
    public static Function makeSinG() { //In gradians
        return new Function("sin", Function.SIN) {
            @Override
            public double perform(double input) {
                return sin(input * (Math.PI / 200));
            }
        };
    }

    public static Function makeASinG() { //Result will be in gradians
        return new Function("arcsin", Function.ARCSIN) {
            @Override
            public double perform(double input) {
                if (Math.abs(input) > 1) {//Makes sure the input is within the range of sin
                    throw new IllegalArgumentException();
                } else {
                    return ((Math.asin(input)) * (200 / Math.PI));
                }
            }
        };
    }

    ////////////////////////////////////////////////////
    ////////////////////COS and ARCCOS//////////////////
    ////////////////////////////////////////////////////
    public static Function makeCosD() {//In  degrees
        return new Function("cos", Function.COS) {
            @Override
            public double perform(double input) {
                return cos(Math.toRadians(input));
            }
        };
    }

    public static Function makeACosD() { //Result will be in degrees
        return new Function("arccos", Function.ARCCOS) {
            @Override
            public double perform(double input) {
                if (Math.abs(input) > 1) {//Makes sure the input is within the range of cos
                    throw new IllegalArgumentException();
                } else {
                    return Math.toDegrees(Math.acos(input));
                }
            }
        };
    }

    ////////////////////////////////////////////////////
    public static Function makeCosR() { //In radians
        return new Function("cos", Function.COS) {
            @Override
            public double perform(double input) {
                return cos(input);
            }
        };
    }

    public static Function makeACosR() { //Result will be in radians
        return new Function("arccos", Function.ARCCOS) {
            @Override
            public double perform(double input) {
                if (Math.abs(input) > 1) {//Makes sure the input is within the range of cos
                    throw new IllegalArgumentException();
                } else {
                    return Math.acos(input);
                }
            }
        };
    }

    ////////////////////////////////////////////////////
    public static Function makeCosG() { //In gradians
        return new Function("cos", Function.COS) {
            @Override
            public double perform(double input) {
                return cos(input * (Math.PI / 200));
            }
        };
    }

    public static Function makeACosG() { //Result will be in gradians
        return new Function("arccos", Function.ARCCOS) {
            @Override
            public double perform(double input) {
                if (Math.abs(input) > 1) {//Makes sure the input is within the range of cos
                    throw new IllegalArgumentException();
                } else {
                    return ((Math.acos(input)) * (200 / Math.PI));
                }
            }
        };
    }

    ////////////////////////////////////////////////////
    ////////////////////TAN and ARCTAN//////////////////
    ////////////////////////////////////////////////////
    public static Function makeTanD() {//In degrees
        return new Function("tan", Function.TAN) {
            @Override
            public double perform(double input) {
                if (Math.toRadians(input) % (Math.PI / 2) == 0) {//Makes sure the input is within the domain of tan
                    throw new IllegalArgumentException();
                } else {
                    return tan(Math.toRadians(input));
                }
            }
        };
    }

    public static Function makeATanD() { //Result will be in degrees
        return new Function("arctan", Function.ARCTAN) {
            @Override
            public double perform(double input) {
                return Math.toDegrees(Math.atan(input));
            }
        };
    }

    ////////////////////////////////////////////////////
    public static Function makeTanR() { //In radians
        return new Function("tan", Function.TAN) {
            @Override
            public double perform(double input) {
                if (input % (Math.PI / 2) == 0) {//Makes sure the input is within the domain of tan
                    throw new IllegalArgumentException();
                } else {
                    return tan(input);
                }
            }
        };
    }

    public static Function makeATanR() { //Result will be in radians
        return new Function("arctan", Function.ARCTAN) {
            @Override
            public double perform(double input) {
                return Math.atan(input);
            }
        };
    }

    ////////////////////////////////////////////////////
    public static Function makeTanG() { //In gradians
        return new Function("tan", Function.TAN) {
            @Override
            public double perform(double input) {
                if ((input * (Math.PI / 200)) % (Math.PI / 2) == 0) {//Makes sure the input is within the domain of tan
                    throw new IllegalArgumentException();
                } else {
                    return tan(input * (Math.PI / 200));
                }
            }
        };
    }

    public static Function makeATanG() { //Result will be in gradians
        return new Function("arctan", Function.ARCTAN) {
            @Override
            public double perform(double input) {
                return ((Math.atan(input)) * (200 / Math.PI));
            }
        };
    }

    ////////////////////////////////////////////////////
    ////////////////HYPERBOLIC FUNCTIONS////////////////
    ////////////////////////////////////////////////////

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
                    throw new IllegalArgumentException();
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
                    throw new IllegalArgumentException();
                }
            }
        };
    }

    ////////////////////////////////////////////////////

    public static Function makeAbs() {
        return new Function("abs", Function.ABS) {
            @Override
            public double perform(double input) {
                return Math.abs(input);
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

	/*
    public static Function makeLog_b() {
        return new Function(null) {
			public double perform(double input, double base) {
                return (Math.log(input))/(Math.log(base)); //TODO: find a way to do this properly
            }
			@Override
			public double perform(double input) {
				return 0;
			}
        };
     }
     public static Function makePowOfB(){
     	return new Function(null){
     		public double perform(double base, double exponent){
     			return Math.pow(base, exponent);
     		}
     	};
     }
     //We're gonna need multi-variable Functions for these^^ or we could use OperatorFactory instead
	 */

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
                    throw new IllegalArgumentException();
                } else {
                    return Math.sqrt(input);
                }
            }
        };
    }

}