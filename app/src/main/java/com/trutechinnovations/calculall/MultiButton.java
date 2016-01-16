/*
 * Copyright (c) 2016 TruTech Innovations Inc - All Rights Reserved
 */

package com.trutechinnovations.calculall;

import android.content.Context;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * A Button that will have multiple functions.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class MultiButton extends Button {

    //Constants
    public static int NORMAL = 0, SECOND = 1, HYP = 2, SECONDHYP = 3;
    //Fields
    private int mode = 0;
    private Spanned[] modeTexts;
    private Command<Void, Void>[] onClicks;

    public MultiButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Creates a new Button capable of switching between two or four texts and onClick results.
     *
     * @param context   The context the Button is being created in
     * @param modeTexts An array of either size 2 or 4 of which the Button will display
     * @param onClicks  What function would be called for each respective mode (size 2 or 4)
     */
    public MultiButton(Context context, Spanned[] modeTexts, Command<Void, Void>[] onClicks) {
        super(context);
        this.modeTexts = modeTexts;
        this.onClicks = onClicks;
        setText(modeTexts[mode]);
    }

    /**
     * Changes the mode and text of this button.
     *
     * @param second If 2nd is toggled
     * @param hyp    If hyp is toggled
     */
    public void changeMode(boolean second, boolean hyp) {
        if (second && hyp) {
            if (modeTexts.length == 4) {
                mode = SECONDHYP;
            } else {
                mode = SECOND;
            }
        } else if (second) {
            mode = SECOND;
        } else if (hyp) {
            if (modeTexts.length == 4) {
                mode = HYP;
            } else {
                mode = NORMAL;
            }
        } else {
            mode = NORMAL;
        }
        setText(modeTexts[mode]);
    }

    /**
     * Call this method when the Button is clicked.
     */
    public void onClick() {
        onClicks[mode].execute(null);
    }

    public void setModeTexts(Spanned[] modeTexts) {
        this.modeTexts = modeTexts;
    }

    public void setOnClicks(Command<Void, Void>[] onClicks) {
        this.onClicks = onClicks;
    }

}
