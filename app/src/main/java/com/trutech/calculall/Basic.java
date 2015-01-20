package com.trutech.calculall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.Toast;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * The activity for the basic calculator mode. The basic mode will only be able to
 * perform the four operations (add, subtract, multiply and divide) including brackets.
 *
 * @version 0.4.0
 */
public class Basic extends Activity implements MoPubInterstitial.InterstitialAdListener {


    public static final String CLASS_NAME = Basic.class.getName();
    public static final int HISTORY_SIZE = 10;
    public static final int AD_RATE = 2; //Ads will show 1 in 2 activity opens
    private static final String FILENAME = "history_basic";
    private static final String AD_ID = "3ae32e9f72e2402cb01bbbaf1d6ba1f4";

    protected ArrayList<Token> tokens = new ArrayList<Token>(); //Tokens shown on screen
    protected boolean changedTokens = false;
    protected DisplayView display;
    protected OutputView output;
    private MoPubInterstitial interstitial;
    private boolean adShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_basic);
        output = (OutputView) findViewById(R.id.output);
        display = (DisplayView) findViewById(R.id.display);
        display.setOutput(output);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(interstitial != null && interstitial.isReady()) && !adShown && !((Object) this).getClass().getName().equals(CLASS_NAME)) {
            Random random = new Random();
            if (random.nextInt(AD_RATE) == 0) {
                // Create the interstitial.
                interstitial = new MoPubInterstitial(this, AD_ID);
                interstitial.setInterstitialAdListener(this);
                interstitial.load();
                interstitial.load();
            }
        }
    }

    @Override
    protected void onPause() {
        if (interstitial != null) {
            interstitial.destroy(); //Prevents Ads from other activities appearing if it is not loaded before switching between them
        }
        super.onPause();
    }


    /**
     * Processes the expression and returns the result using the Shunting Yard Algorithm to convert
     * the expression into reverse polish and then evaluating it.
     *
     * @return The numerical value of the expression
     * @throws IllegalArgumentException If the user has input a invalid expression
     */
    protected double process() {
        ArrayList<Token> tokens = Utility.setupExpression(Utility.condenseDigits(Utility.addMissingBrackets(subVariables())));
        return Utility.evaluateExpression(Utility.convertToReversePolish(tokens));
    }

    /**
     * Substitutes all the variables on the tokens list with the defined values
     *
     * @return The list of tokens with the variables substituted
     */
    protected ArrayList<Token> subVariables() {
        ArrayList<Token> newTokens = new ArrayList<Token>();
        for (Token token : tokens) {
            if (token instanceof Variable) {
                int index = tokens.indexOf(token);
                Variable v = (Variable) token;
                newTokens.add(index, new Number(v.getValue()));
            } else {
                newTokens.add(token);
            }
        }
        return newTokens;
    }

    /**
     * When the user presses the 1 Button.
     *
     * @param v Not Used
     */
    public void clickOne(View v) {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeOne());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the 2 Button.
     *
     * @param v Not Used
     */
    public void clickTwo(View v) {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeTwo());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the 3 Button.
     *
     * @param v Not Used
     */
    public void clickThree(View v) {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeThree());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the 4 Button.
     *
     * @param v Not Used
     */
    public void clickFour(View v) {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeFour());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the 5 Button.
     *
     * @param v Not Used
     */
    public void clickFive(View v) {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeFive());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the 6 Button.
     *
     * @param v Not Used
     */
    public void clickSix(View v) {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeSix());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the 7 Button.
     *
     * @param v Not Used
     */
    public void clickSeven(View v) {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeSeven());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the 8 Button.
     *
     * @param v Not Used
     */
    public void clickEight(View v) {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeEight());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the 9 Button.
     *
     * @param v Not Used
     */
    public void clickNine(View v) {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeNine());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the 0 Button.
     *
     * @param v Not Used
     */
    public void clickZero(View v) {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeZero());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the . Button.
     *
     * @param v Not Used
     */
    public void clickDecimal(View v) {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeDecimal());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the x Button.
     *
     * @param v Not Used
     */
    public void clickMultiply(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeMultiply());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the / Button.
     *
     * @param v Not Used
     */
    public void clickDivide(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeDivide());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the + Button.
     *
     * @param v Not Used
     */
    public void clickAdd(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeAdd());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the - Button.
     *
     * @param v Not Used
     */
    public void clickSubtract(View v) {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeSubtract());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the sqrt Button.
     *
     * @param v Not Used
     */
    public void clickSqrt(View v) {
        tokens.add(display.getRealCursorIndex(), FunctionFactory.makeSqrt());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }


    /**
     * When the user presses the clear Button.
     *
     * @param v Not Used
     */
    public void clickClear(View v) {
        tokens.clear();
        updateInput();
        changedTokens = true; //used to know if the button has been used
        DisplayView display = (DisplayView) findViewById(R.id.display);
        display.displayOutput(new ArrayList<Token>());
        display.reset();
    }


    /**
     * When the user presses the back Button.
     *
     * @param v Not Used
     */
    public void clickBack(View v) {
        if (tokens.isEmpty()) {
            return; //Prevents a bug
        }

        if (display.getCursorIndex() - 1 < 0) {
            return;
        }

        Token toRemove = tokens.get(display.getRealCursorIndex() - 1);

        //Can not remove superscript close Brackets
        if (toRemove instanceof Bracket && ((Bracket) toRemove).getType() == Bracket.SUPERSCRIPT_CLOSE) {
            display.setCursorIndex(display.getCursorIndex() - 1);
            return;
        } else if (toRemove instanceof Bracket && ((Bracket) toRemove).getType() == Bracket.SUPERSCRIPT_OPEN) { //Removes whatever was before it instead
            toRemove = tokens.get(display.getRealCursorIndex() - 2);
        } else if (toRemove instanceof Bracket && ((Bracket) toRemove).getType() == Bracket.NUM_OPEN) {
            display.setCursorIndex(display.getCursorIndex() - 1);
            return;
        } else if (toRemove instanceof Bracket && ((Bracket) toRemove).getType() == Bracket.DENOM_OPEN) {
            toRemove = tokens.get(display.getRealCursorIndex() - 2);
        } else if (toRemove instanceof Bracket && ((Bracket) toRemove).getType() == Bracket.DENOM_CLOSE) {
            Token bracket = toRemove;
            for (Token t : tokens) {
                if (t.getDependencies().contains(bracket)) {
                    toRemove = t;
                }
            }
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
     *
     * @param v Not Used
     */
    public void clickNegative(View v) {
        tokens.add(display.getRealCursorIndex(), DigitFactory.makeNegative());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the equals Button.
     *
     * @param v Not Used
     */
    public void clickEquals(View v) {
        DisplayView display = (DisplayView) findViewById(R.id.display);
        try {
            Number num = new Number(process());
            if (Double.isInfinite(num.getValue())) {
                throw new NumberTooLargeException();
            }
            ArrayList<Token> list = new ArrayList<Token>();
            list.add(num);
            display.displayOutput(list);
            saveEquation(tokens, list, FILENAME);
        } catch (Exception e) { //User did a mistake
            handleExceptions(e);
        }
        scrollDown();
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
        } else {
            message = "Invalid input";
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
            FileInputStream inStream = openFileInput(filepath);
            ObjectInputStream objectStreamIn = new ObjectInputStream(inStream);
            history = (ArrayList<Object[]>) objectStreamIn.readObject();
        } catch (Exception e) {
        }

        FileOutputStream outStream = openFileOutput(filepath, Context.MODE_PRIVATE);
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
        adShown = false;
    }

    /**
     * Opens the calculation history.
     *
     * @param v Not Used
     */
    public void openHistory(View v) throws IOException, ClassNotFoundException {
        setContentView(R.layout.history_view);
        HistoryView hv = (HistoryView) findViewById(R.id.history);
        try {
            FileInputStream stream = openFileInput(FILENAME);
            ObjectInputStream objectStream = new ObjectInputStream(stream);
            hv.setHistory((ArrayList<Object[]>) objectStream.readObject());
        } catch (FileNotFoundException e) {
            ArrayList<Object[]> history = new ArrayList<Object[]>();
            Object[] message = new Object[2];
            message[0] = new StringToken("No History to show");
            message[1] = new StringToken("");
            hv.setHistory(history);
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
     * When the user wants to change to Basic Mode.
     *
     * @param v Not Used
     */
    public void clickBasic(View v) {
        //Goes to the Basic activity
        Intent intent = new Intent(this, Basic.class);
        startActivity(intent);
    }

    /**
     * When the user wants to change to Advanced Mode.
     *
     * @param v Not Used
     */
    public void clickAdvanced(View v) {
        //Goes to the Advanced activity
        Intent intent = new Intent(this, Advanced.class);
        startActivity(intent);
    }

    /**
     * When the user wants to change to Function Mode.
     *
     * @param v Not Used
     */
    public void clickFunction(View v) {
        //Goes to the FunctionMode activity
        Intent intent = new Intent(this, FunctionMode.class);
        startActivity(intent);
    }

    /**
     * When the user wants to change to Vector Mode.
     *
     * @param v Not Used
     */
    public void clickVector(View v) {
        //Goes to the VectorMode activity
        Intent intent = new Intent(this, VectorMode.class);
        startActivity(intent);
    }

    /**
     * When the user wants to change to Matrix Mode.
     *
     * @param v Not Used
     */
    public void clickMatrix(View v) {
        //Goes to the VectorMode activity
        Intent intent = new Intent(this, MatrixMode.class);
        startActivity(intent);
    }

    /**
     * Updates the text on the input screen.
     */
    protected void updateInput() {
        updatePlaceHolders();
        display.displayOutput(new ArrayList<Token>()); //Clears output
        display.displayInput(tokens);
    }

    /**
     * Removes any placeholders that are no longer neccesary, or adds them
     * if they are.
     */
    private void updatePlaceHolders() {
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            Token previous = i - 1 < 0 ? null : tokens.get(i - 1);

            //Adds the block if necessary; looks for ^() (superscripted brackets or frac brackets)
            if ((token instanceof Bracket && ((Bracket) token).getType() == Bracket.SUPERSCRIPT_CLOSE && previous != null && previous instanceof Bracket
                    && ((Bracket) previous).getType() == Bracket.SUPERSCRIPT_OPEN)
                    || (token instanceof Bracket && ((Bracket) token).getType() == Bracket.NUM_CLOSE && previous != null && previous instanceof Bracket
                    && ((Bracket) previous).getType() == Bracket.NUM_OPEN)
                    || (token instanceof Bracket && ((Bracket) token).getType() == Bracket.DENOM_CLOSE && previous != null && previous instanceof Bracket
                    && ((Bracket) previous).getType() == Bracket.DENOM_OPEN)) {
                //Adds the placeholder before the close bracket
                tokens.add(i, PlaceholderFactory.makeBlock());
            }

            //Removes Placeholder if it is not needed - Checks to see if it is not next to a superscript or frac bracket
            if (token instanceof Placeholder && ((Placeholder) token).getType() == Placeholder.BLOCK && !(previous != null && previous instanceof Bracket
                    && (((Bracket) previous).getType() == Bracket.SUPERSCRIPT_OPEN || ((Bracket) previous).getType() == Bracket.NUM_OPEN
                    || ((Bracket) previous).getType() == Bracket.DENOM_OPEN))) {
                tokens.remove(token);
            }
        }
    }

    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        adShown = true;
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
}
