/*
 * Copyright (c) 2016 TruTech Innovations Inc - All Rights Reserved
 */

package com.trutechinnovations.calculall;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * The activity for the basic calculator mode. The basic mode will only be able to
 * perform the four operations (add, subtract, multiply and divide) including brackets.
 * This class contains all the back-end of the Basic Mode.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class Basic implements View.OnClickListener {

    //CONSTANTS
    public static final int HISTORY_SIZE = 25;
    private static final Basic INSTANCE = new Basic();
    //PROTECTED VARIABLES
    protected static PopupWindow historyWindow;
    protected ArrayList<Token> tokens = new ArrayList<Token>(); //Tokens shown on screen
    protected DisplayView display;
    protected MainActivity activity;
    protected boolean changedTokens = false;
    protected String filename = "history_basic";
    protected Fragment fragment;
    private String reference = "";

    /**
     * Makes sure that an instance of Basic cannot be created from outside the class.
     */
    protected Basic() {
        super();
    }

    /**
     * Allows for the Singleton pattern so there would be only one instance.
     *
     * @return The singleton instance
     */
    public static Basic getInstance() {
        return INSTANCE;
    }

    /**
     * Sets that this Mode will run on.
     *
     * @param activity The running activity
     */
    public void setActivity(MainActivity activity) {
        this.activity = activity;
        display = activity.getDisplay();
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    /**
     * When a Button has been clicked, calls the appropriate method.
     *
     * @param v The Button that has been clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.one_button:
                clickOne();
                break;
            case R.id.two_button:
                clickTwo();
                break;
            case R.id.three_button:
                clickThree();
                break;
            case R.id.four_button:
                clickFour();
                break;
            case R.id.five_button:
                clickFive();
                break;
            case R.id.six_button:
                clickSix();
                break;
            case R.id.seven_button:
                clickSeven();
                break;
            case R.id.eight_button:
                clickEight();
                break;
            case R.id.nine_button:
                clickNine();
                break;
            case R.id.zero_button:
                clickZero();
                break;
            case R.id.decimal_button:
                clickDecimal();
                break;
            case R.id.sqrt_button:
                clickSqrt();
                break;
            case R.id.negative_button:
                clickNegative();
                break;
            case R.id.back_button:
                clickBack();
                break;
            case R.id.clear_button:
                clickClear();
                break;
            case R.id.equals_button:
                clickEquals();
                return; //Does not want to updateInput()
            case R.id.add_button:
                clickAdd();
                break;
            case R.id.subtract_button:
                clickSubtract();
                break;
            case R.id.multiply_button:
                clickMultiply();
                break;
            case R.id.history_button:
                clickHistory();
                break;
            case R.id.divide_button:
                clickDivide();
                break;
            default: //Button has not been handled!
                throw new UnsupportedOperationException("A Button has not been handled!");
        }
        updateInput();
    }

    /**
     * When the user clicks the History button.
     */
    public void clickHistory() {
        openHistory(filename);
    }

    /**
     * Exits the history view.
     */
    public void clickExit() {
        historyWindow.dismiss();
    }

    /**
     * When the user presses the 1 Button.
     */
    public void clickOne() {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeOne());
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the 2 Button.
     */
    public void clickTwo() {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeTwo());
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the 3 Button.
     */
    public void clickThree() {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeThree());
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the 4 Button.
     */
    public void clickFour() {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeFour());
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the 5 Button.
     */
    public void clickFive() {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeFive());
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the 6 Button.
     */
    public void clickSix() {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeSix());
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the 7 Button.
     */
    public void clickSeven() {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeSeven());
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the 8 Button.
     */
    public void clickEight() {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeEight());
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the 9 Button.
     */
    public void clickNine() {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeNine());
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the 0 Button.
     */
    public void clickZero() {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeZero());
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the . Button.
     */
    public void clickDecimal() {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeDecimal());
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the x Button.
     */
    public void clickMultiply() {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeMultiply());
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the / Button.
     */
    public void clickDivide() {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeDivide());
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the + Button.
     */
    public void clickAdd() {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeAdd());
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the - Button.
     */
    public void clickSubtract() {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeSubtract());
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the sqrt Button
     */
    public void clickSqrt() {
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeSqrt());
        display.setCursorIndex(display.getCursorIndex() + 1);
    }

    /**
     * When the user presses the clear Button.
     */
    public void clickClear() {
        tokens.clear();
        updateInput();
        changedTokens = true; //used to know if the button has been used
        display.displayOutput(new ArrayList<Token>());
        display.reset();
    }

    /**
     * When the user presses the back Button.
     */
    public void clickBack() {
        if (tokens.isEmpty()) {
            return; //Prevents a bug
        }

        if (display.getCursorIndex() - 1 < 0) {
            return;
        }

        Token toRemove = tokens.get(display.getRealCursorIndex() - 1);

        //Can not remove superscript close Brackets
        if (toRemove instanceof Bracket && toRemove.getType() == Bracket.SUPERSCRIPT_CLOSE) {
            display.setCursorIndex(display.getCursorIndex() - 1);
            return;
        } else if (toRemove instanceof Bracket && toRemove.getType() == Bracket.SUPERSCRIPT_OPEN) { //Removes whatever was before it instead
            toRemove = tokens.get(display.getRealCursorIndex() - 2);
        } else if (toRemove instanceof Bracket && toRemove.getType() == Bracket.NUM_OPEN) {
            if (display.getRealCursorIndex() - 3 < 0) {
                return;
            }
            toRemove = tokens.get(display.getRealCursorIndex() - 3);
            display.setCursorIndex(display.getCursorIndex() - 1);
        } else if (toRemove instanceof Bracket && toRemove.getType() == Bracket.FRACTION_OPEN) {
            toRemove = tokens.get(display.getRealCursorIndex() - 2);
        } else if (toRemove instanceof Bracket && toRemove.getType() == Bracket.DENOM_OPEN) {
            toRemove = tokens.get(display.getRealCursorIndex() - 2);
            display.setCursorIndex(display.getCursorIndex() - 1);
        } else if (toRemove instanceof Bracket && toRemove.getType() == Bracket.FRACTION_CLOSE) {
            display.setCursorIndex(display.getCursorIndex() - 1);
            return;
        }

        tokens.remove(toRemove);

        //Removes any dependencies
        for (Token t : toRemove.getDependencies()) {
            tokens.remove(t);
        }

        display.setCursorIndex(display.getCursorIndex() - 1);
        changedTokens = true; //used to know if the button has been used
        updateInput();
    }

    /**
     * When the user presses the negative Button.
     */
    public void clickNegative() {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeNegative());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the equals Button.
     */
    public void clickEquals() {
        try {
            Number num = new Number(Utility.process(tokens));
            ArrayList<Token> output = new ArrayList<>();
            if (Double.isInfinite(num.getValue())) {
                throw new NumberTooLargeException();
            } else if (num.getValue() == 9001) {
                Toast.makeText(activity, "IT'S OVER 9000!!", Toast.LENGTH_LONG).show();
            } else if (num.getValue() == 420) {
                String[] dank = {
                        "Ayy Lmao",
                        "JET FUEL CAN'T MELT DANK MEMES",
                        "node.js",
                        "node.js is the only REAL dev language",
                        "JET FUEL CAN'T MELT STEEL BEAMS",
                        "#sariahismyOTP"
                };
                Random rand = new Random();
                Toast.makeText(activity, dank[rand.nextInt(dank.length)], Toast.LENGTH_LONG).show();
            } else if (num.getValue() == 69) {
                Toast.makeText(activity, "( ͡° ͜ʖ ͡°)", Toast.LENGTH_LONG).show();
            } else if (num.getValue() == 1.048596) {
                Toast.makeText(activity, "El Psy Congroo", Toast.LENGTH_LONG).show();
            }
            output.add(num);
            display.displayOutput(output);
            saveEquation(tokens, output, filename);
        } catch (Exception e) { //User did a mistake
            handleExceptions(e);
        }
        activity.scrollDown();
    }

    private ArrayList<Token> equals() {
        Number num = new Number(Utility.process(tokens));
        ArrayList<Token> list = new ArrayList<Token>();
        list.add(num);
        return list;
    }

    /**
     * Called when an exception occurs anywhere during processing.
     *
     * @param e The exception that was thrown
     */
    protected void handleExceptions(Exception e) {
        String message = "";
        if (e instanceof NumberTooLargeException) {
            message = "The calculation is to large to perform";
        } else if (e instanceof ArithmeticException) {
            message = "Math Error";
        } else if (e instanceof IllegalArgumentException) {
            message = e.getMessage();
        } else {
            if (e.getMessage() == null || e.getMessage().equals("")) {
                message = "Invalid input";
            } else {
                message = "Unknown Error : " + e.getMessage();
            }
        }
        if (message != "") {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Saves the equation into the calculation history.
     *
     * @param input  The expression that the user inputted into the calculator
     * @param output The result of the calculation
     */
    public void saveEquation(ArrayList<Token> input, ArrayList<Token> output, String filepath) throws IOException, ClassNotFoundException {
        ArrayList<Object[]> history = new ArrayList<Object[]>();
        try {
            FileInputStream inStream = activity.openFileInput(filepath);
            ObjectInputStream objectStreamIn = new ObjectInputStream(inStream);
            history = (ArrayList<Object[]>) objectStreamIn.readObject();
        } catch (Exception e) {
        }

        FileOutputStream outStream = activity.openFileOutput(filepath, Context.MODE_PRIVATE);
        Object[] toWrite = new Object[2];
        toWrite[0] = input;
        toWrite[1] = output;
        history.add(toWrite);

        while (history.size() > HISTORY_SIZE) {
            history.remove(0);
        }

        ObjectOutputStream objectStreamOut = new ObjectOutputStream(outStream);
        objectStreamOut.writeObject(history);
        objectStreamOut.flush();
        objectStreamOut.close();
        outStream.close();
    }

    /**
     * Opens the calculation history.
     *
     * @param filename The file name of the history file
     */
    public void openHistory(String filename){
        Intent intent  = new Intent(activity, HistoryActivity.class);
        intent.putExtra(HistoryActivity.FILENAME,filename);
        activity.startActivity(intent);
    }

    /**
     * Updates the text on the input screen.
     */
    protected void updateInput() {
        updatePlaceHolders();
        ViewPager mPager = (ViewPager) activity.findViewById(R.id.pager);
        if (mPager.getCurrentItem() == MainActivity.BASIC && activity.isAutocalculateOn()) { // checks if the current mode is Basic
            try {
                display.displayOutput(equals()); // Autocalculates if possible
            } catch (Exception e) {
                display.displayOutput(new ArrayList<Token>());
            }
        } else {
            display.displayOutput(new ArrayList<Token>()); //Clears output
        }
        display.displayInput(tokens);
        activity.setShowAd(true);
    }

    protected void updateInputOld() {
        updatePlaceHolders();
        display.displayOutput(new ArrayList<Token>()); //Clears output
        display.displayInput(tokens);
        activity.setShowAd(true);
    }

    /**
     * Removes any placeholders that are no longer necessary, or adds them
     * if they are.
     */
    public void updatePlaceHolders() {
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            Token previous = i - 1 < 0 ? null : tokens.get(i - 1);

            //Adds the block if necessary; looks for ^() (superscripted brackets or frac brackets)
            if ((token instanceof Bracket && token.getType() == Bracket.SUPERSCRIPT_CLOSE && previous != null && previous instanceof Bracket
                    && previous.getType() == Bracket.SUPERSCRIPT_OPEN)
                    || (token instanceof Bracket && token.getType() == Bracket.NUM_CLOSE && previous != null && previous instanceof Bracket
                    && previous.getType() == Bracket.NUM_OPEN)
                    || (token instanceof Bracket && token.getType() == Bracket.DENOM_CLOSE && previous != null && previous instanceof Bracket
                    && previous.getType() == Bracket.DENOM_OPEN)) {
                //Adds the placeholder before the close bracket
                tokens.add(i, PlaceholderFactory.makeSuperscriptBlock());
            }
            //BASE
            if (token instanceof Operator && token.getType() == Operator.EXPONENT && !(previous instanceof Placeholder)
                    && (previous == null || !(previous instanceof Digit || previous instanceof Variable
                    || previous instanceof Bracket && (previous.getType() == Bracket.CLOSE || previous.getType() == Bracket.DENOM_CLOSE)))) {
                tokens.add(i, PlaceholderFactory.makeBaseBlock());
            }
            //Removes Placeholder if it is not needed - Checks to see if it is not next to a superscript or frac bracket - SUPERSCRIPT
            if (token instanceof Placeholder && token.getType() == Placeholder.SUPERSCRIPT_BLOCK && !(previous != null && previous instanceof Bracket
                    && (previous.getType() == Bracket.SUPERSCRIPT_OPEN || previous.getType() == Bracket.NUM_OPEN
                    || previous.getType() == Bracket.DENOM_OPEN))) {
                tokens.remove(token);
            }
            //for BASE
            if (token instanceof Placeholder && token.getType() == Placeholder.BASE_BLOCK && previous != null
                    && (previous instanceof Digit || previous instanceof Variable || (previous instanceof Bracket
                    && (previous.getType() == Bracket.CLOSE || previous.getType() == Bracket.DENOM_CLOSE)))) {
                tokens.remove(token);
            }
        }
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public void setTokens(ArrayList<Token> expression) {
        this.tokens = expression;
    }

}