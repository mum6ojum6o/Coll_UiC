package com.ibeis.wildbook.wildbook;

import android.app.IntentService;
//import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/***************************************************************************************
 * Created by Arjan on 10/19/2017.
 * This class is creates the service that will sync the images as per the availability of
 * Network.
 ************************************************************************************/

public class SyncerService extends IntentService {
    private static final String TAG = "SyncerService";
    private SQLiteCursor mSQLiteCursor;
    public static final int ERROR = 404;
    private ImageRecorderDatabaseSQLiteOpenHelper mDBHelper;
    private static String mName;
    private static Context mContext;
    // there are two ways two send notifications
    //1. throught his handler
    // the benefit of using the handler is that then all the notifications will triggered from here.


   public SyncerService(){
       super(TAG);
       mContext = this;

   }
   public static boolean IsRunning=false; //Field that helps determine is the service is running....

    @Override
    public void onHandleIntent(Intent intent){
        Utilities utilities = new Utilities(this);
        int totalRowsToBeSynced=0;
        Log.i(TAG,"Service started");
        Bundle extras=intent.getExtras();
        IsRunning=true;
        int count=0;
        mDBHelper= new ImageRecorderDatabaseSQLiteOpenHelper(this);
        mDBHelper.getReadableDatabase();
        String where = "isUploaded = 0";
        //String orderBy= "ORDER BY "
        String columns[]={ImageRecorderDatabaseSQLiteOpenHelper._ID, ImageRecorderDatabaseSQLiteOpenHelper.ENCOUNTER_NUM,
                ImageRecorderDatabaseSQLiteOpenHelper.FILE_NAME, ImageRecorderDatabaseSQLiteOpenHelper.LONGITUDE,
                ImageRecorderDatabaseSQLiteOpenHelper.LATITUDE, ImageRecorderDatabaseSQLiteOpenHelper.USER_NAME};
        String column[]={ImageRecorderDatabaseSQLiteOpenHelper.ENCOUNTER_NUM};


        Cursor c = mDBHelper.getReadableDatabase().query(ImageRecorderDatabaseSQLiteOpenHelper.TABLE_NAME,columns, ImageRecorderDatabaseSQLiteOpenHelper.IS_UPLOADED +"=?",
                new String[]{"0"},null,null, ImageRecorderDatabaseSQLiteOpenHelper.ENCOUNTER_NUM);
        c.moveToFirst();

        ArrayList<Integer> filesUploadedIds= new ArrayList<Integer>();
        //String groupBy
        final ArrayList<Uri> successUploads = new ArrayList<Uri>();
        if(new Utilities(getApplicationContext()).isNetworkAvailable()) {

            /*if(extras!=null && extras.get("Messenger")!=null) {
                msngr = (Messenger) extras.get("Messenger");
                Message msg = Message.obtain();
                msg.what = MainActivity.SYNC_STARTED;
                try {
                    msngr.send(msg);
                } catch (android.os.RemoteException e1) {
                    Log.w(getClass().getName(), "Exception sending message", e1);
                }
            }*/

            Log.i(TAG, "Checking NetworkAvailability!!");
        }
        String name=null;
        ArrayList<String> filenames=new ArrayList<String>();
            int encounterNum=-1,rowCount=0;
        filesUploadedIds=new ArrayList<Integer>();
         totalRowsToBeSynced= c.getCount();
         String prevName=null;
            while (c.getCount() > 0 && !c.isAfterLast() && new Utilities(this).isNetworkAvailable()) {

                Log.i(TAG,"Network Available & Sync Started!");

                final String filename =c.getString(c.getColumnIndex(ImageRecorderDatabaseSQLiteOpenHelper.FILE_NAME));
                int colIndex = c.getColumnIndex(ImageRecorderDatabaseSQLiteOpenHelper.USER_NAME);
                mName=c.getString(colIndex);
                Log.i(TAG,"Image for the user:"+name);
                int fileId = c.getInt(c.getColumnIndex(ImageRecorderDatabaseSQLiteOpenHelper._ID));
                filesUploadedIds.add(fileId);
                int currEncNum=c.getInt(c.getColumnIndex(ImageRecorderDatabaseSQLiteOpenHelper.ENCOUNTER_NUM));
                Log.i(TAG, "record:"+fileId+", "+filename+", "+currEncNum);
                Log.i(TAG,"filename Uploading..."+filename);
                String userNetworkPref = utilities.getSyncSharedPreference(name);

                if(utilities.getNetworkType().equals(getResources().getString(R.string.wifiString))){}
                else if(!utilities.getNetworkType().equals(utilities.getSyncSharedPreference())){
                    Log.i(TAG,"Network preferences donot match");
                    c.moveToNext();
                    continue;
                }
                utilities.sendNotification("Sync Started!",null);// could be removed to get uniformity

                if(encounterNum==-1){
                    encounterNum=currEncNum;// ENCOUNTER_NUM is identifies whether two Images are associated to the same encounter or not.
                    filenames.add(filename);
                    prevName=mName;
                }
                else if(encounterNum==currEncNum){
                    filenames.add(filename);

                }
                else if(count>0&& encounterNum!=currEncNum){
                    ImageUploaderTaskRunnable task = new ImageUploaderTaskRunnable(this,filenames,prevName);
                    //task.run(); // why not a separate thread???
                    new Thread(task).start();
                   // updateStatus(fileId);
                    encounterNum=currEncNum;
                    /*filenames=null;
                    filenames = new ArrayList<String>();*/
                    filenames.clear();
                    filenames.add(filename);
                    prevName=mName;
                }
                count++;
                c.moveToNext();
                Log.i(TAG,"After updating count="+c.getCount() );
            }
            if(count>0 && filenames.size()>0) {
                Log.i(TAG,"Instantiating Http Request");
                Log.i(TAG, "filesRead:"+filenames.size());
                ImageUploaderTaskRunnable task = new ImageUploaderTaskRunnable(this,filenames,mName); //the last record....
                task.run(); // this is running on the same thread as the Service.

            }

        c.close();
        mDBHelper.close();
        Log.i(TAG,"Service Stopping");
        Log.i(TAG, "IsRunning is now set to False");
        IsRunning = false;
        Log.i(TAG,"Service ended");
    }

    // This method is deprecated.
    public void updateStatus(int fileId){
        Log.i(TAG,"updattng status of fileId "+fileId);
        ImageRecorderDatabaseSQLiteOpenHelper dbHelper = new ImageRecorderDatabaseSQLiteOpenHelper(this);
        dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ImageRecorderDatabaseSQLiteOpenHelper.IS_UPLOADED, "1");
        dbHelper.getWritableDatabase().update(ImageRecorderDatabaseSQLiteOpenHelper.TABLE_NAME, values, ImageRecorderDatabaseSQLiteOpenHelper._ID + " = "+"?",new String[]{Integer.toString(fileId)});
        dbHelper.close();
        values.clear();
    }

}
