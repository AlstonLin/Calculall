/*
 * Copyright (c) 2016 TruTech Innovations Inc - All Rights Reserved
 */

package com.trutechinnovations.calculall;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Entry point to the application as well as the only Activity. Sets
 * up the fragments and the entry point for UI events.
 *
 * @author Alston Lin, David Liu
 * @version 3.0
 */
public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener { //, MoPubInterstitial.InterstitialAdListener {

    public static final int BASIC = 0, ADVANCED = 1, FUNCTION = 2, VECTOR = 3, MATRIX = 4;
    //Fragment Objects
    public static final int AD_RATE = 2; //Ads will show 1 in 2 activity opens
    private static final String AD_ID = "3ae32e9f72e2402cb01bbbaf1d6ba1f4";
    private static final String TOKENS_FILENAME = "tokens";
    private static final int VIRBRATE_DURATION = 17;
    //Display Objects
    protected DisplayView display;
    protected OutputView output;
    private ViewPager mPager;
    private boolean showAd = false;
    private android.support.v4.app.FragmentManager mg = getSupportFragmentManager();
    private boolean feedbackOn, autocalculateOn;
    private int lastMode;
    private int fontSize;
    private int currentTheme = -1;
    //For temporarily storing expressions between modes
    private ArrayList<Token> basicExpr, advancedExpr, functionExpr, vectorExpr, matrixExpr;
    //private MoPubInterstitial interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int tmpTheme = ThemeHelper.setUpTheme(this,false);
        setContentView(R.layout.frame);
        //Sets up the fragments
        if (savedInstanceState != null) {
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //Initiates the ArrayLists for temporary storage
        basicExpr = new ArrayList<>();
        advancedExpr = new ArrayList<>();
        functionExpr = new ArrayList<>();
        vectorExpr = new ArrayList<>();
        matrixExpr = new ArrayList<>();
    }

    /**
     * Sets up the settings from preferences.
     */
    private void setupSettings() {
        SharedPreferences pref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        //Retrieves the defailt values from Preferences
        feedbackOn = pref.getBoolean(getString(R.string.haptic), SettingsActivity.DEFAULT_FEEDBACK);
        autocalculateOn = pref.getBoolean(getString(R.string.autocalculate), SettingsActivity.AUTOCALCULATE_ON);
        fontSize = pref.getInt(getString(R.string.font_size), SettingsActivity.DEFAULT_FONT_SIZE);
        int roundTo = pref.getInt(getString(R.string.round_to), SettingsActivity.DEFAULT_ROUND);

        //Sets the decimal rounding
        Number.roundTo = roundTo;
        //Sets the font sizes
        display.setFontSize(fontSize);

    }

    public void onPause() {
        //Saves mode
        SharedPreferences pref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(getString(R.string.mode_key), mPager.getCurrentItem());
        editor.apply();
        //Saves tokens
        try {
            FileOutputStream outStream = openFileOutput(TOKENS_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream objectStreamOut = new ObjectOutputStream(outStream);
            objectStreamOut.writeObject(display.getExpression());
            objectStreamOut.flush();
            objectStreamOut.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        mPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new ModeFragmentPagerAdapter(mg);
        mPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.modes);
        tabLayout.setupWithViewPager(mPager);

        mPager.addOnPageChangeListener(this);
        //Sets up the display
        output = (OutputView) findViewById(R.id.output);
        display = (DisplayView) findViewById(R.id.display);
        display.setOutput(output);
        //Sets up the Modes
        Basic.getInstance().setActivity(this);
        Advanced.getInstance().setActivity(this);
        FunctionMode.getInstance().setActivity(this);
        VectorMode.getInstance().setActivity(this);
        MatrixMode.getInstance().setActivity(this);
        mPager.setOffscreenPageLimit(5);
        //Resumes last mode and tokens
        loadFromPrevious();
        setupSettings();
        int tmpTheme = ThemeHelper.setUpTheme(this,false);
        if(currentTheme!=-1 && tmpTheme!=currentTheme){
            finish();
            startActivity(getIntent());
        }
        currentTheme = tmpTheme;
        //In case the expressions beccome null
        if (basicExpr == null) {
            basicExpr = new ArrayList<>();
            advancedExpr = new ArrayList<>();
            functionExpr = new ArrayList<>();
            vectorExpr = new ArrayList<>();
            matrixExpr = new ArrayList<>();
        }
    }

    /**
     * Loads previously saved modes and tokens
     */
    private void loadFromPrevious() {
        //Mode
        SharedPreferences pref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        lastMode = pref.getInt(getString(R.string.mode_key), 0);
        mPager.setCurrentItem(lastMode);
        if (display.getExpression().size()==0) {
            //Tokens
            try {
                FileInputStream stream = openFileInput(TOKENS_FILENAME);
                ObjectInputStream objectStream = new ObjectInputStream(stream);
                ArrayList<Token> tokens = (ArrayList<Token>) objectStream.readObject();
                display.displayInput(tokens);
                objectStream.close();
                stream.close();
                //Now sets the tokens
                switch (lastMode) {
                    case BASIC:
                        Basic.getInstance().setTokens(tokens);
                        break;
                    case ADVANCED:
                        Advanced.getInstance().setTokens(tokens);
                        break;
                    case FUNCTION:
                        FunctionMode.getInstance().setTokens(tokens);
                        break;
                    case VECTOR:
                        VectorMode.getInstance().setTokens(tokens);
                        break;
                    case MATRIX:
                        MatrixMode.getInstance().setTokens(tokens);
                        break;
                }
            } catch (ClassNotFoundException | IOException ignored) {
            }
        }
    }

    /**
     * When the settings button has been pressed.
     *
     * @param v Not Used
     */
    public void clickSettings(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("Mode", mPager.getCurrentItem());
        startActivity(intent);
    }

    /**
     * Moves the cursor on the display left if possible.
     */
    public void scrollLeft(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        if (display != null) {
            display.scrollLeft();
        }
    }

    /**
     * Moves the cursor on the display right if possible.
     */
    public void scrollRight(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        if (display != null) {
            display.scrollRight();
        }
    }

    /**
     * Scrolls down the display (if possible).
     */
    protected void scrollDown() {
        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll);
        if (scrollView != null) {
            //Shows bottom
            scrollView.pageScroll(ScrollView.FOCUS_DOWN);
        }
    }

    /**
     * The entry point for all button clicks.
     *
     * @param v The Button Clicked
     */
    public void onClick(View v) {
        int mode = mPager.getCurrentItem();
        switch (mode) {
            case 0:
                Basic.getInstance().onClick(v);
                break;
            case 1:
                Advanced.getInstance().onClick(v);
                break;
            case 2:
                FunctionMode.getInstance().onClick(v);
                break;
            case 3:
                VectorMode.getInstance().onClick(v);
                break;
            case 4:
                MatrixMode.getInstance().onClick(v);
                break;
            default:
                throw new IllegalArgumentException("The current pager item index could not be handled");
        }
        //Haptic Feedback
        if (feedbackOn) {
            // Get instance of Vibrator from current Context
            Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            mVibrator.vibrate(VIRBRATE_DURATION);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position != lastMode) {
            //Saves the expression to the corrosponding list
            switch (lastMode) {
                case BASIC:
                    basicExpr = Basic.getInstance().getTokens();
                    break;
                case ADVANCED:
                    advancedExpr = Advanced.getInstance().getTokens();
                    break;
                case FUNCTION:
                    functionExpr = FunctionMode.getInstance().getTokens();
                    break;
                case VECTOR:
                    vectorExpr = VectorMode.getInstance().getTokens();
                    break;
                case MATRIX:
                    matrixExpr = MatrixMode.getInstance().getTokens();
                    break;
            }
            display.displayInput(new ArrayList<Token>()); //Changes the reference to a different, blank ArrayList
            display.displayOutput(new ArrayList<Token>());
            lastMode = position;
        }

        switch (position) {
            case BASIC:
                display.displayInput(basicExpr);
                Basic.getInstance().setTokens(basicExpr);
                break;
            case ADVANCED:
                display.displayInput(advancedExpr);
                Advanced.getInstance().setTokens(advancedExpr);
                break;
            case FUNCTION:
                display.displayInput(functionExpr);
                FunctionMode.getInstance().setTokens(functionExpr);
                break;
            case VECTOR:
                display.displayInput(vectorExpr);
                VectorMode.getInstance().setTokens(vectorExpr);
                break;
            case MATRIX:
                display.displayInput(matrixExpr);
                MatrixMode.getInstance().setTokens(matrixExpr);
                break;
            default:
                throw new IllegalArgumentException("The current pager item index could not be handled");
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public DisplayView getDisplay() {
        return display;
    }

    public int getFontSize() {
        return fontSize;
    }

    public boolean isAutocalculateOn() {
        return autocalculateOn;
    }

    public void setShowAd(boolean showAd) {
        this.showAd = showAd;
    }

    @Override
    public void onBackPressed() {
        PopupWindow historyPw = Basic.historyWindow;
        PopupWindow functionPw = ((FunctionMode) FunctionMode.getInstance()).getPw();
        PopupWindow elementPw = ((MatrixMode) MatrixMode.getInstance()).getElementWindow();
        PopupWindow elementsPw = ((MatrixMode) MatrixMode.getInstance()).getElementsWindow();
        if (historyPw != null && historyPw.isShowing()) {
            historyPw.dismiss();
        } else if (functionPw != null && functionPw.isShowing()) {
            functionPw.dismiss();
        } else if (elementPw != null && elementPw.isShowing()) {
            elementPw.dismiss();
        } else if (elementsPw != null && elementsPw.isShowing()) {
            elementsPw.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */

    class ModeFragmentPagerAdapter extends FragmentPagerAdapter{

        private @StringRes int[] title = new int[]{
                R.string.basic,
                R.string.advanced,
                R.string.function,
                R.string.vector,
                R.string.matrix
        };

        public ModeFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new BasicFragment();
            } else if (position == 1) {
                return new AdvancedFragment();
            } else if (position == 2) {
                return new FunctionFragment();
            } else if (position == 3) {
                return new VectorFragment();
            } else {
                return new MatrixFragment();
            }
        }

        @Override
        public int getCount() {
            return title.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(title[position]);
        }
    }
}