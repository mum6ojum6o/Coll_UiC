package com.ibeis.wildbook.wildbook;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Arjan on 10/17/2017.
 *This class will contain the utility methods.
 */

public class Utilities {
    private static final String TAG = "Utilities Class";
    private AlertDialog.Builder mAlertDialogBuilder;
    private SharedPreferences mSharedPreference;
    private Context mContext;
    Utilities (Context context){
        mContext = context;
    }

    /*
        this method checks if the device is connected to internet services..
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /*
    This method displays a dialog to get user preferences
     for syncing images in the absence of Network.
     */
    public AlertDialog.Builder connectivityAlert(){
        mAlertDialogBuilder = new AlertDialog.Builder(mContext);
        Log.i(TAG,"in connectivyAlert!!!!!");
        mAlertDialogBuilder.setMessage(R.string.connectivityLossMessageString)
                .setPositiveButton(R.string.wifiString,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        Log.i(TAG,"+ve Button");
                        Toast.makeText(mContext,"positive Button",Toast.LENGTH_SHORT).show();
                        writeSyncPreferences(mContext.getString(R.string.wifiString));
                        mContext.startActivity(new Intent(mContext,MainActivity.class));
                        ((Activity)mContext).finish();
                    }
                })
                .setNegativeButton(R.string.mobiledataString,new  DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        Log.i(TAG," -ve Button");
                        Toast.makeText(mContext,"negative Button",Toast.LENGTH_SHORT).show();
                        writeSyncPreferences(mContext.getString(R.string.mobiledataString));
                        mContext.startActivity(new Intent(mContext,MainActivity.class));
                        ((Activity)mContext).finish();

                    }
                })
                .setNeutralButton(R.string.anyString,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        Log.i(TAG," = Button");
                        Toast.makeText(mContext,"neutral Button",Toast.LENGTH_SHORT).show();
                        writeSyncPreferences(mContext.getString(R.string.anyString));
                        mContext.startActivity(new Intent(mContext,MainActivity.class));
                        ((Activity)mContext).finish();
                    }
                });
        return mAlertDialogBuilder;
    }
    /*
    This method checks if the user's Syncing properties are set
     */
    public boolean checkSharedPreference(String username){
        mSharedPreference = mContext.getSharedPreferences(mContext.getString(R.string.sharedpreferencesFileName),Context.MODE_PRIVATE);
        if(mSharedPreference.getString(username,"Sync_Preference")=="Sync_Preference"){
            return false;
        }
        else
            return true;
    }

    /*
    This method writes to the sharedpreferences file.
     */
    public void writeSyncPreferences(String string){
        mSharedPreference = mContext.getSharedPreferences(mContext.getString(R.string.sharedpreferencesFileName),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putString(FirebaseAuth.getInstance().getCurrentUser().getEmail(),string);
        editor.commit();
    }
}
