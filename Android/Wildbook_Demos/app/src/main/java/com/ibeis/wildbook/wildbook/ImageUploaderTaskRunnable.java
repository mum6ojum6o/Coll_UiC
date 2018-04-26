package com.ibeis.wildbook.wildbook;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/****************************
 * A class that is responsible for uploading the images to the Wildbook database
 * Created by Arjan on 11/18/2017.
 ***************************/

public class ImageUploaderTaskRunnable implements Runnable {
    public static final int UPLOAD_STARTED=1,UPLOAD_COMPLETED_SUCCESSFULLY=2,UPLOAD_ERROR=-1;
    public static ImageUploadStatusListener imageUploadStatusListener;
    public static final String TAG="com.ibeis.wildbook.wildbook.ImageUploaderTaskRunnable";
    private Context mContext;
    private List<String> filenames;
    private String naam;
    private JSONObject mJSONResponse;
    public static boolean mUploadStatus=false;
    public ImageUploaderTaskRunnable(Context context, List<String> images, String naam){
        this.mContext=context;
        this.filenames = new ArrayList<String>();
        this.naam = naam;

        //hardcopy
        for(String image:images){
            this.filenames.add(image);
        }
    }
    public ImageUploaderTaskRunnable(Context context){
        this.mContext=context;
        this.filenames = new ArrayList<String>();
    }
    public void addFile(String filename){
        this.filenames.add(filename);
    }
    @Override
    public void run() {
        if(imageUploadStatusListener!=null)
            imageUploadStatusListener.onImageUploadStatusChangedListener(UPLOAD_STARTED);
        mUploadStatus=false; //resetting the value on every call to run();
        Utilities utilities = new Utilities(mContext);
        int progress = 3;
        Intent intent1 = new Intent();
        intent1.setAction("com.ibeis.Wildbook.Wildbook_demos");
        intent1.putExtra("string", R.string.uploading_images);
        mContext.sendBroadcast(intent1);
        Log.i(TAG, "Worker Thread started");
        String url = "http://uidev.scribble.com/v2/EncounterForm";
       // String url = "http://pachy.cs.uic.edu:5001/api/upload/image/";
        try {
            Requestor request = new Requestor(url, "UTF-8", "POST");
            if(request!=null) {
                new Utilities(mContext).sendNotification(mContext.getResources().getString(R.string.sync_started), null);
                request.addFormField("jsonResponse", "true");
                try {
                    ExifInterface exif = new ExifInterface(filenames.get(0));
                    String datepicker = exif.getAttribute(ExifInterface.TAG_DATETIME);
                    DateFormat dfrom = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
                    DateFormat dTo = new SimpleDateFormat("yyyy-MM-dd");
                    if (datepicker != null) {
                        try {
                            Date date = dfrom.parse(datepicker);
                            datepicker = dTo.format(date);
                        } catch (ParseException pe) {
                            pe.printStackTrace();
                            Log.i(TAG, "Parsing Issue");
                        }
                    } else {
                        //photo timestamp is null, then enter encounter date is entered as the current date.
                        dTo = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = new Date();
                        datepicker = dTo.format(date);
                    }
                    //comment the below call to add form field to work with the pachy server.
                    request.addFormField("datepicker", datepicker);

                    float[] latLong = new float[2];
                    Double lat = null, Long = null;
                    if (exif.getLatLong(latLong)) {
                        lat = (double) (latLong[0]);
                        Long = (double) (latLong[1]);
                        Log.i(TAG, "lat:" + lat + "long: " + Long);
                        /*comment the below call to add form
                         field to work with the pachy server.*/
                        request.addFormField("lat", Double.toString(lat));
                        request.addFormField("longitude", Double.toString(Long));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "Coordinates could not be extracted!!");
                }
                // comment the below call to add form
                // field to work with the pachy server.
                request.addFormField("submitterEmail", naam);

                for (String file : filenames) {
                    request.addFile("image", file);
                }

                // try {
                ImageRecorderDatabaseSQLiteOpenHelper dbHelper = new ImageRecorderDatabaseSQLiteOpenHelper(mContext);
                //            dbHelper.deleteDatabase();
                Log.i(TAG,"Starting data transfer");
                request.finishRequesting();
                updateDB();

                Log.i(TAG, "Request Completed!!");
                //communicate with the active activity of the application....
            /*if(MainActivity.MAIN_ACTIVITY_IS_RUNNING){
                //too easy. may not be the best approach. I want to reduce the use static variables as much as possible.
                Message msg=MainActivity.handler.obtainMessage(200);
                MainActivity.handler.sendMessage(msg);
            }*/

                //Context ctx= ActiveActivityTracker.getInstance().getmCurrentContext();
                //utilities.sendNotification("Sync Completed!", naam);

                // Log.i(TAG,"Server response error!!");
                //Log.i(TAG, "Saving Response to DB");

                Log.i(TAG, "saving encounterId:" + request.getResponse().getString("encounterId"));
                mJSONResponse = request.getResponse();
               /* intent1.putExtra("string", R.string.imageUploadedString);
                mContext.sendBroadcast(intent1);*/
                mUploadStatus=true;
               /* Message msg = SyncerService.mHandler.obtainMessage(UserContributionsActivity.COMPLETE);
                Bundle b = new Bundle();
                b.putString("naam",this.naam);
                msg.setData(b);
                msg.sendToTarget();*/
                if(imageUploadStatusListener!=null)
                    imageUploadStatusListener.onImageUploadStatusChangedListener(UPLOAD_COMPLETED_SUCCESSFULLY);
                utilities.sendNotification(mContext.getResources().getString(R.string.sync_completed),this.naam);
            }
        }
        catch (FileNotFoundException e){
            Log.i(TAG,"methog: addFileError: Error in addFile!!");
            utilities.sendNotification(mContext.getResources().getString(R.string.file_not_found_error),null);
            // e.printStackTrace();
           /* Message msg = SyncerService.mHandler.obtainMessage(SyncerService.ERROR);
            msg.sendToTarget();*/
            if(imageUploadStatusListener!=null)
                imageUploadStatusListener.onImageUploadStatusChangedListener(UPLOAD_ERROR);
        }
        catch(IOException ioexception){
            Log.i(TAG,"IOException!");
            ioexception.printStackTrace();
            /*Message msg = SyncerService.mHandler.obtainMessage(SyncerService.ERROR);
            msg.sendToTarget();*/
            if(imageUploadStatusListener!=null)
                imageUploadStatusListener.onImageUploadStatusChangedListener(UPLOAD_ERROR);
            utilities.sendNotification(mContext.getResources().getString(R.string.sync_error),null);
        }
        catch (Exception e) {
            Log.i(TAG,"Something Went Wrong!");
            e.printStackTrace();
            utilities.sendNotification(mContext.getResources().getString(R.string.sync_error),null);
            /*Message msg = SyncerService.mHandler.obtainMessage(SyncerService.ERROR);
            msg.sendToTarget();*/
            if(imageUploadStatusListener!=null)
                imageUploadStatusListener.onImageUploadStatusChangedListener(UPLOAD_ERROR);
        }
    }

