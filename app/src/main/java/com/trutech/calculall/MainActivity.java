package com.trutech.calculall;

import android.os.Bundle;
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

/**
 * Entry point to the application as well as the only Activity. Sets
 * up the fragments and the entry point for UI events.
 *
 * @version Alpha 2.0
 * @author Alston Lin, David Liu
 */
public class MainActivity extends FragmentActivity{

    //Fragment Objects
    private static final int NUM_PAGES = 5;
    private ViewPager mPager;
    private android.support.v4.app.FragmentManager mg = getSupportFragmentManager();
    //Display Objects
    protected DisplayView display;
    protected OutputView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.frame);
        //Sets up the fragments
        if (savedInstanceState != null) {
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(mg);
        mPager.setAdapter(mPagerAdapter);
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
     * SMoves the cursor on the display right if possible.
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
    public void onClick(View v){
        int mode = mPager.getCurrentItem();
        switch(mode){
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
            if (position == 0){
                return new BasicFragment();
            }else if (position == 1){
                return new AdvancedFragment();
            }else if (position == 2){
                return new FunctionFragment();
            }else if (position == 3){
                return new VectorFragment();
            }else {
                return new MatrixFragment();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public DisplayView getDisplay(){
        return display;
    }

}
