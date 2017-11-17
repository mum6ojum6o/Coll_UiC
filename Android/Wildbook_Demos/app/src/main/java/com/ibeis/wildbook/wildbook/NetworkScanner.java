package com.ibeis.wildbook.wildbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;
import android.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

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
        /*GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken("488482372443-9n5gv6ghvgebd6r0pp4d1ae5gvi5dcnt.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleApiClient googleApiClient= new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {*/
            Bundle bundle = intent.getExtras();
            NetworkInfo networkInfo = (NetworkInfo) bundle.get(WifiManager.EXTRA_NETWORK_INFO);
            boolean noConnectivity = bundle.getBoolean(EXTRA_NO_CONNECTIVITY);
            Log.i(TAG, "networkInfo=" + networkInfo.isConnected() + " isConnected=" + noConnectivity + " count=" + count);
            //Context context1=context.getApplicationContext();
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo newNetworkInfo = null;

            newNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if ((new Utilities(context).isNetworkAvailable() || (newNetworkInfo != null && newNetworkInfo.isConnected()) || networkInfo.isConnected()) && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.i("Receiver", "Startring Service MainActivity is Running?" + MainActivity.MAIN_ACTIVITY_IS_RUNNING);
                takeAction(context);

            } else if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.isConnected()) { //if mobile data is connected.
                Log.i("Receiver", "Startring Service MainActivity is Running?" + MainActivity.MAIN_ACTIVITY_IS_RUNNING);
                takeAction(context);
            } else {
                if (MainActivity.MAIN_ACTIVITY_IS_RUNNING) {
                    MainActivity.displayOnlineStatus(MainActivity.OFFLINE);

                }
                //Toast.makeText(context, "Wifi Not Connected!!", Toast.LENGTH_SHORT).show();
            }
       /* }
        else{
            Log.i(TAG,"User not logged in. NetworkScanner will not take any action");
            Toast.makeText(context,"No action taken by NetworkScanner as User is not Logged in!!",Toast.LENGTH_LONG).show();
        }*/
    }
    public void takeAction(Context context){
        if(MainActivity.MAIN_ACTIVITY_IS_RUNNING){
            MainActivity.displayOnlineStatus(MainActivity.ONLINE);
        }
        if(!SyncerService.IsRunning) {

            Utilities utility = new Utilities(context);
            Log.i(TAG," Starting Service");
            utility.startSyncing(); //start service....
        }
    }


}
