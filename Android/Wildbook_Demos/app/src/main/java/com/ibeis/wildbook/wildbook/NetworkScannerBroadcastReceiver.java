package com.ibeis.wildbook.wildbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
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
    public static final String LOCAL_BROADCAST_ACTION = "com.ibeis.wildbook.wildbook.demos";
    public static  NetworkChangeReceiverListener networkChangeReceiver;
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
                if(networkChangeReceiver!=null)
                    networkChangeReceiver.onNetworkStatusChanged(true);
                break;
            case NO_INTERNET_ACCESS:
                Log.i(TAG,"NO_INTERNET");
                //Android o onwards, implicit broadcasts are disabled.
                //For more info: https://commonsware.com/blog/2017/04/11/android-o-implicit-broadcast-ban.html
                //updateAppComponents(com.ibeis.wildbook.wildbook.R.string.offline);
                if(networkChangeReceiver!=null)
                    networkChangeReceiver.onNetworkStatusChanged(false);
                break;
        }
      }
    };
    @Override
    public void onReceive(Context context, Intent intent) {

        mContext=context;
            /*Intent broadcastIntent = new Intent();//intent used to send broadcast to ActivityUpdaterBroadcastReceiver Broadcast receiver.
            broadcastIntent.setAction("com.ibeis.Wildbook.Wildbook_demos");*/
            Bundle bundle = intent.getExtras();
            //updateAppComponents(0);

            NetworkInfo networkInfo = (NetworkInfo) bundle.get(WifiManager.EXTRA_NETWORK_INFO);
            boolean noConnectivity = bundle.getBoolean(EXTRA_NO_CONNECTIVITY);
            //Log.i(TAG, "networkInfo=" + networkInfo.isConnected() + " isConnected=" + noConnectivity + " count=" + count);
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo newNetworkInfo = null;
            newNetworkInfo = connectivityManager.getActiveNetworkInfo();
            Utilities utility = new Utilities(mContext);
            try{
                Thread.sleep(50); // intentionally delaying the thread, to allow the phone to connect to the internet
            }catch(Exception e){
                e.printStackTrace();
            }
            if(networkChangeReceiver !=null && newNetworkInfo!=null && newNetworkInfo.isConnectedOrConnecting() && networkInfo!=null
                    && networkInfo.getType()==ConnectivityManager.TYPE_WIFI){
                Log.i(TAG,"Wifi is connected");
                //isOnline();
                takeAction(mContext);
                networkChangeReceiver.onNetworkStatusChanged(true);
            }
            else if(networkChangeReceiver !=null && newNetworkInfo!=null && newNetworkInfo.isConnectedOrConnecting() && networkInfo!=null
                    && networkInfo.getType()==ConnectivityManager.TYPE_MOBILE){
                Log.i(TAG,"Mobiledata is connected");
                //isOnline();
                takeAction(mContext);
                networkChangeReceiver.onNetworkStatusChanged(true);
            }
            else if(!new Utilities(context).isNetworkAvailable()){
                 Message message = networkScannerBRHandler.obtainMessage(NO_INTERNET_ACCESS);
                 networkScannerBRHandler.sendMessage(message);

                //int message = com.ibeis.wildbook.wildbook.R.string.offline;
                //updateAppComponents(com.ibeis.wildbook.wildbook.R.string.offline);
               /* broadcastIntent.putExtra("string",message);
                mContext.sendBroadcast(broadcastIntent);*/
            }
        }
    public static void takeAction(Context context){
        Log.i(TAG,"Is ServiceRunning"+SyncerService.IsRunning);
        if(!SyncerService.IsRunning) {
            Log.i(TAG,"Is ServiceRunning"+SyncerService.IsRunning);
            //SyncerService.IsRunning=true;
            Utilities utility = new Utilities(context);
            Log.i(TAG," Starting Service");
            utility.startSyncing(); //start service....
        }
       //localBroadcastManager.unregisterReceiver(receiver);
    }

    //this is deprecated
public void isOnline() {
new Thread(new Runnable(){
    @Override
    public void run() {
        if(doesDeviceHaveInternet()){
            Log.i(TAG,"Internet detected! ");
            //call handler to inform that network is connected.
            Message msg = networkScannerBRHandler.obtainMessage(NETWORK_HAS_INTERNET);
            networkScannerBRHandler.sendMessage(msg);
        }
        else{
            Log.i(TAG," No Internet detected! ");
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
            Thread.sleep(200);
            int timeoutMs = 1000;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);
            sock.connect(sockaddr, timeoutMs);
            sock.close();
            return true;
        } catch (Exception e) { Log.i(TAG,"SOMETHING WENT WRONG!!");e.printStackTrace(); return false; }
    }

    /*public  void updateAppComponents(int stringId){
       LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
       ActivityUpdaterBroadcastReceiver receiver = new ActivityUpdaterBroadcastReceiver();
       IntentFilter intentFilter = new IntentFilter();
       intentFilter.addAction(LOCAL_BROADCAST_ACTION);
       localBroadcastManager.registerReceiver(receiver,intentFilter);
       Intent intent = new Intent(LOCAL_BROADCAST_ACTION);
       intent.putExtra("string",stringId);
       localBroadcastManager.sendBroadcast(intent);
       //the reason I've not unregistered the receiver is because then the broadcast does reach the
       // intended recipient receiver
       //localBroadcastManager.unregisterReceiver(receiver);

    }*/
    //Interface that acts as a callback to Network Change events.
    /*All activities that implement this receiver can display network change related notification
    when in focus.*/
    interface NetworkChangeReceiverListener{
        public void onNetworkStatusChanged(boolean isConnected);
    }

}
