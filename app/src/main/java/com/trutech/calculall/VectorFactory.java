package com.trutech.calculall;

import java.util.ArrayList;

/**
 * Created by Jason on 2014-08-14.
 */
public class VectorFactory {
    public static Vector makeU(){
        return new Vector(Vector.U, "U"){
            public ArrayList<Token> getVector(){
                return Vector.u_value;
            }
        };
    }
    public static Vector makeV(){
        return new Vector(Vector.V, "V"){
            public ArrayList<Token> getVector(){
                return Vector.v_value;
            }
        };
    }
    public static Vector makeW(){
        return new Vector(Vector.W, "W"){
            public ArrayList<Token> getVector(){
                return Vector.w_value;
            }
        };
    }
    public static Vector makeS(){
        return new Vector(Vector.S, "X"){
            public ArrayList<Token> getVector(){
                return Vector.s_value;
            }
        };

    }
}
