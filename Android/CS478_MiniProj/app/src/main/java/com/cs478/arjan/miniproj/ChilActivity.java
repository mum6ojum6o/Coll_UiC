package com.cs478.arjan.miniproj;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Intent.ACTION_CALL;
import static android.content.Intent.ACTION_DIAL;

public class ChilActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private String number;
    private String input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chil);
        final EditText textChild = (EditText) findViewById(R.id.textChild);
        final Button buttonChild = (Button) findViewById(R.id.buttonChild);

        buttonChild.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                input=null;
                if (textChild.getText().toString()!=null) {
                    //extracting the data entered in the edit text field by the user in the child activity

                    input = textChild.getText().toString();
                    Log.i("ChildActivity","buttonChild.setOnClickLstener_editText"+number);//logging the input extered by the user.
                    //calling the numberExtracter method to phone number
                    number = numberExtracter(input);
                    Log.i("ChildActivity","number extracted:"+number); //logging the number extracted by the function.
                    // if no number is extracted then do not call the phone activity.
                    if (!number.equals("nothing")) {
                        Intent call = new Intent(ACTION_DIAL, Uri.parse("tel:" + number));
                        Log.i("ChildActivity", "Calling Dialer");
                        startActivity(call);
                    }
                }

                //System.out.println("number Extracter output"+number);
            } //onClick Ends

        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }
    // new method to return the result of child activity added 1/29/2017

    //function to extract the phone number
    private String numberExtracter(String input) {
        //regex pattern to extract number format (xxx) xxx-xxxx| (xxx)xxx-xxxx| xxx-xxxx

        String pattern1 = "(\\([0-9]{3}\\)( |)[0-9]{3}-[0-9]{4}|([^0-9]|^)[0-9]{3}-[0-9]{4})";
        Pattern r = Pattern.compile(pattern1);
        if (input != null) {
            Matcher m = r.matcher(input);
            if (m.find()) {// check if a pattern was found
                String extracted=null; //variable to hold the extracted pattern
                Log.i("NumberExtracted:",m.group());//logging the number extracted by the pattern
                extracted=m.group();
                if (Character.isLetter(extracted.charAt(0))) { //checking if the first character of the pattern is a letter
                    extracted=extracted.substring(1);//remove the first character from the pattern if it is a letter.
                    return extracted;
                }
                else
                    return extracted;
            }//return the phone number extracted by the regex.
            else
                return "nothing"; //return nothing if no number was extracted by the pattern.
        } else
            return "nothing"; //return nothing if the no data was entered.
    } //numberExtractor ends.

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Chil Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    //onStop Callback method
    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }//onStop ends
    @Override
    public void onPause(){
        super.onPause();
        // in case no number has been extracted
        if (number==null) {
            System.out.println("no number extracted;if condition");
            setResult(RESULT_CANCELED);
         }
        else if (!number.equals("nothing")){
            //if a phone number was dialled, save that number in the intent's data section.
            Intent retIntent= new Intent();
            retIntent.setData(Uri.parse(number));
            setResult(RESULT_OK,retIntent);
        }
        else{
            System.out.println("no number extracted");
            setResult(RESULT_CANCELED);
        }
    }//onPause ends
}//class ends
