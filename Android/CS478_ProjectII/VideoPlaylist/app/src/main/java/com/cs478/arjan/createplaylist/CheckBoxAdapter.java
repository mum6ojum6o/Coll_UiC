package com.cs478.arjan.createplaylist;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import static com.cs478.arjan.createplaylist.R.id.checkbox;

/**
 * Created by Arjan on 2/19/2017.
 */

public class CheckBoxAdapter extends ArrayAdapter<Songs> {
    Songs[] songsList;
    Context context;
    LayoutInflater inflater;
    CheckBox cb;
    public CheckBoxAdapter(Context ctext,Songs[] songList){
        super(ctext, R.layout.listview_layout, songList);
        this.songsList=songList;
        context=ctext;
    }
    public int checkboxCount(){
        int count=0;
        for (int i=0;i<songsList.length;i++) {
            if (songsList[i].getChecked())
                count += 1;
        }
        return count;
    }
public int getCount(){
    return songsList.length;
}
    public void selectAll(){
        for (int i=0;i<songsList.length;i++)
            songsList[i].setChecked(true);
    }
    public View getView(final int pos, View view, ViewGroup parent){
        inflater =LayoutInflater.from(context);
        view=inflater.inflate(R.layout.listview_layout,parent,false);
        TextView songName=(TextView)view.findViewById(R.id.textView1);
        cb=(CheckBox) view.findViewById(checkbox);
        songName.setText(songsList[pos].getName());
       cb.setChecked(songsList[pos].getChecked());
        cb.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.i("CheckBoxAdapter:","In CheckBox onClick Listener");
                if(((CheckBox)v).isChecked()) {
                    songsList[pos].setChecked(true);
                    Log.i("CheckBoxAdapter:","In CheckBox onClick Listener.Pos:"+pos);
                 }
                else
                    songsList[pos].setChecked(false);
            }
        });
        return view;
    }
}
