/*
 * Copyright (c) 2016 TruTech Innovations Inc - All Rights Reserved
 */

package com.trutechinnovations.calculall;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * Modified DisplayView to display outputs.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class OutputView extends NaturalView {

    private ArrayList<Token> expression = new ArrayList<Token>();

    public OutputView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    /**
     * Displays the given mathematical expression on the view.
     *
     * @param expression The expression to display
     */
    public void display(ArrayList<Token> expression) {
        this.expression = Utility.cleanupExpressionForReading(expression);
        requestLayout();
        invalidate();
    }

    /**
     * Overrides the default android drawing method for this View.
     * This is where all the drawing for the display is handled.
     *
     * @param canvas The canvas that will be drawn on
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawExpression(expression, canvas, 0);
    }

    /**
     * Overrides the superclass' onMeasure to set the dimensions of the View
     * according to the parent's.
     *
     * @param widthMeasureSpec  given by the parent class
     * @param heightMeasureSpec given by the parent class
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Finds max X
        ArrayList<Float> drawX = calculateDrawX(expression);
        if (drawX.size() > 1) {
            maxX = drawX.get(drawX.size() - 1);
        }
        float maxY = drawExpression(expression, new Canvas(), 0);
        int width = (int) (maxX + textHeight);
        int height = expression.size() == 0 ? (int) textHeight : (int) (maxY + textHeight);
        this.setMeasuredDimension(width, height);
    }

}