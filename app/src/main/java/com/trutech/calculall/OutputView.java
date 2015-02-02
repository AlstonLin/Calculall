package com.trutech.calculall;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Modified DisplayView to display outputs.
 *
 * @author Alston Lin
 * @version Alpha 2.0
 */
public class OutputView extends View {

    //CONSTANTS
    private final float TEXT_HEIGHT;
    private final float SUPERSCRIPT_Y_OFFSET;
    private final int FONT_SIZE = 96;
    private final float X_PADDING; //The padding at the start and end of the display (x)
    private final float FRAC_PADDING;
    private final float MATRIX_PADDING;
    private float maxX = 0; //Max start X that the user can scroll to
    private float maxY = 0;
    private ArrayList<Float> drawX = new ArrayList<Float>(); //Stores the width of each counted symbol
    private ArrayList<Float> heights = new ArrayList<Float>();
    private Paint textPaint;
    private Paint fracPaint;
    private ArrayList<Token> expression = new ArrayList<Token>();

    public OutputView(Context context, AttributeSet attr) {
        super(context, attr);
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.displayColor, typedValue, true);
        int displayColor = typedValue.data;
        //Setup the paints
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(displayColor);
        textPaint.setTextSize(FONT_SIZE);

        fracPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fracPaint.setColor(displayColor);
        fracPaint.setTextSize(FONT_SIZE);
        fracPaint.setStrokeWidth(10);

        //Sets constant values
        //Calculates the height of the texts
        Rect textRect = new Rect();
        textPaint.getTextBounds("1", 0, 1, textRect);
        TEXT_HEIGHT = textRect.height() * 1.25f;

        X_PADDING = TEXT_HEIGHT / 3;
        //LINE_HEIGHT_NORMAL = TEXT_HEIGHT;
        FRAC_PADDING = TEXT_HEIGHT / 8;
        SUPERSCRIPT_Y_OFFSET = TEXT_HEIGHT / 2;
        MATRIX_PADDING = textPaint.measureText("  ");
        setWillNotDraw(false);
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
        maxY = 0;
        heights.clear();

        calculateDrawX();
        centerFractions();
        if (drawX.size() > 1) {
            maxX = drawX.get(drawX.size() - 1);
        }

