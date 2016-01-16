/*
 * Copyright (c) 2016 TruTech Innovations Inc - All Rights Reserved
 */

package com.trutechinnovations.calculall;


import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
    public static final double HISTORY_IO_RATIO = 0.7; //The size of the output / input in the history
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
            case R.id.exit_button:
                clickExit();
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
        try {
            openHistory(filename);
        } catch (IOException | ClassNotFoundException e) {
            Toast.makeText(activity, "Error saving to history", Toast.LENGTH_LONG).show();
        }
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
    public void openHistory(String filename) throws IOException, ClassNotFoundException {
        //Inflates the XML file so you get the View to add to the PopupWindow
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.history_view, null, false);

        //Creates the popupWindow, with the width matching the parent's and height matching the parent's
        historyWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        //Retrieves the user data from saved memory
        ArrayList<Object[]> history;
        try {
            FileInputStream stream = activity.openFileInput(filename);
            ObjectInputStream objectStream = new ObjectInputStream(stream);
            history = (ArrayList<Object[]>) objectStream.readObject();
            //Reverses the order so that the most recent is at the top
            Collections.reverse(history);
        } catch (FileNotFoundException e) { //No history
            history = new ArrayList<>();

            ArrayList<Token> list1 = new ArrayList<>();
            ArrayList<Token> list2 = new ArrayList<>();

            list1.add(new StringToken("No History to show"));
            list2.add(new StringToken(""));

            ArrayList<Token>[] message = new ArrayList[2];
            message[0] = list1;
            message[1] = list2;
            history.add(message);
        }

        //Finds the ListView from the inflated History XML so it could be manipulated
        ListView lv = (ListView) layout.findViewById(R.id.historyList);

        //Attaches the custom Adapter to the ListView so that it can configure the items and their Views within it
        lv.setAdapter(new HistoryAdapter(history, activity));

        //Displays the created PopupWindow on top of the LinearLayout with ID frame, which is being shown by the Activity
        historyWindow.setBackgroundDrawable(new BitmapDrawable());
        historyWindow.showAtLocation(activity.findViewById(R.id.frame), Gravity.CENTER, 0, 0);
    }

    /**
     * Updates the text on the input screen.
     */
    protected void updateInput() {
        updatePlaceHolders();
        ViewPager mPager = (ViewPager) activity.findViewById(R.id.pager);
        if (mPager.getCurrentItem() == 0) { // checks if the current mode is Basic
            ArrayList<Token> output = new ArrayList<Token>();
            boolean failed = false;
            try {
                output = equals();
            } catch (Exception e) {
                failed = true;
            }
            if (!failed) {
                display.displayOutput(output);
            } else {
                display.displayOutput(new ArrayList<Token>()); //Clears output
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
    private void updatePlaceHolders() {
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


    /**
     * The custom Adapter for the ListView in the calculation history.
     */
    private class HistoryAdapter extends BaseAdapter {
        private GestureDetector gestureDetector;
        private MainActivity activity;
        private ArrayList<Object[]> history; //The data that will be shown in the ListView

        public HistoryAdapter(ArrayList<Object[]> history, MainActivity activity) {
            this.history = history;
            this.activity = activity;
            gestureDetector = new GestureDetector(activity, new SingleTapUp());
        }

        @Override
        public int getCount() {
            return history.size();
        }

        @Override
        public Object getItem(int position) {
            return history.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Prepares the View of each item in the ListView that this Adapter will be attached to.
         *
         * @param position    The index of the item
         * @param convertView The old view that may be reused, or null if not possible
         * @param parent      The parent view
         * @return The newly prepared View that will visually represent the item in the ListView in the given position
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) { //For efficiency purposes so that it does not unnecessarily inflate Views
                //Inflates the XML file to get the View of the history element
                LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.history_element, parent, false);
            }

            //Sets up the child Views within each item in the ListView
            OutputView input = (OutputView) convertView.findViewById(R.id.input);
            OutputView output = (OutputView) convertView.findViewById(R.id.output);

            //Sets the font size of each OutputView
            input.setFontSize(activity.getFontSize());
            output.setFontSize((int) (activity.getFontSize() * HISTORY_IO_RATIO));

            //Enters the appropriate expressions to the OutputView
            Object[] entry = history.get(position);
            input.display((ArrayList<Token>) entry[0]);
            output.display((ArrayList<Token>) entry[1]);

            //To respond to user touches
            final ArrayList<Token> INPUT = (ArrayList<Token>) history.get(position)[0]; //Makes a constant reference so that history can be accessed by an inner class
            convertView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (gestureDetector.onTouchEvent(event)) {
                        ArrayList<Token> input = new ArrayList<>();
                        //Removes any StringTokens
                        for (Token t : INPUT) {
                            if (!(t instanceof StringToken)) {
                                input.add(t);
                            }
                        }

                        //Adds the input expression to the current tokens
                        tokens.addAll(display.getRealCursorIndex(), input); //Adds the input of the entry
                        int cursorChange = 0;
                        for (Token t : input) { //Determines the correct cursor position after the inserted expression
                            if (!(t instanceof Placeholder && (t.getType() == Placeholder.SUPERSCRIPT_BLOCK || t.getType() == Placeholder.BASE_BLOCK) ||
                                    (t instanceof Operator && t.getType() == Operator.VARROOT) ||
                                    (t instanceof Bracket && t.getType() == Bracket.SUPERSCRIPT_OPEN) ||
                                    (t instanceof Bracket && t.getType() == Bracket.DENOM_OPEN) ||
                                    (t instanceof Bracket && t.getType() == Bracket.NUM_OPEN) ||
                                    (t instanceof Bracket && t.getType() == Bracket.FRACTION_CLOSE) ||
                                    (t instanceof Operator && t.getType() == Operator.FRACTION))) {
                                cursorChange++;
                            }
                        }
                        updatePlaceHolders();
                        display.setCursorIndex(display.getCursorIndex() + cursorChange);
                        historyWindow.dismiss(); //Exits history once an Item has been selected
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            return convertView;
        }

        private class SingleTapUp extends GestureDetector.SimpleOnGestureListener { //CLASSCEPTION

            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                return true;
            }
        }

    }
}