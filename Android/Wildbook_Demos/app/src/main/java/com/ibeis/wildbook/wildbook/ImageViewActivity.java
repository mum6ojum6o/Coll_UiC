package com.ibeis.wildbook.wildbook;

import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/*************************************************************************
 * Class to view Higher resolution of the images
 *********************************************************************/
public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.activity_imageview_activity);
        Log.i("IMAGEVIEWACTIVITY", getIntent().getStringExtra("POS"));
        Uri uri = Uri.parse(getIntent().getStringExtra("POS"));
        ImageView imageView = new ImageView(getApplicationContext());
        ImageView imgView = (ImageView)findViewById(R.id.imgView);
        //imageView.setImageURI(uri);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        //setContentView(imageView);
        Glide
                .with(ImageViewActivity.this)
                .load(uri)// the uri you got from Firebase
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                . override(width,height)
                .centerCrop()
                .crossFade()
                .into(imgView);
    }
}
