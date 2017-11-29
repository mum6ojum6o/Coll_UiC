package com.ibeis.wildbook.wildbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.R;
import static android.net.ConnectivityManager.EXTRA_NO_CONNECTIVITY;

/******************************************************************************
 * Created by Arjan on 11/10/2017.
 * This class/ Broadcast should be activated once Wifi Connection is available.
 * Once signal is available, it should call the SyncerService class.
 ***************************************************************/

public class NetworkScanner extends BroadcastReceiver {
    public static String TAG="NetworkScanner";
    public static int count=0;
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext=context;
            Intent broadcastIntent = new Intent();//intent used to send broadcast to ActivityUpdater Broadcast receiver.
            broadcastIntent.setAction("com.ibeis.Wildbook.Wildbook_demos");
            Bundle bundle = intent.getExtras();
            NetworkInfo networkInfo = (NetworkInfo) bundle.get(WifiManager.EXTRA_NETWORK_INFO);
            boolean noConnectivity = bundle.getBoolean(EXTRA_NO_CONNECTIVITY);
            Log.i(TAG, "networkInfo=" + networkInfo.isConnected() + " isConnected=" + noConnectivity + " count=" + count);
            //Context context1=context.getApplicationContext();
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo newNetworkInfo = null;

            newNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if ((new Utilities(context).isNetworkAvailable() || (newNetworkInfo != null && newNetworkInfo.isConnected()))
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                    && (new Utilities(mContext).getSyncSharedPreference()
                            .equals(mContext.getResources()
                            .getString(com.ibeis.wildbook.wildbook.R.string.wifiString)) ||
                    new Utilities(mContext).getSyncSharedPreference()
                            .equals(mContext.getResources()
                                    .getString(com.ibeis.wildbook.wildbook.R.string.anyString)))) {
                Log.i(TAG,"Wifi is connected");
               // Log.i(TAG, "Startring Service MainActivity is Running?" + MainActivity.MAIN_ACTIVITY_IS_RUNNING);
                takeAction(context);

            } else if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE
                    && networkInfo.isConnected()
                    && (new Utilities(mContext).getSyncSharedPreference()
                    .equals(mContext.getResources()
                            .getString(com.ibeis.wildbook.wildbook.R.string.mobiledataString)) ||
                    new Utilities(mContext).getSyncSharedPreference()
                            .equals(mContext.getResources()
                                    .getString(com.ibeis.wildbook.wildbook.R.string.anyString)))) { //if mobile data is connected.
                Log.i(TAG,"Mobiledata is connected");
                /*Log.i("NetScanner", "Startring Service MainActivity is Running?"
                        + MainActivity.MAIN_ACTIVITY_IS_RUNNING);*/
                takeAction(context);
            }else if(networkInfo != null && networkInfo.isConnected() && new Utilities(mContext).getSyncSharedPreference().equals("Sync_Preference")){
                Log.i(TAG,"Connected but no user preferences found...");
                takeAction(context);
            }
            else if(!new Utilities(context).isNetworkAvailable()){
                //if (MainActivity.MAIN_ACTIVITY_IS_RUNNING) {
                    //MainActivity.displayOnlineStatus(MainActivity.OFFLINE);
                    int message = com.ibeis.wildbook.wildbook.R.string.offline;
                    broadcastIntent.putExtra("string",message);
                    mContext.sendBroadcast(broadcastIntent);
                //}

            }
       /* }
        else{
            Log.i(TAG,"User not logged in. NetworkScanner will not take any action");
            Toast.makeText(context,"No action taken by NetworkScanner as User is not Logged in!!",Toast.LENGTH_LONG).show();
        }*/
    }
    public void takeAction(Context context){
        //if(MainActivity.MAIN_ACTIVITY_IS_RUNNING){
            Intent broadcastIntent = new Intent();//intent used to send broadcast to ActivityUpdater Broadcast receiver.
            broadcastIntent.setAction("com.ibeis.Wildbook.Wildbook_demos");
            int message = com.ibeis.wildbook.wildbook.R.string.online;
            broadcastIntent.putExtra("string",message);
            context.sendBroadcast(broadcastIntent);

        //}
        if(!SyncerService.IsRunning) {

            Utilities utility = new Utilities(context);
            Log.i(TAG," Starting Service");
            utility.startSyncing(); //start service....
        }
    }


}
