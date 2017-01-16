/*
 * Copyright (c) 2016 TruTech Innovations Inc - All Rights Reserved
 */

package com.trutechinnovations.calculall;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Implements the NaturalView with a cursor which can scroll through the expression.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class DisplayView extends NaturalView {

    private float cursorPadding;
    private float startX = 0; //Tracks the starting x position at which the canvas will start drawing (allows side scrolling)
    private int cursorIndex = 0; //The index where the cursor is when shown on screen
    private int drawCount = 0;
    private float cursorY = 0;
    private float xPadding;
    private float maxY;
    private int realCursorIndex = 0; //The index of the cursor in the list of tokens
    private boolean functionMode = false; //If this display is for function mode
    private Paint cursorPaint;
    private ArrayList<Token> expression = new ArrayList<Token>();
    private OutputView output;
    private ArrayList<Float> drawX;

    public DisplayView(Context context, AttributeSet attr) {
        super(context, attr);

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.buttonTextColor, typedValue, true);
        int displayColor = typedValue.data;

        cursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cursorPaint.setColor(displayColor);
    }

    public void setOutput(OutputView o) {
        output = o;
    }

    public void setFontSize(int fontSize) {
        super.setFontSize(fontSize);

        output.setFontSize(fontSize);
        cursorPaint.setTextSize(fontSize);

        Rect textRect = new Rect();
        textPaint.getTextBounds("1", 0, 1, textRect);
        textHeight = textRect.height() * 1.25f;

        xPadding = textHeight / 3;
        cursorPadding = textHeight / 10;
    }

    public ArrayList<Token> getExpression() {
        return expression;
    }

    /**
     * Displays the given mathematical expression on the view.
     *
     * @param expression The expression to display
     */
    public void displayInput(ArrayList<Token> expression) {
        this.expression = expression;
        requestLayout();
        invalidate();
    }

    /**
     * Displays the given output to the display.
     *
     * @param tokens The tokens to display
     */
    public void displayOutput(ArrayList<Token> tokens) {
        output.display(tokens);
    }

    /**
     * Clears the input and output of the display.
     */
    public void clear() {
        expression.clear();
        output.display(new ArrayList<Token>());
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
        ArrayList<Float> drawX = prepareDrawing(expression);
        this.drawX = drawX;
        maxY = 0;

        //Cursor stuff
        calculateRealCursorIndex();
        float cursorX = getCursorX();
        if (cursorX < startX) {
            startX = cursorX - cursorPadding;
        } else if (cursorX > startX + getWidth()) {
            startX = cursorX - getWidth() + cursorPadding;
        }

        float xModifier = -startX;

        //Special case for Function mode
        if (functionMode) {
            final String s = "f(x)=";
            canvas.drawText(s, xPadding, textHeight, textPaint);
            float[] widths = new float[s.length()];
            textPaint.getTextWidths(s, widths);
            xModifier += sum(widths);
        }

        checkCursorIndex();

        //Counter and state variables
        float INITIAL_Y = -getMostNeg(expression) + textHeight;
        float y = INITIAL_Y;
        //float scriptHeightMultiplier = 0; //Height on the superscript level
        for (int i = 0; i < expression.size(); i++) {
            Token token = expression.get(i);

            //Calculates the x and y position of the draw position (modified later)
            y = calculateNextY(y, expression, i, INITIAL_Y);
            float x = drawX.get(i) + xModifier;

            //Saves the y coordinate
            heights.add(i, y);

            //Draws the text
            if (token instanceof Matrix) {
                float matrixY = drawMatrix(x, y, canvas, (Matrix) token, textPaint);
                if (matrixY > maxY) {
                    maxY = matrixY;
                }
            } else {
                String s = token.getSymbol();
                if (s.length() < 3) { //For efficiency
                    canvas.drawText(s, x, y, textPaint);
                } else { //May contain a text modification
                    //Goes through each letter and writes with the appropriate text modification
                    boolean onSubscript = false;
                    float currentX = x;
                    for (int index = 0; index < s.length(); index++) {
                        char c = s.charAt(index);
                        if (c == '☺') { //Toggles subscript
                            onSubscript = !onSubscript;
                        } else { //Draws the character
                            float[] width = new float[1];
                            Paint paint = textPaint;
                            if (onSubscript) {
                                paint = subscriptPaint;
                            }
                            canvas.drawText(Character.toString(c), currentX, y, paint);
                            paint.getTextWidths(Character.toString(c), width);
                            currentX += width[0];
                        }
                    }
                }
            }

            //Updates maxY
            if (y > maxY) {
                maxY = y;
            }

            //Draws fraction sign
            if (token instanceof Operator && token.getType() == Operator.FRACTION) {
                canvas.drawLine(x, y + fracPadding, drawX.get(getDenomEnd(i, expression)) + xModifier, y + fracPadding, fracPaint);
            }

            //Draws cursor
            if (i == realCursorIndex) {
                //Superscripts the cursor if needed
                cursorY = y;
                cursorX = x - cursorPadding;
                if (token instanceof Bracket && token.getType() == Bracket.SUPERSCRIPT_CLOSE) {
                    cursorY = heights.get(i - 1);
                } else if (token instanceof Bracket && token.getType() == Bracket.DENOM_CLOSE) {
                    cursorY = heights.get(i - 1);
                }
                canvas.drawText("|", cursorX, cursorY, cursorPaint);
            }

        }
        try {
            //Draws cursor in special cases
            if (expression.size() == 0) { //No expression
                canvas.drawText("|", xPadding, y, cursorPaint);
            } else if (realCursorIndex == expression.size()) { //Last index (or the cursor index is larger than the draw count
                //Moves the cursor up if its superscript
                cursorY = y;
                cursorX = maxX + xModifier - cursorPadding;
                canvas.drawText("|", cursorX, cursorY, cursorPaint);
                realCursorIndex = expression.size();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            //When rendering in Android Studio (Android bug)
        }
    }

    /**
     * Calculates the value of realCursorX based on cursorX.
     */
    private void calculateRealCursorIndex() {
        boolean doNotCountNext = false;
        drawCount = 0;
        for (int i = 0; i < expression.size(); i++) {
            Token token = expression.get(i);

            //If the cursor should count the current token
            boolean doNotCount = false;

            //Handle do not counts
            if (doNotCountNext) {
                doNotCountNext = false;
                doNotCount = true;
            }

            //SPECIAL CASES FOR DO NOT COUNTS
            if (token instanceof Placeholder && (token.getType() == Placeholder.SUPERSCRIPT_BLOCK || token.getType() == Placeholder.BASE_BLOCK)) {
                doNotCountNext = true;
            } else if ((token instanceof Operator && token.getType() == Operator.VARROOT) ||
                    (token instanceof Bracket && token.getType() == Bracket.SUPERSCRIPT_OPEN) ||
                    (token instanceof Bracket && token.getType() == Bracket.DENOM_OPEN) ||
                    (token instanceof Bracket && token.getType() == Bracket.NUM_OPEN) ||
                    (token instanceof Bracket && token.getType() == Bracket.FRACTION_CLOSE) ||
                    (token instanceof Operator && token.getType() == Operator.FRACTION)) {
                doNotCount = true;
            }

            //Draws cursor
            if (!doNotCount) {
                if (drawCount == cursorIndex) {
                    realCursorIndex = i;
                }
                drawCount++;
            }
        }
        //Draws cursor in special cases
        if (expression.size() == 0) { //Nothing there
            realCursorIndex = 0;
        } else if (cursorIndex >= drawCount) { //Last index (or the cursor index is larger than the draw count
            cursorIndex = drawCount;
            realCursorIndex = expression.size();
        }
    }

    /**
     * Checks if the cursor is shown on the screen. If not, it will redraw the entire canvas through
     * onDraw() again.
     */
    private float getCursorX() {
        float cursorX;
        if (realCursorIndex > expression.size()) {
            cursorX = drawX.get(drawX.size() - 1); //Last index
        } else {
            cursorX = drawX.get(realCursorIndex);
        }
        return cursorX;
    }

    /**
     * Finds the sum of the given array of widths
     *
     * @param widths The array to sum
     * @return The sum
     */
    private float sum(float[] widths) {
        float widthSum = 0;
        for (int j = 0; j < widths.length; j++) {
            widthSum += widths[j];
        }
        return widthSum;
    }

    /**
     * Checks the index of the cursor to make sure it is valid.
     */
    private void checkCursorIndex() {
        if (expression.size() == 0) {
            cursorIndex = 0;
        } else if (cursorIndex > expression.size()) {
            cursorIndex = expression.size();
        }
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
        WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
        Canvas canvas = new Canvas();
        this.draw(canvas); //Lazy way to calculate maxX and maxY

        //Finds max X
        calculateDrawX(expression);
        if (drawX.size() > 1) {
            maxX = drawX.get(drawX.size() - 1);
        }

        int width = parentWidth;
        int height = expression.size() == 0 ? (int) textHeight : (int) (maxY + textHeight);
        this.setMeasuredDimension(width, height);
    }

    /**
     * Scrolls the display left if possible.
     */
    public void scrollLeft() {
        if (cursorIndex > 0) {
            setCursorIndex(cursorIndex - 1);
        } else {
            setCursorIndex(drawCount);
        }
    }

    /**
     * Resets the scrolling (to the initial position)
     */
    public void reset() {
        startX = 0;
        cursorIndex = 0;
    }

    /**
     * Scrolls the display right if possible.
     */
    public void scrollRight() {
        if (cursorIndex < drawCount) {
            setCursorIndex(cursorIndex + 1);
        } else if (cursorIndex == drawCount) { //Wraps around
            setCursorIndex(0);
        } else {
            throw new IllegalStateException("Cursor Index is greater than draw count");
        }
    }

    /**
     * @return The index of the cursor
     */
    public int getRealCursorIndex() {
        return realCursorIndex;
    }

    public int getCursorIndex() {
        return cursorIndex;
    }

    public void setCursorIndex(int index) {
        cursorIndex = index;
        invalidate();
    }
}