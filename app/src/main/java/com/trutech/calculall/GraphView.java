package com.trutech.calculall;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

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
    private ArrayList<Token> function;
    private Activity activity;
    private RectF exitRect; //The rectangle of tne exit button
    private Paint redPaint;
    private Paint blackPaint;
    private Paint backgroundPaint;
    private float originX = 0, originY = 0; //The origin of the graph
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
     * @param context  The context which this view will be in
     * @param activity The activity this view will be part of
     * @param function The function to graph
     */
    public GraphView(Context context, Activity activity, ArrayList<Token> function) {
        super(context);

        this.activity = activity;
        this.function = Utility.setupExpression(Utility.condenseDigits(function));
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

    /**
     * Overrides the default Android draw command to manually draw the screen.
     *
     * @param canvas The canvas to draw on
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Draws the background
        canvas.drawRect(0, 0, width, height, backgroundPaint);
        drawExit(canvas);
        drawGraph(canvas);
        drawAxis(canvas);
    }

    /**
     * Main graphing algorithm for the mode. Takes the tokens
     * given and draws it onto the given canvas.
     *
     * @param canvas The canvas to draw on
     */
    private void drawGraph(Canvas canvas) {
        HashMap<Float, Float> points = new HashMap<Float, Float>();
        //Calculates important values that will adjust how the graph would look
        float xRange = upperX - lowerX;
        float xMultiplier = width / xRange;
        float yMultiplier = (float) (height / (upperY - lowerY));

        //Plots the 100 points across the screen
        for (int i = 0; i < NUM_OF_POINTS; i++) {
            float x = lowerX + i * (xRange / NUM_OF_POINTS);
            float y = (float) Utility.valueAt(function, x);
            points.put(x, y);
        }

        //Now draws a line in between each point
        for (int i = 0; i < NUM_OF_POINTS; i++) {
            float startX = lowerX + i * (xRange / NUM_OF_POINTS);
            float endX = lowerX + (i + 1) * (xRange / NUM_OF_POINTS);
            Float startY = points.get(startX);
            Float endY = points.get(endX);
            if (startY != null && endY != null) { //If both the start and end points exists
                if (startY != Integer.MAX_VALUE && endY != Integer.MAX_VALUE
                        && startY > lowerY - Math.abs(lowerY * 0.1) && endY < upperY + Math.abs(upperY * 0.1)) { //Does not graph discontinuities or outside the screen
                    //Adjusts the x and y values according to the display
                    startX = (startX - lowerX) * xMultiplier;
                    endX = (endX - lowerX) * xMultiplier;
                    startY = (startY - lowerY) * yMultiplier;
                    endY = (endY - lowerY) * yMultiplier;
                    canvas.drawLine(startX, height - startY, endX, height - endY, blackPaint);
                }
            }
        }
        //Saves the origin coordinate
        originX = -lowerX * xMultiplier;
        originY = -lowerY * yMultiplier;
    }


    /**
     * Finds the x values in the functions in which there will be
     * discontinuities. This will return a list of points, where the
     * first index is the x value where the discontinuity is located,
     * and the second is either Float.NEGATIVE_INFINITY or Float.POSITIVE_INFINITY
     * for asymptotes, and Float.NaN for holes.
     *
     * @return A list of points where discontinuities are found
     */
    private ArrayList<float[][]> findDiscontinuities(ArrayList<Token> function) {
        return null;
    }

    /**
     * Draws the axises for the graph. This will draw the x and y axises
     * as well as the incrementations along it.
     *
     * @param canvas The canvas to draw on
     */
    private void drawAxis(Canvas canvas) {
        //X axis
        canvas.drawLine(0, height - originY, width, height - originY, redPaint);
        //Y axis
        canvas.drawLine(originX, 0, originX, height, redPaint);
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
        activity.setContentView(R.layout.activity_function);
        ((FunctionMode) activity).updateInput();
        ((FunctionMode) activity).setupButtons();
    }


}
