package com.trutechinnovations.calculall;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by david on 1/20/2017.
 */

public class DecompositionSelectionActivity extends ListViewActivity {
    ArrayList<Token[]> decompositions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decomposition_view);
        setupListView();
        ListView lv = (ListView) findViewById(R.id.decompList);

        lv.setAdapter(new DecompAdapter());

    }

    public void setupListView(){

        //Retrieves the user data from saved memory

        decompositions = new ArrayList<>();
        try {
            if (MatrixMode.getInstance().tokens.isEmpty()) {
                throw new IllegalArgumentException("Please enter a Matrix expression first");
            }
            ArrayList<Token> temp = MatrixUtils.setupExpression(Utility.condenseDigits(MatrixMode.getInstance().tokens));
            temp = MatrixUtils.convertToReversePolish(temp);
            Token t = MatrixUtils.evaluateExpression(temp, false);
            if (t instanceof Matrix) {
                double[][] a = MatrixUtils.evaluateMatrixEntries((Matrix) t);
                String[][] decompositionsArray = {
                        {"LUP factorization",
                                "Finds matrices <b>P</b>, <b>L</b> and <b>U</b> such that" +
                                        "<div><b>PA = LU</b></div>" +
                                        "where <b>L</b> and <b>U</b> are lower and upper triangular, respectively, and <b>P</b> is the pivot matrix.",
                                "LUP"},
                        {"Diagonalization",
                                "Finds matrices <b>P</b> and <b>D</b> such that" +
                                        "<div><b>A = PDP</b><sup><small>-1</small></sup></div>" +
                                        "where <b>D</b> is a diagonal matrix with the eigenvalues of <b>A</b> as its diagonal entries, " +
                                        "and <b>P</b> is a matrix with the eigenvectors of <b>A</b> as its columns.",
                                "DIAG"},
                        {"QR decomposition",
                                "Finds matrices <b>Q</b> and <b>R</b> such that" +
                                        "<div><b>A = QR</b></div>" +
                                        "where <b>Q</b> is orthogonal, <b>R</b> is upper triangular.",
                                "QR"},
                        {"Rank Revealing QR decomposition",
                                "Finds matrices <b>P</b>, <b>Q</b> and <b>R</b> such that" +
                                        "<div><b>AP = QR</b></div>" +
                                        "where <b>Q</b> is orthogonal, <b>R</b> is upper triangular, and <b>P</b> is the pivot matrix.",
                                "RRQR"},
                        {"Cholesky decomposition",
                                "Finds a matrix <b>L</b> such that" +
                                        "<div><b>A = LL</b><sup><small>T</small></sup></div>" +
                                        "where <b>L</b> is lower triangular.",
                                "Cholesky"},
                        {"Singular value decomposition",
                                "Finds matrices <b>U</b>, <b>Σ</b> and <b>V</b> such that" +
                                        "<div><b>A = UΣV</b><sup><small>T</small></sup></div>" +
                                        "If <b>A</b> is an <i>m×n</i> matrix, then <b>U</b> is an <i>m×p</i> orthogonal matrix, " +
                                        "<b>Σ</b> is a <i>p×p</i> diagonal matrix with positive or null elements " +
                                        "(the nonzero elements are the singular values of <b>A</b>), " +
                                        "<b>V</b> is a <i>p×n</i> orthogonal matrix (hence <b>V</b><sup><small>T</small></sup> is also orthogonal) " +
                                        "where <i>p=min(m,n)</i>.",
                                "SVD"}
                };

                for (int i = 0; i < decompositionsArray.length; i++) {
                    Token[] tempToken = new Token[3];
                    if (i == 0) {
                        if (((Matrix) t).isSquare() && MatrixUtils.findDeterminant(a) != 0) {//LUP
                            for (int j = 0; j < tempToken.length; j++) {
                                tempToken[j] = new StringToken(decompositionsArray[i][j]);
                            }
                            decompositions.add(tempToken);
                        }
                    } else if (i == 1) {
                        if (((Matrix) t).isSquare()) {//Diagonalization
                            for (int j = 0; j < tempToken.length; j++) {
                                tempToken[j] = new StringToken(decompositionsArray[i][j]);
                            }
                            decompositions.add(tempToken);
                        }
                    } else if (i == 4) { //Cholesky
                        if (((Matrix) t).isSymmetric() && MatrixUtils.findDeterminant(a) != 0) {
                            for (int j = 0; j < tempToken.length; j++) {
                                tempToken[j] = new StringToken(decompositionsArray[i][j]);
                            }
                            decompositions.add(tempToken);
                        }
                    } else {//QR, RRQR, SVD
                        for (int j = 0; j < tempToken.length; j++) {
                            tempToken[j] = new StringToken(decompositionsArray[i][j]);
                        }
                        decompositions.add(tempToken);
                    }
                }

                if (decompositions.size() == 0) {
                    Token[] tempToken = new Token[3];
                    tempToken[0] = new StringToken("No decompositions available for this matrix");
                    tempToken[1] = new StringToken("");
                    tempToken[2] = new StringToken("");
                    decompositions.add(tempToken);
                }
            } else {
                throw new IllegalArgumentException("The result must be a single Matrix to be factorizable");
            }
        } catch (Exception e) { //an error was thrown
            Log.d(DecompositionSelectionActivity.class.getSimpleName(),e.getMessage());
        }
    }

    @Override
    public String getViewTitle() {
        return "Matrix Decomposition";
    }

    /**
     * The custom Adapter for the ListView in the row reduction steps.
     */
    private class DecompAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return decompositions.size();
        }

        @Override
        public Object getItem(int position) {
            return decompositions.get(position);
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
                //Inflates the XML file to get the View of the decomposition element
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.decomposition_element, parent, false);
            }

            //Sets up the child Views within each item in the ListView
            TextView type = (TextView) convertView.findViewById(R.id.type);
            TextView info = (TextView) convertView.findViewById(R.id.info);

            //Sets the font size of each OutputView
            /*
            type.setFontSize((int) (activity.getFontSize() * CONSTANTS_IO_RATIO));
            info.setFontSize((int) (activity.getFontSize() * CONSTANTS_IO_RATIO * CONSTANTS_IO_RATIO));
            */

            //Enters the appropriate expressions to the TextViews
            Token[] entry = decompositions.get(position);

            type.setText(entry[0].getSymbol());
            info.setText(Html.fromHtml(entry[1].getSymbol()));

            //To respond to user touches
            final Token INPUT = decompositions.get(position)[2]; //Makes a constant reference so that intermediate matrices can be accessed by an inner class
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    final MatrixMode matrixMode = MatrixMode.getInstance();
                    if (INPUT instanceof StringToken) {
                        String s = INPUT.getSymbol();
                        if (s.equals("LUP")) {
                            matrixMode.lastAction = new Command<Void, Void>() {
                                @Override
                                public Void execute(Void o) {
                                    matrixMode.clickLUP();
                                    return null;
                                }
                            };
                            matrixMode.clickLUP();
                        } else if (s.equals("DIAG")) {
                            matrixMode.lastAction = new Command<Void, Void>() {
                                @Override
                                public Void execute(Void o) {
                                    matrixMode.clickDiagonalize();
                                    return null;
                                }
                            };
                            matrixMode.clickDiagonalize();
                        } else if (s.equals("QR")) {
                            matrixMode.lastAction = new Command<Void, Void>() {
                                @Override
                                public Void execute(Void o) {
                                    matrixMode.clickQR();
                                    return null;
                                }
                            };
                            matrixMode.clickQR();
                        } else if (s.equals("RRQR")) {
                            matrixMode.lastAction = new Command<Void, Void>() {
                                @Override
                                public Void execute(Void o) {
                                    matrixMode.clickRRQR();
                                    return null;
                                }
                            };
                            matrixMode.clickRRQR();
                        } else if (s.equals("Cholesky")) {
                            matrixMode.lastAction = new Command<Void, Void>() {
                                @Override
                                public Void execute(Void o) {
                                    matrixMode.clickCholesky();
                                    return null;
                                }
                            };
                            matrixMode.clickCholesky();
                        } else if (s.equals("SVD")) {
                            matrixMode.lastAction = new Command<Void, Void>() {
                                @Override
                                public Void execute(Void o) {
                                    matrixMode.clickSVD();
                                    return null;
                                }
                            };
                            matrixMode.clickSVD();
                        }
                    }
                }
            });
            return convertView;
        }
    }
}
