package com.trutech.calculall;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class VectorMode extends Basic {

    private boolean mem = false;
    private ToggleButton memButton;
    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vector);
        memButton = (ToggleButton)findViewById(R.id.memButton);
        output = (TextView) findViewById(R.id.txtStack);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * When the user presses the + Button.
     *
     * @param v Not Used
     */
    public void clickAdd(View v){
        tokens.add(OperatorFactory.makeAdd());
        updateInput();
    }

    /**
     * When the user presses the - Button.
     *
     * @param v Not Used
     */
    public void clickSubtract(View v){
        tokens.add(OperatorFactory.makeSubtract());
        updateInput();
    }


    /**
     * When the user presses the equals Button.
     *
     * @param v Not Used
     */
    public void clickEquals(View v){
        TextView output = (TextView) findViewById(R.id.txtStack);
        try{
            String s = Double.toString(process());
            s = s.indexOf(".") < 0  ? s : (s.indexOf("E")>0 ? s.substring(0,s.indexOf("E")).replaceAll("0*$", "")
                    .replaceAll("\\.$", "").concat(s.substring(s.indexOf("E"))) : s.replaceAll("0*$", "")
                    .replaceAll("\\.$", "")); //Removes trailing zeroes
            output.setText(s);
        }catch (Exception e){ //User did a mistake
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
        }
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.pageScroll(ScrollView.FOCUS_DOWN);
    }

    /**
     * When the user presses the MEM button; toggles memory storage
     *
     * @param v Not Used
     */
    public void clickMem(View v){
        if(mem){
            mem = false;
        }else{
            mem = true;
        }
        memButton.setChecked(mem);
    }

    /**
     * When the user presses the A button
     *
     * @param v Not Used
     */
    public void clickA(View v){
        double val = process();
        if(mem){
            tokens.clear();
            output.setText(val + "→ A");
            Variable.a_value = val;
            mem = false;
        }else{
            tokens.add(VariableFactory.makeA());
        }
        memButton.setChecked(mem);
        updateInput();
    }

    /**
     * When the user presses the B button
     *
     * @param v Not Used
     */
    public void clickB(View v){
        double val = process();
        if(mem){
            tokens.clear();
            output.setText(val + "→B");
            Variable.b_value = val;
            mem = false;
        }else{
            tokens.add(VariableFactory.makeB());
        }
        memButton.setChecked(mem);
        updateInput();
    }

    /**
     * When the user presses the C button
     *
     * @param v Not Used
     */
    public void clickC(View v){
        double val = process();
        if(mem){
            tokens.clear();
            output.setText(val + "→C");
            Variable.c_value = val;
            mem = false;
        }else{
            tokens.add(VariableFactory.makeC());
        }
        memButton.setChecked(mem);
        updateInput();
    }

    /**
     * When the user presses the X button
     *
     * @param v Not Used
     */
    public void clickX(View v){
        double val = process();
        if(mem){
            tokens.clear();
            output.setText(val + "→X");
            Variable.x_value = val;
            mem = false;
        }else{
            tokens.add(VariableFactory.makeX());
        }
        memButton.setChecked(mem);
        updateInput();
    }

    /**
     * When the user presses the Y button
     *
     * @param v Not Used
     */
    public void clickY(View v){
        double val = process();
        if(mem){
            tokens.clear();
            output.setText(val + "→Y");
            Variable.y_value = val;
            mem = false;
        }else{
            tokens.add(VariableFactory.makeY());
        }
        memButton.setChecked(mem);
        updateInput();
    }

    /**
     * When the user presses the Z button
     *
     * @param v Not Used
     */
    public void clickZ(View v){
        double val = process();
        if(mem){
            tokens.clear();
            output.setText(val + "→Z");
            Variable.z_value = val;
            mem = false;
        }else{
            tokens.add(VariableFactory.makeZ());
        }
        memButton.setChecked(mem);
        updateInput();
    }

    /**
     * When the user presses the ( Button.
     *
     * @param v Not Used
     */
    public void clickOpenSquareBracket(View v){
        tokens.add(BracketFactory.createOpenSquareBracket());
        updateInput();
    }

    /**
     * When the user presses the ) Button.
     *
     * @param v Not Used
     */
    public void clickCloseSquareBracket(View v){
        tokens.add(BracketFactory.createCloseSquareBracket());
        updateInput();
    }

    /**
     * The parameter vectors should be set up so that each vector is in it's own column
     * for example if the vectors are 2D vectors the first vector's x co-ordinate should be stored in vectors[0][0]
     * the y co-ordinate should be stored in vectors[0][1]
     * for the second vector the x co-ordinate should be stored in vectors[1][0]
     * the y co-ordinate for the second vector should be stored in vectors[1][1]
     * @param vectors     is a 2D array that holds the 2 vectors that we are trying to find the dot product of
     * @return            will return the answer as a double or if it can't calculate it returns null
     */
    public double calculateDotProduct (double[][] vectors){

        //first solution
        //can handle 1D, 2D and 3D vectors
        //for 2D vectors
        if (vectors[0].length == 2){
            double dotProduct = vectors[0][0] * vectors[1][0] + vectors[0][1] * vectors[1][1];
            return dotProduct;
        }
        //for 3D vectors
        elsif (vectors[0].length == 3) {
            double dotProduct = vectors[0][0] * vectors[1][0] + vectors[0][1] * vectors[1][1] + vectors[0][2] * vectors[1][2];
            return dotProduct;
        }
        //for 1D vectors basically just multiplication
        elsif{
            double dotProduct = vectors[0][0] * vectors[1][0];
            return dotProduct;
        }
        //if the vectors[0].length is greater than 3 it would mean dealing with vectors that are 4D or higher
        else{
            return null;
        }




        //second solution
        // can handle vectors no matter how many dimensions the vector has
        // this if is to make sure both vectors will be the same type basically to make sure you are finding the dot product between a 2D vector and another
        // 2D vector not between a 2D vector and a 3D veector
        if (vectors[0].length == vectors[1].length) {
            //are we dealing with 2D vectors?, 3D vectors?, 4D vectors? so on
            int dimensions = vectors[0].length;
            //holds the answer
            double dotProduct = 0;
            //this for loop wll be able to do dot products no matter how many dimensions there are
            //loop as many times as here are vectors
            for (int i = 0; i < dimensions; i++) {
                //first time it goes through it will multiply the x co-ordinate aka first dimension of each vector with each other
                //the product is added to the variable dot product
                //second run it will multiply the y co-ordinate aka second dimension of each vector with each other
                //this second product will be added to the dot product
                //for 2D vectors this would be the dot product and the loop would end if the vectors are 3D or higher it would keep looping
                double dotProduct = dotProduct + vectors[0][i] * vectors[1][i];
            }
            //return the answer
            return dotProduct;
        }
        else {
            return null;
        }

    }

    /**
     *the parameter vectors should be set up the same way as it is for dot product
     * each of the two vectors is in it's own column
     * the co-ordinates for the first vector should be as such x is in vectors[0][0]
     * y is in vectors[0][1]  z is in vectors[0][2]
     * for the second vector x is in vectors[1][0]  y is in vectors[1][1]
     * z is in vectors[1][2]
     * @param vectors  is a 2D array that holds the 2 vectors that we are trying to find the dot product of
     * @return         returns the answer as a 1D array of doubles or if it can't calculate it will return null
     */
    public double[] calculateCrossProduct (double[][] vectors){
        if (vectors[0].length == 3 && vectors[1].length == 3) {
            double[] crossProduct = new double[3];
            crossProduct[0] = vectors[0][1] * vectors[1][2] - vectors[0][2] * vectors[1][1];
            crossProduct[1] = vectors[0][2] * vectors[1][0] - vectors[0][0] * vectors[1][2];
            crossProduct[2] = vectors[0][0] * vectors[1][1] - vectors[0][1] * vectors[1][2];
            return crossProduct;
        }
        else {
            return null;
        }
    }
}
