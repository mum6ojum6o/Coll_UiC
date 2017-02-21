package com.cs478.arjan.createplaylist;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Intent.ACTION_VIEW;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

public class VideoPlaylist extends AppCompatActivity {
    Toolbar myToolbar;
    ArrayAdapter lAdapter;
    CheckBox cb;
    int cbCheck;
    ListView lv;
    Songs[] songs;
    CheckBoxAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playlist);
        myToolbar = (Toolbar) findViewById(R.id.activityToolbar);
        setSupportActionBar(myToolbar);
        lv = (ListView) findViewById(R.id.listview);
        songs= new Songs[]{new Songs("Ecstacy Of Gold",R.drawable.ecstacy),new Songs("Dream On",R.drawable.aerosmith_dream),
                new Songs("Fuel",R.drawable.reload),new Songs("Stairway To Heaven",R.drawable.sth_lz),
                new Songs("Don't Cry",R.drawable.gnr_dc),new Songs("Soldier Of Fortune",R.drawable.dp_sof),
                new Songs("High Hopes",R.drawable.pf_hy),new Songs("Hey You!",R.drawable.pf_hh_),
                new Songs("Until it Sleeps!",R.drawable.load)};
         adapter=new CheckBoxAdapter(this,songs);
        lv.setAdapter(adapter);
         Log.i("VideoPlaylist:", "In onCreate()");
    }
//Displays the app bar menu
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.apptoobar,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //Actions to be taken on the basis of options selected in the ListView.
    public boolean onOptionsItemSelected(MenuItem item){
        Log.i("VideoPlaylist:","In onOptionsItemsSelected");

        switch(item.getItemId()){
            case R.id.action_favorite:
                Log.i("VideoPlaylist:","In onOptionsItemsSelected;action_favorite:"+adapter.checkboxCount());
                if (adapter.checkboxCount()==0)
                    Toast.makeText(getApplicationContext(),"No Songs Selected",LENGTH_SHORT).show();
                else{
                ArrayList<String> ss=new ArrayList<String>();
                for (int i=0;i<songs.length;i++){
                    CheckBox c = (CheckBox)lv.getChildAt(i).findViewById(R.id.checkbox);
                    if(c.isChecked()==true)
                        ss.add(songs[i].getName());
                }
                Intent createPlaylist = new Intent(VideoPlaylist.this,Playlist.class);
                createPlaylist.putExtra("selectedSongs",ss);
                    Log.i("VideoPlaylist:","Creating intent:");
                startActivity(createPlaylist);
            }
                return true;
            case R.id.act_clear: for (int i=0;i<lv.getCount();i++){
                CheckBox c = (CheckBox)lv.getChildAt(i).findViewById(R.id.checkbox);
                if (c.isChecked()){
                    c.setChecked(false);
                    adapter.songsList[i].setChecked(false);
                }
            }
                return true;
            case R.id.act_invert:for (int i=0;i<lv.getCount();i++) {
                CheckBox c = (CheckBox) lv.getChildAt(i).findViewById(R.id.checkbox);
                if (c.isChecked()) {
                    c.setChecked(false);
                    adapter.songsList[i].setChecked(false);
                    Log.i("Videoplaylist:","Checking checkboxes"+ adapter.songsList[i].getName());
                } else {
                    c.setChecked(true);
                    adapter.songsList[i].setChecked(true);
                }
            }

                return true;
            case R.id.act_checkall:

                for (int i=0;i<lv.getCount();i++){
                    CheckBox c = (CheckBox)lv.getChildAt(i).findViewById(R.id.checkbox);
                    if(!c.isChecked()) {
                        c.setChecked(true);
                        adapter.songsList[i].setChecked(true);
                    }
                }
                return true;

        }
        return true;
    }


}
