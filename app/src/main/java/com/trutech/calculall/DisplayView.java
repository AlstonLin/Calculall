package com.trutech.calculall;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.ScrollView;

import java.util.ArrayList;

/**
 * A custom View that displays the given mathematical expression with superscripts, subscripts
 * and fraction support.
 */
public class DisplayView extends ScrollView {

    private final int FONT_SIZE = 96;
    private float startX = 0; //Tracks the starting x position at which the canvas will start drawing (allows side scrolling)
    private float maxX = 0; //Max start X that the user can scroll to
    private boolean functionMode = false; //If this display is for function mode
    private Paint textPaint;
    private Paint smallPaint; //For superscripts and subscripts
    private ArrayList<Token> expression = new ArrayList<Token>();
    private ArrayList<Token> output = new ArrayList<Token>();
    private String outputString = ""; //TEMPORARY

    public DisplayView(Context context, AttributeSet attr) {
        super(context, attr);
        //Setup the paints
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(FONT_SIZE);

        smallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        smallPaint.setColor(Color.BLACK);
        smallPaint.setTextSize(FONT_SIZE / 3);
        setWillNotDraw(false);
    }

    /**
     * Displays the given mathematical expression on the view.
     *
     * @param expression The expression to display
     */
    public void displayInput(ArrayList<Token> expression) {
        this.expression = expression;
        invalidate();
    }

    /**
     * Displays the given output to the display.
     *
     * @param tokens The tokens to display
     */
    public void displayOutput(ArrayList<Token> tokens) {
        output = tokens;
        invalidate();
    }

    /**
     * Displays the given output to the string (TEMPORARY METHOD, WILL
     * BE DEPRECATED BY displayOutput(ArrayList<Token>).
     *
     * @param str String to display
     */
    public void displayOutput(String str) {
        outputString = str;
        invalidate();
    }

    /**
     * Displays the given output for Function mode (includes a f(x)= as a prefix).
     *
     * @param tokens The expression to display
     */
    public void displayInputFunction(ArrayList<Token> tokens) {
        displayInput(tokens);
        functionMode = true;
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
        //TODO: Use canvas.drawLine() to make fractions, and implement a algorithm to do this
        final float xPadding = textPaint.getTextSize() / 3; //The padding at the start and end    of the display (x)
        final float yStartPadding = textPaint.getTextSize();
        final float yPaddingBetweenLines = textPaint.getTextSize();
        float xModifier = xPadding - startX;


        if (functionMode) { //Adds a f(x)= at the start
            canvas.drawText("f(x)=", xPadding, yStartPadding, textPaint);
            xModifier += textPaint.getTextSize() / 2 * 5;
        }

        //Counter and state variables
        float totalWidth = 0;
        int scriptLevel = 0; //superscript = 1, any additionl levels would +1
        int scriptBracketCount = 0; //Counts the brackets for any exponents
        float heightMultiplier = 0; //Determines at what height the text would be drawn at
        boolean doNotDrawNext = false; //Set true if the next token should not be drawn
        boolean doNotDraw = false; //Set true if the current token should not be drawn

        for (int i = 0; i < expression.size(); i++) {
            Token token = expression.get(i);
            Paint paint = textPaint;

            //Handles do not draws
            if (doNotDrawNext) {
                doNotDrawNext = false;
                doNotDraw = true;
            }

            //Special Case for exponents
            if (token instanceof Operator && ((Operator) token).getType() == Operator.EXPONENT) {
                scriptLevel++;
                doNotDraw = true;
                doNotDrawNext = true;
            } else if (scriptLevel > 0) { //Counts brackets if its writing in superscript
                //TODO: Add support for this for multi-level exponents
                if (token instanceof Bracket) {
                    Bracket b = (Bracket) token;
                    if (b.getType() == Bracket.OPEN) {
                        scriptBracketCount++;
                    } else if (b.getType() == Bracket.CLOSE){
                        scriptBracketCount--;
                        if (scriptBracketCount == 0) { //No longer superscript
                            scriptLevel--;
                            doNotDraw = true;
                        }
                    }
                }
            }

            if (doNotDraw == false) {
                //Changes paint for superscript
                if (scriptLevel > 0) {
                    paint = smallPaint;
                }
                //Determines the width of the symbol in text
                float[] widths = new float[token.getSymbol().length()];
                paint.getTextWidths(token.getSymbol(), widths);
                float x = totalWidth + xModifier;
                float y = yStartPadding + yPaddingBetweenLines * heightMultiplier;
                float widthSum = 0;
                for (int j = 0; j < widths.length; j++) {
                    widthSum += widths[j];
                }
                totalWidth += widthSum;

                //Moves the text up if its superscript
                if (scriptLevel > 0) {
                    y += (2 - scriptLevel) * paint.getTextSize() / 4 - textPaint.getTextSize() / 2;
                }

                canvas.drawText(token.getSymbol(), x, y, paint);

                if (x + xPadding > maxX) { //Re-determines the maximum x value
                    maxX = x + xPadding;
                }
            } else {
                doNotDraw = false;
            }

        }

        //Displays output if there is any
        //TODO: Make this work with tokens, and make sure to account for multiple lines (find the height of the lowest line and display it under)
        //TEMPORARY CODE (Will use a list of tokens in the future)
        if (outputString.length() != 0) {
            //Determines width of the output string
            float[] outputWidths = new float[outputString.length()];
            textPaint.getTextWidths(outputString, outputWidths);
            //Adds the array for the total width
            float outputWidth = 0;
            for (int i = 0; i < outputWidths.length; i++) {
                outputWidth += outputWidths[i];
            }
            canvas.drawText(outputString, getWidth() - outputWidth - xPadding, yPaddingBetweenLines * (heightMultiplier + 2) + yStartPadding, textPaint);
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
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
        this.setMeasuredDimension(parentWidth, parentHeight);
    }

    /**
     * Scrolls the display left if possible.
     */
    public void scrollLeft() {
        if (startX > 0) {
            startX--;
        }
    }

    /**
     * Scrolls the display right if possible.
     */
    public void scrollRight() {
        if (startX < maxX) {
            startX++;
        }
    }

}
