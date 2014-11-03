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
    private int cursorIndex = 0; //The index where the cursor is when shown on screen
    private int realCursorIndex = 0; //The index of the cursor in the list of tokens
    private int drawCount = 0; //The maximum amount of symbols drawn
    private boolean functionMode = false; //If this display is for function mode
    private Paint textPaint;
    private Paint smallPaint; //For superscripts and subscripts
    private Paint cursorPaint;
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
        smallPaint.setTextSize(FONT_SIZE / 2);

        cursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cursorPaint.setColor(Color.GREEN);
        cursorPaint.setTextSize(FONT_SIZE);
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
        final float yStartPadding = (1 + getMaxScriptLevel() / 10f) * textPaint.getTextSize(); //More padding for more exponents
        final float yPaddingBetweenLines = textPaint.getTextSize();
        final float cursorPadding = textPaint.getTextSize() / 10;
        float xModifier = xPadding - startX;

        maxX = 0;

        if (functionMode) { //Adds a f(x)= at the start
            final String s = "f(x)=";
            canvas.drawText(s, xPadding, yStartPadding, textPaint);
            float[] widths = new float[s.length()];
            textPaint.getTextWidths(s, widths);
            xModifier += sum(widths);
        }
        checkCursorIndex();


        //Counter and state variables
        float totalWidth = 0;
        drawCount = 0;
        int scriptLevel = 0; //superscript = 1, any additional levels would +1
        int scriptBracketCount = 0; //Counts the brackets for any exponents
        float heightMultiplier = 0; //Determines at what height the text would be drawn at
        boolean doNotCountNext = false;

        for (int i = 0; i < expression.size(); i++) {
            Token token = expression.get(i);
            Paint paint = textPaint;

            //If the cursor should count the current token
            boolean doNotCount = false;

            //Calculates the x and y position of the draw position (modified later)
            float x = totalWidth + xModifier;
            float y = yStartPadding + yPaddingBetweenLines * heightMultiplier;


            //Handle do not counts
            if (doNotCountNext) {
                doNotCountNext = false;
                doNotCount = true;
            }

            if (token instanceof Placeholder && ((Placeholder) token).getType() == Placeholder.BLOCK) {
                doNotCountNext = true;
            } else if (token instanceof Operator && ((Operator) token).getType() == Operator.VARROOT) {
                doNotCount = true;
            } else if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.SUPERSCRIPT_OPEN) {
                scriptLevel++;
                scriptBracketCount++;
                doNotCount = true;
            } else if (scriptLevel > 0) { //Counts brackets if its writing in superscript
                if (token instanceof Bracket) {
                    Bracket b = (Bracket) token;
                    if (b.getType() == Bracket.SUPERSCRIPT_OPEN) {
                        scriptBracketCount++;
                    } else if (b.getType() == Bracket.SUPERSCRIPT_CLOSE) {
                        scriptBracketCount--;
                        if (scriptBracketCount == scriptLevel - 1) { //No longer superscript
                            scriptLevel--;
                        }
                    }
                }
            }


            //Changes paint for superscript
            if (scriptLevel > 0) {
                paint = smallPaint;
            }

            //Determines the width of the symbol in text
            float[] widths = new float[token.getSymbol().length()];
            paint.getTextWidths(token.getSymbol(), widths);
            float widthSum = sum(widths);
            totalWidth += widthSum;

            //Moves the text up if its superscript
            if (scriptLevel > 0) {
                y += (2 - scriptLevel) * paint.getTextSize() / 4 - textPaint.getTextSize() / 2;
            }

            //Draws the text
            canvas.drawText(token.getSymbol(), x, y, paint);

            //Saves the maximum x value
            if (x + xPadding > maxX) {
                maxX = x + xPadding;
            }

            //Draws cursor
            if (!doNotCount) {
                if (drawCount == cursorIndex) {
                    //Superscripts the cursor if needed
                    float cursorY = yStartPadding + yPaddingBetweenLines * heightMultiplier;
                    if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.SUPERSCRIPT_CLOSE) {
                        cursorY += (1 - scriptLevel) * smallPaint.getTextSize() / 4 - textPaint.getTextSize() / 2;
                        canvas.drawText("|", x - cursorPadding, cursorY, cursorPaint);
                    } else {
                        canvas.drawText("|", x - cursorPadding, y, cursorPaint);
                    }
                    realCursorIndex = i;
                }
                drawCount++;
            }
        }
        //Draws cursor in special cases
        if (expression.size() == 0) { //Nothing there
            canvas.drawText("|", xModifier, yStartPadding / 1.1f, cursorPaint);
            realCursorIndex = 0;
        } else if (cursorIndex >= drawCount) { //Last index (or the cursor index is larger than the draw count
            //Moves the cursor up if its superscript
            float cursorY = yStartPadding + yPaddingBetweenLines * heightMultiplier;
            if (scriptLevel > 0) {
                cursorY += (1 - scriptLevel) * smallPaint.getTextSize() / 4 - textPaint.getTextSize() / 2;
            }
            canvas.drawText("|", totalWidth + xModifier - cursorPadding, cursorY, cursorPaint);
            cursorIndex = drawCount;
            realCursorIndex = expression.size();
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
     * Gets the highest script level in the expression.
     *
     * @return The highest script level
     */
    private int getMaxScriptLevel() {
        int maxScriptLevel = 0;
        int scriptLevel = 0;
        int scriptBracketCount = 0;
        for (Token token : expression) {
            if (token instanceof Operator && ((Operator) token).getType() == Operator.EXPONENT) {
                scriptLevel++;
                if (scriptLevel > maxScriptLevel) {
                    maxScriptLevel = scriptLevel;
                }
            } else if (scriptLevel > 0) { //Counts brackets if its writing in superscript
                if (token instanceof Bracket) {
                    Bracket b = (Bracket) token;
                    if (b.getType() == Bracket.OPEN) {
                        scriptBracketCount++;
                    } else if (b.getType() == Bracket.CLOSE) {
                        scriptBracketCount--;
                        if (scriptBracketCount == 0) { //No longer superscript
                            scriptLevel--;
                        }
                    }
                }
            }
        }
        return maxScriptLevel;
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
        if (cursorIndex > 0) {
            setCursorIndex(cursorIndex - 1);
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
        //TODO: READ COMMENTS
        //Determines if the cursor will go off the screen
        //Scrolls appropriately if required
        invalidate();
    }
}
