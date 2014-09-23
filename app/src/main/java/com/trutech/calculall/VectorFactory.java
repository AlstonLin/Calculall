package com.trutech.calculall;

import java.util.ArrayList;

/**
 * Created by Jason on 2014-08-14.
 */
public class VectorFactory {
    public static Vector makeA(){
        return new Vector(Vector.A, "A"){
            public ArrayList<Token> getVector(){
                return Vector.a_value;
            }
        };
    }
    public static Vector makeB(){
        return new Vector(Vector.B, "B"){
            public ArrayList<Token> getVector(){
                return Vector.b_value;
            }
        };
    }
    public static Vector makeC(){
        return new Vector(Vector.C, "C"){
            public ArrayList<Token> getVector(){
                return Vector.c_value;
            }
        };
    }
    public static Vector makeX(){
        return new Vector(Vector.X, "X"){
            public ArrayList<Token> getVector(){
                return Vector.x_value;
            }
        };
    }
    public static Vector makeY(){
        return new Vector(Vector.Y, "Y"){
            public ArrayList<Token> getVector(){
                return Vector.y_value;
            }
        };
    }
    public static Vector makeZ(){
        return new Vector(Vector.Z, "Z"){
            public ArrayList<Token> getVector(){
                return Vector.z_value;
            }
        };
    }
}
