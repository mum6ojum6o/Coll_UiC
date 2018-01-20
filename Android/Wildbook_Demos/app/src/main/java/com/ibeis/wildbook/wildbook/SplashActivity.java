package com.ibeis.wildbook.wildbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**********
 * Activity to display the splash screen of the mobile application.
 ***********/
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(SplashActivity.this,Login.class));
        finish();
    }
}
