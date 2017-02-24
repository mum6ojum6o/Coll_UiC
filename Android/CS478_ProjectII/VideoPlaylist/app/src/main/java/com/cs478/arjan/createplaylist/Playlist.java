package com.cs478.arjan.createplaylist;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import static android.content.Intent.ACTION_VIEW;

public class Playlist extends AppCompatActivity {
    ArrayList<String> selectedSongs=new ArrayList<String>();
    ArrayList<Uri> songPage = new ArrayList<Uri>();
    ArrayList<Uri> artistPage = new ArrayList<Uri>();
    private ArrayList<Uri> UriList = new ArrayList<Uri>();
    Toolbar myToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_view);
        myToolbar = (Toolbar) findViewById(R.id.activityToolbar);
        setSupportActionBar(myToolbar);
        selectedSongs=getIntent().getStringArrayListExtra("selectedSongs");
        initializeUris();

        GridView gv = (GridView)findViewById(R.id.gridview);
        gv.setAdapter(new ImageAdapter(this,selectedSongs));
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                //Create an Intent to start the ImageViewActivity
                Intent intent = new Intent(ACTION_VIEW,UriList.get(position));
                // Start the ImageViewActivity
                startActivity(intent);
            }});
        registerForContextMenu(gv);
    }
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Intent intent;
        int pos=selectedSongs.indexOf(getSongDet(info.id));
        switch (item.getItemId()) {
            case R.id.ctx_play:
                intent = new Intent(ACTION_VIEW,UriList.get(pos));
                startActivity(intent);
                return true;
            case R.id.ctx_web:
                 intent = new Intent(ACTION_VIEW,songPage.get(pos));
                startActivity(intent);
                return true;
            case R.id.ctx_artist:
                intent = new Intent(ACTION_VIEW,artistPage.get(pos));
                startActivity(intent);
                return true;
        }
        return true;
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.gridapptoolbar,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public void initializeUris() {
        for (int i = 0; i < selectedSongs.size(); i++) {
            if (selectedSongs.get(i).equals("Ecstacy Of Gold")) {
                UriList.add(Uri.parse("https://www.youtube.com/watch?v=alJqxE4KVIw"));
                songPage.add(Uri.parse("https://en.wikipedia.org/wiki/The_Ecstasy_of_Gold"));
                artistPage.add(Uri.parse("https://en.wikipedia.org/wiki/Metallica"));
            }
            else if (selectedSongs.get(i).equals("Dream On")){
                UriList.add(Uri.parse("https://www.youtube.com/watch?v=54BCLYNkFKg"));
                songPage.add(Uri.parse("https://en.wikipedia.org/wiki/Dream_On_(Aerosmith_song)"));
                artistPage.add(Uri.parse("https://en.wikipedia.org/wiki/Aerosmith"));
            }
            else if (selectedSongs.get(i).equals("Fuel")){
                UriList.add(Uri.parse("https://www.youtube.com/watch?v=G1cjHbXdU0s"));
                songPage.add(Uri.parse("https://en.wikipedia.org/wiki/Fuel_(song)"));
                artistPage.add(Uri.parse("https://en.wikipedia.org/wiki/Metallica"));
            }
            else if (selectedSongs.get(i).equals("Stairway To Heaven")){
                UriList.add(Uri.parse("https://www.youtube.com/watch?v=uMAL69NO0TY"));
                songPage.add(Uri.parse("https://en.wikipedia.org/wiki/Stairway_to_Heaven"));
                artistPage.add(Uri.parse("https://en.wikipedia.org/wiki/Led_Zeppelin"));
            }
            else if (selectedSongs.get(i).equals("Don't Cry")){
                UriList.add(Uri.parse("https://www.youtube.com/watch?v=zRIbf6JqkNc"));
                songPage.add(Uri.parse("https://en.wikipedia.org/wiki/Don%27t_Cry"));
                artistPage.add(Uri.parse("https://en.wikipedia.org/wiki/Guns_N%27_Roses"));
            }
            else if (selectedSongs.get(i).equals("Soldier Of Fortune")){
                UriList.add(Uri.parse("https://www.youtube.com/watch?v=RKrNdxiBW3Y"));
                songPage.add(Uri.parse("https://en.wikipedia.org/wiki/Soldier_of_Fortune_(song)"));
                artistPage.add(Uri.parse("https://en.wikipedia.org/wiki/Deep_Purple"));
            }
            else if (selectedSongs.get(i).equals("High Hopes")){
                UriList.add(Uri.parse("https://www.youtube.com/watch?v=7jMlFXouPk8"));
                songPage.add(Uri.parse("https://en.wikipedia.org/wiki/High_Hopes_(Pink_Floyd_song)"));
                artistPage.add(Uri.parse("https://en.wikipedia.org/wiki/Pink_Floyd"));
            }
            else if (selectedSongs.get(i).equals("Hey You!")){
                UriList.add(Uri.parse("https://www.youtube.com/watch?v=5K9NPjmgwFI"));
                songPage.add(Uri.parse("https://en.wikipedia.org/wiki/Hey_You_(Pink_Floyd_song)"));
                artistPage.add(Uri.parse("https://en.wikipedia.org/wiki/Pink_Floyd"));
            }
            else if (selectedSongs.get(i).equals("Until it Sleeps!")){
                UriList.add(Uri.parse("https://www.youtube.com/watch?v=eRV9uPr4Dz4"));
                songPage.add(Uri.parse("https://en.wikipedia.org/wiki/Until_It_Sleeps"));
                artistPage.add(Uri.parse("https://en.wikipedia.org/wiki/Metallica"));
            }
        }
    }
    public String getSongDet(long id){
        String ret=null;
        if(id==R.drawable.ecstacy)
            ret= "Ecstacy Of Gold";
        else if (id==R.drawable.reload)
            ret= "Fuel";
       else if (id==R.drawable.load)
            ret= "Until it Sleeps!";
        else if(id==R.drawable.aerosmith_dream)
            ret= "Dream On";
        else if(id==R.drawable.gnr_dc)
            ret= "Don't Cry";
        else if(id==R.drawable.pf_hy)
            ret= "High Hopes";
        else if(id==R.drawable.pf_hh_)
            ret= "Hey You!";
        else if(id==R.drawable.dp_sof)
            ret= "Soldier Of Fortune";
        else if(id==R.drawable.sth_lz)
            ret= "Stairway To Heaven";
        return ret;
    }
}
