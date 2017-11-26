package com.ibeis.wildbook.wildbook;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;
import android.widget.ProgressBar;

/****************************************
 * Created by Arjan on 11/25/2017.
 ****************************************************/

public class ActivityUpdater extends BroadcastReceiver {
    public static Activity activeActivity;
    public String TAG = "ActivityUpdater";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "in OnReceiive!!!");
        int textToDisplay = intent.getIntExtra("string",0);

        if (activeActivity!=null){
            //do the updating....
            //this is just a test.
            if(activeActivity instanceof MainActivity){

                if(textToDisplay== (R.string.offline) && textToDisplay!=0)
                    ((MainActivity)activeActivity).displaySnackBar(textToDisplay,MainActivity.WARNING);
                else if(textToDisplay!=0){
                    ((MainActivity)activeActivity).displaySnackBar(textToDisplay,MainActivity.POS_FEEDBACK);
                }
            }
            /*else if(){

            }*/

        }
    }
}
