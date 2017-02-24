package com.cs478.arjan.createplaylist;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Arjan on 2/20/2017.
 */

public class ImageAdapter extends BaseAdapter {
    private static final int PADDING = 4;
    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;
    private Context c;
    private ArrayList<String> s;
    private ArrayList<Integer>picIds;
    ImageAdapter(Context context, ArrayList<String>songs){
       c=context;
        s=songs;
    }
    public int getCount(){return s.size();}
    public View getView(int position, View view, ViewGroup vg){
        setPicId();
        ImageView imageView=(ImageView)view;
        if(imageView==null){
            imageView = new ImageView(c);
            imageView.setLayoutParams(new GridView.LayoutParams(WIDTH,HEIGHT));
            imageView.setPadding(PADDING, PADDING, PADDING, PADDING);
            //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        imageView.setImageResource(picIds.get(position));
        return imageView;
    }
    public long getItemId(int position) {
        return picIds.get(position);
    }
    public Object getItem(int position){return s.get(position);}

    public void setPicId(){
        picIds=new ArrayList<Integer>();
        for(int i=0;i<s.size();i++){
            if (s.get(i).equals("Ecstacy Of Gold"))
                picIds.add(R.drawable.ecstacy);
            else if(s.get(i).equals("Dream On"))
                picIds.add(R.drawable.aerosmith_dream);
            else if(s.get(i).equals("Fuel"))
                picIds.add(R.drawable.reload);
            else if(s.get(i).equals("Stairway To Heaven"))
                picIds.add(R.drawable.sth_lz);
            else if(s.get(i).equals("Don't Cry"))
                picIds.add(R.drawable.gnr_dc);
            else if(s.get(i).equals("Soldier Of Fortune"))
                picIds.add(R.drawable.dp_sof);
            else if(s.get(i).equals("High Hopes"))
                picIds.add(R.drawable.pf_hy);
            else if(s.get(i).equals("Hey You!"))
                picIds.add(R.drawable.pf_hh_);
            else if(s.get(i).equals("Until it Sleeps!"))
                picIds.add(R.drawable.load);
        Log.i("test","ecstacy value:"+R.drawable.ecstacy);
        }
    }
}
