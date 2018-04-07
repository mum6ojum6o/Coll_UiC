package com.ibeis.wildbook.wildbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import static android.net.ConnectivityManager.EXTRA_NO_CONNECTIVITY;

/******************************************************************************
 * Created by Arjan on 11/10/2017.
 * This class/Broadcast should be activated once Wifi Connection is available.
 * Once signal is available, it should call the SyncerService class.
 ***************************************************************/

public class NetworkScannerBroadcastReceiver extends BroadcastReceiver {
    public static String TAG="NetworkScannerBroadcastReceiver";
    public static int count=0;
    public static final int  NETWORK_HAS_INTERNET= 10244;
    public static final int NO_INTERNET_ACCESS= 7183;
    Context mContext;
    /***********************************
     * Handlers may cause memory leaks. This is because non-static Handlers hold a reference of
     * their outer class(NetworkScannerBroadcastReceiver).
     * In the event the activity is suspended but there is a delayed message in its message queue,
     * the delayed message will continue to remain in the main thread until it is processed.
     * ***********************************************************/
    public Handler networkScannerBRHandler = new Handler(){
      public void handleMessage(Message msg){
        switch(msg.what){
            case NETWORK_HAS_INTERNET:
                Log.i(TAG,"NETWORK_HAS_INTERNET");
                takeAction(mContext);
                break;
            case NO_INTERNET_ACCESS:
                Log.i(TAG,"NO_INTERNET");
                Intent broadcastIntent = new Intent();//intent used to send broadcast to ActivityUpdaterBroadcastReceiver Broadcast receiver.
                broadcastIntent.setAction("com.ibeis.Wildbook.Wildbook_demos");
                int message = com.ibeis.wildbook.wildbook.R.string.offline;
                broadcastIntent.putExtra("string",message);
                mContext.sendBroadcast(broadcastIntent);
                break;
        }
      }
    };
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext=context;
            Intent broadcastIntent = new Intent();//intent used to send broadcast to ActivityUpdaterBroadcastReceiver Broadcast receiver.
            broadcastIntent.setAction("com.ibeis.Wildbook.Wildbook_demos");
            Bundle bundle = intent.getExtras();
            NetworkInfo networkInfo = (NetworkInfo) bundle.get(WifiManager.EXTRA_NETWORK_INFO);
            boolean noConnectivity = bundle.getBoolean(EXTRA_NO_CONNECTIVITY);
            Log.i(TAG, "networkInfo=" + networkInfo.isConnected() + " isConnected=" + noConnectivity + " count=" + count);
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo newNetworkInfo = null;
            newNetworkInfo = connectivityManager.getActiveNetworkInfo();
            /*if ((new Utilities(context).isNetworkAvailable() || (newNetworkInfo != null && newNetworkInfo.isConnected()))
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.i(TAG,"Wifi is connected");
                takeAction(context);
            }
            else if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE
                    && networkInfo.isConnected()
                    && (new Utilities(mContext).getSyncSharedPreference()
                    .equals(mContext.getResources()
                            .getString(com.ibeis.wildbook.wildbook.R.string.mobiledataString)))) { //if mobile data is connected.
                Log.i(TAG,"Mobiledata is connected");
                takeAction(context);
            }else if(networkInfo != null && networkInfo.isConnected() && new Utilities(mContext).getSyncSharedPreference().equals("Sync_Preference")){
                Log.i(TAG,"Connected but no user preferences found...");
                takeAction(context);
            }
            else if(!new Utilities(context).isNetworkAvailable()){
                int message = com.ibeis.wildbook.wildbook.R.string.offline;
                broadcastIntent.putExtra("string",message);
                mContext.sendBroadcast(broadcastIntent);
            }*/
            Utilities utility = new Utilities(mContext);
            if(utility.isNetworkAvailable() && newNetworkInfo.isConnected()
                    && networkInfo.getType()==ConnectivityManager.TYPE_WIFI){
                Log.i(TAG,"Wifi is connected");
                isOnline();
            }
            else if(utility.isNetworkAvailable() && newNetworkInfo.isConnected()
                    && networkInfo.getType()==ConnectivityManager.TYPE_MOBILE){
                Log.i(TAG,"Mobiledata is connected");
                isOnline();
            }
            else if(!new Utilities(context).isNetworkAvailable()){
                int message = com.ibeis.wildbook.wildbook.R.string.offline;
                broadcastIntent.putExtra("string",message);
                mContext.sendBroadcast(broadcastIntent);
            }
        }
    public static void takeAction(Context context){

        Intent broadcastIntent = new Intent();//intent used to send broadcast to ActivityUpdaterBroadcastReceiver Broadcast receiver.
        broadcastIntent.setAction("com.ibeis.Wildbook.Wildbook_demos");
        int message = com.ibeis.wildbook.wildbook.R.string.online;
        broadcastIntent.putExtra("string",message);
        context.sendBroadcast(broadcastIntent);
        Log.i(TAG,"Is ServiceRunning"+SyncerService.IsRunning);
        if(!SyncerService.IsRunning) {
            Log.i(TAG,"Is ServiceRunning"+SyncerService.IsRunning);
            //SyncerService.IsRunning=true;
            Utilities utility = new Utilities(context);
            Log.i(TAG," Starting Service");
            utility.startSyncing(); //start service....
        }
    }
public void isOnline() {
new Thread(new Runnable(){
    @Override
    public void run() {
        if(doesDeviceHaveInternet()){
            //call handler to inform that network is connected.
            Message msg = networkScannerBRHandler.obtainMessage(NETWORK_HAS_INTERNET);
            networkScannerBRHandler.sendMessage(msg);
        }
        else{
            //call handler to inform that network is not connected.
            Message msg = networkScannerBRHandler.obtainMessage(NO_INTERNET_ACCESS);
            networkScannerBRHandler.sendMessage(msg);
        }
    }
}).start();
}
// This method will check whether the current connected network has internet access.
    //Source:-
    public boolean doesDeviceHaveInternet() {
        try {
            int timeoutMs = 1000;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);
            sock.connect(sockaddr, timeoutMs);
            sock.close();
            return true;
        } catch (IOException e) { return false; }
    }

}
