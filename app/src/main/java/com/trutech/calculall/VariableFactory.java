package com.trutech.calculall;

/**
 * Contains static methods that will create Variable tokens.
 *
 * @version 0.3.0
 */
public class VariableFactory {
	public static Variable makeA(){
		return new Variable(Variable.A, "A"){
			public double getValue(){
				return Variable.a_value;
			}
		};
	}
	public static Variable makeB(){
		return new Variable(Variable.B, "B"){
			public double getValue(){
				return Variable.b_value;
			}
		};
	}
	public static Variable makeC(){
		return new Variable(Variable.C, "C"){
			public double getValue(){
				return Variable.c_value;
			}
		};
	}
	public static Variable makeX(){
		return new Variable(Variable.X, "X"){
			public double getValue(){
				return Variable.x_value;
			}
		};
	}
	public static Variable makeY(){
		return new Variable(Variable.Y, "Y"){
			public double getValue(){
				return Variable.y_value;
			}
		};
	}
	public static Variable makeZ(){
		return new Variable(Variable.Z, "Z"){
			public double getValue(){
				return Variable.z_value;
			}
		};
	}
	public static Variable makePI(){
		return new Variable(Variable.PI, "π"){
			public double getValue(){
				return Variable.PI_VALUE;
			}
		};
	}
	public static Variable makeE(){
		return new Variable(Variable.E, "e"){
			public double getValue(){
				return Variable.E_VALUE;
			}
		};
	}
}
