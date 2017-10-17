package com.ibeis.wildbook.wildbook;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Arjan on 10/17/2017.
 *This class will contain the utility methods.
 */

public class Utilities {
    private static final String TAG = "Utilities Class";
    private AlertDialog.Builder mAlertDialogBuilder;
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
    This method displays a dialoge to get user preferences
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
                        mContext.startActivity(new Intent(mContext,MainActivity.class));
                        ((Activity)mContext).finish();
                    }
                })
                .setNegativeButton(R.string.mobiledataString,new  DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        Log.i(TAG," -ve Button");
                        Toast.makeText(mContext,"negative Button",Toast.LENGTH_SHORT).show();
                        mContext.startActivity(new Intent(mContext,MainActivity.class));
                        ((Activity)mContext).finish();

                    }
                })
                .setNeutralButton(R.string.anyString,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        Log.i(TAG," = Button");
                        Toast.makeText(mContext,"neutral Button",Toast.LENGTH_SHORT).show();
                        mContext.startActivity(new Intent(mContext,MainActivity.class));
                        ((Activity)mContext).finish();
                    }
                });
        return mAlertDialogBuilder;
    }
}
