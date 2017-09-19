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
    public static final String TAG ="MainActivity";
    protected Button RepEncounter,SignOutBut,UploadFromGallery;
    private FirebaseAuth mAuth;
    protected TextView UserName;
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
        SignOutBut.setOnClickListener(this);
        RepEncounter.setOnClickListener(this);
        UploadFromGallery.setOnClickListener(this);
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
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                //File photosDirectory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                //String picturesDirectoryPath= photosDirectory.getPath();
                //Uri data = Uri.parse(picturesDirectoryPath);
                //photoPickerIntent.setDataAndType(data,"image/*");
                photoPickerIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(photoPickerIntent,"Select Picture"), IMAGE_GALLERY_REQUEST);
                //startActivityForResult(photoPickerIntent,IMAGE_GALLERY_REQUEST);
                break;
            case R.id.SingOut:
                mAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),Login.class));
        }
    }
@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int []grantResults){

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(getApplicationContext(), CameraActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "In order to contribute to cause we would encourage you to grant us access in the future! ", Toast.LENGTH_LONG).show();
                }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Log.i(TAG,"OnActivityResult OK");
            if(requestCode==IMAGE_GALLERY_REQUEST) {
                ContentResolver contentResolver = getContentResolver();
                String [] filePathColumn = {MediaStore.Images.Media.DATA};
                ArrayList<String> selectedImages = new ArrayList<String>();
                Cursor cursor = contentResolver.query(data.getData(),filePathColumn,null,null, null);
                if (null != cursor ) {
                    cursor.moveToFirst();
                    while(!cursor.isAfterLast()) {
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        selectedImages.add(cursor.getString(columnIndex));
                        cursor.moveToNext();
                    }
                    cursor.close();
                }
                Log.i(TAG, "OnActivityResult");
                Toast.makeText(getApplicationContext(), "Please select upto 10 images only", Toast.LENGTH_LONG).show();
                //}
             //   else{

               // }
            }
        }
    }
}
