package com.ibeis.wildbook.wildbook;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class UploadCamPics extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter ;
    private ArrayList<Uri> imagesList=new ArrayList<Uri>();
    private ArrayList<String> imagesNames = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_upload_preview);
        Intent intent = getIntent();
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

    }
}
