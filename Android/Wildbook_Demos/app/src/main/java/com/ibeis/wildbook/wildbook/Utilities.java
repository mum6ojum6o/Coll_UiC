package com.ibeis.wildbook.wildbook;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/****************************************************************
 * Created by Arjan on 10/17/2017.
 *This class will contain the utility methods.
 ***************************************************************/

public class Utilities {
    private static final String TAG = "Utilities Class";
    private AlertDialog.Builder mAlertDialogBuilder;
    private SharedPreferences mSharedPreference;
    private Context mContext;
    private List<ImageRecordDBRecord> mRecord;
    private ImageRecorderDatabase mDbhelper;
    Utilities (Context context){
        mContext = context;
    }
    Utilities (Context context, ImageRecorderDatabase dbHelper,List<ImageRecordDBRecord> record){
        this.mContext = context;
        this.mRecord=record;
        this.mDbhelper=dbHelper;
    }
    /***************************************************************
        this method checks if the device is connected to internet services..
     ***************************************************************/
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /***************************************************************
    This method displays a dialog to get user preferences
     for syncing images in the absence of Network.
     ***************************************************************/
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
                        //TODO - call a function that enters records to the SQLite database
                        //Challenge- how to get the values from the UploadCamPics class?
                       insertRecords();
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
                        //TODO - call a function that enters records to the SQLite database
                        insertRecords();
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
                        //TODO - call a function that enters records to the SQLite database
                        mContext.startActivity(new Intent(mContext,MainActivity.class));
                        insertRecords();
                        ((Activity)mContext).finish();
                    }
                });
        return mAlertDialogBuilder;
    }
    /*****************************************************************
    This method checks if the user's Syncing properties are set
     ****************************************************************/
    public boolean checkSharedPreference(String username){
        mSharedPreference = mContext.getSharedPreferences(mContext.getString(
                R.string.sharedpreferencesFileName),Context.MODE_PRIVATE);
        if(mSharedPreference.getString(username,"Sync_Preference")=="Sync_Preference"){
            return false;
        }
        else
            return true;
    }

    /****************************************************************
    This method writes to the sharedpreferences file.
     ****************************************************************/
    public void writeSyncPreferences(String string){
        mSharedPreference = mContext.getSharedPreferences(mContext.getString(
                R.string.sharedpreferencesFileName),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putString(FirebaseAuth.getInstance().getCurrentUser().getEmail(),string);
        editor.commit();
    }


    /****
     * This method will be used to insert data into the imageRecorder database.
     * @param fileName
     * @param latitude
     * @param longitude
     * @param username
     * @param date
     * @param isUploaded
     */
    //final static String[] columns_imagesrecorder = {_ID,FILE_NAME,LONGITUDE,LATITUDE,USER_NAME,DATE,IS_UPLOADED};
    public void writeToDB(String fileName,
                          long latitude,
                          long longitude,
                          String username,
                          Date date,
                          boolean isUploaded){

    }

    public void insertRecords(){
        InsertToDB insertRecords= new InsertToDB(mDbhelper,mRecord);
        insertRecords.run();

    }

public class InsertToDB implements Runnable{
        private ImageRecorderDatabase mDbhelper;
        private List<ImageRecordDBRecord> mRecords;
        public InsertToDB(ImageRecorderDatabase dbHelper,List<ImageRecordDBRecord> records){
            this.mDbhelper=dbHelper;
            this.mRecords=records;
        }
    @Override
    public void run() {
        Log.i(TAG,"inserting records");
        SQLiteDatabase db=mDbhelper.getWritableDatabase();
       for (ImageRecordDBRecord record: this.mRecords){
           ContentValues values = new ContentValues();
           values.put(ImageRecorderDatabase.FILE_NAME,record.getmFileName());
           values.put(ImageRecorderDatabase.LONGITUDE,record.getmLongitude());
           values.put(ImageRecorderDatabase.LATITUDE,record.getmLatitude());
           values.put(ImageRecorderDatabase.USER_NAME,record.getmUsername());
           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
           String date = sdf.format(record.getmDate());
           values.put(ImageRecorderDatabase.DATE,record.getmDate().toString());
           values.put(ImageRecorderDatabase.IS_UPLOADED,record.getmIsUploaded());
           db.insert(ImageRecorderDatabase.TABLE_NAME,null,values);
           values.clear();

        }
       //  //Start the Syncing Service.
        db.close();
        startSyncing();

    }

    }
    /*
    This method starts the Sync Service.
     */
    public void startSyncing(){
        Intent intent = new Intent(mContext,SyncerService.class);
        mContext.startService(intent);

    }

}
