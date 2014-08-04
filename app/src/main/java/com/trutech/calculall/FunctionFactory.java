package com.trutech.calculall;

/**
 * Contains static methods that will create function pieces.
 *
 * @version 0.4.0
 */

public class FunctionFactory {
	private static double sin(double a){
		if(a % Math.PI == 0){
			return 0;
		}else{return Math.sin(a);}
	}

	private static double cos(double a){
		if((a % Math.PI/2 == 0) && (a % Math.PI != 0)){
			return 0;
		}else{return Math.cos(a);}
	}

	private static double tan(double a){
		if(sin(a) == 0){
			return 0;
		}else{return Math.tan(a);}
	}

	////////////////////////////////////////////////////
	////////////////////SIN and ARCSIN//////////////////
	////////////////////////////////////////////////////

	public static Function makeSinD(){ //In degrees
		return new Function("sin", Function.SIN) { 
			@Override
			public double perform(double input) {
				return sin(Math.toRadians(input));
			}
		};
	}

	public static Function makeASinD(){ //Result will be in degrees
		return new Function("arcsin", Function.ARCSIN) { 
			@Override
			public double perform(double input) {
				if(Math.abs(input)>1){//Makes sure the input is within the range of sin
					throw new IllegalArgumentException();
				}else{
					return Math.toDegrees(Math.asin(input));
				}
			}
		};
	}
	////////////////////////////////////////////////////
	public static Function makeSinR(){ //In radians
		return new Function("sin", Function.SIN) { 
			@Override
			public double perform(double input) {
				return sin(input);
			}
		};
	}

	public static Function makeASinR(){ //Result will be in radians
		return new Function("arcsin", Function.ARCSIN) { 
			@Override
			public double perform(double input) {
				if(Math.abs(input)>1){//Makes sure the input is within the range of sin
					throw new IllegalArgumentException();
				}else{
					return Math.asin(input);
				}
			}
		};
	}
	////////////////////////////////////////////////////
	public static Function makeSinG(){ //In gradians
		return new Function("sin", Function.SIN) { 
			@Override
			public double perform(double input) {
				return sin(input * (Math.PI / 200));
			}
		};
	}

	public static Function makeASinG(){ //Result will be in gradians
		return new Function("arcsin", Function.ARCSIN) { 
			@Override
			public double perform(double input) {
				if(Math.abs(input)>1){//Makes sure the input is within the range of sin
					throw new IllegalArgumentException();
				}else{
					return ((Math.asin(input))*(200/Math.PI));
				}
			}
		};
	}

	////////////////////////////////////////////////////
	////////////////////CSC and ARCCSC//////////////////
	////////////////////////////////////////////////////
	/*public static Function makeCscD(){ //In degrees
		return new Function("csc") { 
			@Override
			public double perform(double input) {
				if(Math.sin(Math.toRadians(input)) ==0){
					throw new IllegalArgumentException();
				}else{
					return 1/(Math.sin(Math.toRadians(input)));
				}
			}
		};
	}

	public static Function makeACscD(){ //Result will be in degrees
		return new Function("arccsc") { 
			@Override
			public double perform(double input) {
				if(Math.abs(input)<1){//Makes sure the input is within the range of csc
					throw new IllegalArgumentException();
				}else{
					return Math.toDegrees(Math.asin(1/input));
				}
			}
		};
	}
	////////////////////////////////////////////////////
	public static Function makeCscR(){ //In radians
		return new Function("csc") { 
			@Override
			public double perform(double input) {
				if(Math.sin(input)==0){
					throw new IllegalArgumentException();
				}else{
					return 1/(Math.sin(input));
				}
			}
		};
	}

	public static Function makeACscR(){ //Result will be in radians
		return new Function("arccsc") { 
			@Override
			public double perform(double input) {
				if(Math.abs(input)<1){//Makes sure the input is within the range of csc
					throw new IllegalArgumentException();
				}else{
					return Math.asin(1/input);
				}
			}
		};
	}
	////////////////////////////////////////////////////
	public static Function makeCscG(){ //In gradians
		return new Function("csc") { 
			@Override
			public double perform(double input) {
				if(Math.sin(input*(Math.PI/200)) ==0){
					throw new IllegalArgumentException();
				}else{
					return 1/(Math.sin(input * (Math.PI / 200)));
				}
			}
		};
	}

	public static Function makeACscG(){ //Result will be in gradians
		return new Function("arccsc") { 
			@Override
			public double perform(double input) {
				if(Math.abs(input)<1){//Makes sure the input is within the range of csc
					throw new IllegalArgumentException();
				}else{
					return ((Math.asin(1/input))*(200/Math.PI));
				}
			}
		};
	}
	 */
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

