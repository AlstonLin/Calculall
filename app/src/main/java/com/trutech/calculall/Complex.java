package com.trutech.calculall;


/**
 * Object which represents a complex number
 *
 * @version 0.3.0
 * @author ejaaz
 */
public class Complex {
    private double real, imaginary;

    public Complex() {
        real = 0;
        imaginary = 0;
    }

    public Complex(double r, double i) {
        real = r;
        imaginary = i;
    }

    public Complex(int r, int i) {
        real = r;
        imaginary = i;
    }

    public double getReal(){
        return real;
    }

    public double getImaginary(){
        return imaginary;
    }

    public Complex add(Complex b){//this+b
        return new Complex(this.real+b.real,this.imaginary+b.imaginary);
    }

    public Complex add(double b){
        return new Complex(this.real+b,this.imaginary);
    }

    public Complex subtract(Complex b){//this-b
        return new Complex(this.real-b.real,this.imaginary-b.imaginary);
    }

    public Complex subtract(double b){
        return new Complex(this.real-b,this.imaginary);
    }

    public Complex times(Complex b){
        return new Complex(
                this.real*b.real - this.imaginary*b.imaginary,
                this.real*b.imaginary + this.imaginary*b.real);
    }

    public Complex times(double a){//scalar multiplication
        return new Complex(a*this.real, a*this.imaginary);
    }

    public Complex times(int a){
        return new Complex(a*this.real, a*this.imaginary);
    }
    
  
    public Complex recip() {
        return this.conjugate().times(1/Math.pow(this.abs(),2));
    }

    public Complex divide(Complex b) {
        return this.times(b.recip());
    }

    public Complex conjugate(){
        return new Complex(real,-imaginary);
    }

    public double abs(){
        return Math.sqrt(Math.pow(real,2) + Math.pow(imaginary,2));
    }
    
    private final double e = Math.E;
    
    public Complex complexExp(){//returns e raised to the power of this complex object
        double r = real, i=imaginary;
        return new Complex(Math.cos(i),Math.sin(i)).times(Math.pow(e,r));
    }

    public Complex complexExp(double b){//returns b raised to the power of this complex object
        if(b<0){
            throw new IllegalArgumentException();
        }else if(b == 0){
            return null;
        }else {
            double r = real, i = imaginary;
            return new Complex(Math.cos(i*Math.log(b)), Math.sin(i*Math.log(b))).times(Math.pow(b, r));
        }
    }

    public static Complex sqrt(double a){//returns the complex sqrt of a negative number
        double x = Math.abs(a);
        return new Complex(0,Math.sqrt(x));
    }

	public static Complex pow(Complex expression, int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Complex cbrt() {
		// TODO Auto-generated method stub
		return null;
	}
    
    /*private int round(double a){
        if(a>0) {
            if(a >= Math.floor(a)+0.5){
                return Math.ceiling(a);
            }else{
                return Math.floor(a);
            }
        }else if(a<0){
            if(a <= Math.floor(a)+0.5){
                return Math.
            }
        }
    }*/
}