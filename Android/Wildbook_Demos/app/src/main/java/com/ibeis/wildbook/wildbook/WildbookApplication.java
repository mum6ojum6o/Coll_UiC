package com.ibeis.wildbook.wildbook;

import android.app.Application;
import android.util.Log;

/***************
 * Created by Arjan on 4/20/2018.
 ***************/

public class WildbookApplication extends Application {
    public static WildbookApplication mInstance;
    @Override
    public void onCreate(){
        super.onCreate();
        mInstance = this;
    }
    public static WildbookApplication getmInstance(){
        return mInstance;
    }
    //method to register the NetworkStatusListener with an activity
    public void setNetworkStatusChangeListener(NetworkScannerBroadcastReceiver.NetworkChangeReceiverListener listener){
        if(listener!=null)
            NetworkScannerBroadcastReceiver.networkChangeReceiver = listener;
        else
            listener=null;
    }
    public void setImageStatusChangedListener(ImageUploaderTaskRunnable.ImageUploadStatusListener listener){
        if(listener!=null)
            ImageUploaderTaskRunnable.imageUploadStatusListener = listener;
        else
            ImageUploaderTaskRunnable.imageUploadStatusListener = null;
    }


}