	public static Function makeACosD(){ //Result will be in degrees
		return new Function("arccos", Function.ARCCOS) { 
			@Override
			public double perform(double input) {
				if(Math.abs(input)>1){//Makes sure the input is within the range of cos
					throw new IllegalArgumentException();
				}else{
					return Math.toDegrees(Math.acos(input));
				}
			}
		};
	}
	////////////////////////////////////////////////////
	public static Function makeCosR(){ //In radians
		return new Function("cos", Function.COS) { 
			@Override
			public double perform(double input) {
				return cos(input);
			}
		};
	}

	public static Function makeACosR(){ //Result will be in radians
		return new Function("arccos", Function.ARCCOS) { 
			@Override
			public double perform(double input) {
				if(Math.abs(input)>1){//Makes sure the input is within the range of cos
					throw new IllegalArgumentException();
				}else{
					return Math.acos(input);
				}
			}
		};
	}
	////////////////////////////////////////////////////
	public static Function makeCosG(){ //In gradians
		return new Function("cos", Function.COS) { 
			@Override
			public double perform(double input) {
				return cos(input * (Math.PI / 200));
			}
		};
	}

	public static Function makeACosG(){ //Result will be in gradians
		return new Function("arccos", Function.ARCCOS) { 
			@Override
			public double perform(double input) {
				if(Math.abs(input)>1){//Makes sure the input is within the range of cos
					throw new IllegalArgumentException();
				}else{
					return ((Math.acos(input))*(200/Math.PI));
				}
			}
		};
	}
	////////////////////////////////////////////////////
	////////////////////SEC and ARCSEC//////////////////
	////////////////////////////////////////////////////
	/*
	public static Function makeSecD(){ //In degrees
		return new Function("sec") { 
			@Override
			public double perform(double input) {
				if(Math.cos(Math.toRadians(input)) ==0){
					throw new IllegalArgumentException();
				}else{
					return 1/(Math.cos(Math.toRadians(input)));
				}
			}
		};
	}

	public static Function makeASecD(){ //Result will be in degrees
		return new Function("arcsec") { 
			@Override
			public double perform(double input) {
				if(Math.abs(input)<1){//Makes sure the input is within the range of sec
					throw new IllegalArgumentException();
				}else{
					return Math.toDegrees(Math.acos(1/input));
				}
			}
		};
	}
	////////////////////////////////////////////////////
	public static Function makeSecR(){ //In radians
		return new Function("sec") { 
			@Override
			public double perform(double input) {
				if(Math.cos(input)==0){
					throw new IllegalArgumentException();
				}else{
					return 1/(Math.cos(input));
				}
			}
		};
	}

	public static Function makeASecR(){ //Result will be in radians
		return new Function("arcsec") { 
			@Override
			public double perform(double input) {
				if(Math.abs(input)<1){//Makes sure the input is within the range of sec
					throw new IllegalArgumentException();
				}else{
					return Math.acos(1/input);
				}
			}
		};
	}
	////////////////////////////////////////////////////
	public static Function makeSecG(){ //In gradians
		return new Function("sec") { 
			@Override
			public double perform(double input) {
				if(Math.cos(input*(Math.PI/200)) ==0){
					throw new IllegalArgumentException();
				}else{
					return 1/(Math.cos(input * (Math.PI / 200)));
				}
			}
		};
	}

	public static Function makeASecG(){ //Result will be in gradians
		return new Function("arcsec") { 
			@Override
			public double perform(double input) {
				if(Math.abs(input)<1){//Makes sure the input is within the range of sec
					throw new IllegalArgumentException();
				}else{
					return ((Math.acos(1/input))*(200/Math.PI));
				}
			}
		};
	}
	 */
	////////////////////////////////////////////////////
	////////////////////TAN and ARCTAN//////////////////
	////////////////////////////////////////////////////
	public static Function makeTanD() {//In degrees
		return new Function("tan", Function.TAN) { 
			@Override
			public double perform(double input) {
				if(Math.toRadians(input) % (Math.PI/2) == 0){//Makes sure the input is within the domain of tan
					throw new IllegalArgumentException();
				}else{
					return tan(Math.toRadians(input));
				}
			}
		};
	}

