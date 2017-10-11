package com.ibeis.wildbook.wildbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private static final int PADDING = 4;
    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;
    private Context c;
    private ArrayList<String> imagesPath;
    //private ArrayList<Integer>picIds;
    ImageAdapter(Context context, ArrayList<String>selectedImages){
        c=context;
        //imagesPath= new ArrayList<String>();
        imagesPath=selectedImages;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public Object getItem(int position){return imagesPath.get(position);}
    @Override
    public int getCount(){return imagesPath.size();}
    @Override
    public View getView(int position, View view, ViewGroup vg){
        //setPicId();
        ImageView imageView=(ImageView)view;
        if(imageView==null){
            imageView = new ImageView(c);
            imageView.setLayoutParams(new GridView.LayoutParams(WIDTH,HEIGHT));
            imageView.setPadding(PADDING, PADDING, PADDING, PADDING);

        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //imageView.setImageURI(Uri.parse(new File(imagesPath.get(position)).getAbsolutePath().toString()));
        File f = new File(imagesPath.get(position));
        Bitmap imgBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
        imageView.setImageBitmap(imgBitmap);
        return imageView;
    }

}
