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
     * When the user presses the 1 Button.
     *
     * @param v Not Used
     */
    public void clickOne(View v){
        tokens.add(DigitFactory.makeOne());
        updateInput();
    }

    /**
     * When the user presses the 2 Button.
     *
     * @param v Not Used
     */
    public void clickTwo(View v){
        tokens.add(DigitFactory.makeTwo());
        updateInput();
    }

    /**
     * When the user presses the 3 Button.
     *
     * @param v Not Used
     */
    public void clickThree(View v){
        tokens.add(DigitFactory.makeThree());
        updateInput();
    }

    /**
     * When the user presses the 4 Button.
     *
     * @param v Not Used
     */
    public void clickFour(View v){
        tokens.add(DigitFactory.makeFour());
        updateInput();
    }

    /**
     * When the user presses the 5 Button.
     *
     * @param v Not Used
     */
    public void clickFive(View v){
        tokens.add(DigitFactory.makeFive());
        updateInput();
    }

    /**
     * When the user presses the 6 Button.
     *
     * @param v Not Used
     */
    public void clickSix(View v){
        tokens.add(DigitFactory.makeSix());
        updateInput();
    }

    /**
     * When the user presses the 7 Button.
     *
     * @param v Not Used
     */
    public void clickSeven(View v){
        tokens.add(DigitFactory.makeSeven());
        updateInput();
    }

    /**
     * When the user presses the 8 Button.
     *
     * @param v Not Used
     */
    public void clickEight(View v){
        tokens.add(DigitFactory.makeEight());
        updateInput();
    }

    /**
     * When the user presses the 9 Button.
     *
     * @param v Not Used
     */
    public void clickNine(View v){
        tokens.add(DigitFactory.makeNine());
        updateInput();
    }

    /**
     * When the user presses the 0 Button.
     *
     * @param v Not Used
     */
    public void clickZero(View v){
        tokens.add(DigitFactory.makeZero());
        updateInput();
    }

    /**
     * When the user presses the . Button.
     *
     * @param v Not Used
     */
    public void clickDecimal(View v){
        tokens.add(DigitFactory.makeDecimal());
        updateInput();
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
     * When the user presses the clear Button.
     *
     * @param v Not Used
     */
    public void clickClear(View v){
        tokens.clear();
        updateInput();
        TextView output = (TextView) findViewById(R.id.txtStack);
        output.setText(""); //Clears stack
    }

    /**
     * When the user presses the back Button.
     *
     * @param v Not Used
     */
    public void clickBack(View v){
        if (tokens.isEmpty()){
            return; //Prevents a bug
        }
        tokens.remove(tokens.size() - 1); //Removes last token
        updateInput();
    }

    /**
     * When the user presses the negative Button.
     *
     * @param v Not Used
     */
    public void clickNegative(View v){
        tokens.add(DigitFactory.makeNegative());
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
        tokens.add(BracketFactory.createOpenBracket());
        updateInput();
    }

    /**
     * When the user presses the ) Button.
     *
     * @param v Not Used
     */
    public void clickCloseSquareBracket(View v){
        tokens.add(BracketFactory.createCloseBracket());
        updateInput();
    }
}