	public static Function makeATanD(){ //Result will be in degrees
		return new Function("arctan", Function.ARCTAN) { 
			@Override
			public double perform(double input) {
				return Math.toDegrees(Math.atan(input));
			}
		};
	}
	////////////////////////////////////////////////////
	public static Function makeTanR(){ //In radians
		return new Function("tan", Function.TAN) { 
			@Override
			public double perform(double input) {
				if(input % (Math.PI/2) == 0){//Makes sure the input is within the domain of tan
					throw new IllegalArgumentException();
				}else{
					return tan(input);
				}
			}
		};
	}

	public static Function makeATanR(){ //Result will be in radians
		return new Function("arctan", Function.ARCTAN) { 
			@Override
			public double perform(double input) {
				return Math.atan(input);
			}
		};
	}
	////////////////////////////////////////////////////
	public static Function makeTanG(){ //In gradians
		return new Function("tan", Function.TAN) { 
			@Override
			public double perform(double input) {
				if((input * (Math.PI / 200)) % (Math.PI/2) == 0){//Makes sure the input is within the domain of tan
					throw new IllegalArgumentException();
				}else{
					return tan(input * (Math.PI / 200));
				}
			}
		};
	}

	public static Function makeATanG(){ //Result will be in gradians
		return new Function("arctan", Function.ARCTAN) { 
			@Override
			public double perform(double input) {
				return ((Math.atan(input))*(200/Math.PI));
			}
		};
	}

	//////////////////////////////////////////////////// //Due to some inconsistencies/disagreements in definitions of arccot we
	////////////////////COT and ARCCOT////////////////// //might need to make two different arccot functions and let the user decide in
	//////////////////////////////////////////////////// //the settings or something, or just omit the function entirely 
	/*
	public static Function makeCotD(){ //In degrees
		return new Function("cot") { 
			@Override
			public double perform(double input) {
				if(Math.sin(Math.toRadians(input)) ==0){
					throw new IllegalArgumentException();
				}else{
					return (Math.cos(Math.toRadians(input)))/(Math.sin(Math.toRadians(input)));
				}
			}
		};
	}

	public static Function makeACotD(){ //Result will be in degrees
		return new Function("arccot") { 
			@Override
			public double perform(double input) {
				if(Math.toRadians(input) % (Math.PI) == 0){//Makes sure the input is within the range of cot
					throw new IllegalArgumentException();
				}else{
					return Math.toDegrees(Math.atan(1/input));
				}
			}
		};
	}
	//this might be useful: arccotx = pi/2 - arctanx
	////////////////////////////////////////////////////
	public static Function makeCotR(){ //In radians
		return new Function("cot") { 
			@Override
			public double perform(double input) {
				if(Math.sin(input) ==0){
					throw new IllegalArgumentException();
				}else{
					return (Math.cos(input))/(Math.sin(input));
				}
			}
		};
	}

	/*public static Function makeACotR(){ //Result will be in radians
		return new Function("arccot") { 
			@Override
			public double perform(double input) {
				if((input) % (Math.PI) == 0){//Makes sure the input is within the range of cot
					throw new IllegalArgumentException();
				}else{
					return (Math.atan(1/input));
				}
			}
		};
	}

	////////////////////////////////////////////////////
	public static Function makeCotG(){ //In gradians
		return new Function("cot") { 
			@Override
			public double perform(double input) {
				if(Math.sin((input * (Math.PI / 200))) ==0){
					throw new IllegalArgumentException();
				}else{
					return (Math.cos((input * (Math.PI / 200))))/(Math.sin((input * (Math.PI / 200))));
				}
			}
		};
	}

	public static Function makeACotG(){ //Result will be in gradians
		return new Function("arccot") { 
			@Override
			public double perform(double input) {
				if((input * (Math.PI / 200)) % (Math.PI) == 0){//Makes sure the input is within the range of cot
					throw new IllegalArgumentException();
				}else{
					return Math.toDegrees(Math.atan(1/input));
				}
			}
		};
	}*/
	////////////////////////////////////////////////////
	////////////////HYPERBOLIC FUNCTIONS////////////////
	////////////////////////////////////////////////////

