/*
 * Copyright (c) 2016 TruTech Innovations Inc - All Rights Reserved
 */

package com.trutechinnovations.calculall;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Contains the framework of a View that displays the given mathematical expression with
 * superscripts and fraction supports.
 *
 * This View also supports text modifications within the Token's String itself. Use the following
 * Unicode symbols to toggle it (eg.  N☺A☺ for a subscript A)
 *  ☺  - Subscript
 *
 * @author Alston Lin
 * @version 3.0
 */
public abstract class NaturalView extends View {

    //Constant ratios of variables to the text height value
    public static final float RESERVED_TO_ACTUAL_TEXT_HEIGHT = 5 / 4f; // The ratio of the height of the space reserved for text to actually used
    public static final float X_PADDING_TO_TEXT_HEIGHT = 1 / 3f;
    public static final float FRAC_PADDING_TO_TEXT_HEIGHT = 1 / 8f;
    public static final float SUPERSCRIPT_OFFSET_TO_TEXT_HEIGHT = 1 / 2f;
    public static final float MATRIX_PADDING_TO_TEXT_HEIGHT = 0f;
    public static final float SUBSCRIPT_TO_TEXT_HEIGHT = 1 / 2f;

    private static int lastColor = Color.BLACK; //The last color that any display has shown; used as a backup in case attribute can not be resolved

    protected float fracPadding; //The padding between the bottom of the numerator of a fraction and the fraction line
    protected float textHeight; //The height of the area reserved (not actually used) for text; Note - Change this to change space between a num and denom of a fraction
    //Variables Used while drawing
    protected float maxX;
    protected ArrayList<Float> heights = new ArrayList<Float>();
    protected Paint textPaint, fracPaint, subscriptPaint;
    //Drawing Modifier Variables
    private float xPadding; //The padding at the start and end of the display (x)
    private float superscriptYOffset; //The y offset up that a superscript would have
    private float matrixPadding; //The padding between Matrix entries
    private float paddingAfterMatrix; //The padding after a Matrix

    /**
     * Should only be called by a subclass.
     *
     * @param context The context of the app
     * @param attr    Attributes sent by the XML
     */
    public NaturalView(Context context, AttributeSet attr) {
        super(context, attr);
        int displayColor = getDisplayColor(context);
        setupPaints(displayColor);
        setWillNotDraw(false);
    }

    /**
     * Gets the Display Color of the current Theme.
     *
     * @param context The context of the app
     * @return The color integer of the display color
     */
    private int getDisplayColor(Context context) {
        TypedValue typedValue = new TypedValue();
        boolean resolved = context.getTheme().resolveAttribute(R.attr.buttonTextColor, typedValue, true);
        if (resolved) {
            lastColor = typedValue.data; //Saves it
            return typedValue.data;
        } else {
            return lastColor;
        }
    }

    /**
     * Sets up the Paints.
     *
     * @param displayColor The color of the paints
     */
    public void setupPaints(int displayColor) {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(displayColor);
        fracPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fracPaint.setColor(displayColor);
        fracPaint.setStrokeWidth(10);
        subscriptPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        subscriptPaint.setColor(displayColor);
    }

    /**
     * Sets the font size and updates all the variables that depend on the font size.
     *
     * @param fontSize The new font size.
     */
    public void setFontSize(int fontSize) {
        textPaint.setTextSize(fontSize);
        fracPaint.setTextSize(fontSize);
        subscriptPaint.setTextSize(fontSize * SUBSCRIPT_TO_TEXT_HEIGHT);
        calculateAttributes();
    }

    /**
     * Calculates specific attributes of the NaturalView
     */
    private void calculateAttributes() {
        //Calculates the height occupied by text
        Rect textRect = new Rect();
        textPaint.getTextBounds("1", 0, 1, textRect);

        //Recalculates based on ratio constants
        textHeight = textRect.height() * RESERVED_TO_ACTUAL_TEXT_HEIGHT;
        xPadding = X_PADDING_TO_TEXT_HEIGHT * textHeight;
        fracPadding = FRAC_PADDING_TO_TEXT_HEIGHT * textHeight;
        superscriptYOffset = SUPERSCRIPT_OFFSET_TO_TEXT_HEIGHT * textHeight;
        matrixPadding = textPaint.measureText("  ");
        paddingAfterMatrix = MATRIX_PADDING_TO_TEXT_HEIGHT * textHeight;
    }

