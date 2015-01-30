package com.trutech.calculall;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Custom View that will be shown when graphing.
 *
 * @author Alston Lin
 * @version 0.9.0
 */
public class GraphView extends View {

    private static final int NUM_OF_POINTS = 10000;
    private PopupWindow popupWindow;
    private ArrayList<Token> function;
    private RectF exitRect; //The rectangle of tne exit button
    private Paint redPaint;
    private Paint blackPaint;
    private Paint backgroundPaint;
    private double originX = 0, originY = 0; //The origin of the graph
    private int width;
    private int height;
    //The bounds for the graph
    private int lowerX = -50;
    private int lowerY = -30;
    private int upperX = 50;
    private int upperY = 30;

    private OnTouchListener listener = new OnTouchListener() {

        /**
         * Whenever a touch occurs, this is the method that will be called.
         *
         * @param v The view that was touched (should be this)
         * @param event Information regarding the touch
         * @return If the touch was consumed (if false, Android will use its default touch
         * reactions)
         */
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            //Checks if user touched the exit Button
            if (exitRect.contains(x, y)) {
                exit();
            }
            return true;
        }
    };


    /**
     * Constructor for a Graph View, a custom view with the sole purpose
     * of graphing a function.
     *
     * @param context The context which this view will be in
     * @param attrs   The attribute set
     */
    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //Sets up paints
        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setColor(Color.RED);
        blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blackPaint.setColor(Color.BLACK);
        blackPaint.setTextSize(52);
        blackPaint.setFakeBoldText(true);
        blackPaint.setStrokeWidth(3);
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.FILL);
        width = getWidth();
        height = getHeight();
        setOnTouchListener(listener);
    }

    public void setFunction(ArrayList<Token> function) {
        this.function = Utility.setupExpression(Utility.condenseDigits(function));
    }

    public void setPopupWindow(PopupWindow popupWindow) {
        this.popupWindow = popupWindow;
    }

    /**
     * Overrides the default Android draw command to manually draw the screen.
     *
     * @param canvas The canvas to draw on
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (function != null) {
            //Draws the background
            canvas.drawRect(0, 0, width, height, backgroundPaint);
            drawExit(canvas);
            drawGraph(canvas);
            drawAxis(canvas);
        }
    }

    /**
     * Main graphing algorithm for the mode. Takes the tokens
     * given and draws it onto the given canvas.
     *
     * @param canvas The canvas to draw on
     */
    private void drawGraph(Canvas canvas) {
        final int NULL = 0, POSITIVE = 1, NEGATIVE = 2;

        ArrayList<Double> xValues = new ArrayList<>();
        ArrayList<Double> yValues = new ArrayList<>();

        //Calculates important values that will adjust how the graph would look
        double xRange = upperX - lowerX;
        double xMultiplier = width / xRange;
        double yMultiplier = (float) (height / (upperY - lowerY));

        //Plots the 100 points across the screen
        for (int i = 0; i <= NUM_OF_POINTS; i++) {
            double x = lowerX + i * (xRange / NUM_OF_POINTS);
            double y = Utility.valueAt(function, x);
            xValues.add(x);
            yValues.add(y);
        }

        //Keeps tracks on the slopes
        ArrayList<Double> slopes = findSlopes(yValues, lowerX, xRange / NUM_OF_POINTS);
        int beforeLastSlopePositive = NULL;
        int lastSlopePositive = NULL;
        //Now draws a line in between each point
        for (int i = 0; i < NUM_OF_POINTS; i++) {
            Double startX = xValues.get(i);
            Double endX = xValues.get(i + 1);
            Double startY = yValues.get(i);
            Double endY = yValues.get(i + 1);
            int slopeIsPositive;
            if (startY != Integer.MAX_VALUE && endY != Integer.MAX_VALUE) { //Does not graph points that DNE or outside the screen
                slopeIsPositive = i + 1 >= slopes.size() ? NULL : slopes.get(i + 1) > 0 ? POSITIVE : NEGATIVE; //Checks after
                //Checks for conditions for asympotes: +-+ or -+- slope
                boolean hasAsymptote = (beforeLastSlopePositive == POSITIVE && lastSlopePositive == NEGATIVE && slopeIsPositive == POSITIVE) ||
                        (beforeLastSlopePositive == NEGATIVE && lastSlopePositive == POSITIVE && slopeIsPositive == NEGATIVE);
                if (!hasAsymptote) {
                    startX = (startX - lowerX) * xMultiplier;
                    endX = (endX - lowerX) * xMultiplier;
                    startY = (startY - lowerY) * yMultiplier;
                    endY = (endY - lowerY) * yMultiplier;
                    canvas.drawLine(startX.floatValue(), height - startY.floatValue(), endX.floatValue(), (float)(height - endY), blackPaint);
                } else {
                    slopeIsPositive = NULL;
                }
            } else { //Either does not matter or points DNE
                slopeIsPositive = NULL;
            }
            beforeLastSlopePositive = lastSlopePositive;
            lastSlopePositive = slopeIsPositive;
            //Saves the origin coordinate
            originX = -lowerX * xMultiplier;
            originY = -lowerY * yMultiplier;
        }
    }


    /**
     * Finds th slopes in between each points on the given list.
     *
     * @param yValues    The list of y values
     * @param lowerX     The starting X value
     * @param increments Distance between each point
     * @return The slopes in between the points
     */
    private ArrayList<Double> findSlopes(ArrayList<Double> yValues, double lowerX, double increments) {
        ArrayList<Double> slopes = new ArrayList<>();
        double startX = lowerX;
        for (int i = 0; i < NUM_OF_POINTS; i++) {
            double endX = lowerX + (i + 1) * increments;
            Double startY = yValues.get(i);
            Double endY = yValues.get(i + 1);
            double slope = Integer.MAX_VALUE; //Default value of MAX_VALUE
            if (startY != Integer.MAX_VALUE && endY != Integer.MAX_VALUE) { //Both points exists
                slope = (endY - startY) / (endX - startX);
            }
            slopes.add(slope);
            startX = endX;
        }
        return slopes;
    }


    /**
     * Draws the axises for the graph. This will draw the x and y axises
     * as well as the incrementations along it.
     *
     * @param canvas The canvas to draw on
     */
    private void drawAxis(Canvas canvas) {
        //X axis
        canvas.drawLine(0, height - (float)originY, width, height - (float)originY, redPaint);
        //Y axis
        canvas.drawLine((float)originX, 0, (float)originX, height, redPaint);
    }

    /**
     * Draws the exit rectangle.
     *
     * @param canvas The canvas to draw on
     */
    private void drawExit(Canvas canvas) {
        //Exit rectangle
        exitRect = new RectF(width / 1.1f, 0, width, height / 20f);
        canvas.drawRect(exitRect, backgroundPaint); //Invisible rectangle; only used to sense touches
        canvas.drawText("X", width / 1.065f, height / 25f, blackPaint);
    }

    /**
     * Called by Android whenver the screen size changes
     *
     * @param w    New screen width
     * @param h    New screen height
     * @param oldw Old screen width
     * @param oldh Old screen height
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
    }

    /**
     * Exits the graphing window.
     */
    private void exit() {
        popupWindow.dismiss();
    }

}