	//NOTE: Hyperbolic functions do not use angles 
	public static Function makeSinh(){
		return new Function("sinh", Function.SINH){
			@Override
			public double perform(double input){
				return Math.sinh(input);
			}
		};
	}

	public static Function makeASinh(){
		return new Function("arsinh", Function.ARCSINH){
			@Override
			public double perform(double input){
				return Math.log(input + Math.sqrt(Math.pow(input, 2)+1));
			}
		};
	}

	public static Function makeCosh(){
		return new Function("cosh", Function.COSH){
			@Override
			public double perform(double input){
				return Math.cosh(input);
			}
		};
	}

	public static Function makeACosh(){
		return new Function("arcosh", Function.ARCCOSH){
			@Override
			public double perform(double input){
				if(input>=1){
					return Math.log(input + Math.sqrt(Math.pow(input, 2)-1));
				}else{
					throw new IllegalArgumentException();
				}
			}
		};
	}
	/*
	public static Function makeSech(){
		return new Function("sech"){
			@Override
			public double perform(double input){
				return 1/Math.cosh(input);
			}
		};
	}

	public static Function makeASech(){
		return new Function("arsech"){
			@Override
			public double perform(double input){
				if(input > 0 && input <= 1){
					return Math.log( (1/input) + (Math.sqrt(1-(input*input))/input) );
				}else{
					throw new IllegalArgumentException();
				}
			}
		};
	}
	 */
	public static Function makeTanh(){
		return new Function("tanh", Function.TANH){
			@Override
			public double perform(double input){		
				return Math.tanh(input);
			}
		};
	}

	public static Function makeATanh(){
		return new Function("artanh", Function.ARCTANH){
			@Override
			public double perform(double input){
				if(Math.abs(input)<1){
					return 0.5*Math.log((input+1)/(1-input));
				}else{
					throw new IllegalArgumentException();
				}
			}
		};
	}

	/*
	public static Function makeCoth(){
		return new Function("coth"){
			@Override
			public double perform(double input){
				if(input==0){
					throw new IllegalArgumentException();
				}else{
					return Math.cosh(input)/Math.sinh(input);
				}
			}
		};
	}

	public static Function makeACoth(){
		return new Function("arcoth"){
			@Override
			public double perform(double input){
				if(Math.abs(input)>1){
					return 0.5*Math.log((input+1)/(input-1));
				}else{
					throw new IllegalArgumentException();
				}
			}
		};
	}
	 */
	////////////////////////////////////////////////////
	////////////////////////////////////////////////////

	public static Function makeAbs(){
		return new Function("abs", Function.ABS){
			@Override
			public double perform(double input){
				return Math.abs(input);
			}
		};
	}

	public static Function makeFloor(){
		return new Function("floor", Function.FLOOR){
			@Override
			public double perform(double input){
				return Math.floor(input);
			}
		};
	}

	public static Function makeCeiling(){
		return new Function("ceiling", Function.CEILING){
			@Override
			public double perform(double input){
				return Math.ceil(input);
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

	public static Function makePowOfTen(){
		return new Function("10^", Function.POW10){
			@Override
			public double perform(double input){
				return Math.pow(10, input);
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

	public static Function makeExp(){
		return new Function("e^", Function.EXP){
			@Override
			public double perform(double input){
				return Math.exp(input);
			}
		};
	}

	public static Function makeSquare(){
		return new Function("²", Function.SQUARE){
			@Override
			public double perform(double input){
				return Math.pow(input, 2);
			}
		};

	}

	public static Function makeSqrt(){
		return new Function("√", Function.SQRT) { 
			@Override
			public double perform(double input) { 
				if(input < 0){
					throw new IllegalArgumentException();
				}else{
					return Math.sqrt(input);
				}
			}
		};
	}

	public static Function makeCube(){
		return new Function("³", Function.CUBE){
			@Override
			public double perform(double input){
				return Math.pow(input, 3);
			}
		};

	}

	public static Function makeCbrt(){
		return new Function("³√", Function.CBRT) { 
			@Override
			public double perform(double input) { 
				return Math.cbrt(input);
			}
		};
	}

	public static Function makeReciprocal(){
		return new Function("^-1", Function.RECIP){
			@Override
			public double perform(double input){
				if(input==0){
					throw new IllegalArgumentException();
				}else{
					return 1/input;
				}
			}
		};
	}

}