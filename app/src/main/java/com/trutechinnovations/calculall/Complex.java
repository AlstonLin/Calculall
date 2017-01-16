/*
 * Copyright (c) 2016 TruTech Innovations Inc - All Rights Reserved
 */

package com.trutechinnovations.calculall;

/**
 * Object which represents a complex number
 *
 * @author Ejaaz Merali
 * @version 3.0
 */
public class Complex {
    private static final double e = Math.E;
    private double real, imaginary;

    public Complex() {
        real = 0;
        imaginary = 0;
    }

    public Complex(Complex c) {
        real = c.getReal();
        imaginary = c.getImaginary();
    }

    public Complex(double r, double i) {
        real = r;
        imaginary = i;
    }

    public Complex(int r, int i) {
        real = r;
        imaginary = i;
    }

    public static boolean isReal(Complex c) {
        if (c.getImaginary() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static double abs(Complex c) {
        return c.abs();
    }

    public static Complex sqrt(double a) {//returns the complex sqrt of a negative number
        double x = Math.abs(a);
        return new Complex(0, Math.sqrt(x));
    }

    public static Complex pow(Complex expression, int power) {
        if (power == 1) {
            return expression;
        } else {
            Complex c = expression;
            for (int i = 2; i <= power; i++) {
                c = c.times(expression);
            }
            return c;
        }
    }

    public static Complex cis(double angle) {
        return new Complex(Math.cos(angle), Math.sin(angle));
    }

    public static double arg(Complex c) {
        double x = c.getReal();
        double y = c.getImaginary();
        if (x == 0 && y == 0) {
            throw new IllegalArgumentException();
        }

        if (x > 0 && y > 0) {
            return Math.atan(y / x);
        } else if (x < 0 && y > 0) {
            return Math.PI - Math.atan(Math.abs(x / y));
        } else if (x < 0 && y < 0) {
            return Math.atan(Math.abs(y / x)) - Math.PI;
        } else if (x > 0 && y < 0) {
            return (-1) * Math.atan(Math.abs(y / x));
        } else if (y == 0 && x > 0) {
            return 0;
        } else if (y == 0 && x < 0) {
            return Math.PI;
        } else if (x == 0 && y > 0) {
            return Math.PI / 2;
        } else if (x == 0 && y < 0) {
            return 3 * Math.PI / 2;
        } else {
            return 0;
        }
    }

    public double getReal() {
        return real;
    }

    public double getImaginary() {
        return imaginary;
    }

    public boolean isReal() {
        if (imaginary == 0) {
            return true;
        } else {
            return false;
        }
    }

    public Complex add(Complex b) {//this+b
        return new Complex(this.real + b.real, this.imaginary + b.imaginary);
    }

    public Complex add(double b) {
        return new Complex(this.real + b, this.imaginary);
    }

    public Complex subtract(Complex b) {//this-b
        return new Complex(this.real - b.real, this.imaginary - b.imaginary);
    }

    public Complex subtract(double b) {
        return new Complex(this.real - b, this.imaginary);
    }

    public Complex times(Complex b) {
        return new Complex(
                this.real * b.real - this.imaginary * b.imaginary,
                this.real * b.imaginary + this.imaginary * b.real);
    }

    public Complex times(double a) {//scalar multiplication
        return new Complex(a * this.real, a * this.imaginary);
    }

    public Complex times(int a) {
        return new Complex(a * this.real, a * this.imaginary);
    }

    public Complex recip() {
        return this.conjugate().times(1 / Math.pow(this.abs(), 2));
    }

    public Complex divide(Complex b) {
        return this.times(b.recip());
    }

    public Complex conjugate() {
        return new Complex(real, -imaginary);
    }

    public double abs() {
        return Math.sqrt(Math.pow(real, 2) + Math.pow(imaginary, 2));
    }

    public Complex complexExp() {//returns e raised to the power of this complex object
        double r = real, i = imaginary;
        return new Complex(Math.cos(i), Math.sin(i)).times(Math.pow(e, r));
    }

    public Complex complexExp(double b) {//returns b raised to the power of this complex object
        if (b < 0) {
            throw new IllegalArgumentException();
        } else if (b == 0) {
            return null;
        } else {
            double r = real, i = imaginary;
            return new Complex(Math.cos(i * Math.log(b)), Math.sin(i * Math.log(b))).times(Math.pow(b, r));
        }
    }
}