    /********************
     * Method that updates the SQLDBrecord once a record has been uploaded
     *
     ***************/
    public void updateDB(){
         new Thread(new Runnable() {
                    public void run() {
                        ContentValues values = new ContentValues();
                        values.put(ImageRecorderDatabaseSQLiteOpenHelper.IS_UPLOADED, "1");
                        ImageRecorderDatabaseSQLiteOpenHelper dbHelper = new ImageRecorderDatabaseSQLiteOpenHelper(mContext);
                        String[] files = new String[filenames.size()];
                        for (int i = 0; i < filenames.size(); i++) {
                            files[i] = filenames.get(i);
                        }
                        dbHelper.getWritableDatabase().update(ImageRecorderDatabaseSQLiteOpenHelper.TABLE_NAME, values, ImageRecorderDatabaseSQLiteOpenHelper.FILE_NAME + " IN " + placeholders(files.length), files);
                        dbHelper.close();
                        values.clear();
                    }
                }).start();
    }

    /*****************************
     * Method that adds placeholders to construct an SQLite update query
     * @param length
     * @return
     ***************************/
    public String placeholders(int length){
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i=0;i<length;i++){
            sb.append("?");
            if(i!=length-1)
                sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }

    public JSONObject getmJSONResponse(){
        return mJSONResponse;
    }
    //creating a listener to update respective activities about the status of image upload.
    interface ImageUploadStatusListener{
        public void onImageUploadStatusChangedListener(int imagesUploadedStatus);
    }
}
