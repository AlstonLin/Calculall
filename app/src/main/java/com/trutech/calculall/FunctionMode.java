package com.trutech.calculall;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
   import android.widget.PopupWindow;
import android.widget.Toast;

import org.matheclipse.core.eval.exception.WrongNumberOfArguments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Contains the back-end for Function Mode. Ability to define functions and perform various
 * actions with them, such as finding roots, integration, differentiation, and graphing.
 *
 * @version Alpha 2.0
 * @author Alston Lin
 */
public class FunctionMode extends Advanced{


    //Some Threading stuff
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
    private static final int STACK_SIZE = 1000000; //1MB STACK SIZE
    private static final Basic INSTANCE = new FunctionMode();
    private static final ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            ThreadGroup group = new ThreadGroup("threadGroup");
            return new Thread(group, r, "Calculus Thread", STACK_SIZE);
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);
    public static final Executor EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, threadFactory);
    //Actual variables used
    private ProgressDialog pd;
    private PopupWindow pw;

    { //lazy constructor
        angleMode = RADIAN;
    }

    /**
     * Allows for the Singleton pattern so there would be only one instance.
     * @return The singleton instance
     */
    public static Basic getInstance(){
        return INSTANCE;
    }

    /**
     * When a Button has been clicked, calls the appropriate method.
     *
     * @param v The Button that has been clicked
     */
    @Override
    public void onClick(View v){
        switch(v.getId()){
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

    public void clickX(){
        tokens.add(display.getRealCursorIndex(), VariableFactory.makeX());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    public void clickRoots() {
        final Context context = activity;
        AsyncTask<ArrayList<Token>, Void, ArrayList<ArrayList<Token>>> task = new AsyncTask<ArrayList<Token>, Void, ArrayList<ArrayList<Token>>>() {

            private Exception error;

            @Override
            protected void onPreExecute() {
                pd.show();
                super.onPreExecute();
            }

            @Override
            protected ArrayList<ArrayList<Token>> doInBackground(ArrayList<Token>... params) {
                try {
                    return MathUtilities.findRoots(params[0]);
                } catch (Exception e) {
                    error = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ArrayList<ArrayList<Token>> roots) {
                pd.dismiss();

                if (roots == null) {
                    if (error == null) {
                        showMalformedExpressionToast();
                    } else if (error instanceof UnsupportedOperationException) {
                        Toast.makeText(context, "Sorry, we're unable to find the root(s) of this function. Root finding for this function may not be supported yet.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Something weird happened in our system, and we can't find the derivative. We'll try to fix this as soon as we can. Sorry! :(", Toast.LENGTH_LONG).show();
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

                    super.onPostExecute(roots);
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
                return MathUtilities.differentiate(tokens);
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
        //Passes the rest onto the Thread
        MathThread thread = new MathThread(task, errorHandler);
        thread.executeOnExecutor(EXECUTOR, params);

    }

    public void clickIntegrate() {
        final Context context = activity;

        Command<ArrayList<Token>, ArrayList<Token>> task = new Command<ArrayList<Token>, ArrayList<Token>>() {
            @Override
            public ArrayList<Token> execute(ArrayList<Token> tokens) {
                return MathUtilities.integrate(tokens);
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
        //Passes the rest onto the Thread
        MathThread thread = new MathThread(task, errorHandler);
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
                return MathUtilities.expand(tokens);
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
                    Toast.makeText(context, "Something weird happened in our system, and we can't find the integral. We'll try to fix this as soon as we can. Sorry! :(", Toast.LENGTH_LONG).show();
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
        //Passes the rest onto the Thread
        MathThread thread = new MathThread(task, errorHandler);
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
                return MathUtilities.factor(tokens);
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
                    Toast.makeText(context, "Something weird happened in our system, and we can't find the integral. We'll try to fix this as soon as we can. Sorry! :(", Toast.LENGTH_LONG).show();
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
        //Passes the rest onto the Thread
        MathThread thread = new MathThread(task, errorHandler);
        thread.executeOnExecutor(EXECUTOR, params);
    }

    /**
     * Makes the toast that shows a message saying "Malformed Expression".
     */
    private void showMalformedExpressionToast() {
        Toast.makeText(activity, "Malformed Expression", Toast.LENGTH_SHORT).show();
    }

    /**
     * Graphs the inputted function.
     */
    public void clickGraph() {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.graph_view, null, false);
        GraphView gv = (GraphView) layout.findViewById(R.id.graph_content);
        gv.setFunction(tokens);
        pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        gv.setPopupWindow(pw);
        pw.showAtLocation(activity.findViewById(R.id.frame), Gravity.CENTER, 0, 0);
    }


    /**
     * A generalization of the Thread that all the heavy worload calculus functions will use.
     */
    private class MathThread extends AsyncTask<ArrayList<Token>, Void, ArrayList<Token>> {
        private Exception error; //If any Exception were to occur
        private Command<ArrayList<Token>, ArrayList<Token>> task;
        private Command<Void, Exception> errorHandler;

        /**
         * Constructor for MathThread.
         *
         * @param task         The Command to be executed
         * @param errorHandler The Command that will be called to handle errors
         */
        public MathThread(Command<ArrayList<Token>, ArrayList<Token>> task, Command<Void, Exception> errorHandler) {
            this.task = task;
            this.errorHandler = errorHandler;
        }

        @Override
        protected void onPreExecute() {
            if (pd == null){ //Lazy Initialization
                //Loading dialog
                pd = new ProgressDialog(activity);
                pd.setTitle("Calculating...");
                pd.setMessage("This may take a while. ");
                pd.setCancelable(false);
            }
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected final ArrayList<Token> doInBackground(ArrayList<Token>[] params) {
            try {
                return task.execute(params[0]);
            } catch (Exception e) {
                error = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Token> result) {
            pd.dismiss();
            if (result == null) { //Something went Wrong
                errorHandler.execute(error);
            } else {
                //Got the Integral!
                display.displayOutput(result);
            }
            super.onPostExecute(tokens);
        }
    }
}
