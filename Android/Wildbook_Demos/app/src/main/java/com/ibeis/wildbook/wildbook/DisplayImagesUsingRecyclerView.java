package com.ibeis.wildbook.wildbook;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

public class DisplayImagesUsingRecyclerView extends BaseActivity {
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
                errorMessage.setText(getResources().getString(R.string.server_offline));
                errorMessage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                findViewById(R.id.encounter_details).setVisibility(View.INVISIBLE);
                break;
        }
    }
};
    private static final int DISPLAY=123;
    // Creating DatabaseReference.
    //DatabaseReference databaseReference;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    private TextView errorMessage;
    // Creating Progress dialog
    ProgressDialog progressDialog;
   // StorageReference storageReference;

    // Creating List of ImageUploadInfo class.
    List<ImageUploadInfo> list = new ArrayList<>();
    List<Uri> imagesList = new ArrayList<Uri>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,getIntent().hasExtra("source")+" From Notification????");
        Log.i(TAG, getIntent().hasExtra("UploadedBy")+ "User INFORMATION");
       // Log.i(TAG, "checking user: "+new Utilities(this).checkUsername());
        String naam=new Utilities(this).getCurrentIdentity();
        String uploadedBy=getIntent().getStringExtra("UploadedBy");
        if(getIntent().hasExtra("source")
                && naam.equals("")) {//means no user has logged in....
            Log.i(TAG,"Not connected with the same user");
            Toast.makeText(this,"Please login with the appropriate userId.",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,Login.class));
            finish();
        }
        //someone has logged in but with different account
        else if(getIntent().hasExtra("source")
                && ((!uploadedBy.equals(naam) && !naam.equals("")))){
            Toast.makeText(this,"The previous sync was requested for another user",Toast.LENGTH_LONG);
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        else {
            setContentView(R.layout.activity_display_images_using_recycler_view);
            /*getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.wildbook2);
            getSupportActionBar().setTitle(R.string.historyString);*/
            /*action.setBackgroundDrawable(
                    new ColorDrawable(getResources().getColor(R.color.action_bar, null)));*/

            // Assign id to RecyclerView.
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

            // Setting RecyclerView size true.
            recyclerView.setHasFixedSize(true);

            // Setting RecyclerView layout as LinearLayout.
            recyclerView.setLayoutManager(new GridLayoutManager(DisplayImagesUsingRecyclerView.this, 4));
            findViewById(R.id.encounter_details).setVisibility(View.INVISIBLE);
            errorMessage = (TextView) findViewById(R.id.errormsgtxtvw);
            errorMessage.setVisibility(View.GONE);
        }

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



            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (android.os.Build.VERSION.SDK_INT > 9) {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                        }
                        URL url = new URL("http://uidev.scribble.com/v2/fakeListing.jsp?email=" + new Utilities(getApplicationContext()).getCurrentIdentity());
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        JSONArray jsonArray = null;
                        JSONObject jsonObject = null;
                        int statusCode = httpURLConnection.getResponseCode();
                        if (statusCode == HttpURLConnection.HTTP_OK) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(
                                    httpURLConnection.getInputStream()));
                            String line = null;
                            StringBuilder sb = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                Log.i(TAG, line);
                                sb.append(line);
                            }
                            String response = sb.toString();
                            jsonArray = new JSONArray(response);
                            //jsonObject=new JSONObject(response);
                            reader.close();
                            httpURLConnection.disconnect();
                            ArrayList<String> imagePaths = new ArrayList<String>();

                            if (jsonArray != null) {
                                int size = jsonArray.length();
                                for (int i = 0; i < size; i++) {
                                    if (jsonArray.getJSONObject(i).has("thumbnailUrl"))
                                        imagePaths.add((jsonArray.getJSONObject(i).get("thumbnailUrl").toString()));
                                }
                                Message msg = mHandler.obtainMessage(200);
                                Bundle bundle = new Bundle();
                                bundle.putStringArrayList("JSON_RESPONE", imagePaths);
                                bundle.putString("JSON_RESPONEI", response);
                                msg.setData(bundle);
                                msg.sendToTarget();
                            /*RecyclerViewAdapter adapter = new RecyclerViewAdapter(getApplicationContext(),imagePaths);
                            recyclerView.setAdapter(adapter);*/
                            } else {
                                Message msg = mHandler.obtainMessage(404);
                                Bundle bundle = new Bundle();
                                bundle.putString("404", getResources().getString(R.string.server_offline));
                                msg.setData(bundle);
                                msg.sendToTarget();
                            }
                        }
                        //Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(TAG, "Error!!");
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
        String naam=new Utilities(this).getCurrentIdentity();
        Log.i(TAG,"in on Resume:"+ naam);
        if(naam.equals("No Email ID")||naam==null){
            startActivity(new Intent(this,Login.class));
            finish();

        }
    }

    protected void signOut() {
       final Context ctx = getApplicationContext();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        mGoogleApiClient.disconnect();
                        mGoogleApiClient=null;
                        finish();
                        ActivityUpdater.activeActivity=null;
                        Intent intent = new Intent(DisplayImagesUsingRecyclerView.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|
                                Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                        Log.i(TAG,"Logging out from DisplayImagesUsingRecyclerView");
                        new Utilities(DisplayImagesUsingRecyclerView.this).setCurrentIdentity("");
                        DisplayImagesUsingRecyclerView.this.finish();
                        DisplayImagesUsingRecyclerView.this.finishAffinity();

                    }
                });


    }

}
