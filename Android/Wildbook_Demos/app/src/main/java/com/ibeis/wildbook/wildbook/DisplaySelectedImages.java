package com.ibeis.wildbook.wildbook;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.R.attr.tag;

public class DisplaySelectedImages extends AppCompatActivity implements View.OnClickListener{
    final static public String TAG= "DisplaySelectedImages";
protected ArrayList<String> selectedImages = new ArrayList<String>();
    protected Button UploadBtn,DiscardBtn;
    protected StorageReference mStorage;
    protected FirebaseAuth mAuth;
    protected DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mStorage= FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(MainActivity.databasePath);
        UploadBtn=(Button)findViewById(R.id.UploadBtn2);
        DiscardBtn=(Button)findViewById(R.id.DiscardBtn2);
        selectedImages = getIntent().getStringArrayListExtra("selectedImages");
        GridView gv = (GridView)findViewById(R.id.gridview);
        gv.setAdapter(new ImageAdapter(this,selectedImages));
        UploadBtn.setOnClickListener(this);
        DiscardBtn.setOnClickListener(this);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.UploadBtn2:
                if (new Utilities(this).isNetworkAvailable()) { //check for network availability
                    UploadTask upload = null;
                    StorageReference filePath = null;
                    final ArrayList<Uri> successUploads = new ArrayList<Uri>();
                    //StorageReference filePath=mStorage.child("Photos").child(mAuth.getCurrentUser().getEmail());
                    for (String s : selectedImages) {
                        Uri uploadImage = Uri.fromFile(new File(s));
                        filePath = mStorage.child("Photos/" + mAuth.getCurrentUser().getEmail()).child(uploadImage.getLastPathSegment());
                        String fileName = new File(s).getName();
                        upload = filePath.putFile(uploadImage);
                        String ImageUploadId = databaseReference.push().getKey();
                        databaseReference.child(ImageUploadId).setValue(fileName);
                       /* filePath.putFile(uploadImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(),"Upload Successfull",Toast.LENGTH_SHORT).show();
                            }
                        });*/

                    }
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


                        }
                    });
                    redirect(successUploads.size(), selectedImages.size());
                }
                else{
                    List<ImageRecordDBRecord> records= new ArrayList<ImageRecordDBRecord>();
                    ImageRecorderDatabase dbHelper = new ImageRecorderDatabase(this);
                    Utilities utility = new Utilities(this,dbHelper,records);
                    for(String filename : selectedImages){
                        ImageRecordDBRecord record = new ImageRecordDBRecord();
                        record.setFileName(filename);
                        record.setUsername(mAuth.getCurrentUser().getEmail());
                        Date date = new Date();
                        record.setDate(date);
                        records.add(record);
                    }
                    //Utilities utility = new Utilities(this,)
                    /*If user preferences do not exist in the sharedpreferences*/
                    if(!(new Utilities(this).checkSharedPreference(mAuth.getCurrentUser().getEmail()))) {
                        utility.connectivityAlert().show();
                    }
                    else {
                        utility.insertRecords();

                        redirect(0, 0);
                    }
                }
                break;
            case R.id.DiscardBtn2:
                startActivity(new Intent(DisplaySelectedImages.this , MainActivity.class));
        }
    }
    /*
    the redirect method will redirect the user based on the upload status
     */
    public void redirect(int imagesUploaded, int imagesRequested){
                if(imagesRequested==imagesUploaded){
                    Toast.makeText(getApplicationContext(),"All images were uploaded!",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),imagesRequested-imagesUploaded+"were uploaded!",Toast.LENGTH_LONG).show();
                }
        finish();
        startActivity(new Intent(DisplaySelectedImages.this,MainActivity.class));

    }

}
