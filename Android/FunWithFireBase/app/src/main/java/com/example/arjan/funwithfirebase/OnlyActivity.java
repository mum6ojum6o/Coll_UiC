package com.example.arjan.funwithfirebase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class OnlyActivity extends AppCompatActivity  {
    private ImageView image1,image2;
    private FirebaseAuth mAuth;
    StorageReference mStorageRef;
    private TextView currentUser;
    FirebaseStorage storage;
    final private String TAG="FunWithFireBase";
    private Button signOut;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only);
        Intent intent =getIntent();


        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this,RegisterActivity.class));
        }

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        currentUser = (TextView) findViewById(R.id.currUserTxtVw);
        currentUser.setText("Welcome!"+ firebaseUser.getEmail());
        image1 = (ImageView) findViewById(R.id.FireBase_imageView1);
        image2 = (ImageView) findViewById(R.id.FireBase_imageView2);
        signOut = (Button)findViewById(R.id.SignOutBtn);
        loadPics();
        image1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                loadPics();
            }
        });
        image2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                loadPics();
            }
        });
        signOut.setOnClickListener(new View.OnClickListener(){
        public void onClick(View view){
            mAuth.getInstance().signOut();

            finish();
            startActivity(new Intent(getApplicationContext(),MainLoginActivity.class));

        }
    });
    }
    protected void loadPics(){
        storage= FirebaseStorage.getInstance();
        int picNumber1 = getRandNum();
        int picNumber2 = getRandNum();
        //keep generating numbers till both are not equal.
        while (picNumber1==picNumber2){
            picNumber1=getRandNum();
            picNumber2=getRandNum();
        }
        picInflator(image1,picNumber1);
        picInflator(image2,picNumber2);
    }
    //pic inflator is a method that loads pictures.
    protected void picInflator(final ImageView imageVw,int pic){
        mStorageRef = storage.getReferenceFromUrl("gs://funwithfirebase-a712c.appspot.com/TestApp").child(pic+".png");
        try{
            final File aFile = File.createTempFile("images","png");
            mStorageRef.getFile(aFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(aFile.getAbsolutePath());
                    imageVw.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }catch(IOException e){}
    }
protected int getRandNum(){
    Random rand = new Random();
    return rand.nextInt(14)+1;
}
}
