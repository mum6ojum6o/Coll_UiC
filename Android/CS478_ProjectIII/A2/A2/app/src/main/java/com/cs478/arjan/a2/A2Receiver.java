package com.cs478.arjan.a2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Arjan on 3/16/2017.
 */

public class A2Receiver extends BroadcastReceiver {
    public void onReceive(Context c, Intent i){

        String a= i.getStringExtra("A1");
        Log.i("Project III",a+" was selected");
        Toast.makeText(c, a+" was selected.", Toast.LENGTH_LONG).show() ;
    }
}
