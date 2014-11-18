package com.trutech.calculall;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Stack;

/**
 * A custom View that displays the given mathematical expression with superscripts, subscripts
 * and fraction support.
 */
public class DisplayView extends View {

    //CONSTANTS
    private final float TEXT_HEIGHT;
    private final float SMALL_HEIGHT;
    private final float Y_PADDING_BETWEEN_LINES;
    private final float CURSOR_PADDING;
    private final float fracPadding;
    private final int FONT_SIZE = 96;
    private final float X_PADDING; //The padding at the start and end    of the display (x)
    private float startX = 0; //Tracks the starting x position at which the canvas will start drawing (allows side scrolling)
    private float maxX = 0; //Max start X that the user can scroll to
    private int cursorIndex = 0; //The index where the cursor is when shown on screen
    private int drawCount = 0;
    private float maxY = 0;
    private float cursorY = 0;
    private ArrayList<Float> drawX = new ArrayList<Float>(); //Stores the width of each counted symbol
    private ArrayList<Float> heights = new ArrayList<Float>();
    private int realCursorIndex = 0; //The index of the cursor in the list of tokens
    private boolean functionMode = false; //If this display is for function mode
    private Paint textPaint;
    private Paint smallPaint; //For superscripts and subscripts
    private Paint fracPaint;
    private Paint cursorPaint;
    private ArrayList<Token> expression = new ArrayList<Token>();
    private ArrayList<Token> output = new ArrayList<Token>();
    private String outputString = ""; //TEMPORARY

    public DisplayView(Context context, AttributeSet attr) {
        super(context, attr);
        //Setup the paints
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#F64B55"));
        textPaint.setTextSize(FONT_SIZE);

        smallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        smallPaint.setColor(Color.parseColor("#F64B55"));
        smallPaint.setTextSize(FONT_SIZE / 2);

        cursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cursorPaint.setColor(Color.parseColor("#F64B55"));
        cursorPaint.setTextSize(FONT_SIZE);

        fracPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fracPaint.setColor(Color.parseColor("#F64B55"));
        fracPaint.setTextSize(FONT_SIZE);
        fracPaint.setStrokeWidth(10);

        //Sets constant values
        //Calculates the height of the texts
        Rect textRect = new Rect();
        Rect smallRect = new Rect();
        textPaint.getTextBounds("1", 0, 1, textRect);
        smallPaint.getTextBounds("1", 0, 1, smallRect);
        TEXT_HEIGHT = textRect.height() * 1.25f;
        SMALL_HEIGHT = smallRect.height();

        X_PADDING = TEXT_HEIGHT / 3;
        Y_PADDING_BETWEEN_LINES = TEXT_HEIGHT;
        CURSOR_PADDING = TEXT_HEIGHT / 10;
        fracPadding = TEXT_HEIGHT / 8;
        setWillNotDraw(false);
    }

    /**
     * Displays the given mathematical expression on the view.
     *
     * @param expression The expression to display
     */
    public void displayInput(ArrayList<Token> expression) {
        this.expression = expression;
        calculateMaxY();
        requestLayout();
        invalidate();
    }

