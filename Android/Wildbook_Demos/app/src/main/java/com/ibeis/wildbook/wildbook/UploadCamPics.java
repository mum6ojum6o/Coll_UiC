package com.ibeis.wildbook.wildbook;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/*
This Activity is used for displaying the preview of images captured by the user using the
cameraMainActivity.
This activity also enables user to either Upload or Discard the pictures clicked by the user.

 */
public class UploadCamPics extends BaseActivity implements View.OnClickListener {
    public static final String url="http://uidev.scribble.com/v2/EncounterForm";
    final static public String TAG= "DisplaySelectedImages";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter ;
    private Button mUploadBtn,mDiscardBtn,mSelectAll,mUnselectAll;
    //private FirebaseAuth mAuth;

    private ArrayList<Uri> imagesUris=new ArrayList<Uri>();
    private ArrayList<String> imagesNames = new ArrayList<String>();
    private ArrayList<String>mSelectedImages = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_camera_upload_preview);
        mSelectAll = (Button) findViewById(R.id.SelectAllBtn);
        mUploadBtn = (Button) findViewById(R.id.UploadBtn2);
        mDiscardBtn = (Button) findViewById(R.id.DiscardBtn2);
        mUnselectAll = (Button) findViewById(R.id.UnselectAll);
        Intent intent = getIntent();

        imagesNames =intent.getStringArrayListExtra("Files");
        for(String file: imagesNames){
            imagesUris.add(Uri.parse(new File(file).toString()));
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new  GridLayoutManager(UploadCamPics.this,3));
        adapter = new DispPicAdapter(this, imagesUris,imagesNames);
        //adapter = new RecyclerViewAdapter(getApplicationContext(),imagesList);
        recyclerView.setAdapter(adapter);
        mUploadBtn.setOnClickListener(this);
        mDiscardBtn.setOnClickListener(this);
        mSelectAll.setOnClickListener(this);
        mUnselectAll.setOnClickListener(this);
        super.onCreate(savedInstanceState);

    }

    public void getSelectedImages(){
        for (int i=0;i<imagesNames.size();i++){
            if(recyclerView.getChildAt(i).findViewById(R.id.imageView2).getVisibility()==View.VISIBLE){
                mSelectedImages.add(imagesNames.get(i));
            }
        }
    }

    @Override
    public void onClick(View view){

        switch (view.getId()){
            case R.id.UploadBtn2:
                getSelectedImages();
                if(mSelectedImages.size()>0 && mSelectedImages.size()<=10) {
                    if (new Utilities(this).isNetworkAvailable()) {

                        int uploadCount = 0;
                        //adapter.getSelectedImages();
                        ImageUploaderTask task = new ImageUploaderTask(this, mSelectedImages, new Utilities(this).getCurrentIdentity());
                        new Thread(task).start();
                        Log.i(TAG, "redirecting....");
                        //redirect(imagesNames.size(), imagesNames.size());

                        startActivity(new Intent(UploadCamPics.this, MainActivity.class));
                        finish();
                    /*Utilities util = new Utilities(getApplicationContext(),imagesNames,new ImageRecorderDatabase(this));
                    util.uploadPictures(imagesNames);
                    redirect(imagesNames.size(), imagesNames.size());*/

                        /*************************************
                         *
                         * this commented block runs just fine!
                         * ***************************************
                         for (String filename:imagesNames){
                         if(new Utilities(this).uploadPictures(filename)){
                         uploadCount++;
                         }
                         }
                         finish();
                         Toast.makeText(this,uploadCount+" pictures were uploaded!",Toast.LENGTH_LONG).show();
                         startActivity(new Intent(UploadCamPics.this,MainActivity.class));*/

                        /****************************
                         Requestor request = new Requestor(url,"UTF-8","POST");
                         request.addFormField("jsonResponse","true");
                         try {


                         ExifInterface exif = new ExifInterface(imagesNames.get(0));
                         String datepicker = exif.getAttribute(ExifInterface.TAG_DATETIME);
                         DateFormat dfrom = new  SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
                         DateFormat dTo = new  SimpleDateFormat("yyyy-MM-dd");

                         try{
                         Date date = dfrom.parse(datepicker);
                         datepicker = dTo.format(date);
                         }catch(ParseException pe){pe.printStackTrace();Log.i(TAG,"Parsing Issue");}
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
                         for(String file:imagesNames){
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
                         Toast.makeText(this," Images uploaded!",Toast.LENGTH_LONG).show();
                         //startActivity(new Intent(UploadCamPics.this,MainActivity.class));
                         redirect(imagesNames.size(),imagesNames.size());
                         }catch(Exception e){
                         Log.i(TAG,"Server response error!!");
                         Toast.makeText(getApplicationContext(),"The images were not uploaded!!",Toast.LENGTH_LONG).show();
                         } **************************************/
                    } else {
                        //this is running on the main thread!!!!!!!!
                        ImageRecorderDatabase dbHelper = new ImageRecorderDatabase(this);
                        Utilities utility = new Utilities(this, mSelectedImages, dbHelper);
                        utility.prepareBatchForInsertToDB(false);
                        Log.i(TAG, "Loggedin as: " + utility.getCurrentIdentity());
                        if (!(utility.checkSharedPreference(utility.getCurrentIdentity()))) {
                            utility.connectivityAlert().show();
                        } else {
                            utility.insertRecords();
                            Log.i(TAG, "Results saved to SQLite3");
                            Toast.makeText(getApplicationContext(), R.string.uploadRequestOnNoNetwork, Toast.LENGTH_LONG).show();
                            // redirect(0, 0);
                            startActivity(new Intent(UploadCamPics.this, MainActivity.class));
                            finish();
                        }


                    }
                }
                else if(mSelectedImages.size()>10){
                    Dialog dialog = new Dialog(UploadCamPics.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_layout);
                    dialog.setTitle(getResources().getString(R.string.maxuploadHeaderString));
                    TextView tv = (TextView)dialog.findViewById(R.id.dialog_txt);
                    tv.setText(getResources().getString(R.string.maxuploadString));
                    TextView tv1 = (TextView)dialog.findViewById(R.id.dialog_txt1);
                    tv1.setText(" ");
                    dialog.show();
                }
                else{
                    Dialog dialog = new Dialog(UploadCamPics.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_layout);
                    dialog.setTitle(getResources().getString(R.string.noimageselected));
                    TextView tv = (TextView)dialog.findViewById(R.id.dialog_txt);
                    tv.setText(getResources().getString(R.string.noimageselected));
                    TextView tv1 = (TextView)dialog.findViewById(R.id.dialog_txt1);
                    tv1.setText(getResources().getString(R.string.requestimageselection));
                    dialog.show();
                }

                break;
            case R.id.DiscardBtn2:
                startActivity(new Intent(UploadCamPics.this , CameraMainActivity.class));
                finish();
                break;
            case R.id.SelectAllBtn:
                    selectAll();
                    break;
            case R.id.UnselectAll:
                unselectAll();
                break;
        }

    }
    //method to select all images
