package com.ibeis.wildbook.wildbook;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    protected static final int IMAGE_GALLERY_REQUEST = 20;
    protected static final int CAMERA_PERMISSION_REQUEST_CODE=88;
    protected static final int READ_STORAGE_PERMISSION_REQUEST=99;
    public static final String TAG ="MainActivity";
    protected Button RepEncounter,SignOutBut,UploadFromGallery,History;
    private FirebaseAuth mAuth;
    protected TextView UserName;
    protected ArrayList<String> selectedImages= new ArrayList<String>();
    protected static String storagePath="Photos/";
    protected static String databasePath="Photos/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"MainActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this,Login.class));
        }
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        UserName=(TextView)findViewById(R.id.username);
        UserName.setText(firebaseUser.getDisplayName()+"!");
        RepEncounter = (Button) findViewById(R.id.RepEnc);
        UploadFromGallery=(Button)findViewById(R.id.GallUpload);
        SignOutBut = (Button)findViewById(R.id.SingOut);
        History = (Button)findViewById(R.id.historyBtn);
        SignOutBut.setOnClickListener(this);
        RepEncounter.setOnClickListener(this);
        History.setOnClickListener(this);
        UploadFromGallery.setOnClickListener(this);
        storagePath = storagePath+firebaseUser.getEmail();
        if(databasePath.equals("Photos/"))
            databasePath = databasePath+firebaseUser.getUid();
    }
    @Override
    public void onResume(){
        super.onResume();

        //selectedImages = new ArrayList<String>();
        Log.i(TAG,"OnResume selectedImagesCreated n size");//+selectedImages.size());
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.RepEnc:
               if(ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED){
                    startActivity(new Intent(getApplicationContext(), CameraActivity.class));
                }
                else{
                   ActivityCompat.requestPermissions(this,new String[]{"android.permission.CAMERA"},CAMERA_PERMISSION_REQUEST_CODE);
                }
                break;
            case R.id.GallUpload:
                //Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                if(ContextCompat.checkSelfPermission(getApplicationContext(),"android.permission.READ_EXTERNAL_STORAGE")==PackageManager.PERMISSION_GRANTED) {
                    requestPictureGalleryUpload();
                }
                else{
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"},READ_STORAGE_PERMISSION_REQUEST);
                }
                break;
            case R.id.historyBtn:
                    startActivity(new Intent(MainActivity.this, DisplayImagesUsingRecyclerView.class));
                break;
            case R.id.SingOut:
                mAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),Login.class));
        }
    }

    /*
    *fetch the pictures from Firebase.
     */
    public void retrieveFirebaseData(){

    }
@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int []grantResults){
        switch(requestCode) {
            case IMAGE_GALLERY_REQUEST:
                //
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(getApplicationContext(), CameraActivity.class));
            } else {
                displayToasts(PackageManager.PERMISSION_DENIED);
                //Toast.makeText(getApplicationContext(), "In order to contribute to cause we would encourage you to grant us access in the future! ", Toast.LENGTH_LONG).show();
            }
            break;
            case READ_STORAGE_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestPictureGalleryUpload();
                }
                else{
                    displayToasts(PackageManager.PERMISSION_DENIED);
                }
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        String selectedImage=null;
        if(resultCode==RESULT_OK){
            Log.i(TAG,"OnActivityResult OK");
            if(requestCode==IMAGE_GALLERY_REQUEST) {
                selectedImages=new ArrayList<String>();
                ContentResolver contentResolver = getContentResolver();
                String [] filePathColumn = {MediaStore.Images.Media.DATA};


                if (data.getClipData() != null ) {

                    ClipData mClipData = data.getClipData();
                    if (mClipData.getItemCount()>10){
                        Log.i(TAG, "More than 10 images selected");
                        Toast.makeText(getApplicationContext(), "Please select upto 10 images only", Toast.LENGTH_LONG).show();
                    }
                    else {
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            selectedImage = cursor.getString(columnIndex);
                            selectedImages.add(selectedImage);
                            cursor.close();

                        }
                        showGallPicturesPreview(selectedImages);
                    }
                }
                else if (data.getData()!=null){
                    Cursor cursor = getContentResolver().query(data.getData(),filePathColumn,null,null, null);
                    cursor.moveToFirst();
                    while(!cursor.isAfterLast()) {
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        selectedImages.add(cursor.getString(columnIndex));
                        cursor.moveToNext();
                    }
                    cursor.close();
                    showGallPicturesPreview(selectedImages);
                }

            }
        }
    }
    //metho
    public void showGallPicturesPreview(ArrayList<String> images){
        Intent intent = new Intent(MainActivity.this,DisplaySelectedImages.class);
        intent.putExtra("selectedImages",images);
        startActivity(intent);
    }
    //this method enables users to select multiple pictures from the mobile device.
    public void requestPictureGalleryUpload(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        photoPickerIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), IMAGE_GALLERY_REQUEST);
    }

    public void displayToasts(int type){
        switch (type){
            case PackageManager.PERMISSION_DENIED:
                Toast.makeText(getApplicationContext(), "In order to contribute to cause we would encourage you to grant us access in the future! ", Toast.LENGTH_LONG).show();
                break;
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG,"OnResume");
        selectedImages=null;
    }
}
