package com.ibeis.wildbook.wildbook;

import android.content.Context;
import android.media.ExifInterface;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ibeis.wildbook.wildbook.ActiveActivityTracker;
import com.ibeis.wildbook.wildbook.MainActivity;
import com.ibeis.wildbook.wildbook.Requestor;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Arjan on 11/18/2017.
 */

public class ImageUploaderTask implements Runnable {
    public static final String TAG="com.ibeis.wildbook.wildbook.ImageUploaderTask";

    List<String> filenames;
    public ImageUploaderTask(List<String> images){
        this.filenames = new ArrayList<String>();
        //hardcopy
        for(String image:images){
            filenames.add(image);
        }
    }
    @Override
    public void run() {
        Log.i(TAG,"Worker Thread started");
        String url = "http://uidev.scribble.com/v2/EncounterForm";
        Requestor request = new Requestor(url,"UTF-8","POST");
        request.addFormField("jsonResponse","true");
        try {
            ExifInterface exif = new ExifInterface(filenames.get(0));
            String datepicker = exif.getAttribute(ExifInterface.TAG_DATETIME);
            DateFormat dfrom = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
            DateFormat dTo = new  SimpleDateFormat("yyyy-MM-dd");

            try{
                Date date = dfrom.parse(datepicker);
                datepicker = dTo.format(date);
            }catch(ParseException pe){pe.printStackTrace();
                Log.i(TAG,"Parsing Issue");}
            request.addFormField("datepicker",datepicker);
            float[] latLong = new float[2];
            String lat=null,Long=null;
            if(exif.getLatLong(latLong)){
                lat=Float.toString(latLong[0]);
                Long=Float.toString(latLong[1]);
                Log.i(TAG,"lat:"+lat+"long: "+Long);
                request.addFormField("lat",lat);
                request.addFormField("longitude",Long);
            }
        }catch(IOException e){
            e.printStackTrace();
            Log.i(TAG,"Coordinates could not be extracted!!");
        }
        for(String file:filenames){
            try {
                request.addFile("theFiles", file);
            }catch(Exception e){
                e.printStackTrace();
                Log.i(TAG,"case UploadBtn2: exception occured while building request");
                break;
            }
        }
        try{
            request.finishRequesting();
            Log.i(TAG,"Reuest Completed!!");
            //communicate with the active activity of the application....
            if(MainActivity.MAIN_ACTIVITY_IS_RUNNING){
                //too easy. may not be the best approach. I want to reduce the use static variables as much as possible.
                Message msg=MainActivity.handler.obtainMessage(200);
                MainActivity.handler.sendMessage(msg);
            }
             //Context ctx= ActiveActivityTracker.getInstance().getmCurrentContext();


        }catch(Exception e){

            Log.i(TAG,"Server response error!!");

        }
    }
}
