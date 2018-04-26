package com.ibeis.wildbook.wildbook;

import android.app.Activity;
//import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/****************************************************************
 * Created by Arjan on 10/17/2017.
 *This class will contain the utility methods.
 * REFACTOR,REFACTOR,REFACTOR!!!
 ***************************************************************/

public class Utilities {
    public CountingIdlingResource idlingResource = new CountingIdlingResource("TEST1");
    private static final String TAG = "Utilities Class";
    private AlertDialog.Builder mAlertDialogBuilder;
    private SharedPreferences mSharedPreference;
    private Context mContext;
    private List<ImageRecordDBRecord> mRecord;
    private ImageRecorderDatabaseSQLiteOpenHelper mDbhelper;
    private List<String> mImagePaths;
    Utilities (Context context){
        mContext = context;
    }
    Utilities (Context context, ImageRecorderDatabaseSQLiteOpenHelper dbHelper, List<ImageRecordDBRecord> record){
        this.mContext = context;
        this.mRecord=record;
        this.mDbhelper=dbHelper;
    }
    Utilities (Context context, List<String> images,ImageRecorderDatabaseSQLiteOpenHelper dbHelper){
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
                        //Challenge- how to get the values from the UploadCamPicsActivity class?
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

        return mAlertDialogBuilder;
    }
    /*****************************************************************
    This method checks if the user's Syncing properties are set
     ****************************************************************/
    public boolean checkSharedPreference(String username){
        mSharedPreference = mContext.getSharedPreferences(mContext.getString(
                R.string.sharedpreferencesFileName),Context.MODE_PRIVATE);
        if(mSharedPreference.getString(username,"Sync_Preference").equals("Sync_Preference")){
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
     @param string
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

    /*****************************************
     * Gets the details of the user that is currently logged in
     * @return
     ***************************************/
    public String getCurrentIdentity(){
    mSharedPreference = mContext.getSharedPreferences( mContext.getString(R.string.sharedpreferencesFileName),Context.MODE_PRIVATE);
    if(mSharedPreference.contains("zzxxyz")) {

        return mSharedPreference.getString("zzxxyz", "");
    }
    else
        return "";
}
    /*************************************
     * Sets the details of the user that is currently logged in
     * @return
     *************************************/
    public void setCurrentIdentity(String zzxxyz){
        mSharedPreference = mContext.getSharedPreferences( mContext.getString(R.string.sharedpreferencesFileName),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=mSharedPreference.edit();
        editor.putString("zzxxyz",zzxxyz);
        editor.commit();
    }

    /****************************************************
     * Method to insert record in to the SQLite database
     ****************************************************/
    public void insertRecords(){
        InsertToDB insertRecords= new InsertToDB(mDbhelper,mRecord);
        Log.i(TAG, "starting insertion on a new Thread!!");
        idlingResource.increment();
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
        private ImageRecorderDatabaseSQLiteOpenHelper mDbhelper;
        private List<ImageRecordDBRecord> mRecords;
        private String mEncounterId;
        private String mEmail;
        public InsertToDB(ImageRecorderDatabaseSQLiteOpenHelper dbHelper, List<ImageRecordDBRecord> records){
            this.mDbhelper=dbHelper;
            this.mRecords=records;
        }
        public InsertToDB(ImageRecorderDatabaseSQLiteOpenHelper dbHelper, String encounterId){
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
                    mDbhelper=new ImageRecorderDatabaseSQLiteOpenHelper(mContext);
                }
               db= mDbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(mRecords !=null){
           for (ImageRecordDBRecord record: this.mRecords){
               values.put(ImageRecorderDatabaseSQLiteOpenHelper.FILE_NAME,record.getmFileName());
               values.put(ImageRecorderDatabaseSQLiteOpenHelper.ENCOUNTER_NUM,record.getmEncounterNum());
               values.put(ImageRecorderDatabaseSQLiteOpenHelper.LONGITUDE,record.getmLongitude());
               values.put(ImageRecorderDatabaseSQLiteOpenHelper.LATITUDE,record.getmLatitude());
               values.put(ImageRecorderDatabaseSQLiteOpenHelper.USER_NAME,record.getmUsername());
               SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
               String date = sdf.format(record.getmDate());
               values.put(ImageRecorderDatabaseSQLiteOpenHelper.DATE,record.getmDate().toString());
               values.put(ImageRecorderDatabaseSQLiteOpenHelper.IS_UPLOADED,record.getmIsUploaded());
               db.insert(ImageRecorderDatabaseSQLiteOpenHelper.TABLE_NAME,null,values);
               values.clear();

            }
        }
        else if(mEncounterId!=null){

            DateFormat dTo = new  SimpleDateFormat("yyyy-MM-dd");

            values.put(ImageRecorderDatabaseSQLiteOpenHelper.ENCOUNTER_ID,mEncounterId);
            values.put(ImageRecorderDatabaseSQLiteOpenHelper.DATE, dTo.format(new Date()));
            values.put(ImageRecorderDatabaseSQLiteOpenHelper.USER_NAME,mEmail);
            db.insert(ImageRecorderDatabaseSQLiteOpenHelper.UPLOAD_RESPONSE,null,values);
            values.clear();
        }
       //close the database.
        db.close();
        idlingResource.decrement();
     }

    }


    /**********************************
    This method starts the Sync Service.
     @buttonId refers to the Id of the button that initiates the sync. value is -1 if service is started internally
     *************************************/
    public void startSyncing(){
        Log.i(TAG,"in start Syncing");
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
        String CHANNEL_ID = "Wildbook Notification Channel";
        /* //This code works just fine. but it has been commented because the Target SDK is 25.
        //It needs Target SDK to be 26+ in order to work.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name =  mContext.getResources().getString(R.string.channel_name);
            String description = mContext.getResources().getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }*/
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setContentTitle(mContext.getResources().getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent=null;
        if(msg.equals(mContext.getResources().getString(R.string.sync_started))) {
            contentIntent = PendingIntent.getActivity(mContext, 0,
                    new Intent(mContext, MainActivity.class), 0);
            mBuilder.setSmallIcon(R.drawable.notification_sync);
            mBuilder.setContentText(msg);
        }
        else if(msg.equals(mContext.getResources().getString(R.string.upload_started))){//difference between this and the one on top?
            mBuilder.setSmallIcon(R.drawable.imageuploading);
            mBuilder.setContentText(msg);
        }
        else if(msg.equals(mContext.getString(R.string.sync_error))||
                msg.equals(mContext.getString(R.string.file_not_found_error))||
                msg.equals(mContext.getResources().getString(R.string.error))){
            Log.i(TAG,"Error notification reported!");
            mBuilder.setSmallIcon(R.drawable.error);
            mBuilder.setContentText(mContext.getResources().getString(R.string.error_title));
            mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(mContext.getResources().getString(R.string.sync_error)));
        }
        else{
            Intent intent = new Intent(mContext,UserContributionsActivity.class);
            Log.i(TAG,"naam in Utilities:"+naam);
            intent.putExtra("UploadedBy",naam);
            intent.putExtra("source","notification");
            contentIntent = PendingIntent.getActivity(mContext, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setSmallIcon(R.drawable.notification_sync_complete);
            mBuilder.setContentText(mContext.getResources().getString(R.string.sync_completed));
        }
        if(contentIntent!=null) {
            mBuilder.setContentIntent(contentIntent);
        }
        mNotificationManager.notify(1, mBuilder.build());
    }

    /**********************************************
     * Method that displays a snackbar
     * @param message to be displayed in the snackbar
     * @param bgcolor background color of the snackbar
     ***********************************************/
    public void  displaySnackBar(View layout,int message,int bgcolor){
        Snackbar snack=null;
        View snackView;

        if(message == com.ibeis.wildbook.wildbook.R.string.offline)
            snack=Snackbar.make(layout,message,Snackbar.LENGTH_INDEFINITE);
        else
            snack=Snackbar.make(layout,message,Snackbar.LENGTH_SHORT);
        snackView = snack.getView();
        snackView.setBackgroundColor(bgcolor);
        snack.show();
    }
    public void  displaySnackBar(View layout,String message,int bgcolor){
        Snackbar snack=null;
        View snackView;
        snack=Snackbar.make(layout,message,Snackbar.LENGTH_SHORT);
        snackView = snack.getView();
        snackView.setBackgroundColor(bgcolor);
        snack.show();
    }
    @VisibleForTesting
    @NonNull
    public CountingIdlingResource getIdlingResource(){
        if(idlingResource==null)
            idlingResource = new CountingIdlingResource("TEST1");
        return idlingResource;
    }


}
