package com.trutechinnovations.calculall;

import java.util.ArrayList;

/**
 * Contains static methods that will create Variable tokens.
 *
 * @author Alston Lin, Ejaaz Merali
 * @version Alpha 2.0
 */
public class VariableFactory {
    public static ArrayList<Token> a_value = new ArrayList<>();
    public static ArrayList<Token> b_value = new ArrayList<>();
    public static ArrayList<Token> c_value = new ArrayList<>();
    public static ArrayList<Token> x_value = new ArrayList<>();
    public static ArrayList<Token> y_value = new ArrayList<>();
    public static ArrayList<Token> ans_value = new ArrayList<>(); //Values of the variables

    public static Variable makeA() {
        return new Variable(Variable.A, "A") {
            public ArrayList<Token> getValue() {
                return a_value;
            }
        };
    }

    public static Variable makeB() {
        return new Variable(Variable.B, "B") {
            public ArrayList<Token> getValue() {
                return b_value;
            }
        };
    }

    public static Variable makeC() {
        return new Variable(Variable.C, "C") {
            public ArrayList<Token> getValue() {
                return c_value;
            }
        };
    }

    public static Variable makeConstant() {
        return new Variable(Variable.CONSTANT, "Constant") {
            public ArrayList<Token> getValue() {
                return c_value;
            }
        };
    }

    public static Variable makeX() {
        return new Variable(Variable.X, "X") {
            public ArrayList<Token> getValue() {
                return x_value;
            }
        };
    }

    public static Variable makeY() {
        return new Variable(Variable.Y, "Y") {
            public ArrayList<Token> getValue() {
                return y_value;
            }
        };
    }

    public static Variable makePI() {
        return new Variable(Variable.PI, "π") {
            public ArrayList<Token> getValue() {
                ArrayList<Token> tokens = new ArrayList<>();
                tokens.add(new Number(PI_VALUE));
                return tokens;
            }
        };
    }

    public static Variable makeE() {
        return new Variable(Variable.E, "e") {
            public ArrayList<Token> getValue() {
                ArrayList<Token> tokens = new ArrayList<>();
                tokens.add(new Number(E_VALUE));
                return tokens;
            }
        };
    }

    public static Variable makeAns() {
        return new Variable(Variable.ANS, "ANS") {
            public ArrayList<Token> getValue() {
                return ans_value;
            }
        };
    }

    public static Variable makeSpeedOfLight() {
        return new Variable(Variable.C0, "c") {
            public ArrayList<Token> getValue() {
                ArrayList<Token> tokens = new ArrayList<>();
                tokens.add(new Number(Constant.SPEED_OF_LIGHT.getValue()));
                return tokens;
            }
        };
    }

    public static Variable makeMagnetic() {
        return new Variable(Variable.MU0, "μ0") {
            public ArrayList<Token> getValue() {
                ArrayList<Token> tokens = new ArrayList<>();
                tokens.add(new Number(Constant.MAGNETIC_VAL.getValue()));
                return tokens;
            }
        };
    }
}
