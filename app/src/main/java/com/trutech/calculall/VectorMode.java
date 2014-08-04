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
     *
     * @param vectors
     * @return
     */
    public int calculateDotProduct (int[][] vectors){
        return null;
    }

    /**
     *
     * @param vectors
     * @return
     */
    public int calculateCrossProduct (int[][] vectors){
        return null;
    }
}
