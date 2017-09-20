package com.ibeis.wildbook.wildbook;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class DisplaySelectedImages extends AppCompatActivity implements View.OnClickListener{
protected ArrayList<String> selectedImages = new ArrayList<String>();
    protected Button UploadBtn,DiscardBtn;
    protected StorageReference mStorage;
    protected FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mStorage= FirebaseStorage.getInstance().getReference();
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
                    StorageReference filePath=mStorage.child("Photos").child(mAuth.getCurrentUser().getEmail());
                    for(String s:selectedImages){
                        filePath.putFile(Uri.parse(s)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(),"Upload Successfull",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                break;
            case R.id.DiscardBtn2:
                startActivity(new Intent(DisplaySelectedImages.this , MainActivity.class));
        }
    }
}