        //Counter and state variables
        final float INITIAL_MODIFIER = -getMostNeg(expression) + TEXT_HEIGHT;
        float yModifier = INITIAL_MODIFIER;
        //float scriptHeightMultiplier = 0; //Height on the superscript level
        for (int i = 0; i < expression.size(); i++) {
            Token token = expression.get(i);
            Paint paint = textPaint;

            if (token instanceof Bracket) {
                switch (((Bracket) token).getType()) {
                    case Bracket.SUPERSCRIPT_OPEN: {
                        //Extract the exponent expression
                        ArrayList<Token> exponent = new ArrayList<Token>();
                        int j = i + 1;
                        int scriptCount = 1;
                        while (scriptCount != 0) {
                            Token t = expression.get(j);
                            if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.SUPERSCRIPT_OPEN) {
                                scriptCount++;
                            } else if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.SUPERSCRIPT_CLOSE) {
                                scriptCount--;
                            }
                            exponent.add(t);
                            j++;
                        }
                        exponent.remove(exponent.size() - 1); //Removes the SUPERSCRIPT_CLOSE Bracket
                        yModifier -= SUPERSCRIPT_Y_OFFSET + (getMaxLinesHeight(exponent) == 1 ? 0 : getHeight(exponent, false) / 2);
                        break;
                    }
                    case Bracket.SUPERSCRIPT_CLOSE: {
                        yModifier += SUPERSCRIPT_Y_OFFSET;
                        break;
                    }
                    case Bracket.NUM_OPEN: {
                        int j = i + 1;
                        ArrayList<Token> num = new ArrayList<Token>();
                        int bracketCount = 1;
                        while (bracketCount != 0) {
                            Token t = expression.get(j);
                            if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.NUM_OPEN) {
                                bracketCount++;
                            } else if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.NUM_CLOSE) {
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
                            yModifier += -getHeight(fraction, true) / 2 + getHeight(num, true);
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
                            if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.DENOM_OPEN) {
                                bracketCount--;
                            } else if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.DENOM_CLOSE) {
                                bracketCount++;
                            }
                            j--;
                        }

                        //Now j is at the index of the fraction. Looking for the height of the NUM_OPEN bracket
                        bracketCount = 1;
                        j -= 2;
                        while (bracketCount > 0) {
                            Token t = expression.get(j);
                            if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.NUM_OPEN) {
                                bracketCount--;
                            } else if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.NUM_CLOSE) {
                                bracketCount++;
                            }
                            j--;
                        }

                        //Changes height to the height of the Token before the NUM_OPEN
                        if (j >= 0) {
                            yModifier = heights.get(j);
                        } else { //Very first token; cannot check token before it
                            yModifier = INITIAL_MODIFIER;
                        }
                        break;
                    }
                }
            } else if (token instanceof Operator && ((Operator) token).getType() == Operator.FRACTION) {

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

            //Calculates the x and y position of the draw position (modified later)
            float x = drawX.get(i);
            float y = yModifier;
            heights.add(i, y);

            //Draws the text
            if (token instanceof Matrix) {
                ArrayList<Token>[][] entries = ((Matrix) token).getEntries();
                y -= (entries.length - 1) * TEXT_HEIGHT; //Starts at the top
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
                        float drawX = columnX[k] + k * MATRIX_PADDING;
                        drawX += padding;
                        canvas.drawText(str, drawX, y, paint);
                    }
                    y += TEXT_HEIGHT;
                }
                y -= TEXT_HEIGHT; //Undeos the last iteration
            } else {
                canvas.drawText(token.getSymbol(), x, y, paint);
            }
            //Updates maxY
            if (y > maxY) {
                maxY = y;
            }

            //Draws fraction sign
            if (token instanceof Operator && ((Operator) token).getType() == Operator.FRACTION) {
                //Looks for index where the denom ends
                int j = i + 2;
                int bracketCount = 1;
                while (bracketCount > 0) {
                    Token t = expression.get(j);
                    if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.DENOM_CLOSE) {
                        bracketCount--;
                    } else if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.DENOM_OPEN) {
                        bracketCount++;
                    }
                    j++;
                }
                canvas.drawLine(x, y + FRAC_PADDING, drawX.get(j), y + FRAC_PADDING, fracPaint);
            }

        }
    }

    /**
     * Determines what would be the most negative pixel drawn, assuming the expression that drawing at zero.
     *
     * @param expression The expression to draw
     * @return The most negative y coordinate drawn on
     */
    private float getMostNeg(ArrayList<Token> expression) {
        float mostNeg = Float.POSITIVE_INFINITY;
        float yModifier = 0;
        for (int i = 0; i < expression.size(); i++) {
            Token token = expression.get(i);
            if (token instanceof Bracket) {
                switch (((Bracket) token).getType()) {
                    case Bracket.SUPERSCRIPT_OPEN: {
                        //Extract the exponent expression
                        ArrayList<Token> exponent = new ArrayList<Token>();
                        int j = i + 1;
                        int scriptCount = 1;
                        while (scriptCount != 0) {
                            Token t = expression.get(j);
                            if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.SUPERSCRIPT_OPEN) {
                                scriptCount++;
                            } else if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.SUPERSCRIPT_CLOSE) {
                                scriptCount--;
                            }
                            exponent.add(t);
                            j++;
                        }
                        exponent.remove(exponent.size() - 1); //Removes the SUPERSCRIPT_CLOSE Bracket
                        yModifier -= SUPERSCRIPT_Y_OFFSET + (getMaxLinesHeight(exponent) == 1 ? 0 : getHeight(exponent, false) / 2);
                        break;
                    }
                    case Bracket.SUPERSCRIPT_CLOSE: {
                        yModifier += SUPERSCRIPT_Y_OFFSET;
                        break;
                    }
                    case Bracket.NUM_OPEN: {
                        int j = i + 1;
                        ArrayList<Token> num = new ArrayList<Token>();
                        int bracketCount = 1;
                        while (bracketCount != 0) {
                            Token t = expression.get(j);
                            if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.NUM_OPEN) {
                                bracketCount++;
                            } else if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.NUM_CLOSE) {
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
                            yModifier += -getHeight(fraction, true) / 2 + getHeight(num, true);
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
                            if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.DENOM_OPEN) {
                                bracketCount--;
                            } else if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.DENOM_CLOSE) {
                                bracketCount++;
                            }
                            j--;
                        }

                        //Now j is at the index of the fraction. Looking for the height of the NUM_OPEN bracket
                        bracketCount = 1;
                        j -= 2;
                        while (bracketCount > 0) {
                            Token t = expression.get(j);
                            if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.NUM_OPEN) {
                                bracketCount--;
                            } else if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.NUM_CLOSE) {
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
            } else if (token instanceof Operator && ((Operator) token).getType() == Operator.FRACTION) {

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
            if (token instanceof Matrix){
                float negY = -TEXT_HEIGHT * (((Matrix)token).getNumOfRows() - 1) + yModifier;
                heights.add(negY);
                if (negY < mostNeg) {
                    mostNeg = negY;
                }
            }else {
                //Sets the most neg if it is lower than current
                if (yModifier < mostNeg) {
                    mostNeg = yModifier;
                }
                heights.add(yModifier);
            }
        }
        heights.clear();
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
        float maxHeight = TEXT_HEIGHT;
        float temp = 0;
        for (int i = 0; i < expression.size(); i++) {
            Token t = expression.get(i);
            //Handles parts of fractions seperately
            if (t instanceof Bracket) {
                switch (((Bracket) t).getType()) {
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
            } else if (t instanceof Operator && ((Operator) t).getType() == Operator.EXPONENT) {
                ArrayList<Token> exponent = new ArrayList<Token>();
                int j = i + 2;
                int scriptCount = 1;
                while (scriptCount != 0) {
                    Token token = expression.get(j);
                    if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.SUPERSCRIPT_OPEN) {
                        scriptCount++;
                    } else if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.SUPERSCRIPT_CLOSE) {
                        scriptCount--;
                    }
                    exponent.add(token);
                    j++;
                }
                exponent.remove(exponent.size() - 1); //Removes the SUPERSCRIPT_CLOSE Bracket
                if (!countEndExponents) {
                    //Takes out all cases where there is a ^(E) and the end
                    while (exponent.size() > 1 && exponent.get(exponent.size() - 1) instanceof Bracket && ((Bracket) exponent.get(exponent.size() - 1)).getType() == Bracket.SUPERSCRIPT_CLOSE) {
                        int k = exponent.size() - 2;
                        exponent.remove(k + 1);
                        int bracketCount = 1;
                        while (bracketCount != 0) { //Keeps removing until the end exponents begins
                            Token token = exponent.get(k);
                            if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.SUPERSCRIPT_OPEN) {
                                bracketCount--;
                            } else if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.SUPERSCRIPT_CLOSE) {
                                bracketCount++;
                            }
                            exponent.remove(k);
                            k--;
                        }
                        exponent.remove(k); //Removes the exponent
                    }
                }
                temp = (countEndExponents ? SUPERSCRIPT_Y_OFFSET : 0) + getHeight(exponent, countEndExponents);
                if (temp > maxHeight) {
                    maxHeight = temp;
                }
                temp = 0;
            } else if (t instanceof Matrix){
                temp = TEXT_HEIGHT * (((Matrix)t).getNumOfRows() - 1);
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
                switch (((Bracket) t).getType()) {
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
            } else if (t instanceof Matrix){
                int height = ((Matrix)t).getNumOfRows();
                if (height > maxFracHeight){
                    maxFracHeight = height;
                }
            }

            if (numBracketCount == 0 && denomBracketCount == 0 && !inExponent) { //Cannot be in a numerator or denom or an exponent
                if (t instanceof Operator && ((Operator) t).getType() == Operator.FRACTION) {
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
        if (!(expression.get(i) instanceof Operator && ((Operator) expression.get(i)).getType() == Operator.FRACTION)) {
            throw new IllegalArgumentException("Given index of the expression is not a Fraction Token.");
        }
        ArrayList<Token> num = new ArrayList<Token>();
        //Finds the numerator
        int bracketCount = 1;
        int j = i - 2;
        while (bracketCount > 0) {
            Token token = expression.get(j);
            if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.NUM_OPEN) {
                bracketCount--;
            } else if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.NUM_CLOSE) {
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
        if (!(expression.get(i) instanceof Operator && ((Operator) expression.get(i)).getType() == Operator.FRACTION)) {
            throw new IllegalArgumentException("Given index of the expression is not a Fraction Token.");
        }
        ArrayList<Token> denom = new ArrayList<Token>();
        //Now denom
        int bracketCount = 1;
        int j = i + 2;
        while (bracketCount > 0) {
            Token token = expression.get(j);
            if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.DENOM_OPEN) {
                bracketCount++;
            } else if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.DENOM_CLOSE) {
                bracketCount--;
            }
            denom.add(token);
            j++;
        }
        denom.remove(denom.size() - 1); //Takes out the close bracket
        return denom;
    }


    /**
     * Fills the drawWidth arrays with the width values.
     */
    private void calculateDrawX() {
        drawX.clear();
        Paint paint;
        int scriptLevel = 0; //superscript = 1, any additional levels would +1
        int scriptBracketCount = 0; //Counts the brackets for any exponents
        Stack<Float> fracStarts = new Stack<Float>(); //The indices where fractions start
        float x = X_PADDING;

        for (int i = 0; i < expression.size(); i++) {
            Token token = expression.get(i);
            //Stores the width of this draw count into the array
            drawX.add(x);

            if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.SUPERSCRIPT_OPEN) {
                scriptLevel++;
                scriptBracketCount++;
            } else if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.NUM_OPEN) {
                fracStarts.push(x);
            } else if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.NUM_CLOSE) {
                if (!fracStarts.isEmpty()) {
                    x = fracStarts.pop();
                }
            } else if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.DENOM_CLOSE) {
                //Finds index where the numerator ends
                int j = i - 1;
                int bracketCount = 1;
                //DENOM
                while (bracketCount > 0) {
                    Token t = expression.get(j);
                    if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.DENOM_OPEN) {
                        bracketCount--;
                    } else if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.DENOM_CLOSE) {
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

            if (token instanceof Matrix){
                float maxX = 0;
                float currentX;
                ArrayList<Token>[][] entries = ((Matrix)token).getEntries();
                //Finds whats the largest x delta when drawing the matrix
                for (int j = 0; j < entries.length; j++){
                    currentX = 0;
                    for (int k = 0; k < entries[j].length; k++){
                        String str = Utility.printExpression(entries[j][k]);
                        float[] widths = new float[str.length()];
                        textPaint.getTextWidths(str, widths);
                        float widthSum = sum(widths) + k * MATRIX_PADDING;
                        currentX += widthSum;
                    }
                    if (currentX > maxX){
                        maxX = currentX;
                    }
                }
                //Adds the largest delta x to the x value
                x += maxX;
            }else {
                //Changes paint for superscript
                paint = textPaint;
                //Determines the width of the symbol in text
                float[] widths = new float[token.getSymbol().length()];
                paint.getTextWidths(token.getSymbol(), widths);
                float widthSum = sum(widths);
                x += widthSum;
            }
        }
        drawX.add(x);
    }

    /**
     * Centers the fractions through modification of the drawX list.
     */
    private void centerFractions() {
        for (int i = 0; i < expression.size(); i++) {
            Token t = expression.get(i);
            if (t instanceof Operator && ((Operator) t).getType() == Operator.FRACTION) {
                ArrayList<Token> numerator = getNumerator(expression, i);
                ArrayList<Token> denom = getDenominator(expression, i);
                float numWidth = getWidth(i - numerator.size() - 1, i - 1);
                float denomWidth = getWidth(i + 1, denom.size() + i + 1);
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
    }

    /**
     * Determines the width of a given expression from the drawX list.
     *
     * @param start The start index on the drawX
     * @param end   The end index on the drawX
     * @return The width of the expression
     */
    private float getWidth(int start, int end) {
        //Counts to the END for the expression (last pixel drawn)
        String symb = expression.get(end).getSymbol();
        float[] widths = new float[symb.length()];
        textPaint.getTextWidths(symb, widths);

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
        calculateDrawX();
        if (drawX.size() > 1) {
            maxX = drawX.get(drawX.size() - 1);
        }

        Canvas canvas = new Canvas();
        this.draw(canvas); //Lazy way to calculate maxX and maxY

        int width = (int) (maxX + TEXT_HEIGHT);
        ;
        int height = expression.size() == 0 ? (int) TEXT_HEIGHT : (int) (maxY + TEXT_HEIGHT);
        this.setMeasuredDimension(width, height);
    }

}
