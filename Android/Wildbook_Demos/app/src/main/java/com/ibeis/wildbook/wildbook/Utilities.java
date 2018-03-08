package com.ibeis.wildbook.wildbook;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Messenger;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Log.i("Utilities","isNetworkAvailable()"+(activeNetworkInfo != null
                && activeNetworkInfo.isConnected()));
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /***************************************************************
     * This method will return a string that will suggest the type of network connected
     * it would return Wifi or MobileData
     ****************************************************************/
    public String getNetworkType(){
        String networkType=null;
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo newNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(newNetworkInfo.getType()== ConnectivityManager.TYPE_WIFI){
            networkType=mContext.getResources()
                    .getString(R.string.wifiString);
        }
        else if(newNetworkInfo.getType()== ConnectivityManager.TYPE_MOBILE){
            networkType=mContext.getResources()
                    .getString(R.string.mobiledataString);
        }
        return networkType;

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
                       // Toast.makeText(mContext,"positive Button",Toast.LENGTH_SHORT).show();
                        Toast.makeText(mContext,R.string.uploadRequestOnNoNetwork,Toast.LENGTH_SHORT).show();
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
                      //  Log.i(TAG," -ve Button");
                        //Toast.makeText(mContext,"negative Button",Toast.LENGTH_SHORT).show();
                        Toast.makeText(mContext,R.string.uploadRequestOnNoNetwork,Toast.LENGTH_SHORT).show();
                        writeSyncPreferences(mContext.getString(R.string.mobiledataString));
                        //TODO - call a function that enters records to the SQLite database
                        insertRecords();

                        mContext.startActivity(new Intent(mContext,MainActivity.class));
                        ((Activity)mContext).finish();

                    }
                });
                /*.setNeutralButton(R.string.anyString,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        Log.i(TAG," = Button");
                   //     Toast.makeText(mContext,"neutral Button",Toast.LENGTH_SHORT).show();
                        writeSyncPreferences(mContext.getString(R.string.anyString));
                        //TODO - call a function that enters records to the SQLite database
                        mContext.startActivity(new Intent(mContext,MainActivity.class));
                        insertRecords();
                        ((Activity)mContext).finish();
                    }
                });*/
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
public String getSyncSharedPreference(){
    mSharedPreference = mContext.getSharedPreferences(mContext.getString(
            R.string.sharedpreferencesFileName),Context.MODE_PRIVATE);
    String username = getCurrentIdentity();
    return mSharedPreference.getString(username,"Sync_Preference");
}
    public String getSyncSharedPreference(String zzxxyz){
        mSharedPreference = mContext.getSharedPreferences(mContext.getString(
                R.string.sharedpreferencesFileName),Context.MODE_PRIVATE);
        return mSharedPreference.getString(zzxxyz,"Sync_Preference");
    }
    /****************************************************************
    Method writes to the sharedPreferences file
     ****************************************************************/
    public void writeSyncPreferences(String string){
        mSharedPreference = mContext.getSharedPreferences(mContext.getString(
                R.string.sharedpreferencesFileName),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putString(getCurrentIdentity(),string);
        editor.commit();
    }
    /****************************************************************
     This method writes to the sharedpreferences file.
     the latest encounter number that will be used to identify an encounter locally in the absence of network.
     only maximum of 10 encounter numbers will be inserted in the SQLite table.
     ****************************************************************/
public void writeEncounterNumPreferences(long encounterNum){
        mSharedPreference = mContext.getSharedPreferences(mContext.getString(
                R.string.sharedpreferencesFileName),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=mSharedPreference.edit();
        editor.putLong(mContext.getString(R.string.encounter),encounterNum);
        editor.commit();
}
public long getEncounterNumPreferences(){
    long enctrNum=-1;
    mSharedPreference = mContext.getSharedPreferences(mContext.getString(
            R.string.sharedpreferencesFileName),Context.MODE_PRIVATE);
    if(mSharedPreference.contains(mContext.getString(R.string.encounter))){
        enctrNum=mSharedPreference.getLong(mContext.getString(R.string.encounter),-1);

    }
    else{
        enctrNum=1;
    }
    writeEncounterNumPreferences(enctrNum+1);
    return enctrNum;
}

    /*******
     * Gets the details of the user that is currently logged in
     * @return
     */
    public String getCurrentIdentity(){
    mSharedPreference = mContext.getSharedPreferences( mContext.getString(R.string.sharedpreferencesFileName),Context.MODE_PRIVATE);
    if(mSharedPreference.contains("zzxxyz"))
        return mSharedPreference.getString("zzxxyz","");
    else
        return "";
}
    /*******
     * Sets the details of the user that is currently logged in
     * @return
     */
public void setCurrentIdentity(String zzxxyz){
    mSharedPreference = mContext.getSharedPreferences( mContext.getString(R.string.sharedpreferencesFileName),Context.MODE_PRIVATE);
    SharedPreferences.Editor editor=mSharedPreference.edit();
    editor.putString("zzxxyz",zzxxyz);
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

    /****
     * Method to insert record in to the SQLite database
     */
    public void insertRecords(){
        InsertToDB insertRecords= new InsertToDB(mDbhelper,mRecord);
        Log.i(TAG, "starting insertion on a new Thread!!");
        new Thread(insertRecords).start();

    }
    //insert encounterId.....
    public void insertRecords(String encounterId){
    InsertToDB insertRecords= new InsertToDB(mDbhelper,encounterId);
    Log.i(TAG, "saving response on a new Thread!!");
    new Thread(insertRecords).start();
}



    /************************************************************************************
     * A thread that will insert records to the SQLite database
     **********************************************************************************/
    public class InsertToDB implements Runnable{
        private ImageRecorderDatabase mDbhelper;
        private List<ImageRecordDBRecord> mRecords;
        private String mEncounterId;
        private String mEmail;
        public InsertToDB(ImageRecorderDatabase dbHelper,List<ImageRecordDBRecord> records){
            this.mDbhelper=dbHelper;
            this.mRecords=records;
        }
        public InsertToDB(ImageRecorderDatabase dbHelper,String encounterId){
            this.mDbhelper=dbHelper;
            this.mEncounterId=encounterId;
            this.mEmail=getCurrentIdentity();
            this.mRecords=null;
        }
    @Override
    public void run() {
        Log.i(TAG,"inserting records");

        SQLiteDatabase db=null;
                if(mDbhelper==null) {
                    mDbhelper=new ImageRecorderDatabase(mContext);
                }
               db= mDbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(mRecords !=null){
           for (ImageRecordDBRecord record: this.mRecords){
               values.put(ImageRecorderDatabase.FILE_NAME,record.getmFileName());
               values.put(ImageRecorderDatabase.ENCOUNTER_NUM,record.getmEncounterNum());
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
        }
        else if(mEncounterId!=null){

            DateFormat dTo = new  SimpleDateFormat("yyyy-MM-dd");

            values.put(ImageRecorderDatabase.ENCOUNTER_ID,mEncounterId);
            values.put(ImageRecorderDatabase.DATE, dTo.format(new Date()));
            values.put(ImageRecorderDatabase.USER_NAME,mEmail);
            db.insert(ImageRecorderDatabase.UPLOAD_RESPONSE,null,values);
            values.clear();
        }
       //close the database.
        db.close();
        //Start the Syncing Service.
     }

    }


    /**********************************
    This method starts the Sync Service.
     @buttonId refers to the Id of the button that initiates the sync. value is -1 if service is started internally
     *************************************/
    public void startSyncing(){
        Intent intent = new Intent(mContext,SyncerService.class);
        if(!SyncerService.IsRunning) {
            intent.putExtra("SyncStarted", MainActivity.SYNC_STARTED);
            intent.putExtra("SyncComplete", MainActivity.SYNC_COMPLETE);
        }
        if(!SyncerService.IsRunning) {
            mContext.startService(intent);
           // Toast.makeText(mContext, "Service will be started!!", Toast.LENGTH_SHORT).show();
        }
        else{
           // Toast.makeText(mContext, "Syncing in progress already!!", Toast.LENGTH_SHORT).show();
        }

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
        long encounterNum = getEncounterNumPreferences();
        mRecord=new ArrayList<ImageRecordDBRecord>();
        for (String filename : mImagePaths) {
            ImageRecordDBRecord record = new ImageRecordDBRecord();
            record.setFileName(filename);
            record.setUsername(getCurrentIdentity());
            Date date = new Date();
            record.setDate(date);
            record.setIsUploaded(isUploaded);
            record.setmEncounterNum(encounterNum);
            mRecord.add(record);

        }

    }

 /*//Volley!!! not used as of now....
    public void createPostRequest(String image){
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String API_URL="http://tobedecided.com/wildbook/";
        final ImageModel imageModel = new ImageModel(image);
        StringRequest request = new StringRequest(Request.Method.POST,API_URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                Log.i("Recieving Response",response);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.i("Receiving Response ERROR!!", error.toString());
            }
        }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("image",imageModel.mImage.toString());
                params.put("long",Double.toString(imageModel.mLongitude));
                params.put("lat",Double.toString(imageModel.mLatitude));
                return params;
            }
        };
        try{String rq=request.getBody().toString();
            Log.i("Creating Requiest",rq);}
        catch(AuthFailureError r){}

        queue.add(request);
    }
    *//* Vvvvv important method.
    Method uploads images to the wildbook db using the Goog Old httpurlconnection.
     *//*
    public boolean uploadPictures(String imagesNames){
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            URL url = new URL("http://uidev.scribble.com/v2/EncounterForm");
            String boundary = "==="+System.currentTimeMillis()+"===";
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoOutput(true); // indicates POST method
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream,"UTF-8"),true);
            writer.append("User-Agent: " + "Wildbook.Mobile").append("\r\n");
            writer.flush();

            File image = new File(imagesNames);
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"" + "jsonResponse" + "\"")
                    .append("\r\n");
            writer.append("Content-Type: text/plain; charset=" + "UTF-8").append(
                    "\r\n");
            writer.append("\r\n");
            writer.append("true").append("\r\n");
            writer.flush();
            //adding image details....

            writer.append("\r\n").flush();
            writer.append("--" + boundary + "--").append("\r\n");
            //Log.i(TAG,httpURLConnection.getContent().toString());
            writer.close();
            int status = httpURLConnection.getResponseCode();
            if(status==HttpURLConnection.HTTP_OK){

                BufferedReader reader = new BufferedReader( new InputStreamReader(httpURLConnection.getInputStream()));
                String line = null;
                StringBuilder responseString = new StringBuilder();
                while((line=reader.readLine())!=null){
                    Log.i("RESPONSE",line);
                    responseString.append(line+"\n");
                }

                 String responseForJSON=responseString.toString();
                JSONObject json = new JSONObject(responseForJSON);
                Log.i(TAG,json.get("success")+" encounterId"+json.get("encounterId"));


                httpURLConnection.disconnect();
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.i(TAG,"ERROR!!"+e.getMessage().toString());
            return false;
        }
        return false;
    }*/

    // Method used for sending Syncing Notifications....
    public void sendNotification(String msg,  String naam) {
        NotificationManager mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder;
        PendingIntent contentIntent=null;
        mBuilder = new NotificationCompat.Builder(mContext)
                        .setContentTitle("Wildbook")
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);
        if(msg.equals("Sync Started!")) {
            contentIntent = PendingIntent.getActivity(mContext, 0,
                    new Intent(mContext, MainActivity.class), 0);
            mBuilder.setSmallIcon(R.drawable.notification_sync);


        }
        else if(msg.equals("Upload Started")){
            mBuilder.setSmallIcon(R.drawable.imageuploading);
        }
        else if(msg.equals("Error")){
            mBuilder.setSmallIcon(R.drawable.error);
            //mBuilder.setContentText(mContext.getResources().getString(R.string.sync_error));
        }
        else{
            Intent intent = new Intent(mContext,DisplayImagesUsingRecyclerView.class);
            Log.i(TAG,"naam in Utilities:"+naam);
            intent.putExtra("UploadedBy",naam);
            intent.putExtra("source","notification");
            contentIntent = PendingIntent.getActivity(mContext, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setSmallIcon(R.drawable.notification_sync_complete);

        }
        if(contentIntent!=null) {
            mBuilder.setContentIntent(contentIntent);
        }
        mNotificationManager.notify(1, mBuilder.build());
    }





}
