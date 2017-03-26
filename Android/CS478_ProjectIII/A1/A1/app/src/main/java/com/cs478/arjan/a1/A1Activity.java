package com.cs478.arjan.a1;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static android.content.Intent.FLAG_INCLUDE_STOPPED_PACKAGES;

public class A1Activity extends Activity {
    private Button buttonBaseball;
    private Button buttonBasketball;
    private static final String PROJ_PERMISSION = "edu.uic.cs478.project3";
    private static final String ACTION = "edu.uic.cs478.project3.broadcast";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a1);
        buttonBaseball = (Button) findViewById(R.id.buttonBaseball);
        buttonBasketball = (Button) findViewById(R.id.buttonBasketball);
        buttonBaseball.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // do something
                Intent intent = new Intent(ACTION);
                intent.setFlags(FLAG_INCLUDE_STOPPED_PACKAGES);//citing:- http://stackoverflow.com/questions/35666172/application-have-only-broadcastreceiver-is-not-working
                //intent.setData(Uri.parse("content:baseball"));
                intent.putExtra("A1","Baseball");
                Log.i("ProjectIII","A1 Before Broadcast");
                sendOrderedBroadcast(intent,null);
            }
        });
        buttonBasketball.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Is there a way I can make this run using a single onclicklistener method?

                Intent intent = new Intent();
                intent.setAction(ACTION);
                intent.putExtra("A1","Basketball");
                intent.setFlags(FLAG_INCLUDE_STOPPED_PACKAGES);
                //intent.setData(Uri.parse("content: basketball"));
                if (ContextCompat.checkSelfPermission(getApplicationContext(), PROJ_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                    Log.i("Project III","A1: Application granted to Broadcast!");
                    sendOrderedBroadcast(intent, null);
                }
                else{
                    Log.i("Project III", "A2: Requesting permission!");
                   // ActivityCompat.requestPermissions(getApplicationContext(), new String[]{"edu.uic.cs478.project3"}, 0);
                }
            }
        });
    } //onCreate ends
    public void onResume() {
        super.onResume()  ;
        if (ContextCompat.checkSelfPermission(this, "edu.uic.cs478.project3") !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"edu.uic.cs478.project3"}, 0);
        }
    }

    public void onRequestPermissionsResult(int code, String[] permissions, int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, go ahead and display map
            Log.i("PojectII", "Permission Granted!!!") ;
        }
        else {
            Toast.makeText(this, "BUMMER: No Permission :-(", Toast.LENGTH_LONG).show() ;
            onStop();
        }


    }

   }

