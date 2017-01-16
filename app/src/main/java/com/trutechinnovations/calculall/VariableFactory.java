package com.trutechinnovations.calculall;

import java.util.ArrayList;

/**
 * Contains static methods that will create Variable tokens.
 *
 * @author Alston Lin, Ejaaz Merali
 * @version 3.0
 */
public class VariableFactory {
    //Advanced
    public static ArrayList<Token> aValue = new ArrayList<>();
    public static ArrayList<Token> bValue = new ArrayList<>();
    public static ArrayList<Token> cValue = new ArrayList<>();

    //Function
    public static ArrayList<Token> xValue = new ArrayList<>();
    public static ArrayList<Token> yValue = new ArrayList<>();

    //Vector
    public static ArrayList<Token> uValue = new ArrayList<>();
    public static ArrayList<Token> vValue = new ArrayList<>();
    public static ArrayList<Token> sValue = new ArrayList<>();
    public static ArrayList<Token> tValue = new ArrayList<>();

    //Matrix
    public static ArrayList<Token> matrixAValue = new ArrayList<>();
    public static ArrayList<Token> matrixBValue = new ArrayList<>();
    public static ArrayList<Token> matrixCValue = new ArrayList<>();

    //Ans
    public static ArrayList<Token> ansValueAdv = new ArrayList<>(); //Values of the variables
    public static ArrayList<Token> ansValueFunc = new ArrayList<>(); //Values of the variables
    public static ArrayList<Token> ansValueVec = new ArrayList<>(); //Values of the variables
    public static ArrayList<Token> ansValueMat = new ArrayList<>(); //Values of the variables

    public static Variable makeA() {
        return new Variable(Variable.A, "A") {
            public ArrayList<Token> getValue() {
                return aValue;
            }
        };
    }

    public static Variable makeB() {
        return new Variable(Variable.B, "B") {
            public ArrayList<Token> getValue() {
                return bValue;
            }
        };
    }

    public static Variable makeC() {
        return new Variable(Variable.C, "C") {
            public ArrayList<Token> getValue() {
                return cValue;
            }
        };
    }

    public static Variable makeConstant() {
        return new Variable(Variable.CONSTANT, "Constant") {
            public ArrayList<Token> getValue() {
                return cValue;
            }
        };
    }

    public static Variable makeX() {
        return new Variable(Variable.X, "X") {
            public ArrayList<Token> getValue() {
                return xValue;
            }
        };
    }

    public static Variable makeY() {
        return new Variable(Variable.Y, "Y") {
            public ArrayList<Token> getValue() {
                return yValue;
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

            public String toLaTeX() {
                return "$\\pi$";
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

            public String toLaTeX() {
                return "$e$";
            }
        };
    }

    public static Variable makeAnsAdv() {
        return new Variable(Variable.ANS, "ANS") {
            public ArrayList<Token> getValue() {
                return ansValueAdv;
            }
        };
    }

    public static Variable makeAnsFunc() {
        return new Variable(Variable.ANS, "ANS") {
            public ArrayList<Token> getValue() {
                return ansValueFunc;
            }
        };
    }

    public static Variable makeAnsVec() {
        return new Variable(Variable.ANS, "ANS") {
            public ArrayList<Token> getValue() {
                return ansValueVec;
            }
        };
    }

    public static Variable makeAnsMat() {
        return new Variable(Variable.ANS, "ANS") {
            public ArrayList<Token> getValue() {
                return ansValueMat;
            }
        };
    }

    public static Variable makeU() {
        return new Variable(Variable.U, "U") {
            public ArrayList<Token> getValue() {
                return uValue;
            }
        };
    }

    public static Variable makeV() {
        return new Variable(Variable.V, "V") {
            public ArrayList<Token> getValue() {
                return vValue;
            }
        };
    }

    public static Variable makeS() {
        return new Variable(Variable.S, "s") {
            public ArrayList<Token> getValue() {
                return sValue;
            }
        };
    }

    public static Variable makeT() {
        return new Variable(Variable.T, "t") {
            public ArrayList<Token> getValue() {
                return tValue;
            }
        };
    }

    public static Variable makeMatrixA() {
        return new Variable(Variable.MATRIX_A, "A") {
            public ArrayList<Token> getValue() {
                return matrixAValue;
            }
        };
    }

    public static Variable makeMatrixB() {
        return new Variable(Variable.MATRIX_B, "B") {
            public ArrayList<Token> getValue() {
                return matrixBValue;
            }
        };
    }

    public static Variable makeMatrixC() {
        return new Variable(Variable.MATRIX_C, "C") {
            public ArrayList<Token> getValue() {
                return matrixCValue;
            }
        };
    }

    public static Variable makeConstantToken(Constant inConstant) {
        final Constant constant = inConstant;
        return new Variable(Variable.CONSTANT, constant.getSymbol()) {
            public ArrayList<Token> getValue() {
                ArrayList<Token> tokens = new ArrayList<>();
                tokens.addAll(constant.getValue());
                return tokens;
            }

            public String toLaTeX() {
                return constant.getSymbol();
            }
        };
    }

}
