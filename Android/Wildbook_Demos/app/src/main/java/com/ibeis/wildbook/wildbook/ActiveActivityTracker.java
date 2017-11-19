package com.ibeis.wildbook.wildbook;

import android.app.Activity;
import android.content.Context;

/************************************************
 * Created by Arjan on 11/18/2017.
 * Static class that maintains the status of the current activity.....
 **********************************************/

public class ActiveActivityTracker {
    private Context mCurrentContext;
    private static ActiveActivityTracker instance;
    private ActiveActivityTracker(){

    }

    public static ActiveActivityTracker getInstance(){
        if(instance == null){
            return new ActiveActivityTracker();
        }
        else {
            return instance;
        }
    }


     public void setCurrentContext(Context context){
        this.mCurrentContext=context;
    }
    public Context getmCurrentContext(){
         return this.mCurrentContext;
    }

}