protected void selectAll(){
    for (int i=0;i<imagesNames.size();i++) {
        if (recyclerView.getChildAt(i).findViewById(R.id.imageView2).getVisibility() != View.VISIBLE) {
            recyclerView.getChildAt(i).findViewById(R.id.imageView2).setVisibility(View.VISIBLE);
            int pos=recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i));
            //recyclerView.getAdapter().;
            //mSelectedImages.add(imagesNames.get(i));
        }
    }


}
    //method to unselcet all images
protected void unselectAll(){
    for (int i=0;i<imagesNames.size();i++) {
        if (recyclerView.getChildAt(i).findViewById(R.id.imageView2).getVisibility() == View.VISIBLE) {
            recyclerView.getChildAt(i).findViewById(R.id.imageView2).setVisibility(View.INVISIBLE);
            //mSelectedImages.add(imagesNames.get(i));
        }
    }
    mSelectedImages.clear();
}

    protected void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        mGoogleApiClient.disconnect();
                        mGoogleApiClient=null;
                        finish();
                        ActivityUpdater.activeActivity=null;
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Log.i("ImageViewActivity","Logging out from ImageViewActivity");
                        new Utilities(UploadCamPics.this).setCurrentIdentity("");
                        startActivity(intent);

                        finishAffinity();
                    }
                });


    }
}
