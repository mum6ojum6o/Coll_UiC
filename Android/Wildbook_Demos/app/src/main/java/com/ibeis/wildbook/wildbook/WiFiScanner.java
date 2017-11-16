package com.ibeis.wildbook.wildbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApi;

import static android.net.ConnectivityManager.EXTRA_NO_CONNECTIVITY;

/******************************************************************************
 * Created by Arjan on 11/10/2017.
 * This class/ Broadcast should be activated once Wifi Connection is available.
 * Once signal is available, it should call the SyncerService class.
 ***************************************************************/

public class WiFiScanner extends BroadcastReceiver {
    public static String TAG="WiFiScanner";
    public static int count=0;

    @Override
    public void onReceive(Context context, Intent intent){
        Bundle bundle= intent.getExtras();
        NetworkInfo networkInfo = (NetworkInfo) bundle.get(WifiManager.EXTRA_NETWORK_INFO);
        boolean noConnectivity= bundle.getBoolean(EXTRA_NO_CONNECTIVITY);
        Log.i(TAG,"networkInfo="+networkInfo.isConnected()+" isConnected="+noConnectivity+" count="+count);
        //Context context1=context.getApplicationContext();
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo newNetworkInfo=null;

            newNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (new Utilities(context).isNetworkAvailable() ||(newNetworkInfo!=null && newNetworkInfo.isConnected())||networkInfo.isConnected()) {
                Log.i("Receiver", "Startring Service MainActivity is Running?"+MainActivity.MAIN_ACTIVITY_IS_RUNNING);

                if(MainActivity.MAIN_ACTIVITY_IS_RUNNING){
                    MainActivity.displayOnlineStatus(MainActivity.ONLINE);
                }
                if(!SyncerService.IsRunning) {

                    Utilities utility = new Utilities(context);
                    Log.i(TAG," Starting Service");
                    utility.startSyncing(); //start service....
                }
            } else {
                if(MainActivity.MAIN_ACTIVITY_IS_RUNNING){
                    MainActivity.displayOnlineStatus(MainActivity.OFFLINE);

                }
                //Toast.makeText(context, "Wifi Not Connected!!", Toast.LENGTH_SHORT).show();
            }

    }
}
