package com.ibeis.wildbook.wildbook;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

/***************************************************************************************
 * Created by Arjan on 10/19/2017.
 * This class is creates the service that will sync the images as per the availability of
 * Network.
 ************************************************************************************/

public class SyncerService extends IntentService {


    private static final String TAG = "SyncerService";
    private SQLiteCursor mSQLiteCursor;
    private ImageRecorderDatabaseSQLiteOpenHelper mDBHelper;

   public SyncerService(){
       super(TAG);
   }
   public static boolean IsRunning=false; //Field that helps determine is the service is running....
   //The service also has to be syncronized.
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
        HashMap<Integer,String> filesRead = new HashMap<Integer, String>();  //to keep track of whether the file has been read or not.
        HashMap<Integer,HashMap<Integer,String>> recordTracker = new HashMap<>();
        ArrayList<String> filenames=new ArrayList<String>();
            int encounterNum=-1,rowCount=0;
        filesUploadedIds=new ArrayList<Integer>();
         totalRowsToBeSynced= c.getCount();
         String prevName=null;
            while (c.getCount() > 0 && !c.isAfterLast() && new Utilities(this).isNetworkAvailable()) {

                Log.i(TAG,"Network Available & Sync Started!");

                final String filename =c.getString(c.getColumnIndex(ImageRecorderDatabaseSQLiteOpenHelper.FILE_NAME));
                int colIndex = c.getColumnIndex(ImageRecorderDatabaseSQLiteOpenHelper.USER_NAME);
                name=c.getString(colIndex);
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
                utilities.sendNotification("Sync Started!",null);
                if(encounterNum==-1){
                    encounterNum=currEncNum;// ENCOUNTER_NUM is identifies whether two Images are associated to the same encounter or not.
                    filenames.add(filename);
                    prevName=name;
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
                    prevName=name;
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
                        values.put(ImageRecorderDatabaseSQLiteOpenHelper.IS_UPLOADED,"1");
                        ImageRecorderDatabaseSQLiteOpenHelper dbHelper= new ImageRecorderDatabaseSQLiteOpenHelper(getApplicationContext());
                        dbHelper.getWritableDatabase().update(ImageRecorderDatabaseSQLiteOpenHelper.TABLE_NAME,values,ImageRecorderDatabaseSQLiteOpenHelper.FILE_NAME +"=?", new String[]{filename});
                        dbHelper.close();
                        values.clear();
                        //how to make this synchronous?????????

                    }
                });*/
                count++;
                /*Log.i(TAG, "There are " + c.getCount() + "remaining. Record " + count);
                Log.i(TAG, "Column ID=" + c.getString(c.getColumnIndex(ImageRecorderDatabaseSQLiteOpenHelper._ID)));*/
                c.moveToNext();

                //Log.i(TAG, "Checking Next Record!");
                /*c = mDBHelper.getReadableDatabase().query(ImageRecorderDatabaseSQLiteOpenHelper.TABLE_NAME,columns,ImageRecorderDatabaseSQLiteOpenHelper.IS_UPLOADED +"=?",
                        new String[]{"0"},null,null,null);*/
                Log.i(TAG,"After updating count="+c.getCount() );
                //c.moveToFirst();
                /*try{
                    Thread.sleep(1000);
                }catch(Exception e){e.printStackTrace();}*/
               // Log.i(TAG,name);
            }
            if(count>0 && filenames.size()>0) {
                Log.i(TAG,"Instantiating Http Request");
                Log.i(TAG, "filesRead:"+filenames.size());
                ImageUploaderTaskRunnable task = new ImageUploaderTaskRunnable(this,filenames,name); //the last record....
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
                Log.i(TAG,"name:"+name);
                new Utilities(this).sendNotification("Sync Completed!",name);
               // sendNotification("Sync Completed!");
            }
        Log.i(TAG,"Service ended");

    }


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
    /*private void sendNotification(String msg) {
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
                    new Intent(this, UserContributionsActivity.class), 0);
            mBuilder.setSmallIcon(R.drawable.notification_sync_complete);

        }
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1, mBuilder.build());
    }*/

}
