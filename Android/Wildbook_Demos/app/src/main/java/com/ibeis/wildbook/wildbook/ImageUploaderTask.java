package com.ibeis.wildbook.wildbook;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Message;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**************
 * A class that is responsible for uploading the images to the Wildbook database
 * Created by Arjan on 11/18/2017.
 */

public class ImageUploaderTask implements Runnable {
    public static final String TAG="com.ibeis.wildbook.wildbook.ImageUploaderTask";
    Context mContext;
    List<String> filenames;
    String naam;
    public ImageUploaderTask(Context context,List<String> images,String naam){
        this.mContext=context;
        this.filenames = new ArrayList<String>();
        this.naam = naam;
        //hardcopy
        for(String image:images){
            this.filenames.add(image);
        }
    }
    public ImageUploaderTask(Context context){
        this.mContext=context;
        this.filenames = new ArrayList<String>();
    }
    public void addFile(String filename){
        this.filenames.add(filename);
    }
    @Override
    public void run() {
        Utilities utilities = new Utilities(mContext);
        int progress = 3;
        Intent intent1 = new Intent();
        intent1.setAction("com.ibeis.Wildbook.Wildbook_demos");
        intent1.putExtra("string", R.string.uploading_images);
        mContext.sendBroadcast(intent1);
        Log.i(TAG, "Worker Thread started");
        String url = "http://uidev.scribble.com/v2/EncounterForm";
        try {
            Requestor request = new Requestor(url, "UTF-8", "POST");
            if(request!=null) {
                new Utilities(mContext).sendNotification("Upload Started", null);
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
                    request.addFormField("datepicker", datepicker);

                    float[] latLong = new float[2];
                    Double lat = null, Long = null;
                    if (exif.getLatLong(latLong)) {
                        lat = (double) (latLong[0]);
                        Long = (double) (latLong[1]);
                        Log.i(TAG, "lat:" + lat + "long: " + Long);
                        request.addFormField("lat", Double.toString(lat));
                        request.addFormField("longitude", Double.toString(Long));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "Coordinates could not be extracted!!");
                }

                request.addFormField("submitterEmail", naam);

                for (String file : filenames) {
                    //try {
                    request.addFile("theFiles", file);
                    //}
                /*catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "case UploadBtn2: exception occured while building request");
                    new Utilities(mContext).sendNotification("Error", null);
                    request = null;
                    return;

                }*/

                }

                // try {
                ImageRecorderDatabase dbHelper = new ImageRecorderDatabase(mContext);
//            dbHelper.deleteDatabase();
                request.finishRequesting();
                new Thread(new Runnable() {
                    public void run() {
                        ContentValues values = new ContentValues();
                        values.put(ImageRecorderDatabase.IS_UPLOADED, "1");
                        ImageRecorderDatabase dbHelper = new ImageRecorderDatabase(mContext);
                        String[] files = new String[filenames.size()];
                        for (int i = 0; i < filenames.size(); i++) {
                            files[i] = filenames.get(i);
                        }
                        dbHelper.getWritableDatabase().update(ImageRecorderDatabase.TABLE_NAME, values, ImageRecorderDatabase.FILE_NAME + " IN " + placeholders(files.length), files);
                        dbHelper.close();
                        values.clear();
                    }
                }).start();
                Log.i(TAG, "Reuest Completed!!");
                //communicate with the active activity of the application....
            /*if(MainActivity.MAIN_ACTIVITY_IS_RUNNING){
                //too easy. may not be the best approach. I want to reduce the use static variables as much as possible.
                Message msg=MainActivity.handler.obtainMessage(200);
                MainActivity.handler.sendMessage(msg);
            }*/

                //Context ctx= ActiveActivityTracker.getInstance().getmCurrentContext();
                utilities.sendNotification("Sync Completed!", naam);
                // Log.i(TAG,"Server response error!!");
                //Log.i(TAG, "Saving Response to DB");
                Log.i(TAG, "saving encounterId:" + request.getResponse().getString("encounterId"));
                String email = utilities.getCurrentIdentity();
                utilities.insertRecords(request.getResponse().getString("encounterId"));
            /*} catch (Exception e) {
                new Utilities(mContext).sendNotification("Error", null);
                e.printStackTrace();
                request = null;
                Log.i(TAG, "Server response error!!");
                return;
            }*/
                intent1.putExtra("string", R.string.imageUploadedString);
                mContext.sendBroadcast(intent1);
            }
        }
        catch (FileNotFoundException e){
            Log.i(TAG,"methog: addFileError: Error in addFile!!");
            utilities.sendNotification(mContext.getResources().getString(R.string.file_not_found_error),null);
            // e.printStackTrace();
        }
        catch(IOException ioexception){

            ioexception.printStackTrace();
            utilities.sendNotification(mContext.getResources().getString(R.string.sync_error),null);
        }
        catch (Exception e) {
            e.printStackTrace();
            utilities.sendNotification(mContext.getResources().getString(R.string.sync_error),null);
        }
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
}
