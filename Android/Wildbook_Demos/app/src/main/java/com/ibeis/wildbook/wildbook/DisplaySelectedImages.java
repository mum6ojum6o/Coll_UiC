package com.ibeis.wildbook.wildbook;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
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
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.wildbook2);
        getSupportActionBar().setTitle(R.string.imagePreviewString);
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.action_bar,null)));
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
                   /* Utilities util = new Utilities(getApplicationContext(),selectedImages,new ImageRecorderDatabase(this));
                    util.uploadPictures(selectedImages);

                    redirect(selectedImages.size(),selectedImages.size());*/
                   /*Requestor request = new Requestor(UploadCamPics.url,"UTF-8","POST");
                    request.addFormField("jsonResponse","true");
                    for(String file:selectedImages){
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
                        startActivity(new Intent(DisplaySelectedImages.this,MainActivity.class));
                    }catch(Exception e){
                        Log.i(TAG,"Server response error!!");
                        Toast.makeText(getApplicationContext(),"The images were not uploaded!!",Toast.LENGTH_LONG).show();
                    }finally{
                        redirect(selectedImages.size(),selectedImages.size());
                    }*/

                    ImageUploaderTask task = new ImageUploaderTask(this,selectedImages);
                    Log.i(TAG,"Starting fileupload request using worker thread!!");
                    new Thread(task).start();
                    Log.i(TAG,"redirecting....to mainActivity");
                    //redirect(selectedImages.size(), selectedImages.size());
                    startActivity(new Intent(DisplaySelectedImages.this,MainActivity.class));
                    finish();


                }
                else{//no connectivity
                    ImageRecorderDatabase dbHelper = new ImageRecorderDatabase(this);
                    Utilities utility = new Utilities(this,selectedImages,dbHelper);
                    utility.prepareBatchForInsertToDB(false);
                    Log.i(TAG,"prepared");
                    if(!(new Utilities(this).checkSharedPreference(new Utilities(this).getUserEmail()))) {
                        utility.connectivityAlert().show();
                    }
                    else {

                        utility.insertRecords();
                        Log.i(TAG,"Results saved to SQLite3");
                        Toast.makeText(getApplicationContext(),R.string.uploadRequestOnNoNetwork,Toast.LENGTH_LONG).show();
                        startActivity(new Intent(DisplaySelectedImages.this,MainActivity.class));
                        finish();
                       // redirect(0, 0);
                    }
                }

                break;
            case R.id.DiscardBtn2:
                startActivity(new Intent(DisplaySelectedImages.this , MainActivity.class));
                finish();
        }
    }
    /*
    the redirect method will redirect the user based on the upload status
     */
    //this is duplicated code... try to refactor it in the utility class...

    public void redirect(int imagesUploaded, int imagesRequested){
                if(imagesRequested==imagesUploaded){
                    Toast.makeText(getApplicationContext(),"All images were uploaded!",Toast.LENGTH_LONG).show();
                }
                else if(imagesUploaded == 0 && imagesRequested ==0){
                    Toast.makeText(getApplicationContext(),"The images will be uploaded on the availability of appropriate network!",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),imagesRequested-imagesUploaded+"were uploaded!",Toast.LENGTH_LONG).show();
                }
        finish();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));

    }

}
