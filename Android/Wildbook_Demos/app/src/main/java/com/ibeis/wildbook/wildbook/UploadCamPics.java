package com.ibeis.wildbook.wildbook;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/*
This Activity is used for displayin the preview of images captured by the user using the
cameraMainActivity.
This activity also enables user to either Upload or Discard the pictures clicked by the user.

 */
public class UploadCamPics extends Activity implements View.OnClickListener {
    final static public String TAG= "DisplaySelectedImages";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter ;
    private Button mUploadBtn,mDiscardBtn;
    private FirebaseAuth mAuth;

    private ArrayList<Uri> imagesList=new ArrayList<Uri>();
    private ArrayList<String> imagesNames = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_upload_preview);
        mUploadBtn = (Button) findViewById(R.id.UploadBtn2);
        mDiscardBtn = (Button) findViewById(R.id.DiscardBtn2);
        Intent intent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        imagesNames =intent.getStringArrayListExtra("Files");
        for(String file: imagesNames){
            imagesList.add(Uri.parse(new File(file).toString()));
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new LinearLayoutManager(UploadCamPics.this));
        adapter = new DispPicAdapter(getApplicationContext(), imagesList,imagesNames);
        recyclerView.setAdapter(adapter);
        mUploadBtn.setOnClickListener(this);
        mDiscardBtn.setOnClickListener(this);

    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.UploadBtn2:
                if (new Utilities(this).isNetworkAvailable()) {
                    StorageReference storage;
                    FirebaseAuth auth;
                    DatabaseReference databaseReference;
                    auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    storage = FirebaseStorage.getInstance().getReference();
                    databaseReference = FirebaseDatabase.getInstance().getReference(MainActivity.databasePath);
                    UploadTask upload = null;
                    StorageReference filePath = null;
                    final ArrayList<Uri> successUploads = new ArrayList<Uri>();
                    //StorageReference filePath=mStorage.child("Photos").child(mAuth.getCurrentUser().getEmail());
                    for (String s : imagesNames) {
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
                            Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            successUploads.add(downloadUrl);


                        }
                    });
                    redirect(successUploads.size(), imagesNames.size());
                }
                else{
                    List<ImageRecordDBRecord> records= new ArrayList<ImageRecordDBRecord>();
                    ImageRecorderDatabase dbHelper = new ImageRecorderDatabase(this);
                    Utilities utility = new Utilities(this,dbHelper,records);
                    for(String filename : imagesNames){
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
                startActivity(new Intent(UploadCamPics.this , CameraMainActivity.class));
                finish();
                break;
        }

    }
    public void redirect(int imagesUploaded, int imagesRequested){
        if(imagesRequested==imagesUploaded && imagesRequested>0 && imagesUploaded >0){
            Toast.makeText(getApplicationContext(),"All images were uploaded!",Toast.LENGTH_LONG).show();
        }
        else if(imagesUploaded == 0 && imagesRequested ==0){
            Toast.makeText(getApplicationContext(),"The images will be uploaded on the availability of appropriate network!",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),imagesRequested-imagesUploaded+"were uploaded!",Toast.LENGTH_LONG).show();
        }
        finish();
        startActivity(new Intent(UploadCamPics.this,MainActivity.class));

    }

}
