package com.cs478.arjan.a3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Arjan on 3/16/2017.
 */

public class A3Receiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent i){
            Intent intent;
            String a= i.getStringExtra("A1");
            Log.i("Project III",a+" was selected in A3");
            if (a.equals("Baseball")){
                intent = new Intent(c, Baseball.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(intent);
            }
            else if (a.equals("Basketball")){
                intent = new Intent(c, Basketball.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(intent);
            }
            //Toast.makeText(c, "In A3 "+a+" was selected.", Toast.LENGTH_LONG).show() ;
        }
    }
