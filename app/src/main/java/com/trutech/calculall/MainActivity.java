package com.trutech.calculall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * This is the class for the main activity (entry-point to the app). It will simply configure
 * the setting then go to the basic activity.
 *
 * @version 0.4.0
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Goes to the basic activity
        Intent intent = new Intent(this, Basic.class);
        startActivity(intent);
    }


}