    /**
     * Displays the given output to the display.
     *
     * @param tokens The tokens to display
     */
    public void displayOutput(ArrayList<Token> tokens) {
        output = tokens;
        calculateMaxY();
        requestLayout();
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
        calculateMaxY();
        requestLayout();
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
        calculateMaxY();
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
        heights.clear();
        //TODO: Use canvas.drawLine() to make fractions, and implement a algorithm to do this
        final float maxHeight = getMaxFracHeight(expression);
        final float yFracModifier = Y_PADDING_BETWEEN_LINES * (1 + maxHeight) / 2f;
        final float yScriptModifier = SMALL_HEIGHT * getMaxScriptLevel() / 3;

        calculateDrawX();
        calculateRealCursorIndex();
        if (drawX.size() > 1) {
            maxX = drawX.get(drawX.size() - 1);
        }

        //Determines the starting X position
        float cursorX = getCursorX();

        if (cursorX < startX) {
            startX = cursorX - CURSOR_PADDING;
        } else if (cursorX > startX + getWidth()) {
            startX = cursorX - getWidth() + CURSOR_PADDING;
        }


        float xModifier = -startX;

        if (functionMode) { //Adds a f(x)= at the start
            final String s = "f(x)=";
            canvas.drawText(s, X_PADDING, Y_PADDING_BETWEEN_LINES, textPaint);
            float[] widths = new float[s.length()];
            textPaint.getTextWidths(s, widths);
            xModifier += sum(widths);
        }
        checkCursorIndex();


        //Counter and state variables
        int scriptLevel = 0; //superscript = 1, any additional levels would +1
        int scriptBracketCount = 0; //Counts the brackets for any exponents
        float heightMultiplier = 0; //Determines at what height the text would be drawn at
        for (int i = 0; i < expression.size(); i++) {
            Token token = expression.get(i);
            Paint paint = textPaint;

            //SPECIAL CASE BRACKETS
            if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.SUPERSCRIPT_OPEN) {
                scriptLevel++;
                scriptBracketCount++;
            } else if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.NUM_OPEN) {
                ArrayList<Token> num = new ArrayList<Token>();

                //Finds the nuemrator
                int bracketCount = 1;
                int j = i + 1;
                while (bracketCount > 0) {
                    Token t = expression.get(j);
                    if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.NUM_OPEN) {
                        bracketCount++;
                    } else if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.NUM_CLOSE) {
                        bracketCount--;
                    }
                    num.add(t);
                    j++;
                }

                int height = getMaxFracHeight(num);
                heightMultiplier -= 1 / 2d;
            } else if (token instanceof Operator && ((Operator) token).getType() == Operator.FRACTION) {
                //Finds the max height in the numerator
                ArrayList<Token> numerator = getNumerator(expression, i);
                float maxHeightMultiplier = Float.NEGATIVE_INFINITY;
                for (Token t : numerator) {
                    float height = heights.get(expression.indexOf(t));
                    if (height > maxHeightMultiplier) {
                        maxHeightMultiplier = height;
                    }
                }
                heightMultiplier = maxHeightMultiplier;
            } else if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.DENOM_OPEN) {
                //For the denom open
                int denomHeight = getMaxFracHeight(getDenominator(expression, i - 1));
                heightMultiplier += (denomHeight + 1) / 2d;
            } else if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.DENOM_CLOSE) {
                //Finds the denom
                ArrayList<Token> denom = new ArrayList<Token>();
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
                //Changes height to the height of the NUM_OPEN bracket + 0.5
                heightMultiplier = heights.get(j + 1) + 0.5f;
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

            //Calculates the x and y position of the draw position (modified later)
            float x = drawX.get(i) + xModifier;
            float y = Y_PADDING_BETWEEN_LINES * heightMultiplier + yFracModifier + yScriptModifier;
            heights.add(i, heightMultiplier);

            //Changes paint for superscript
            if (scriptLevel > 0) {
                paint = smallPaint;
                y += (2 - scriptLevel) * paint.getTextSize() / 4 - TEXT_HEIGHT / 2;
            }

            //Draws the text
            canvas.drawText(token.getSymbol(), x, y, paint);

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
                canvas.drawLine(x, y + fracPadding, drawX.get(j), y + fracPadding, fracPaint);
            }

            //Draws cursor
            if (i == realCursorIndex) {
                //Superscripts the cursor if needed
                cursorY = Y_PADDING_BETWEEN_LINES * heightMultiplier + yFracModifier + yScriptModifier;
                cursorX = x - CURSOR_PADDING;
                if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.SUPERSCRIPT_CLOSE) {
                    cursorY += (1 - scriptLevel) * SMALL_HEIGHT / 4 - TEXT_HEIGHT / 2;
                    canvas.drawText("|", cursorX, cursorY, cursorPaint);
                } else if (token instanceof Bracket && ((Bracket) token).getType() == Bracket.DENOM_CLOSE) {
                    cursorY = Y_PADDING_BETWEEN_LINES * (heightMultiplier + 0.5f) + yFracModifier + yScriptModifier;
                    canvas.drawText("|", cursorX, cursorY, cursorPaint);
                } else {
                    canvas.drawText("|", cursorX, y, cursorPaint);
                }
            }
        }

        //Draws cursor in special cases
        if (expression.size() == 0) { //No expression
            canvas.drawText("|", X_PADDING, Y_PADDING_BETWEEN_LINES * heightMultiplier + yFracModifier + yScriptModifier, cursorPaint);
        } else if (realCursorIndex == expression.size()) { //Last index (or the cursor index is larger than the draw count
            //Moves the cursor up if its superscript
            cursorY = Y_PADDING_BETWEEN_LINES * heightMultiplier + yFracModifier + yScriptModifier;
            if (scriptLevel > 0) {
                cursorY += (1 - scriptLevel) * SMALL_HEIGHT / 4 - TEXT_HEIGHT / 2;
            }
            cursorX = maxX + xModifier - CURSOR_PADDING;
            canvas.drawText("|", cursorX, cursorY, cursorPaint);
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
            canvas.drawText(outputString, getWidth() - outputWidth - X_PADDING, maxY + Y_PADDING_BETWEEN_LINES, textPaint);
        }
    }

    /**
     * Finds the max number of continued fractions (height) in a given expression
     *
     * @param expression The expression to find the height
     * @return The maximum height of a fraction in the given expression
     */
    private int getMaxFracHeight(ArrayList<Token> expression) {
        int maxFracHeight = 1;
        int numBracketCount = 0;
        int denomBracketCount = 0;
        for (int i = 0; i < expression.size(); i++) {
            Token t = expression.get(i);
            if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.NUM_OPEN) {
                numBracketCount++;
            } else if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.NUM_CLOSE) {
                numBracketCount--;
            } else if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.DENOM_OPEN) {
                denomBracketCount++;
            } else if (t instanceof Bracket && ((Bracket) t).getType() == Bracket.DENOM_CLOSE) {
                denomBracketCount--;
            }

            if (numBracketCount == 0 && denomBracketCount == 0) { //Cannot be in a numerator or denom
                if (t instanceof Operator && ((Operator) t).getType() == Operator.FRACTION) {
                    ArrayList<Token> num = getNumerator(expression, i);
                    ArrayList<Token> denom = getDenominator(expression, i);
                    //And adds the height of both + 1
                    int height = getMaxFracHeight(num) + getMaxFracHeight(denom);
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
            if (j < 0) {
                String s = printExpression(expression);
                s = "";
            }
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

    private String printExpression(ArrayList<Token> e) {
        String s = "";
        for (Token t : e) {
            s += t.getSymbol();
            if (t instanceof Bracket) {
                Bracket b = (Bracket) t;
                if (b.getType() == Bracket.NUM_OPEN) {
                    s += "[";
                } else if (b.getType() == Bracket.NUM_CLOSE) {
                    s += "]";
                } else if (b.getType() == Bracket.DENOM_OPEN) {
                    s += "{";
                } else if (b.getType() == Bracket.DENOM_CLOSE) {
                    s += "}";
                }
            } else if (t instanceof Operator && ((Operator) t).getType() == Operator.FRACTION) {
                s += "F";
            }

        }
        return s;
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
            if (j >= expression.size()) {
                String s = printExpression(expression);
                s = "";
            }
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
            if (token instanceof Placeholder && ((Placeholder) token).getType() == Placeholder.BLOCK) {
                doNotCountNext = true;
            } else if ((token instanceof Operator && ((Operator) token).getType() == Operator.VARROOT) ||
                    (token instanceof Bracket && ((Bracket) token).getType() == Bracket.SUPERSCRIPT_OPEN) ||
                    (token instanceof Bracket && ((Bracket) token).getType() == Bracket.DENOM_OPEN) ||
                    (token instanceof Operator && ((Operator) token).getType() == Operator.FRACTION)) {
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

            //Changes paint for superscript
            if (scriptLevel > 0) {
                paint = smallPaint;
            } else {
                paint = textPaint;
            }
            //Determines the width of the symbol in text
            float[] widths = new float[token.getSymbol().length()];
            paint.getTextWidths(token.getSymbol(), widths);
            float widthSum = sum(widths);
            x += widthSum;
        }
        drawX.add(x);
    }

    /**
     * Calculates the maximum height of the expression
     */
    private void calculateMaxY() {
        final float maxHeight = getMaxFracHeight(expression) - 1;
        final float yMaxFrac = Y_PADDING_BETWEEN_LINES * (maxHeight + 2);
        final float yMaxScript = SMALL_HEIGHT * getMaxScriptLevel() / 3;
        maxY = yMaxFrac + yMaxScript;
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
        this.setMeasuredDimension(parentWidth, (int) (parentHeight > maxY ? parentHeight : maxY * 1.5));
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
