package com.trutechinnovations.calculall;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by david on 1/15/2017.
 */

public class ThemeHelper {

    public final static int BLACK_AND_YELLOW = 2;
    public final static int DEFAULT_THEME = 2;
    public final static int DAVID = 1;
    public final static int PURPLE = 3;
    public final static int BLUE = 4;
    public final static int BLUEGREEN = 5;
    public final static int DONATE = 6;

    public static int setUpTheme(Context context, boolean appbar){
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.preference_key), Context.MODE_PRIVATE);
        int currentTheme = pref.getInt(context.getString(R.string.theme), 2);
        //Sets up the current theme
        switch (currentTheme) {
            case DAVID:
                context.setTheme(appbar?
                        R.style.AppTheme_David:
                        R.style.AppThemeNoActionBar_David);
                return DAVID;
            case PURPLE:
                context.setTheme(appbar?
                        R.style.AppTheme_Purple:
                        R.style.AppThemeNoActionBar_Purple);
                return PURPLE;
            case BLUE:
                context.setTheme(appbar?
                        R.style.AppTheme_Blue:
                        R.style.AppThemeNoActionBar_Blue);
                return BLUE;
            case BLUEGREEN:
                context.setTheme(appbar?
                        R.style.AppTheme_BlueGreen:
                        R.style.AppThemeNoActionBar_BlueGreen);
                return BLUEGREEN;
            case DONATE:
                context.setTheme(appbar?
                        R.style.AppTheme_Donate:
                        R.style.AppThemeNoActionBar_Donate);
                return DONATE;
            default:
                context.setTheme(appbar?
                        R.style.AppTheme_BlackAndYellow:
                        R.style.AppThemeNoActionBar_BlackAndYellow);
                return BLACK_AND_YELLOW;
        }
        
    }
}
