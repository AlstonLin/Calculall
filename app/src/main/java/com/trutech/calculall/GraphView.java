package com.trutech.calculall;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

/**
 * A Custom View that will be shown when graphing.
 *
 * @author Alston Lin
 * @version 0.9.0
 */
public class GraphView extends View {

    private Activity activity;
    private RectF exitRect; //The rectangle of tne exit button
    private Paint redPaint;
    private Paint blackPaint;
    private int width;
    private int height;

    private OnTouchListener listener = new OnTouchListener(){

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
            if (exitRect.contains(x, y)){
                exit();
            }
            return true;
        }
    };

    /**
     * Constructor for a Graph View, a custom view with the sole purpose
     * of graphing a function.
     * @param context The context which this view will be in
     * @param activity The activity this view will be part of
     */
    public GraphView(Context context, Activity activity) {
        super(context);
        this.activity = activity;
        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setColor(Color.RED);
        blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blackPaint.setColor(Color.BLACK);
        blackPaint.setTextSize(52);
        blackPaint.setFakeBoldText(true);
        width = getWidth();
        height = getHeight();
        setOnTouchListener(listener);
    }

    /**
     * Overrides the default Android draw command to manually draw the screen.
     *
     * @param canvas The canvas to draw on
     */
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        exitRect = new RectF(width / 1.08f, height / 95f, width / 1.01f, height / 20f);
        canvas.drawRect(exitRect, redPaint);
        canvas.drawText("X", width / 1.065f, height / 25f, blackPaint);

    }

    /**
     * Called by Android whenver the screen size changes
     *
     * @param w New screen width
     * @param h New screen height
     * @param oldw Old screen width
     * @param oldh Old screen height
     */
    protected void onSizeChanged (int w, int h, int oldw, int oldh){
        width = w;
        height = h;
    }

    /**
     * Exits the graphing window.
     */
    private void exit() {
        activity.setContentView(R.layout.activity_function);
        ((FunctionMode)activity).updateInput();
    }


}
