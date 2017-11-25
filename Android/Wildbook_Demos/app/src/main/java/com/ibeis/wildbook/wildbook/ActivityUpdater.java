package com.ibeis.wildbook.wildbook;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Arjan on 11/25/2017.
 */

public class ActivityUpdater extends BroadcastReceiver {
    public static Activity activeActivity;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (activeActivity!=null){
            //do the updating....
            //activeActivity.

        }
    }
}
