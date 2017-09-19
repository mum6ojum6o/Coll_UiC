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
import android.widget.Button;
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
    private Toolbar myToolbar;
    private ArrayAdapter lAdapter;
    private CheckBox cb;
    private int cbCheck;
    private ListView lv;
    private ArrayList<String> songsII = new ArrayList<String>();
    Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_playlist);
        myToolbar = (Toolbar) findViewById(R.id.activityToolbar);
        b=(Button)findViewById(R.id.button);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true); // citing: http://stackoverflow.com/questions/26472417/logo-are-not-displayed-in-actionbar-using-appcompat
        getSupportActionBar().setDisplayShowHomeEnabled(true);// citing: http://stackoverflow.com/questions/26472417/logo-are-not-displayed-in-actionbar-using-appcompat
        getSupportActionBar().setIcon(R.drawable.electric_guitar);// citing: http://stackoverflow.com/questions/26472417/logo-are-not-displayed-in-actionbar-using-appcompat
        setListView();
        b.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                createPlaylist();
            }
        });

}
//Displays the app bar menu
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.apptoobar,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public int checkboxCounter(){
        int count=0;
        CheckBox c;
        for (int i=0;i<songsII.size();i++){
            cb=(CheckBox)lv.getChildAt(i).findViewById(R.id.checkbox);
            if(cb.isChecked())
                count++;
        }
        return count;
    }
    //Actions to be taken on the basis of options selected in the ListView.
    public boolean onOptionsItemSelected(MenuItem item){
        Log.i("VideoPlaylist:","In onOptionsItemsSelected");

        switch(item.getItemId()){
            case R.id.action_favorite:
                //Log.i("VideoPlaylist:","In onOptionsItemsSelected;action_favorite:"+adapter.checkboxCount());
                createPlaylist();
                 return true;
            case R.id.act_clear: for (int i=0;i<songsII.size();i++){
                cb = (CheckBox)lv.getChildAt(i).findViewById(R.id.checkbox);
                if (cb.isChecked()){
                    cb.setChecked(false);

                }
            }
                return true;
            case R.id.act_invert:for (int i=0;i<songsII.size();i++) {
                 cb = (CheckBox) lv.getChildAt(i).findViewById(R.id.checkbox);
                if (cb.isChecked()) {
                    cb.setChecked(false);
                 } else {
                    cb.setChecked(true);
                }
            }

                return true;
            case R.id.act_checkall:

                for (int i=0;i<songsII.size();i++){
                    cb = (CheckBox)lv.getChildAt(i).findViewById(R.id.checkbox);
                    if(!cb.isChecked()) {
                        cb.setChecked(true);
                        //adapter.songsList[i].setChecked(true);
                    }
                }
                return true;

        }
        return true;
    }

    //just to implement an added functionality.
public void setGridView(){
    setContentView(R.layout.grid_view);
    GridView gv=(GridView)findViewById(R.id.gridview);

    ArrayList<String> gv_strings=new ArrayList<String>();
    for (int i=0; i<songsII.size();i++) {
        cb = (CheckBox) lv.getChildAt(i).findViewById(R.id.checkbox);
        if (cb.isChecked())
                gv_strings.add(songsII.get(i));
    }
    gv.setAdapter(new ImageAdapter(this,gv_strings));
}
    public void setListView(){
        lv = (ListView) findViewById(R.id.listview);
        songsII.add("Ecstacy Of Gold");songsII.add("Dream On");songsII.add("Fuel"); songsII.add("Stairway To Heaven");
        songsII.add("High Hopes");songsII.add("Don't Cry");songsII.add("Soldier Of Fortune");
        songsII.add("Hey You!");songsII.add("Until it Sleeps!");
        lAdapter= new ArrayAdapter(this,R.layout.listview_layout,R.id.checkbox,songsII);
        lv.setAdapter(lAdapter);
        Log.i("VideoPlaylist:", "In onCreate()");

    }
    public void createPlaylist() {
        if (checkboxCounter() == 0)
            Toast.makeText(getApplicationContext(), "No Songs Selected", LENGTH_SHORT).show();
        else {

            ArrayList<String> ss = new ArrayList<String>();
            for (int i = 0; i < songsII.size(); i++) {
                cb = (CheckBox) lv.getChildAt(i).findViewById(R.id.checkbox);
                if (cb.isChecked() == true)
                    ss.add(songsII.get(i));
            }

            Intent createPlaylist = new Intent(VideoPlaylist.this, Playlist.class);
            createPlaylist.putExtra("selectedSongs", ss);
            Log.i("VideoPlaylist:", "Creating intent:");
            startActivity(createPlaylist);
        }
    }
}
