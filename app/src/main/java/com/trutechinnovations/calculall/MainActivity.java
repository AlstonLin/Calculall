package com.trutechinnovations.calculall;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.ToggleButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 import com.mopub.mobileads.MoPubErrorCode;
 import com.mopub.mobileads.MoPubInterstitial;
 **/

/**
 * Entry point to the application as well as the only Activity. Sets
 * up the fragments and the entry point for UI events.
 *
 * @author Alston Lin, David Liu
 * @version Alpha 2.0
 */
public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener { //, MoPubInterstitial.InterstitialAdListener {

    //Fragment Objects
    public static final int AD_RATE = 2; //Ads will show 1 in 2 activity opens
    private static final String AD_ID = "3ae32e9f72e2402cb01bbbaf1d6ba1f4";
    private static final String TOKENS_FILENAME = "tokens";
    private static final int NUM_PAGES = 5;
    private static final int VIRBRATE_DURATION = 17;
    //Display Objects
    protected DisplayView display;
    protected OutputView output;
    private ViewPager mPager;
    private boolean showAd = false;
    private android.support.v4.app.FragmentManager mg = getSupportFragmentManager();
    private boolean feedbackOn;
    private int roundTo;
    private int lastMode;
    private int fontSize;
    private boolean swipeOnly;
    //private MoPubInterstitial interstitial;
    private int currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setupThemes();
        setContentView(R.layout.frame);
        //Sets up the fragments
        if (savedInstanceState != null) {
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    }


    /**
     * Sets up the settings from preferences.
     */
    private void setupSettings() {
        SharedPreferences pref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        feedbackOn = pref.getBoolean(getString(R.string.haptic), SettingsActivity.DEFAULT_FEEDBACK);
        roundTo = pref.getInt(getString(R.string.round_to), SettingsActivity.DEFAULT_ROUND);
        fontSize = pref.getInt(getString(R.string.font_size), SettingsActivity.DEFAULT_FONT_SIZE);
        swipeOnly = pref.getBoolean(getString(R.string.mode_switch), SettingsActivity.DEFAULT_SWIPE);
        int theme = pref.getInt(getString(R.string.theme), SettingsActivity.DEFAULT_THEME);
        //Sets the decimal rounding
        Number.roundTo = roundTo;
        //Sets the font sizes
        display.setFontSize(fontSize);
        HistoryView.fontSize = fontSize;
        //Checks if the Theme has changes
        if (theme != currentTheme) {
            //Needs to restart the activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        ToggleButton basic = (ToggleButton) findViewById(R.id.basic_button);
        ToggleButton advanced = (ToggleButton) findViewById(R.id.advanced_button);
        ToggleButton function = (ToggleButton) findViewById(R.id.function_button);
        ToggleButton vector = (ToggleButton) findViewById(R.id.vector_button);
        basic.setEnabled(!swipeOnly);
        advanced.setEnabled(!swipeOnly);
        function.setEnabled(!swipeOnly);
        vector.setEnabled(!swipeOnly);
    }

    /**
     * Sets the current theme.
     */
    private void setupThemes() {
        SharedPreferences pref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        currentTheme = pref.getInt(getString(R.string.theme), SettingsActivity.DEFAULT_THEME);
        //Sets Theme
        switch (currentTheme) {
            case SettingsActivity.DAVID:
                setTheme(R.style.Theme1);
                break;
            case SettingsActivity.ALSTON:
                setTheme(R.style.Theme1);
                break;
            case SettingsActivity.PANDA:
                setTheme(R.style.Theme1);
                break;
            case SettingsActivity.TRAILBLAZER:
                setTheme(R.style.Theme1);
                break;
            case SettingsActivity.HAWKS:
                setTheme(R.style.Theme1);
                break;
            case SettingsActivity.GEESE:
                setTheme(R.style.Theme1);
                break;
            case SettingsActivity.SUNSET:
                setTheme(R.style.Theme1);
                break;
            case SettingsActivity.FOREST:
                setTheme(R.style.Theme1);
                break;
            case SettingsActivity.MATERIAL:
                setTheme(R.style.Theme1);
                break;
            case SettingsActivity.OCEAN:
                setTheme(R.style.Theme1);
                break;
            default:
                throw new IllegalStateException("Illegal Theme");
        }
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
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(mg);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(this);
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
    }

    /**
     * Loads previously saved modes and tokens
     */
    private void loadFromPrevious() {
        //Mode
        SharedPreferences pref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        lastMode = pref.getInt(getString(R.string.mode_key), 0);
        mPager.setCurrentItem(lastMode);
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
                case 0:
                    Basic.getInstance().setTokens(tokens);
                    break;
                case 1:
                    Advanced.getInstance().setTokens(tokens);
                    break;
                case 2:
                    FunctionMode.getInstance().setTokens(tokens);
                    break;
                case 3:
                    VectorMode.getInstance().setTokens(tokens);
                    break;
                case 4:
                    MatrixMode.getInstance().setTokens(tokens);
                    break;
            }
        } catch (ClassNotFoundException | IOException ignored) {
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

    /**
     * When the user wants to change to Basic Mode.
     *
     * @param v Not Used
     */
    public void clickBasic(View v) {
        mPager.setCurrentItem(0);
    }

    /**
     * When the user wants to change to Advanced Mode.
     *
     * @param v Not Used
     */
    public void clickAdvanced(View v) {
        mPager.setCurrentItem(1);
    }

    /**
     * When the user wants to change to Function Mode.
     *
     * @param v Not Used
     */
    public void clickFunction(View v) {
        mPager.setCurrentItem(2);
    }

    /**
     * When the user wants to change to Vector Mode.
     *
     * @param v Not Used
     */
    public void clickVector(View v) {
        mPager.setCurrentItem(3);
    }

    /**
     * When the user wants to change to Vector Mode.
     *
     * @param v Not Used
     */
    public void clickMatrix(View v) {
        mPager.setCurrentItem(4);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position != lastMode) {
            display.clear();
            lastMode = position;
        }
        ToggleButton basic = (ToggleButton) findViewById(R.id.basic_button);
        ToggleButton advanced = (ToggleButton) findViewById(R.id.advanced_button);
        ToggleButton function = (ToggleButton) findViewById(R.id.function_button);
        ToggleButton vector = (ToggleButton) findViewById(R.id.vector_button);
        ToggleButton matrix = (ToggleButton) findViewById(R.id.matrix_button);

        switch (position) {
            case 0:
                basic.setChecked(true);
                advanced.setChecked(false);
                function.setChecked(false);
                vector.setChecked(false);
                matrix.setChecked(false);
                break;
            case 1:
                basic.setChecked(false);
                advanced.setChecked(true);
                function.setChecked(false);
                vector.setChecked(false);
                matrix.setChecked(false);
                break;
            case 2:
                basic.setChecked(false);
                advanced.setChecked(false);
                function.setChecked(true);
                vector.setChecked(false);
                matrix.setChecked(false);
                break;
            case 3:
                basic.setChecked(false);
                advanced.setChecked(false);
                function.setChecked(false);
                vector.setChecked(true);
                matrix.setChecked(false);
                break;
            case 4:
                basic.setChecked(false);
                advanced.setChecked(false);
                function.setChecked(false);
                vector.setChecked(false);
                matrix.setChecked(true);
                break;
            default:
                throw new IllegalArgumentException("The current pager item index could not be handled");
        }
        /**
        //Possibly shows ads
        if (interstitial != null) {
            interstitial.destroy(); //Prevents Ads from other activities appearing if it is not loaded before switching between them
        }
        if (position != 0 && showAd) { //Deos not show ads in Basic
            Random random = new Random();
            if (random.nextInt(AD_RATE) == 0) {
                // Create the interstitial.
                interstitial = new MoPubInterstitial(this, AD_ID);
                interstitial.setInterstitialAdListener(this);
                interstitial.load();
                interstitial.load();
            }
        }
         **/
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

    public void setShowAd(boolean showAd) {
        this.showAd = showAd;
    }

    /**
    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        showAd = false;
        if (interstitial.isReady()) {
            interstitial.show();
        }
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {

    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {

    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {

    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {

    }
     **/

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm) {
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
            return NUM_PAGES;
        }
    }
}