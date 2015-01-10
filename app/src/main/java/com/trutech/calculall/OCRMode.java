package com.trutech.calculall;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

public class OCRMode extends Activity {

    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO:READD THESE PERMISSIONS:    <uses-permission android:name="android.permission.CAMERA" />
        //<uses-feature android:name="android.hardware.camera" />
        //<uses-feature android:name="android.hardware.camera.autofocus" />
        //super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocrmode);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        image = (Bitmap) data.getExtras().get("data");

    }

}
