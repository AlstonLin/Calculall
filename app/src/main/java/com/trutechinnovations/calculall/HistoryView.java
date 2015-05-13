package com.trutechinnovations.calculall;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;

import java.util.ArrayList;

/**
 * Shows the calculation history of a mode.
 *
 * @author Alston Lin
 */
public class HistoryView extends NaturalView {

    public static int fontSize;
    //CONSTANTS
    private float entry_padding;
    private int backgroundColor;
    private ArrayList<Object[]> history;
    private float maxGlobalY = 0;

    public HistoryView(Context context, AttributeSet attr) {
        super(context, attr);
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        backgroundColor = typedValue.data;
        setFontSize(fontSize);
        entry_padding = textHeight;
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
        //Clears canvas
        canvas.drawColor(backgroundColor);
        heights.clear();
        float lastHeight = 0;
        if (history == null) {
            return;
        }
        for (int i = history.size() - 1; i >= 0; i--) {
            Object[] elements = history.get(i);
            ArrayList<Token> input = (ArrayList<Token>) elements[0];
            ArrayList<Token> output = (ArrayList<Token>) elements[1];
            lastHeight = drawExpression(input, canvas, lastHeight);
            lastHeight = drawExpression(output, canvas, lastHeight);
            lastHeight += entry_padding;
            if (lastHeight > maxGlobalY) {
                maxGlobalY = lastHeight;
            }
        }
    }

    public void setHistory(ArrayList<Object[]> history) {
        this.history = history;
        for (Object[] o : history) {
            ((ArrayList<Token>) o[1]).add(0, new StringToken(" ="));
        }
        requestLayout();
        invalidate();
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
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        float maxWidth = 0;
        try {
            Canvas canvas = new Canvas();
            this.draw(canvas);
            if (history != null) {
                //Calculates max width
                for (Object[] o : history) {
                    ArrayList<Token> input = (ArrayList<Token>) o[0];
                    ArrayList<Token> output = (ArrayList<Token>) o[1];
                    float width = 0;
                    for (Token t : input) {
                        width += textPaint.measureText(t.getSymbol());
                    }
                    if (width > maxWidth) {
                        maxWidth = width;
                    }
                    width = 0;
                    for (Token t : output) {
                        width += textPaint.measureText(t.getSymbol());
                    }
                    if (width > maxWidth) {
                        maxWidth = width;
                    }
                }
            }
        } catch (NullPointerException e) {
            //In XML Layout
        }
        int width = (int) (maxWidth + textHeight) > parentWidth ? (int) (maxWidth + textHeight) : parentWidth;
        int height = (int) maxGlobalY;
        this.setMeasuredDimension(width, height);
    }

}