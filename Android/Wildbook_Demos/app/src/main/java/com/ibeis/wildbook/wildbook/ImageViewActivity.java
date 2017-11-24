package com.ibeis.wildbook.wildbook;

import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/*************************************************************************
 * Class to view Higher resolution of the images
 *********************************************************************/
public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Bundle bundle = getIntent().getExtras();

       // Log.i("IMAGEVIEWACTIVITY", getIntent().getStringExtra("POS"));
        if(getIntent().getStringArrayListExtra("assets")==null) {
            setContentView(R.layout.activity_imageview_activity);
            Uri uri = Uri.parse(getIntent().getStringExtra("POS"));
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
                imgView.setImageURI(uri);
            }
        }
        else if(getIntent().getStringArrayListExtra("assets") !=null){
            setContentView(R.layout.activity_display_images_using_recycler_view);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.wildbook2);
            getSupportActionBar().setTitle(R.string.encounterdetails);
            RecyclerView recyclerView =(RecyclerView)findViewById(R.id.recyclerView);
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
