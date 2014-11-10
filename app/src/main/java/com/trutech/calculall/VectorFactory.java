package com.trutech.calculall;

import java.util.ArrayList;

/**
 * Created by Jason on 2014-08-14.
 */
public class VectorFactory {
    public static Vector makeU(){
        return new Vector(Vector.U, "u"){
            public ArrayList<Token> getVector(){
                return Vector.u_value;
            }
        };
    }
    public static Vector makeV(){
        return new Vector(Vector.V, "v"){
            public ArrayList<Token> getVector(){
                return Vector.v_value;
            }
        };
    }
    public static Vector makeW(){
        return new Vector(Vector.W, "w"){
            public ArrayList<Token> getVector(){
                return Vector.w_value;
            }
        };
    }
    public static Vector makeS(){
        return new Vector(Vector.S, "s"){
            public ArrayList<Token> getVector(){
                return Vector.s_value;
            }
        };

    }
}
