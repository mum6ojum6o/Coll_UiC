package com.ibeis.wildbook.wildbook;

import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/*************************************************************************
 * Class to view Higher resolution of the images
 *********************************************************************/
public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.wildbook2);
        getSupportActionBar().setTitle(R.string.imagePreviewString);
        RecyclerView recyclerView =null;
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.action_bar,null)));
        Uri uri=null;
        String filePath=null;
        if(getIntent().getStringArrayListExtra("assets")==null) {
            setContentView(R.layout.activity_imageview_activity);
            if(getIntent().hasExtra("POS"))
                uri= Uri.parse(getIntent().getStringExtra("POS"));
            if(getIntent().hasExtra("filePath"))
                filePath= getIntent().getStringExtra("filePath");
            recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
            ImageView imageView = new ImageView(getApplicationContext());
            ImageView imgView = (ImageView) findViewById(R.id.imgView);
            //imageView.setImageURI(uri);
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            //setContentView(imageView);
            if (!getIntent().getStringExtra("Adapter").equals("DispPic")) {
                Glide
                        .with(ImageViewActivity.this)
                        .load(uri)// the uri you got from Firebase
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                        .crossFade()
                        .into(imgView);
            } else {
                //imgView.setImageURI(uri);
                //Note:- Loading an image through uri with Glide can sometimes lead an issue.
                //thats because,sometime the uri may not contain headers like (Uri:, http:, https:)
                //
                if(filePath!=null) {
                    Glide
                            .with(ImageViewActivity.this)
                            .load(new File(filePath).getPath())
                            .placeholder(R.mipmap.wildbook2)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fitCenter()
                            .crossFade()
                            .into(imgView);
                }
                else if(uri!=null) {
                Glide
                        .with(this)
                        .load(uri)
                        .placeholder(R.mipmap.wildbook2)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                        .crossFade()
                        .into(imgView);
                }
            }
        }
        else if(getIntent().getStringArrayListExtra("assets") !=null){
            setContentView(R.layout.activity_display_images_using_recycler_view);
            recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
            findViewById(R.id.encounter_details).setVisibility(View.VISIBLE);
            TextView longitude,latitude,date,encounterId;
            longitude=(TextView)findViewById(R.id.encounter_long);
            latitude=(TextView)findViewById(R.id.encounter_lat);
            date=(TextView)findViewById(R.id.encounter_date);
            encounterId=(TextView)findViewById(R.id.encounter_id);

            date.setText(getIntent().getStringExtra("encounter_date"));
            longitude.setText(getIntent().getStringExtra("longitude"));
            latitude.setText(getIntent().getStringExtra("latitude"));
            encounterId.setText(getIntent().getStringExtra("encounterId"));


            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(ImageViewActivity.this,3));
            //JSONArray jsonObject = new JSONObject(getIntent().getStringArrayListExtra("assets"));
            ArrayList<String> imagePaths = getIntent().getStringArrayListExtra("assets");
            ArrayList<Uri> imageUris = new ArrayList<Uri>();
            for (String aUri:imagePaths)
                imageUris.add(Uri.parse(aUri));
            RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(ImageViewActivity.this,imageUris);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }
}
