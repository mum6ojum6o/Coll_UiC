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
import android.os.StrictMode;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        if(!SyncerService.IsRunning && !isNetworkAvailable()) {
            startSyncing();
        }

    }

    }
    /**********************************
    This method starts the Sync Service.
     @buttonId refers to the Id of the button that initiates the sync. value is -1 if service is started internally
     *************************************/
    public void startSyncing(){
        Intent intent = new Intent(mContext,SyncerService.class);
        if(MainActivity.handler!=null) {
            intent.putExtra("SyncStarted", MainActivity.SYNC_STARTED);
            intent.putExtra("SyncComplete", MainActivity.SYNC_COMPLETE);
            Log.i("Utilities", "Mainactivity.handler" + (MainActivity.handler == null) + " MainActivity.SYNC_STARTED=" +
                    MainActivity.SYNC_STARTED + "MainActivity.SYNC_COMPLETE" + MainActivity.SYNC_COMPLETE);
            intent.putExtra("Messenger", new Messenger(MainActivity.handler));
        }
        mContext.startService(intent);
        Toast.makeText(mContext,"Service will be started!!",Toast.LENGTH_SHORT).show();


    }

    /**********************************************************
     * This method uploads pictures.....
     * and then redirects to the mainactivity....
     **********************************************************/
    public void uploadPictures(ArrayList<String> _images ){
        ArrayList<ImageModel> imageModels=new ArrayList<ImageModel>();
        for(String image:_images){
            ImageModel anImage=new ImageModel(image);
            imageModels.add(anImage);
            //createPostRequest(image);
        }

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
    /* Vvvvv important method.
    Method uploads images to the wildbook db using the Goog Old httpurlconnection.
     */
    public boolean uploadPictures(String imagesNames){
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            URL url = new URL("http://uidev.scribble.com/v2/EncounterForm");
            String boundary = "----------------------------------------";
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoOutput(true); // indicates POST method
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            // httpURLConnection.setRequestProperty("jsonResponse","true");
            OutputStream outputStream = httpURLConnection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream,"UTF-8"),true);
            writer.append(boundary).append("\r\n");
            File image = new File(imagesNames);
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"" + "jsonResponse" + "\"")
                    .append("\r\n");
            writer.append("Content-Type: text/plain; charset=" + "UTF-8").append(
                    "\r\n");
            writer.append("\r\n");
            writer.append("\"true\"").append("\r\n");
            writer.flush();
            //adding image details....
            writer.append(boundary).append("\r\n");
            writer.append(
                    "Content-Disposition: form-data; name=\"" + "theFiles"
                            + "\"; filename=\"" + image.getName() + "\"")
                    .append("\r\n");
            writer.append("Content-Transfer-Encoding: binary").append("\r\n");
            writer.append("\r\n");
            writer.flush();
            FileInputStream inputStream = new FileInputStream(image);
            byte[] buffer = new byte[4096]; //whats the significance of this!!!
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // Log.i(TAG,buffer.toString());
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();
            writer.append("\r\n");
            writer.flush();
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

                Response2Json msg = new Gson().fromJson(responseForJSON,Response2Json.class);
                reader.close();
                Log.i("JSON_RESPONSE:","Success:"+msg.getSuccessStatus()+" Encounter ID:"+msg.getEncounterId());
                httpURLConnection.disconnect();
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.i(TAG,"ERROR!!"+e.getMessage().toString());
            return false;
        }
        return false;
    }

}
