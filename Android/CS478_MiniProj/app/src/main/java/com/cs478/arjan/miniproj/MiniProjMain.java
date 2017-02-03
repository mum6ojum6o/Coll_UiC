package com.cs478.arjan.miniproj;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static android.content.Intent.ACTION_VIEW;

public class MiniProjMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_proj_main);
        final Button topButton = (Button) findViewById(R.id.buttonA);
        final Button botButton = (Button) findViewById(R.id.buttonB);

        // Top Button's onClickListener
        topButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent activateChild =new Intent(MiniProjMain.this,ChilActivity.class);
               // Calling the child activity
                startActivityForResult(activateChild,1);
            }
        });
        //Bottom button's onCLickListener
        botButton.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               // initializing the implicit intent
            Intent activateBotButton = new Intent(ACTION_VIEW, Uri.parse("https://developer.android.com/index.html"));
            startActivity(activateBotButton);
        }
        });
    }
    //onActivityResult method
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.i("MiniProjMain", "in onActivityResult");
        String number;
        TextView message=(TextView)findViewById(R.id.message); //Declaring/initializing the TextView to display the appropriate message.
        message.setText(""); //Setting the intial value for the text to blank
        //checking if the result returned by the child activity was true.
        if (resultCode==RESULT_OK) {
            number=data.getData().toString(); //Obtaining the previous number dialed via the intent data
            //Initializing the message to be displayed on succesfully extracting the number and returning from the child activity.
            message.setText(number+" was dialed!");
        }
        else
            message.setText("Number not found!");
    }//onActivity Result Ends

}
