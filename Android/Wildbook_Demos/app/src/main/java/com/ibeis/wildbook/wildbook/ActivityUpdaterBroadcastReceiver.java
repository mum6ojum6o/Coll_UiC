package com.ibeis.wildbook.wildbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/****************************************
 * Created by Arjan on 11/25/2017.
 ****************************************************/

public class ActivityUpdaterBroadcastReceiver extends BroadcastReceiver {
    public static BaseActivity activeActivity;
    public String TAG = "ActivityUpdaterBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "in OnReceiive!!!");
        int textToDisplay = intent.getIntExtra("string",0);

        if (activeActivity!=null){

            if(activeActivity instanceof MainActivity){
                if(textToDisplay== (R.string.offline) && textToDisplay!=0) {
                    Log.i(TAG,"Displaying Ofline Message");
                    ((MainActivity) activeActivity).displaySnackBar(textToDisplay, MainActivity.WARNING);
                }
                else if(textToDisplay!=0){
                    Log.i(TAG,"Displaying Online Message");
                    ((MainActivity)activeActivity).displaySnackBar(textToDisplay,MainActivity.POS_FEEDBACK);
                }
            }
            else if(activeActivity instanceof UserContributionsActivity){
                if(textToDisplay== (R.string.offline) && textToDisplay!=0) {
                    Log.i(TAG,"Displaying Ofline Message");
                    ((UserContributionsActivity) activeActivity).displaySnackBar(textToDisplay, MainActivity.WARNING);
                }
                else if(textToDisplay!=0){
                    Log.i(TAG,"Displaying Online Message");
                    ((UserContributionsActivity)activeActivity).displaySnackBar(textToDisplay,MainActivity.POS_FEEDBACK);
                }
            }
            else {
                if(textToDisplay== (R.string.offline) && textToDisplay!=0){
                    Log.i(TAG,"Displaying Ofline Message to other Activities");
                    activeActivity.setLAYOUT();
                    activeActivity.displaySnackBar(textToDisplay, MainActivity.WARNING);
                }
                else if(textToDisplay!=0){
                    Log.i(TAG,"Displaying Online Message to other Activities");
                    activeActivity.displaySnackBar(textToDisplay,MainActivity.POS_FEEDBACK);
                }
            }
        }
    }
}
