package com.trutech.calculall;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
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
 * Ability to define functions and perform various actions with them, such as finding
 * roots, integration, differentiation, and graphing.
 *
 * @version 0.4.0
 */
public class FunctionMode extends Advanced {


    //Some Threading stuff
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
    private static final int STACK_SIZE = 1000000; //1MB STACK SIZE

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        setupButtons();
        updateInput();
        //IO Setup
        output = (OutputView) findViewById(R.id.output);
        display = (DisplayView) findViewById(R.id.display);
        display.setOutput(output);
        //Loading dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Calculating...");
        pd.setMessage("This may take a while. ");
        pd.setCancelable(false);
    }

    /**
     * Programmatically sets the texts that can't be defined with XML.
     */
    public void setupButtons() {
        Button powButton = (Button) findViewById(R.id.powButton);
        Button expButton = (Button) findViewById(R.id.powerButton);
        Button recipButton = (Button) findViewById(R.id.reciprocal);
        recipButton.setText(Html.fromHtml(getString(R.string.recip)));
        powButton.setText(Html.fromHtml(getString(R.string.powOfTen)));
        expButton.setText(Html.fromHtml(getString(R.string.exponent)));
    }

    public void clickRoots(View view) {
        final Context context = this;
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
                    scrollDown();

                    super.onPostExecute(roots);
                }
            }
        };

        if (tokens.size() == 0) { //No tokens
            Toast.makeText(this, "There is no expression. You would need to enter an expression first, then press the roots button.", Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<Token> tokens = Utility.condenseDigits(this.tokens);
        tokens = Utility.setupExpression(tokens);
        task.execute(tokens);
    }

    public void clickDerivative(View view) {
        final Context context = this;

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
            Toast.makeText(this, "There is no expression. You would need to enter an expression first, then press the differentiate button.", Toast.LENGTH_LONG).show();
            return;
        }

        //Sets up the params
        ArrayList<Token>[] params = new ArrayList[1];
        params[0] = tokens;
        //Passes the rest onto the Thread
        MathThread thread = new MathThread(task, errorHandler);
        thread.executeOnExecutor(EXECUTOR, params);

    }

    public void clickIntegrate(View view) {
        final Context context = this;

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
            Toast.makeText(this, "There is no expression. You would need to enter an expression first, then press the integrate button.", Toast.LENGTH_LONG).show();
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
     *
     * @param v Not Used
     */
    public void clickExpand(View v) {
        final Context context = this;

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
            Toast.makeText(this, "There is no expression. You would need to enter an expression first, then press the expand button.", Toast.LENGTH_LONG).show();
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
     *
     * @param v Not Used
     */
    public void clickFactor(View v) {
        final Context context = this;

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
            Toast.makeText(this, "There is no expression. You would need to enter an expression first, then press the factor button.", Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, "Malformed Expression", Toast.LENGTH_SHORT).show();
    }

    /**
     * Graphs the inputted function.
     *
     * @param view
     */
    public void clickGraph(View view) {
        setContentView(new GraphView(this, this, tokens));
    }

    /**
     * Maps the coefficients to the degree of the x value they are from.
     *
     * @param tokens The expression to map
     * @return A map that uses the degree of the x as the key and the coefficient of that as the value. It will also
     * map the highest degree found as Double.POSITIVE_INFINITY for the key and whether or not all the degrees are integers
     * for the Double.NEGATIVE_INFINITY key (1 = true, 0 = false)
     * @throws java.lang.IllegalArgumentException The user has inputted an invalid expression
     */
    private HashMap<Double, Double> mapCoefficients(ArrayList<Token> tokens) {
        HashMap<Double, Double> map = new HashMap<Double, Double>();
        double highestDegree = 0;
        int allIntegers = 1; //1 = All degrees are integers, 0 = not
        boolean negative = false;
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            Token previous = i - 1 >= 0 ? tokens.get(i - 1) : null;
            Token beforePrevious = i - 2 >= 0 ? tokens.get(i - 2) : null;
            Token next = i + 1 < tokens.size() ? tokens.get(i + 1) : null;
            Token afterNext = i + 2 < tokens.size() ? tokens.get(i + 2) : null;
            if (token instanceof Variable) { //Has to be an X; there's no other variables
                //Assumed a value of 1 until defined (ex. 5X has a degree of 1)
                double coefficient = 1;
                double degree = 1;
                //Looks for the coefficient first if any
                if (beforePrevious != null && previous instanceof Operator && ((Operator) previous).getType() == Operator.MULTIPLY) { //It has a coefficient as the previous number
                    if (beforePrevious instanceof Number) {
                        coefficient = ((Number) beforePrevious).getValue();
                    } else {
                        throw new IllegalArgumentException(); //The user inputted a invalid function
                    }
                }
                if (negative) {
                    coefficient *= -1;
                } else {
                    coefficient *= 1;
                }
                //Now it looks for the term if any
                if (afterNext != null && next instanceof Operator && ((Operator) next).getType() == Operator.EXPONENT) { //It has a coefficient as the previous number
                    if (afterNext instanceof Number) {
                        degree = (int) ((Number) afterNext).getValue();
                        //Checks if its the highest degree
                        if (degree > highestDegree) {
                            highestDegree = degree;
                        }
                        //Checks if the degree is an integer
                        if (degree % 1 != 0) { //Not an integer
                            allIntegers = 0;
                        }
                    } else {
                        throw new IllegalArgumentException(); //The user inputted a invalid function
                    }
                }
                //Now maps the coefficient to the term
                map.put(degree, coefficient);
            } else if (token instanceof Operator) {
                if (((Operator) token).getType() == Operator.ADD) {
                    negative = false;
                } else if (((Operator) token).getType() == Operator.SUBTRACT) {
                    negative = true;
                }

            } else if (token instanceof Number && previous instanceof Operator) {
                Operator o = (Operator) previous;
                if ((o.getType() == Operator.ADD || o.getType() == Operator.SUBTRACT) && i == tokens.size() - 1) { //Constant
                    double value = ((Number) token).getValue();
                    value *= o.getType() == Operator.SUBTRACT ? -1 : 1; //Accounts the Subtraction as a negative
                    map.put(0d, value);
                }
            }
        }

        Iterator<Double> keySetIterator = map.keySet().iterator();
        double hd = 0;
        double temp;
        while (keySetIterator.hasNext()) {
            temp = keySetIterator.next();
            if (temp > hd) {
                hd = temp;
            }
        }
        if (highestDegree < hd) {
            highestDegree = hd;
        }
        //Maps if the metadata
        map.put(Double.POSITIVE_INFINITY, highestDegree);
        map.put(Double.NEGATIVE_INFINITY, (double) allIntegers);
        if (!map.containsKey(0d)) {
            map.put(0d, 0d);
        }
        return map;
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
