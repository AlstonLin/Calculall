/*
 * Copyright (c) 2016 TruTech Innovations Inc - All Rights Reserved
 */

package com.trutechinnovations.calculall;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.matheclipse.core.eval.exception.WrongNumberOfArguments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Contains the back-end for Function Mode. Ability to define functions and perform various
 * actions with them, such as finding roots, integration, differentiation, and graphing.
 *
 * @author Alston Lin
 * @version 3.0
 */
public class FunctionMode extends Advanced {

    //Some Threading stuff
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
    private static final String FILENAME = "history_function";
    private static final int STACK_SIZE = 500000; //1MB STACK SIZE
    private static final ThreadFactory threadFactory = new ThreadFactory() {
        public Thread newThread(Runnable r) {
            ThreadGroup group = new ThreadGroup("threadGroup");
            return new Thread(group, r, "Calculus Thread", STACK_SIZE);
        }
    };
    private static final FunctionMode INSTANCE = new FunctionMode();
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);
    public static final Executor EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, threadFactory);
    //Actual variables used
    private ProgressDialog pd;
    private PopupWindow pw;
    private Dialog graphDialog;

    { //lazy constructor
        Function.angleMode = Function.RADIAN;
        filename = FILENAME;
    }

    /**
     * Allows for the Singleton pattern so there would be only one instance.
     *
     * @return The singleton instance
     */
    public static FunctionMode getInstance() {
        return INSTANCE;
    }

    /**
     * When a Button has been clicked, calls the appropriate method.
     *
     * @param v The Button that has been clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shift_button:
                clickShift();
                break;
            case R.id.var_x:
                clickX();
                break;
            case R.id.roots_button:
                clickRoots();
                break;
            case R.id.factor:
                clickFactor();
                break;
            case R.id.expand:
                clickExpand();
                break;
            case R.id.log_button:
                clickLn();
                break;
            case R.id.derivative_button:
                clickDerivative();
                break;
            case R.id.integral_button:
                clickIntegrate();
                break;
            case R.id.graph:
                clickGraph();
                break;
            default:
                super.onClick(v);
        }
    }

    public void clickX() {
        tokens.add(display.getRealCursorIndex(), VariableFactory.makeX());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user clicks the History button.
     */
    public void clickHistory() {
        openHistory(filename);
    }

    public void clickRoots() {
        final Context context = activity;
        AsyncTask<ArrayList<Token>, Void, ArrayList<ArrayList<Token>>> task = new AsyncTask<ArrayList<Token>, Void, ArrayList<ArrayList<Token>>>() {

            private Exception error;

            @Override
            protected void onPreExecute() {
                if (pd == null) { //Lazy Initialization
                    //Loading dialog
                    pd = new ProgressDialog(activity, R.style.progressDialog);
                    pd.setTitle("Calculating...");
                    pd.setMessage("This may take a while. ");
                    pd.setCancelable(false);
                    pd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancel(true);
                        }
                    });
                }
                pd.show();
                super.onPreExecute();
            }

            @Override
            protected ArrayList<ArrayList<Token>> doInBackground(ArrayList<Token>... params) {
                try {
                    return MathUtilities.findRoots(subAns(params[0]));
                } catch (Exception e) {
                    error = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ArrayList<ArrayList<Token>> roots) {
                pd.dismiss();
                if (!isCancelled()) {
                    if (roots == null) {
                        if (error == null) {
                            showMalformedExpressionToast();
                        } else if (error instanceof UnsupportedOperationException) {
                            Toast.makeText(context, "Sorry, we're unable to find the root(s) of this function. Root finding for this function may not be supported yet.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Something weird happened in our system, and we can't find the roots. We'll try to fix this as soon as we can. Sorry! :(", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        ArrayList<Token> toOutput = new ArrayList<>();
                        int counter = 0;
                        toOutput.add(new StringToken("X = "));
                        while (counter < roots.size()) {
                            ArrayList<Token> root = roots.get(counter);
                            if (counter != 0) {
                                toOutput.add(new StringToken(" OR "));
                            }
                            toOutput.addAll(root);
                            counter++;
                        }
                        if (counter == 0) { //No roots
                            toOutput.add(new StringToken("No Real Roots"));
                        }
                        display.displayOutput(toOutput);
                        activity.scrollDown();
                        //Saves to history
                        try {
                            ArrayList<Token> saveInput = new ArrayList<>();
                            saveInput.addAll(subAns(tokens));
                            saveInput.add(0, new StringToken("Roots of "));
                            saveEquation(saveInput, toOutput, filename);
                        } catch (IOException | ClassNotFoundException e) {
                            Toast.makeText(activity, "Error saving to history", Toast.LENGTH_LONG).show();
                        }
                        super.onPostExecute(roots);
                    }
                }
            }
        };

        if (tokens.size() == 0) { //No tokens
            Toast.makeText(activity, "There is no expression. You would need to enter an expression first, then press the roots button.", Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<Token> tokens = Utility.condenseDigits(this.tokens);
        tokens = Utility.setupExpression(tokens);
        task.execute(tokens);
    }

    public void clickDerivative() {
        final Context context = activity;

        Command<ArrayList<Token>, ArrayList<Token>> task = new Command<ArrayList<Token>, ArrayList<Token>>() {
            @Override
            public ArrayList<Token> execute(ArrayList<Token> tokens) {
                return MathUtilities.differentiate(subAns(tokens));
            }
        };

        Command<Void, Exception> errorHandler = new Command<Void, Exception>() {
            @Override
            public Void execute(Exception error) {
                if (error == null) {
                    showMalformedExpressionToast();
                } else {
                    Toast.makeText(context, "Something weird happened in our system, and we can't find the derivative. We'll try to fix this as soon as we can. Sorry! :(", Toast.LENGTH_LONG).show();
                }
                return null;
            }
        };

        if (tokens.size() == 0) { //No tokens
            Toast.makeText(activity, "There is no expression. You would need to enter an expression first, then press the differentiate button.", Toast.LENGTH_LONG).show();
            return;
        }

        //Sets up the params
        ArrayList<Token>[] params = new ArrayList[1];
        params[0] = tokens;
        ArrayList<Token> input = new ArrayList<>();
        input.add(new StringToken("d/dx "));
        input.addAll(subAns(tokens));
        //Passes the rest onto the Thread
        MathThread thread = new MathThread(task, errorHandler, input);
        thread.executeOnExecutor(EXECUTOR, params);

    }

    public void clickIntegrate() {
        final Context context = activity;

        Command<ArrayList<Token>, ArrayList<Token>> task = new Command<ArrayList<Token>, ArrayList<Token>>() {
            @Override
            public ArrayList<Token> execute(ArrayList<Token> tokens) {
                return MathUtilities.integrate(subAns(tokens));
            }
        };

        Command<Void, Exception> errorHandler = new Command<Void, Exception>() {
            @Override
            public Void execute(Exception error) {
                if (error == null) {
                    showMalformedExpressionToast();
                } else {
                    if (error instanceof UnsupportedOperationException) {
                        Toast.makeText(context, "Cannot find the integral. Either the integral cannot be expressed as an elementary function, " +
                                "or the algorithm needed for this integration is not yet supported.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Something weird happened in our system, and we can't find the integral. We'll try to fix this as soon as we can. Sorry! :(", Toast.LENGTH_LONG).show();
                    }
                }
                return null;
            }
        };

        if (tokens.size() == 0) { //No tokens
            Toast.makeText(activity, "There is no expression. You would need to enter an expression first, then press the integrate button.", Toast.LENGTH_LONG).show();
            return;
        }

        //Sets up the params
        ArrayList<Token>[] params = new ArrayList[1];
        params[0] = tokens;
        ArrayList<Token> input = new ArrayList<>();
        input.add(0, new StringToken("∫ "));
        input.addAll(subAns(tokens));
        //Passes the rest onto the Thread
        MathThread thread = new MathThread(task, errorHandler, input);
        thread.executeOnExecutor(EXECUTOR, params);
    }

    /**
     * When the user presses the simplify button
     */
    public void clickExpand() {
        final Context context = activity;

        Command<ArrayList<Token>, ArrayList<Token>> task = new Command<ArrayList<Token>, ArrayList<Token>>() {
            @Override
            public ArrayList<Token> execute(ArrayList<Token> tokens) {
                return MathUtilities.expand(subAns(tokens));
            }
        };

        Command<Void, Exception> errorHandler = new Command<Void, Exception>() {
            @Override
            public Void execute(Exception error) {
                if (error == null) {
                    showMalformedExpressionToast();
                } else if (error instanceof WrongNumberOfArguments) {
                    showMalformedExpressionToast();
                } else {
                    Toast.makeText(context, "Something weird happened in our system, and we can't expand this. We'll try to fix this as soon as we can. Sorry! :(", Toast.LENGTH_LONG).show();
                }
                return null;
            }
        };

        if (tokens.size() == 0) { //No tokens
            Toast.makeText(activity, "There is no expression. You would need to enter an expression first, then press the expand button.", Toast.LENGTH_LONG).show();
            return;
        }

        //Sets up the params
        ArrayList<Token>[] params = new ArrayList[1];
        params[0] = tokens;
        ArrayList<Token> input = new ArrayList<>();
        input.add(0, new StringToken("Expand "));
        input.addAll(subAns(tokens));
        //Passes the rest onto the Thread
        MathThread thread = new MathThread(task, errorHandler, input);
        thread.executeOnExecutor(EXECUTOR, params);
    }

    /**
     * When the user presses the simplify button
     */
    public void clickFactor() {
        final Context context = activity;

        Command<ArrayList<Token>, ArrayList<Token>> task = new Command<ArrayList<Token>, ArrayList<Token>>() {
            @Override
            public ArrayList<Token> execute(ArrayList<Token> tokens) {
                return MathUtilities.factor(subAns(tokens));
            }
        };

        Command<Void, Exception> errorHandler = new Command<Void, Exception>() {
            @Override
            public Void execute(Exception error) {
                if (error == null) {
                    showMalformedExpressionToast();
                } else if (error instanceof WrongNumberOfArguments) {
                    showMalformedExpressionToast();
                } else {
                    Toast.makeText(context, "Something weird happened in our system, and we can't factor this. We'll try to fix this as soon as we can. Sorry! :(", Toast.LENGTH_LONG).show();
                }
                return null;
            }
        };

        if (tokens.size() == 0) { //No tokens
            Toast.makeText(activity, "There is no expression. You would need to enter an expression first, then press the factor button.", Toast.LENGTH_LONG).show();
            return;
        }

        //Sets up the params
        ArrayList<Token>[] params = new ArrayList[1];
        params[0] = tokens;
        ArrayList<Token> input = new ArrayList<>();
        input.add(0, new StringToken("Factor "));
        input.addAll(subAns(tokens));
        //Passes the rest onto the Thread
        MathThread thread = new MathThread(task, errorHandler, input);
        thread.executeOnExecutor(EXECUTOR, params);
    }

    /**
     * Makes the toast that shows a message saying "Malformed Expression".
     */
    protected void showMalformedExpressionToast() {
        Toast.makeText(activity, "Malformed Expression", Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows the graph dialog.
     */
    public void clickGraph() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.alertDialog);
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.graph_dialog, null);
        builder.setView(layout);
        builder.setTitle(R.string.select_bounds);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clickGraphDialog(dialog);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                graphDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.cancel();
            }
        });
        graphDialog = builder.create();
        //Sets the numbers from the previous graph, if any
        SharedPreferences pref = activity.getSharedPreferences(activity.getString(R.string.preference_key), Context.MODE_PRIVATE);
        float minX = pref.getFloat(activity.getString(R.string.x_min), -10f);
        float minY = pref.getFloat(activity.getString(R.string.y_min), -10f);
        float maxX = pref.getFloat(activity.getString(R.string.x_max), 10f);
        float maxY = pref.getFloat(activity.getString(R.string.y_max), 10f);

        ((EditText) layout.findViewById(R.id.x_min)).setText(Float.toString(minX));
        //((EditText) layout.findViewById(R.id.x_min)).setTextColor(typedValue.data);

        ((EditText) layout.findViewById(R.id.y_min)).setText(Float.toString(minY));
        //((EditText) layout.findViewById(R.id.y_min)).setTextColor(typedValue.data);

        ((EditText) layout.findViewById(R.id.x_max)).setText(Float.toString(maxX));
        //((EditText) layout.findViewById(R.id.x_max)).setTextColor(typedValue.data);

        ((EditText) layout.findViewById(R.id.y_max)).setText(Float.toString(maxY));
        //((EditText) layout.findViewById(R.id.y_max)).setTextColor(typedValue.data);

        graphDialog.show();
    }


    /**
     * User had finished choosing graph bounds; shows
     * the graph dialog.
     */
    public void clickGraphDialog(DialogInterface dialog) {
        try {
            EditText xMinEdit = (EditText) graphDialog.findViewById(R.id.x_min);
            EditText xMaxEdit = (EditText) graphDialog.findViewById(R.id.x_max);
            EditText yMinEdit = (EditText) graphDialog.findViewById(R.id.y_min);
            EditText yMaxEdit = (EditText) graphDialog.findViewById(R.id.y_max);

            float xMin = Float.parseFloat(xMinEdit.getText().toString());
            float xMax = Float.parseFloat(xMaxEdit.getText().toString());
            float yMin = Float.parseFloat(yMinEdit.getText().toString());
            float yMax = Float.parseFloat(yMaxEdit.getText().toString());

            //Makes sure that the floats are valid
            if (xMin >= xMax) {
                Toast.makeText(activity, "The min x must be smaller than max x", Toast.LENGTH_SHORT).show();
                return;
            }

            if (yMin >= yMax) {
                Toast.makeText(activity, "The min y must be smaller than max y", Toast.LENGTH_SHORT).show();
                return;
            }
            //Saves to preferences
            SharedPreferences pref = activity.getSharedPreferences(activity.getString(R.string.preference_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putFloat(activity.getString(R.string.x_min), xMin);
            editor.putFloat(activity.getString(R.string.y_min), yMin);
            editor.putFloat(activity.getString(R.string.x_max), xMax);
            editor.putFloat(activity.getString(R.string.y_max), yMax);
            editor.commit();
            //Graphs and hides the keyboard
            graph(xMin, xMax, yMin, yMax);
            InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(xMinEdit.getWindowToken(), 0);
            im.hideSoftInputFromWindow(xMaxEdit.getWindowToken(), 0);
            im.hideSoftInputFromWindow(yMinEdit.getWindowToken(), 0);
            im.hideSoftInputFromWindow(yMaxEdit.getWindowToken(), 0);
            dialog.cancel();
        } catch (NumberFormatException e) { //Wrong text format
            Toast.makeText(activity, "Invalid Number Format", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * When the user presses the ANS button
     */
    public void clickAns() {
        tokens.add(display.getRealCursorIndex(), VariableFactory.makeAnsFunc());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * Graph the function at the given bounds.
     *
     * @param minX Minimum x value
     * @param maxX Maximum x value
     * @param minY Minimum y value
     * @param maxY Maximum y value
     */
    private void graph(float minX, float maxX, float minY, float maxY) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.graph_view, null, false);

        GraphView gv = (GraphView) layout.findViewById(R.id.graph_content);
        gv.setFunction(subAns(tokens));
        gv.setBounds(minX, maxX, minY, maxY);
        pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        gv.setPopupWindow(pw);
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.showAtLocation(activity.findViewById(R.id.frame), Gravity.CENTER, 0, 0);
    }

    private ArrayList<Token> subAns(ArrayList<Token> tokens) {
        ArrayList<Token> function = new ArrayList<>();

        for (Token token : tokens) {
            if (token instanceof Variable && token.getType() == Variable.ANS) {
                for (Token t : VariableFactory.ansValueFunc) {
                    if (t instanceof Variable && t.getType() == Variable.CONSTANT) {
                        function.add(new Number(0));
                    } else {
                        function.add(t);
                    }
                }
            } else {
                function.add(token);
            }
        }
        return function;
    }

    public PopupWindow getPw() {
        return pw;
    }

    /**
     * A generalization of the Thread that all the heavy worload calculus functions will use.
     */
    protected class MathThread extends AsyncTask<ArrayList<Token>, Void, ArrayList<Token>> {
        private Exception error; //If any Exception were to occur
        private Command<ArrayList<Token>, ArrayList<Token>> task;
        private Command<Void, Exception> errorHandler;
        private ArrayList<Token> historyInput;

        /**
         * Constructor for MathThread.
         *
         * @param task         The Command to be executed
         * @param errorHandler The Command that will be called to handle errors
         * @param historyInput The tokens that would appear on history as input
         */
        public MathThread(Command<ArrayList<Token>, ArrayList<Token>> task, Command<Void, Exception> errorHandler, ArrayList<Token> historyInput) {
            this.task = task;
            this.errorHandler = errorHandler;
            this.historyInput = historyInput;
        }

        @Override
        protected void onPreExecute() {
            if (pd == null) { //Lazy Initialization
                //Loading dialog
                pd = new ProgressDialog(activity, R.style.progressDialog);
                pd.setTitle("Calculating...");
                if (historyInput.get(0).getSymbol().equals("∫ ")) { //Very lazy way of doing this
                    pd.setMessage("This may take a while. Some integrations, especially ones relating to partial fractions can take several minutes or may be impossible.");
                } else {
                    pd.setMessage("This may take a while.");
                }
                pd.setCancelable(false);
            }
            pd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancel(true);
                }
            });
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected final ArrayList<Token> doInBackground(ArrayList<Token>[] params) {
            try {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE); //Higher priority
                return task.execute(params[0]);
            } catch (Exception e) {
                error = e;
                return null;
            } catch (StackOverflowError e) {
                throw new IllegalArgumentException("Not enough processing power to compute!");
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Token> result) {
            pd.dismiss();
            if (!isCancelled()) {
                if (result == null) { //Something went Wrong
                    errorHandler.execute(error);
                } else {
                    try {
                        saveEquation(historyInput, result, filename);
                    } catch (IOException | ClassNotFoundException e) {
                        Toast.makeText(activity, "Error saving to history", Toast.LENGTH_LONG).show();
                    }
                    display.displayOutput(result);
                    VariableFactory.ansValueFunc = result;
                }
                super.onPostExecute(tokens);
            }
        }
    }
}
