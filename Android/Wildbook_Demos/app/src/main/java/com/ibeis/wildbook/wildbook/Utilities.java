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
import android.net.Uri;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/****************************************************************
 * Created by Arjan on 10/17/2017.
 *This class will contain the utility methods.
 * REFACTOR,REFACTOR,REFACTOR!!!
 ***************************************************************/

public class Utilities {
    private static final String TAG = "Utilities Class";
    private AlertDialog.Builder mAlertDialogBuilder;
    private SharedPreferences mSharedPreference;
    private Context mContext;
    private List<ImageRecordDBRecord> mRecord;
    private ImageRecorderDatabase mDbhelper;
    private List<String> mImagePaths;
    Utilities (Context context){
        mContext = context;
    }
    Utilities (Context context, ImageRecorderDatabase dbHelper,List<ImageRecordDBRecord> record){
        this.mContext = context;
        this.mRecord=record;
        this.mDbhelper=dbHelper;
    }
    Utilities (Context context, List<String> images,ImageRecorderDatabase dbHelper){
        this.mContext = context;
        this.mDbhelper=dbHelper;
        this.mImagePaths=images;
    }
    /***************************************************************
        This method checks if the device is connected to internet services..
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

    /************************************************************************************
     * A thread that will insert records to the SQLite database
     **********************************************************************************/
    public class InsertToDB implements Runnable{
        private ImageRecorderDatabase mDbhelper;
        private List<ImageRecordDBRecord> mRecords;
        public InsertToDB(ImageRecorderDatabase dbHelper,List<ImageRecordDBRecord> records){
            this.mDbhelper=dbHelper;
            this.mRecords=records;
        }
    @Override
   synchronized public void run() {
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
       //close the database.
        db.close();
        //Start the Syncing Service.
        startSyncing();

    }

    }
    /**********************************
    This method starts the Sync Service.
     @buttonId refers to the Id of the button that initiates the sync. value is -1 if service is started internally
     *************************************/
    public void startSyncing(){
        Intent intent = new Intent(mContext,SyncerService.class);
        intent.putExtra("SyncStarted",MainActivity.SYNC_STARTED);
        intent.putExtra("SyncComplete",MainActivity.SYNC_COMPLETE);
        intent.putExtra("Messenger",new Messenger(MainActivity.handler));
        mContext.startService(intent);

    }

    /**********************************************************
     * This method uploads pictures.....
     * and then redirects to the mainactivity....
     **********************************************************/
    public void uploadPictures(ArrayList<String> _images ){
        DatabaseReference databaseReference;
        final Context context = mContext;
        UploadTask upload = null;
        StorageReference filePath = null;
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(MainActivity.databasePath);

        final ArrayList<Uri> successUploads = new ArrayList<Uri>();
        ArrayList<String> selectedImages=_images;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //StorageReference filePath=mStorage.child("Photos").child(mAuth.getCurrentUser().getEmail());
        for (String s : selectedImages) {
            Uri uploadImage = Uri.fromFile(new File(s));
            filePath = storage.child("Photos/" + auth.getCurrentUser().getEmail()).child(uploadImage.getLastPathSegment());
            String fileName = new File(s).getName();

                upload = filePath.putFile(uploadImage);
                String ImageUploadId = databaseReference.push().getKey();
                databaseReference.child(ImageUploadId).setValue(fileName);


        }
        upload.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.i(TAG, "Error!");
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //

                String fileName = downloadUrl.getLastPathSegment().toString();
                Log.i(TAG,"Uploaded FILE:"+fileName);
                ImageRecordDBRecord aRecord = new ImageRecordDBRecord();
                aRecord.setFileName(fileName);
                aRecord.setIsUploaded(true);
                aRecord.setDate(new Date());
                aRecord.setUsername(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                successUploads.add(downloadUrl);
                ArrayList<ImageRecordDBRecord> aRec = new ArrayList<ImageRecordDBRecord>();
                aRec.add(aRecord);
                mRecord=new ArrayList<ImageRecordDBRecord>();
                mRecord.add(aRecord);
                insertRecords();

            }
        });
        //redirect(successUploads.size(), selectedImages.size(),this,MainActivity.class);
    }

    /*********************
     *
     * This method redirects from one activity to the other..
     * @param imagesUploaded -represents number of images uploaded
     * @param imagesRequested - represents total number of images that were requested to be uploaded
     * @param FromClass - the context of the class that request for upload.
     * @param ToClass   - the class that will be redirected to once the upload process is complete.
     *                  THIS METHOD NEEDS WORK!
     *                  LINE:  ((Activity)FromClass).finish(); THROIWS AN ERROR!!
     ******************************************/
    public void redirect(int imagesUploaded, int imagesRequested,Activity FromClass,Class ToClass){
        if(imagesRequested==imagesUploaded){
            Toast.makeText(mContext,"All images were uploaded!",Toast.LENGTH_LONG).show();
        }
        else if(imagesUploaded == 0 && imagesRequested ==0){
            Toast.makeText(mContext,"The images will be uploaded on the availability of appropriate network!",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(mContext,imagesRequested-imagesUploaded+"were uploaded!",Toast.LENGTH_LONG).show();
        }
        ((Activity)FromClass).finish();
        mContext.startActivity(new Intent(FromClass,ToClass));

    }


    /*********************************************************
     * Prepares records for insertion to Database
     *
     *******************************************************/
    public void prepareBatchForInsertToDB(Boolean isUploaded) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mRecord=new ArrayList<ImageRecordDBRecord>();
        for (String filename : mImagePaths) {
            ImageRecordDBRecord record = new ImageRecordDBRecord();
            record.setFileName(filename);
            record.setUsername(auth.getCurrentUser().getEmail());
            Date date = new Date();
            record.setDate(date);
            record.setIsUploaded(isUploaded);
            mRecord.add(record);
        }

    }


    public void setmRecord(List<ImageRecordDBRecord> records){
        this.mRecord = records;
    }

}