    /**
     * Draws the expression onto the canvas at a vertical offset.
     *
     * @param expression The expression to draw
     * @param canvas     The canvas that will be drawn on
     * @param offset     The vertical offset that the top of the expression will be from the top of the View
     * @return The y value of the bottommost pixel of the expression
     */
    public float drawExpression(ArrayList<Token> expression, Canvas canvas, float offset) {
        float INITIAL_Y = -getMostNeg(expression) + offset + textHeight;
        float maxY = offset;
        float y = INITIAL_Y;
        maxY = 0;

        //Calculates the x coordinates to draw each Token
        ArrayList<Float> drawX = prepareDrawing(expression);

        for (int i = 0; i < expression.size(); i++) {
            Token token = expression.get(i);

            float x = drawX.get(i);
            //Calculates the y position to draw at and saves it
            y = calculateNextY(y, expression, i, INITIAL_Y);
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
                canvas.drawLine(x, y + fracPadding, drawX.get(getDenomEnd(i, expression)), y + fracPadding, fracPaint);
            }

        }
        return maxY;
    }

    /**
     * Calculates all the x positions of the Tokens for the given expression to draw.
     *
     * @param expression The expression to calculate
     * @return drawX The drawX values for the expression
     */
    protected ArrayList<Float> calculateDrawX(ArrayList<Token> expression) {
        ArrayList<Float> drawX = new ArrayList<Float>();
        drawX.clear();
        Paint paint;
        int scriptLevel = 0; //superscript = 1, any additional levels would +1
        int scriptBracketCount = 0; //Counts the brackets for any exponents
        Stack<Float> fracStarts = new Stack<Float>(); //The indices where fractions start
        float x = xPadding;

        for (int i = 0; i < expression.size(); i++) {
            Token token = expression.get(i);
            //Stores the width of this draw count into the array
            drawX.add(x);

            if (token instanceof Bracket && token.getType() == Bracket.SUPERSCRIPT_OPEN) {
                scriptLevel++;
                scriptBracketCount++;
            } else if (token instanceof Bracket && token.getType() == Bracket.NUM_OPEN) {
                fracStarts.push(x);
            } else if (token instanceof Bracket && token.getType() == Bracket.DENOM_CLOSE) {
                //Finds index where the numerator ends
                int j = i - 1;
                int bracketCount = 1;
                //DENOM
                while (bracketCount > 0) {
                    Token t = expression.get(j);
                    if (t instanceof Bracket && t.getType() == Bracket.DENOM_OPEN) {
                        bracketCount--;
                    } else if (t instanceof Bracket && t.getType() == Bracket.DENOM_CLOSE) {
                        bracketCount++;
                    }
                    j--;
                }
                //NUM
                j -= 1;
                int endNum = j;
                float newX = x > drawX.get(endNum) ? x : drawX.get(endNum); //Takes bigger of two
                x = newX;
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

            if (token instanceof Matrix) {
                ArrayList<Token>[][] entries = ((Matrix) token).getEntries();
                //Calculates at what x value to start drawing each column
                float[] columnX = new float[entries[0].length + 1];
                columnX[0] = x;
                for (int j = 1; j < columnX.length; j++) {
                    float maxWidth = 0;
                    for (int k = 0; k < entries.length; k++) {
                        float width = textPaint.measureText(Utility.printExpression(entries[k][j - 1]));
                        if (width > maxWidth) {
                            maxWidth = width;
                        }
                    }
                    columnX[j] = columnX[j - 1] + maxWidth;
                }
                x = columnX[columnX.length - 1] + (entries[0].length - 1) * matrixPadding + paddingAfterMatrix;
            } else {
                paint = textPaint;
                //Determines the width of the symbol in text
                String s = token.getSymbol();
                if (s.length() < 3) { //For efficiency
                    float[] widths = new float[token.getSymbol().length()];
                    paint.getTextWidths(token.getSymbol(), widths);
                    float widthSum = sum(widths);
                    x += widthSum;
                } else { //May contain a text modification
                    //Goes through each letter and writes with the appropriate text modification
                    boolean onSubscript = false;
                    for (int index = 0; index < s.length(); index++) {
                        char c = s.charAt(index);
                        if (c == '☺') { //Toggles subscript
                            onSubscript = !onSubscript;
                        } else { //Draws the character
                            float[] width = new float[1];
                            paint = textPaint;
                            if (onSubscript) {
                                paint = subscriptPaint;
                            }
                            paint.getTextWidths(Character.toString(c), width);
                            x += width[0];
                        }
                    }
                }
            }

            if (token instanceof Bracket && token.getType() == Bracket.NUM_CLOSE) {
                if (!fracStarts.isEmpty()) {
                    x = fracStarts.pop();
                }
            }
        }
        drawX.add(x);
        return drawX;
    }

    /**
     * Calculates the next y coordinate to draw at based on the current y coordinate and given token and expression.
     *
     * @param y          The current y coordinate
     * @param expression The expression to draw
     * @param i          The index of the next Token to find the y coordinate
     * @param INITIAL_Y  The initial y position
     * @return The next y coordinate
     */
    protected float calculateNextY(float y, ArrayList<Token> expression, int i, final float INITIAL_Y) {
        Token token = expression.get(i);
        if (token instanceof Bracket) {
            switch (token.getType()) {
                case Bracket.SUPERSCRIPT_OPEN: {
                    //Extract the exponent expression
                    ArrayList<Token> exponent = new ArrayList<Token>();
                    int j = i + 1;
                    int scriptCount = 1;
                    while (scriptCount != 0) {
                        Token t = expression.get(j);
                        if (t instanceof Bracket && t.getType() == Bracket.SUPERSCRIPT_OPEN) {
                            scriptCount++;
                        } else if (t instanceof Bracket && t.getType() == Bracket.SUPERSCRIPT_CLOSE) {
                            scriptCount--;
                        }
                        exponent.add(t);
                        j++;
                    }
                    exponent.remove(exponent.size() - 1); //Removes the SUPERSCRIPT_CLOSE Bracket
                    y -= superscriptYOffset + (getMaxLinesHeight(exponent) == 1 ? 0 : getHeight(exponent, false) / 2);
                    break;
                }
                case Bracket.SUPERSCRIPT_CLOSE: {
                    //Finds the height of the SUPERSCRIPT_OPEN
                    int bracketCount = 1;
                    int j = i - 1;
                    while (bracketCount > 0) {
                        Token t = expression.get(j);
                        if (t instanceof Bracket && t.getType() == Bracket.SUPERSCRIPT_OPEN) {
                            bracketCount--;
                        } else if (t instanceof Bracket && t.getType() == Bracket.SUPERSCRIPT_CLOSE) {
                            bracketCount++;
                        }
                        j--;
                    }

                    if (j == -1) { //Some idiot did ^E (with no base)
                        y = INITIAL_Y;
                    } else {
                        y = heights.get(j);
                    }
                    break;
                }
                case Bracket.NUM_OPEN: {
                    int j = i + 1;
                    ArrayList<Token> num = new ArrayList<Token>();
                    int bracketCount = 1;
                    while (bracketCount != 0) {
                        Token t = expression.get(j);
                        if (t instanceof Bracket && t.getType() == Bracket.NUM_OPEN) {
                            bracketCount++;
                        } else if (t instanceof Bracket && t.getType() == Bracket.NUM_CLOSE) {
                            bracketCount--;
                        }
                        num.add(t);

                        j++;
                    }
                    num.remove(num.size() - 1); //Removes the NUM_CLOSE Bracket

                    //Generates an expression containing the fraction
                    ArrayList<Token> fraction = new ArrayList<Token>();
                    fraction.add(BracketFactory.makeNumOpen());
                    fraction.addAll(num);
                    fraction.add(expression.get(j - 1)); //NUM_CLOSE Bracket
                    fraction.add(expression.get(j)); //FRACTION Operator
                    fraction.add(BracketFactory.makeDenomOpen());
                    fraction.addAll(getDenominator(expression, j)); //Adds the entire denom
                    fraction.add(BracketFactory.makeDenomClose());

                    if (getMaxLinesHeight(num) == 1) {
                        y += -getHeight(fraction, true) / 4; //HEREEEEE!!!!!
                    } else {
                        y += -getHeight(fraction, true) / 2 + getHeight(num, true) / 2;
                    }
                    break;
                }
                case Bracket.DENOM_OPEN: {
                    ArrayList<Token> denom = getDenominator(expression, i - 1);
                    y += -getMostNeg(denom) + textHeight;
                    break;
                }
                case Bracket.DENOM_CLOSE: {
                    int bracketCount = 1;
                    int j = i - 1;
                    while (bracketCount > 0) {
                        Token t = expression.get(j);
                        if (t instanceof Bracket && t.getType() == Bracket.DENOM_OPEN) {
                            bracketCount--;
                        } else if (t instanceof Bracket && t.getType() == Bracket.DENOM_CLOSE) {
                            bracketCount++;
                        }
                        j--;
                    }

                    //Now j is at the index of the fraction. Looking for the height of the NUM_OPEN bracket
                    bracketCount = 1;
                    j -= 2;
                    while (bracketCount > 0) {
                        Token t = expression.get(j);
                        if (t instanceof Bracket && t.getType() == Bracket.NUM_OPEN) {
                            bracketCount--;
                        } else if (t instanceof Bracket && t.getType() == Bracket.NUM_CLOSE) {
                            bracketCount++;
                        }
                        j--;
                    }

                    //Changes height to the height of the Token before the NUM_OPEN
                    if (j >= 0) {
                        y = heights.get(j);
                    } else { //Very first token; cannot check token before it
                        y = INITIAL_Y;
                    }
                    break;
                }
            }
        } else if (token instanceof Operator && token.getType() == Operator.FRACTION) {

            //Finds the max height in the numerator
            ArrayList<Token> numerator = getNumerator(expression, i);
            float maxHeight = Float.NEGATIVE_INFINITY;
            for (Token t : numerator) {
                float height = heights.get(expression.indexOf(t));
                if (height > maxHeight) {
                    maxHeight = height;
                }
            }
            y = maxHeight;
        }
        return y;
    }

    /**
     * Draws the given Matrix on the given Canvas.
     *
     * @param x      The left-most x coordinate of the Matrix
     * @param y      The y coordinate of the middle of the Matrix that would be drawn
     * @param canvas The canvas to draw the Matrix on
     * @param matrix The Matrix to draw
     * @param paint  The Paint to draw the Matrix with
     * @return The bottommost y coordinate drawn at
     */
    protected float drawMatrix(float x, float y, Canvas canvas, Matrix matrix, Paint paint) {
        ArrayList<Token>[][] entries = matrix.getEntries();
        y -= (entries.length - 1) * textHeight / 2; //Starts at the top
        //Calculates at what x value to start drawing each column
        float[] columnX = new float[entries[0].length + 1];
        columnX[0] = x;
        for (int j = 1; j < columnX.length; j++) {
            float maxWidth = 0;
            for (int k = 0; k < entries.length; k++) {
                float width = paint.measureText(Utility.printExpression(entries[k][j - 1]));
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
            columnX[j] = columnX[j - 1] + maxWidth;
        }

        //Draws all the Matrix entries
        for (int j = 0; j < entries.length; j++) {
            for (int k = 0; k < entries[j].length; k++) {
                String str = Utility.printExpression(entries[j][k]);
                //Centers the text (determines what x value to print it at
                float currentWidth = paint.measureText(str);
                float targetWidth = columnX[k + 1] - columnX[k];
                float padding = (targetWidth - currentWidth) / 2; //Padding on each side
                float drawMatrixX = columnX[k] + k * matrixPadding;
                drawMatrixX += padding;
                canvas.drawText(str, drawMatrixX, y, paint);
            }
            y += textHeight;
        }
        y -= textHeight;
        return y;
    }

    /**
     * Prepares the variables for drawing.
     *
     * @param expression The expression to prepare to draw
     * @return A list of Floats that indicate the x coordinates to draw at
     */
    protected ArrayList<Float> prepareDrawing(ArrayList<Token> expression) {
        ArrayList<Float> drawX = calculateDrawX(expression); //Stores the width of each counted symbol

        heights.clear();
        centerFractions(expression, drawX);
        if (drawX.size() > 1) {
            maxX = drawX.get(drawX.size() - 1);
        }
        return drawX;
    }

    /**
     * Determines what would be the most negative pixel drawn, assuming the expression that drawing at zero.
     *
     * @param expression The expression to draw
     * @return The most negative y coordinate drawn on
     */
    protected float getMostNeg(ArrayList<Token> expression) {
        float mostNeg = Float.POSITIVE_INFINITY;
        float yModifier = 0;
        ArrayList<Float> heights = new ArrayList<>();
        for (int i = 0; i < expression.size(); i++) {
            Token token = expression.get(i);
            if (token instanceof Bracket) {
                switch (token.getType()) {
                    case Bracket.SUPERSCRIPT_OPEN: {
                        //Extract the exponent expression
                        ArrayList<Token> exponent = new ArrayList<Token>();
                        int j = i + 1;
                        int scriptCount = 1;
                        while (scriptCount != 0) {
                            Token t = expression.get(j);
                            if (t instanceof Bracket && t.getType() == Bracket.SUPERSCRIPT_OPEN) {
                                scriptCount++;
                            } else if (t instanceof Bracket && t.getType() == Bracket.SUPERSCRIPT_CLOSE) {
                                scriptCount--;
                            }
                            exponent.add(t);
                            j++;
                        }
                        exponent.remove(exponent.size() - 1); //Removes the SUPERSCRIPT_CLOSE Bracket
                        yModifier -= superscriptYOffset + (getMaxLinesHeight(exponent) == 1 ? 0 : getHeight(exponent, false) / 2);
                        break;
                    }
                    case Bracket.SUPERSCRIPT_CLOSE: {
                        yModifier += superscriptYOffset;
                        break;
                    }
                    case Bracket.NUM_OPEN: {
                        int j = i + 1;
                        ArrayList<Token> num = new ArrayList<Token>();
                        int bracketCount = 1;
                        while (bracketCount != 0) {
                            Token t = expression.get(j);
                            if (t instanceof Bracket && t.getType() == Bracket.NUM_OPEN) {
                                bracketCount++;
                            } else if (t instanceof Bracket && t.getType() == Bracket.NUM_CLOSE) {
                                bracketCount--;
                            }
                            num.add(t);

                            j++;
                        }
                        num.remove(num.size() - 1); //Removes the NUM_CLOSE Bracket

                        //Generates an expression containing the fraction
                        ArrayList<Token> fraction = new ArrayList<Token>();
                        fraction.add(BracketFactory.makeNumOpen());
                        fraction.addAll(num);
                        fraction.add(expression.get(j - 1)); //NUM_CLOSE Bracket
                        fraction.add(expression.get(j)); //FRACTION Operator
                        fraction.add(BracketFactory.makeDenomOpen());
                        fraction.addAll(getDenominator(expression, j)); //Adds the entire denom
                        fraction.add(BracketFactory.makeDenomClose());

                        if (getMaxLinesHeight(num) == 1) {
                            yModifier += -getHeight(fraction, true) / 4; //HEREEEEE!!!!!
                        } else {
                            yModifier += -getHeight(fraction, true) / 2 + getHeight(num, true) / 2;
                        }
                        break;
                    }
                    case Bracket.DENOM_OPEN: {
                        ArrayList<Token> denom = getDenominator(expression, i - 1);
                        if (getMaxLinesHeight(denom) == 1) {
                            yModifier += getHeight(denom, true);
                        } else {
                            yModifier += getHeight(denom, true) / 2;
                        }
                        break;
                    }
                    case Bracket.DENOM_CLOSE: {
                        int bracketCount = 1;
                        int j = i - 1;
                        while (bracketCount > 0) {
                            Token t = expression.get(j);
                            if (t instanceof Bracket && t.getType() == Bracket.DENOM_OPEN) {
                                bracketCount--;
                            } else if (t instanceof Bracket && t.getType() == Bracket.DENOM_CLOSE) {
                                bracketCount++;
                            }
                            j--;
                        }

                        //Now j is at the index of the fraction. Looking for the height of the NUM_OPEN bracket
                        bracketCount = 1;
                        j -= 2;
                        while (bracketCount > 0) {
                            Token t = expression.get(j);
                            if (t instanceof Bracket && t.getType() == Bracket.NUM_OPEN) {
                                bracketCount--;
                            } else if (t instanceof Bracket && t.getType() == Bracket.NUM_CLOSE) {
                                bracketCount++;
                            }
                            j--;
                        }

                        //Changes height to the height of the Token before the NUM_OPEN
                        if (j >= 0) {
                            yModifier = heights.get(j);
                        } else { //Very first token; cannot check token before it
                            yModifier = 0;
                        }
                        break;
                    }
                }
            } else if (token instanceof Operator && token.getType() == Operator.FRACTION) {

                //Finds the max height in the numerator
                ArrayList<Token> numerator = getNumerator(expression, i);
                float maxHeight = Float.NEGATIVE_INFINITY;
                for (Token t : numerator) {
                    float height = heights.get(expression.indexOf(t));
                    if (height > maxHeight) {
                        maxHeight = height;
                    }
                }
                yModifier = maxHeight;
            }
            if (token instanceof Matrix) {
                float negY = -(textHeight * (((Matrix) token).getNumOfRows() - 1)) / 2 + yModifier;
                heights.add(negY);
                if (negY < mostNeg) {
                    mostNeg = negY;
                }
            } else {
                //Sets the most neg if it is lower than current
                if (yModifier < mostNeg) {
                    mostNeg = yModifier;
                }
                heights.add(yModifier);
            }
        }
        return mostNeg;
    }

    /**
     * Determines the height, in pixels, of the expression
     *
     * @param expression        The expression
     * @param countEndExponents If exponents at the end of the expression should count
     * @return The height of the given expression, in pixels
     */
    private float getHeight(ArrayList<Token> expression, boolean countEndExponents) {
        float maxHeight = textHeight;
        float temp = 0;
        for (int i = 0; i < expression.size(); i++) {
            Token t = expression.get(i);
            //Handles parts of fractions seperately
            if (t instanceof Bracket) {
                switch (t.getType()) {
                    case Bracket.NUM_CLOSE:
                        ArrayList<Token> numerator = getNumerator(expression, i + 1);
                        temp += getHeight(numerator, true);
                        break;
                    case Bracket.DENOM_OPEN:
                        ArrayList<Token> denom = getDenominator(expression, i - 1);
                        temp += getHeight(denom, true);
                        if (temp > maxHeight) {
                            maxHeight = temp;
                        }
                        temp = 0;
                        break;
                }
            } else if (t instanceof Operator && t.getType() == Operator.EXPONENT) {
                ArrayList<Token> exponent = new ArrayList<Token>();
                int j = i + 2;
                int scriptCount = 1;
                while (scriptCount != 0) {
                    Token token = expression.get(j);
                    if (token instanceof Bracket && token.getType() == Bracket.SUPERSCRIPT_OPEN) {
                        scriptCount++;
                    } else if (token instanceof Bracket && token.getType() == Bracket.SUPERSCRIPT_CLOSE) {
                        scriptCount--;
                    }
                    exponent.add(token);
                    j++;
                }
                exponent.remove(exponent.size() - 1); //Removes the SUPERSCRIPT_CLOSE Bracket
                if (!countEndExponents) {
                    //Takes out all cases where there is a ^(E) and the end
                    while (exponent.size() > 1 && exponent.get(exponent.size() - 1) instanceof Bracket && exponent.get(exponent.size() - 1).getType() == Bracket.SUPERSCRIPT_CLOSE) {
                        int k = exponent.size() - 2;
                        exponent.remove(k + 1);
                        int bracketCount = 1;
                        while (bracketCount != 0) { //Keeps removing until the end exponents begins
                            Token token = exponent.get(k);
                            if (token instanceof Bracket && token.getType() == Bracket.SUPERSCRIPT_OPEN) {
                                bracketCount--;
                            } else if (token instanceof Bracket && token.getType() == Bracket.SUPERSCRIPT_CLOSE) {
                                bracketCount++;
                            }
                            exponent.remove(k);
                            k--;
                        }
                        exponent.remove(k); //Removes the exponent
                    }
                }
                temp = (countEndExponents ? superscriptYOffset : 0) + getHeight(exponent, countEndExponents);
                if (temp > maxHeight) {
                    maxHeight = temp;
                }
                temp = 0;
            } else if (t instanceof Matrix) {
                temp = textHeight * (((Matrix) t).getNumOfRows() - 1);
                if (temp > maxHeight) {
                    maxHeight = temp;
                }
                temp = 0;
            }
        }
        return maxHeight;
    }

    /**
     * Finds the max number of lines of text (vertically) there are in the expression
     *
     * @param expression The expression to find the height
     * @return The maximum height of a fraction in the given expression
     */
    private int getMaxLinesHeight(ArrayList<Token> expression) {
        int maxFracHeight = 1;
        int numBracketCount = 0;
        int denomBracketCount = 0;
        boolean inExponent = false;
        int expBracketCount = 0;
        for (int i = 0; i < expression.size(); i++) {
            Token t = expression.get(i);
            if (t instanceof Bracket) {
                switch (t.getType()) {
                    case Bracket.SUPERSCRIPT_OPEN:
                        expBracketCount++;
                        inExponent = true;
                        break;
                    case Bracket.SUPERSCRIPT_CLOSE:
                        expBracketCount--;
                        if (expBracketCount == 0) {
                            inExponent = false;
                        }
                        break;
                    case Bracket.NUM_OPEN:
                        numBracketCount++;
                        break;
                    case Bracket.NUM_CLOSE:
                        numBracketCount--;
                        break;
                    case Bracket.DENOM_OPEN:
                        denomBracketCount++;
                        break;
                    case Bracket.DENOM_CLOSE:
                        denomBracketCount--;
                        break;
                }
            } else if (t instanceof Matrix) {
                int height = ((Matrix) t).getNumOfRows();
                if (height > maxFracHeight) {
                    maxFracHeight = height;
                }
            }

            if (numBracketCount == 0 && denomBracketCount == 0 && !inExponent) { //Cannot be in a numerator or denom or an exponent
                if (t instanceof Operator && t.getType() == Operator.FRACTION) {
                    ArrayList<Token> num = getNumerator(expression, i);
                    ArrayList<Token> denom = getDenominator(expression, i);
                    //And adds the height of both + 1
                    int height = getMaxLinesHeight(num) + getMaxLinesHeight(denom);
                    if (height > maxFracHeight) {
                        maxFracHeight = height;
                    }
                }
            }
        }
        return maxFracHeight;
    }

    /**
     * Gets the numerator of a specified fraction in the given expression.
     *
     * @param expression The expression where the numerator is located
     * @param i          The index of the fraction Token
     * @return The numerator of the fraction
     */
    private ArrayList<Token> getNumerator(ArrayList<Token> expression, int i) {
        if (!(expression.get(i) instanceof Operator && expression.get(i).getType() == Operator.FRACTION)) {
            throw new IllegalArgumentException("Given index of the expression is not a Fraction Token.");
        }
        ArrayList<Token> num = new ArrayList<Token>();
        //Finds the numerator
        int bracketCount = 1;
        int j = i - 2;
        while (bracketCount > 0) {
            Token token = expression.get(j);
            if (token instanceof Bracket && token.getType() == Bracket.NUM_OPEN) {
                bracketCount--;
            } else if (token instanceof Bracket && token.getType() == Bracket.NUM_CLOSE) {
                bracketCount++;
            }
            num.add(0, token);
            j--;
        }
        num.remove(0); //Takes out the open bracket
        return num;
    }

    /**
     * Gets the denominator of a specified fraction in the given expression.
     *
     * @param expression The expression where the denom is located
     * @param i          The index of the fraction Token
     * @return The denom of the fraction
     */
    private ArrayList<Token> getDenominator(ArrayList<Token> expression, int i) {
        if (!(expression.get(i) instanceof Operator && expression.get(i).getType() == Operator.FRACTION)) {
            throw new IllegalArgumentException("Given index of the expression is not a Fraction Token.");
        }
        ArrayList<Token> denom = new ArrayList<Token>();
        //Now denom
        int bracketCount = 1;
        int j = i + 2;
        while (bracketCount > 0) {
            Token token = expression.get(j);
            if (token instanceof Bracket && token.getType() == Bracket.DENOM_OPEN) {
                bracketCount++;
            } else if (token instanceof Bracket && token.getType() == Bracket.DENOM_CLOSE) {
                bracketCount--;
            }
            denom.add(token);
            j++;
        }
        denom.remove(denom.size() - 1); //Takes out the close bracket
        return denom;
    }

    /**
     * Finds the index where the denominator of the fraction Token at index i ends.
     *
     * @param i          The index of the fraction Token
     * @param expression The expression to find the denominator end
     * @return The index of the end of the denominator
     */
    protected int getDenomEnd(int i, ArrayList<Token> expression) {
        assert expression.get(i).getType() == Operator.FRACTION : "The given index i is NOT a fraction Token ";
        int j = i + 2;
        int bracketCount = 1;
        while (bracketCount > 0) {
            Token t = expression.get(j);
            if (t instanceof Bracket && t.getType() == Bracket.DENOM_CLOSE) {
                bracketCount--;
            } else if (t instanceof Bracket && t.getType() == Bracket.DENOM_OPEN) {
                bracketCount++;
            }
            j++;
        }
        return j;
    }

    /**
     * Centers the fractions through modification of the drawX list.
     *
     * @param expression The expression to center
     * @param drawX      The uncentered drawing x coordinates
     * @return The centered x coordinates for the expression
     */
    private ArrayList<Float> centerFractions(ArrayList<Token> expression, ArrayList<Float> drawX) {
        for (int i = 0; i < expression.size(); i++) {
            Token t = expression.get(i);
            if (t instanceof Operator && t.getType() == Operator.FRACTION) {
                ArrayList<Token> numerator = getNumerator(expression, i);
                ArrayList<Token> denom = getDenominator(expression, i);
                float numWidth = getWidth(i - numerator.size() - 1, i - 1, expression, drawX);
                float denomWidth = getWidth(i + 1, denom.size() + i + 1, expression, drawX);
                if (numWidth > denomWidth) {
                    float adjust = (numWidth - denomWidth) / 2;
                    for (int j = i + 1; j <= denom.size() + i + 2; j++) {
                        drawX.add(j, drawX.remove(j) + adjust);
                    }
                } else if (numWidth < denomWidth) {
                    float adjust = (denomWidth - numWidth) / 2;
                    for (int j = i - numerator.size() - 2; j < i; j++) {
                        drawX.add(j, drawX.remove(j) + adjust);
                    }
                } else { //Equals
                    //Nothing happens
                }
            }
        }
        return drawX;
    }

    /**
     * Determines the width of a given expression from the drawX list.
     *
     * @param start      The start index on the drawX
     * @param end        The end index on the drawX
     * @param expression The expression to center
     * @param drawX      The uncentered drawing x coordinates
     * @return The width of the expression
     */
    private float getWidth(int start, int end, ArrayList<Token> expression, ArrayList<Float> drawX) {
        //Counts to the END for the expression (last pixel drawn)
        String symb = expression.get(end).getSymbol();
        String filtered = "";
        //Filters out the subscript markers
        for (int i = 0; i < symb.length(); i++){
            char c = symb.charAt(i);
            if (c != '☺'){
                filtered += c;
            }
        }
        float[] widths = new float[filtered.length()];
        textPaint.getTextWidths(filtered, widths);
        return drawX.get(end) + sum(widths) - drawX.get(start);
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
}