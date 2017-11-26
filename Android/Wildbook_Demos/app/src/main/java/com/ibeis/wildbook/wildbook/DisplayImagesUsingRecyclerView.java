package com.ibeis.wildbook.wildbook;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DisplayImagesUsingRecyclerView extends AppCompatActivity {
    private static final int DOWNLOADING = 77;
    private static final int COMPLETE = 200;
    private static final int PROG_UPDATE = 853;
    public static String TAG = "DisplayImagesUsingRecyclerView ";
public Handler mHandler = new Handler(){
    @Override
    public void handleMessage(Message message){

        switch(message.what){
            case DOWNLOADING:
                break;
            case PROG_UPDATE:
                break;
            case 200:
                Bundle b=message.getData();
                JSONArray jsonArray=null;
                ArrayList<String> images= b.getStringArrayList("JSON_RESPONE");

                try {
                    jsonArray= new JSONArray(b.getString("JSON_RESPONEI"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ArrayList<Uri> imagesPath= new ArrayList<Uri>();
                for(String anImage:images){
                    imagesPath.add(Uri.parse(anImage));
                }

                RecyclerViewAdapter adapter = new RecyclerViewAdapter(getApplicationContext(),jsonArray);
                recyclerView.setAdapter(adapter);
                break;
            case 404:
                break;
        }
    }
};
    private static final int DISPLAY=123;
    // Creating DatabaseReference.
    DatabaseReference databaseReference;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;

    // Creating Progress dialog
    ProgressDialog progressDialog;
    StorageReference storageReference;

    // Creating List of ImageUploadInfo class.
    List<ImageUploadInfo> list = new ArrayList<>();
    List<Uri> imagesList = new ArrayList<Uri>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images_using_recycler_view);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.wildbook2);
        getSupportActionBar().setTitle(R.string.historyString);
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.action_bar,null)));
        // Assign id to RecyclerView.
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new GridLayoutManager(DisplayImagesUsingRecyclerView.this,3));
        findViewById(R.id.encounter_details).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStart(){
        super.onStart();
        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(DisplayImagesUsingRecyclerView.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage(getResources().getString(R.string.imageloading));
        progressDialog.show();
        //creating a worker thread to get images from the network.
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    if (android.os.Build.VERSION.SDK_INT > 9)
                    {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    }
                    URL url = new URL("http://uidev.scribble.com/v2/fakeListing.jsp?email=" + new Utilities(getApplicationContext()).getUserEmail());
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    JSONArray jsonArray=null;
                    JSONObject jsonObject= null;
                    int statusCode =httpURLConnection.getResponseCode();
                    if(statusCode==HttpURLConnection.HTTP_OK){
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                httpURLConnection.getInputStream()));
                        String line=null;
                        StringBuilder sb=new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            Log.i(TAG, line);
                            sb.append(line);
                        }
                        String response = sb.toString();
                        jsonArray=new JSONArray(response);
                        //jsonObject=new JSONObject(response);
                        reader.close();
                        httpURLConnection.disconnect();
                        ArrayList<String>imagePaths = new ArrayList<String>();

                        if (jsonArray!=null){
                            int size=jsonArray.length();
                            for(int i=0;i<size;i++){
                                if(jsonArray.getJSONObject(i).has("thumbnailUrl"))
                                    imagePaths.add((jsonArray.getJSONObject(i).get("thumbnailUrl").toString()));
                            }
                            Message msg = mHandler.obtainMessage(200);
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("JSON_RESPONE",imagePaths);
                            bundle.putString("JSON_RESPONEI",response);
                            msg.setData(bundle);
                            msg.sendToTarget();
                            /*RecyclerViewAdapter adapter = new RecyclerViewAdapter(getApplicationContext(),imagePaths);
                            recyclerView.setAdapter(adapter);*/
                        }
                    }
                    //Thread.sleep(5000);
                }
                catch(Exception e){
                    e.printStackTrace();
                    Log.i(TAG,"Error!!");
                }

                progressDialog.dismiss();

            }
        }).start();


    }
    @Override
    public void onResume(){

        super.onResume();
        //progressDialog.show();
        /*databaseReference = FirebaseDatabase.getInstance().getReference(MainActivity.databasePath);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.i(TAG,"DATA CHANGED!!!");
                long recordsRead=0;
                final long objeCount=snapshot.getChildrenCount();
                if(null != snapshot.getValue()) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        long recordCount = snapshot.getChildrenCount();

                        String storagePath = MainActivity.storagePath + "/" + postSnapshot.getValue();

                        recordsRead++;

                        Log.i(TAG, "snapshot_ChildCount" + objeCount);
                        storageReference = FirebaseStorage.getInstance().getReference().child(storagePath);


                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imagesList.add(uri);
                                Log.i(TAG, "Successfully Downloaded Uri!!!Size" + imagesList.size());
                                if ((long) imagesList.size() == objeCount) {
                                    Log.i(TAG, "ObjeCount=" + objeCount);
                                    Collections.reverse(imagesList);
                                    adapter = new RecyclerViewAdapter(getApplicationContext(), imagesList);
                                    recyclerView.setAdapter(adapter);
                                    // Hiding the progress dialog.
                                    progressDialog.dismiss();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Log.i(TAG, "Something went wrong!!");
                            }
                        });
                    }
                }
                else{
                    Log.i(TAG,"No Contributions");
                    Toast.makeText(getApplicationContext(),"No Contributions",Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();

                //Log.i(TAG,"Download completed!!!imagesList.SIZE()="+imagesList.size());

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }
        });*/

        ///this code should be in worker thread.

    }

}
