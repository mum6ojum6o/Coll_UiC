package com.ibeis.wildbook.wildbook;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Button;
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
import java.util.ArrayList;

import static java.lang.Thread.sleep;

/***************************************************************************************
 * Created by Arjan on 10/19/2017.
 * This class is creates the service that will sync the images as per the availability of
 * Network.
 ************************************************************************************/

public class SyncerService extends IntentService {


    private static final String TAG = "SyncerService";
    private SQLiteCursor mSQLiteCursor;
    private ImageRecorderDatabase mDBHelper;
    private Button syncButton;
   public SyncerService(){
       super(TAG);
   }
   public static boolean IsRunning=false; //Field that helps determine is the service is running....
   //The service also has to be syncronized.
    @Override
    public void onHandleIntent(Intent intent){

        Log.i(TAG,"Service started");
        Bundle extras=intent.getExtras();
        IsRunning=true;
        int count=0;
        mDBHelper= new ImageRecorderDatabase(this);
        mDBHelper.getReadableDatabase();
        String where = "isUploaded = 0";
        //String orderBy= "ORDER BY "
        String columns[]={ImageRecorderDatabase._ID,ImageRecorderDatabase.ENCOUNTER_NUM,ImageRecorderDatabase.FILE_NAME,ImageRecorderDatabase.LONGITUDE,ImageRecorderDatabase.LATITUDE};
        String column[]={ImageRecorderDatabase.ENCOUNTER_NUM};


        Cursor c = mDBHelper.getReadableDatabase().query(ImageRecorderDatabase.TABLE_NAME,columns,ImageRecorderDatabase.IS_UPLOADED +"=?",
                new String[]{"0"},null,null,ImageRecorderDatabase.ENCOUNTER_NUM);
        c.moveToFirst();
        Messenger msngr;
        ArrayList<Integer> filesUploadedIds= new ArrayList<Integer>();
        //String groupBy
        final ArrayList<Uri> successUploads = new ArrayList<Uri>();
        if(new Utilities(getApplicationContext()).isNetworkAvailable()) {

            if(extras!=null && extras.get("Messenger")!=null) {
                msngr = (Messenger) extras.get("Messenger");
                Message msg = Message.obtain();
                msg.what = MainActivity.SYNC_STARTED;
                try {
                    msngr.send(msg);
                } catch (android.os.RemoteException e1) {
                    Log.w(getClass().getName(), "Exception sending message", e1);
                }
            }

            Log.i(TAG, "Checking NetworkAvailability!!");
        }
        ArrayList<String> filenames=new ArrayList<String>();
            int encounterNum=-1,rowCount=0;
        filesUploadedIds=new ArrayList<Integer>();
            while (c.getCount() > 0 && !c.isAfterLast() && new Utilities(this).isNetworkAvailable()) {

                Log.i(TAG,"Network Available & Sync Started!");
                new Utilities(this).sendNotification("Sync Started!");
                final String filename =c.getString(c.getColumnIndex(ImageRecorderDatabase.FILE_NAME));

                int fileId = c.getInt(c.getColumnIndex(ImageRecorderDatabase._ID));
                filesUploadedIds.add(fileId);
                int currEncNum=c.getInt(c.getColumnIndex(ImageRecorderDatabase.ENCOUNTER_NUM));
                Log.i(TAG, "record:"+fileId+", "+filename+", "+currEncNum);
                Log.i(TAG,"filename Uploading..."+filename);
                if(encounterNum==-1){
                    encounterNum=currEncNum;
                    filenames.add(filename);
                }
                else if(encounterNum==currEncNum){
                    filenames.add(filename);
                }
                else if(count>0&& encounterNum!=currEncNum){
                    ImageUploaderTask task = new ImageUploaderTask(this,filenames);
                    task.run();
                    encounterNum=currEncNum;
                    filenames=null;
                    filenames = new ArrayList<String>();
                    filenames.add(filename);
                }
             /*   StorageReference storage;
                FirebaseAuth auth;
                DatabaseReference databaseReference;
                StorageReference filePath = null;
                UploadTask upload = null;
                auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                storage = FirebaseStorage.getInstance().getReference();
                File file = new File(filename);
                Uri uploadImage = Uri.fromFile(file);
                databaseReference = FirebaseDatabase.getInstance().getReference("Photos/"+auth.getCurrentUser().getUid());
                filePath = storage.child("Photos/" + auth.getCurrentUser().getEmail()).child(uploadImage.getLastPathSegment());
                upload = filePath.putFile(uploadImage);
                String ImageUploadId = databaseReference.push().getKey();
                databaseReference.child(ImageUploadId).setValue(file.getName());
                upload.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads

                        Log.i(TAG, "Error!");
                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        successUploads.add(downloadUrl);
                        // set the isUploaded field to 1 for that file....
                        ContentValues values = new ContentValues();
                        values.put(ImageRecorderDatabase.IS_UPLOADED,"1");
                        ImageRecorderDatabase dbHelper= new ImageRecorderDatabase(getApplicationContext());
                        dbHelper.getWritableDatabase().update(ImageRecorderDatabase.TABLE_NAME,values,ImageRecorderDatabase.FILE_NAME +"=?", new String[]{filename});
                        dbHelper.close();
                        values.clear();
                        //how to make this synchronous?????????

                    }
                });*/
                count++;
                Log.i(TAG, "There are " + c.getCount() + "remaining. Record " + count);
                Log.i(TAG, "Column ID=" + c.getString(c.getColumnIndex(ImageRecorderDatabase._ID)));
                c.moveToNext();

                Log.i(TAG, "Checking Next Record!");
                /*c = mDBHelper.getReadableDatabase().query(ImageRecorderDatabase.TABLE_NAME,columns,ImageRecorderDatabase.IS_UPLOADED +"=?",
                        new String[]{"0"},null,null,null);*/
                Log.i(TAG,"After updating count="+c.getCount() );
                //c.moveToFirst();
                /*try{
                    Thread.sleep(1000);
                }catch(Exception e){e.printStackTrace();}*/
            }
            if(count>0) {
                Log.i(TAG,"Instantiating Http Request");
                Log.i(TAG, "filesRead:"+filenames.size());
                ImageUploaderTask task = new ImageUploaderTask(this,filenames); //the last record....
                task.run();

            }

        c.close();
        mDBHelper.close();
        Log.i(TAG,"Service Stopping");



       /* if(extras!=null && extras.get("Messenger")!=null) {
            msngr = (Messenger) extras.get("Messenger");
            Message msg1 = Message.obtain();
            msg1.what = MainActivity.SYNC_COMPLETE;
            if (msg1 != null) {
                try {
                    msngr.send(msg1);
                } catch (android.os.RemoteException e1) {
                    Log.w(getClass().getName(), "Exception sending message", e1);
                }
            }
        }*/

            // h = new Handler(Looper.getMainLooper());
            Log.i(TAG, "IsRunning is now set to False");
            IsRunning = false;
            //if no records were uploaded... do not send any notification...
            if(count>0) {
                new Utilities(this).sendNotification("Sync Completed!");
               // sendNotification("Sync Completed!");
            }
        Log.i(TAG,"Service ended");

    }

    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder;
        PendingIntent contentIntent;
        mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Wildbook")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);
        if(msg.equals("Sync Started!")) {
            contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), 0);
            mBuilder.setSmallIcon(R.drawable.notification_sync);


        }
        else{
            contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, DisplayImagesUsingRecyclerView.class), 0);
            mBuilder.setSmallIcon(R.drawable.notification_sync_complete);

        }
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1, mBuilder.build());
    }

}
