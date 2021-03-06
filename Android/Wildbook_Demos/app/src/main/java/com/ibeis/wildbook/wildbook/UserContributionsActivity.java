package com.ibeis.wildbook.wildbook;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

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

/******************************************************************
 * Activity to preview the images uploaded by the user
 * Displays User Contribution.
 *******************************************************************/
public class UserContributionsActivity extends BaseActivity
        implements NetworkScannerBroadcastReceiver.NetworkChangeReceiverListener {
    public static final int DOWNLOADING = 77;
    public static final int COMPLETE = 200;
    public static final int PROG_UPDATE = 853;
    public static final String TAG = "UserContributionsActivity ";
    // this handler may cause a memory leak.
    /****************************************
     * "Messages posted to the message queue will hold a reference to the Handler so that the framework
     * can call Handler#handleMessage(Message) when the Looper eventually processes the message." - https://www.androiddesignpatterns.com/2013/01/inner-class-handler-memory-leak.html
     *  When:

    ****************************/
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case DOWNLOADING:
                    break;
                case PROG_UPDATE:
                    break;
                case 200:
                    /* indicates when the user contributions have been downloaded
                    successfully
                    */
                    loadingImageView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    errorMessage.setVisibility(View.GONE);
                    Bundle b = message.getData();
                    JSONArray jsonArray = null,reversedJSONArray=new JSONArray();
                    try {
                        jsonArray = new JSONArray(b.getString("JSON_RESPONEI"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        for (int i = jsonArray.length() - 1; i >= 0; i--) {
                            reversedJSONArray.put(jsonArray.getJSONObject(i));
                        }

                    }catch(JSONException je) {
                        Toast.makeText(getApplicationContext(), "Error receiving response from Server", Toast.LENGTH_SHORT).show();
                        je.printStackTrace();
                    }
                    adapter = new RecyclerViewAdapter(getApplicationContext(), reversedJSONArray);
                    recyclerView.setAdapter(adapter);
                    break;
                case 404: // When an error is encountered while downloading the images.
                    loadingImageView.setVisibility(View.INVISIBLE);
                    Dialog dialog = new Dialog(UserContributionsActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_layout);
                    dialog.setTitle(getResources().getString(R.string.file_not_found_error));
                    TextView tv = (TextView)dialog.findViewById(R.id.dialog_txt);
                    tv.setText(getResources().getString(R.string.sync_error));
                    TextView tv1 = (TextView)dialog.findViewById(R.id.dialog_txt1);
                    tv1.setText("Your contributions could not retrieved. Either the server is down or you are not connected to the Internet");
                    dialog.show();

                    errorMessage.setText(getResources().getString(R.string.server_offline));
                    errorMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    findViewById(R.id.encounter_details).setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };
    private static final int DISPLAY = 123;
    // Creating DatabaseReference.
    //DatabaseReference databaseReference;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    //CountingResources are used to test asynchronous tasks.
    CountingIdlingResource idlingResource = new CountingIdlingResource("TEST");
    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter;
    private TextView errorMessage;
    // Creating Progress dialog
    ProgressDialog progressDialog;
    // StorageReference storageReference;
    private ImageView loadingImageView;


    List<Uri> imagesList = new ArrayList<Uri>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, getIntent().hasExtra("source") + " From Notification????");
        Log.i(TAG, getIntent().hasExtra("UploadedBy") + "User INFORMATION");
        String naam = new Utilities(this).getCurrentIdentity();
        String uploadedBy = getIntent().getStringExtra("UploadedBy");
        if (getIntent().hasExtra("source")
                && naam.equals("")) {//means no user has logged in....
            Log.i(TAG, "Not connected with the same user");
            Toast.makeText(this, "Please login with the appropriate userId.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        //someone has logged in but with different account
        else if (getIntent().hasExtra("source")
                && ((!uploadedBy.equals(naam) && !naam.equals("")))) {
            Toast.makeText(this, "The previous sync was requested for another user", Toast.LENGTH_LONG);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Log.i(TAG,"Viewing images on clicking the Button in MainActivity");
            setContentView(R.layout.activity_display_images_using_recycler_view);
            loadingImageView = (ImageView)findViewById(R.id.loadingImgView);
            loadingImageView.setVisibility(View.VISIBLE);
            int drawable = R.drawable.ic_ellipsis_1s_200px;
            Glide.with(this).load(drawable).into(loadingImageView);
            // Assign id to RecyclerView.
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            // Setting RecyclerView size true.
            recyclerView.setHasFixedSize(true);
            recyclerView.setVisibility(View.INVISIBLE);
            // Setting RecyclerView layout as LinearLayout.
            recyclerView.setLayoutManager(new GridLayoutManager(UserContributionsActivity.this, 3));
            findViewById(R.id.cardview_EncDetails_encompassing).setVisibility(View.GONE);
            errorMessage = (TextView) findViewById(R.id.errormsgtxtvw);
            errorMessage.setText(R.string.loadingImages);

            if(!new Utilities(getApplicationContext()).isNetworkAvailable()) {
               // setLAYOUT();
                //displaySnackBar(R.string.offline, getColor(R.color.red));
            }
            else {
                progressDialog = new ProgressDialog(UserContributionsActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.imageloading));
                progressDialog.show();
                idlingResource.increment();
                //initiate the download of user contribution in a worker thread
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            if (android.os.Build.VERSION.SDK_INT > 9) {
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                            }
                            Log.i(TAG,"Launching Worker thread to initiate download!");
                            URL url = new URL("http://uidev.scribble.com/v2/fakeListing.jsp?email=" + new Utilities(getApplicationContext()).getCurrentIdentity());
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            JSONArray jsonArray = null, reversedJSONArray = null;
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
                                reader.close();
                                httpURLConnection.disconnect();
                                ArrayList<String> imagePaths = new ArrayList<String>();

                                if (jsonArray != null) {
                                    reversedJSONArray = new JSONArray();
                                    int size = jsonArray.length();
                                    for (int i = 0; i < size; i++) {
                                        if (jsonArray.getJSONObject(i).has("thumbnailUrl"))
                                            imagePaths.add((jsonArray.getJSONObject(i).get("thumbnailUrl").toString()));
                                    }
                                    // could this statement cause a Memory leak on config change?
                                    Message msg = mHandler.obtainMessage(200);
                                    Bundle bundle = new Bundle();
                                    bundle.putStringArrayList("JSON_RESPONE", imagePaths);
                                    bundle.putString("JSON_RESPONEI", response);

                                    idlingResource.decrement();
                                    msg.setData(bundle);
                                    msg.sendToTarget();
                                } else {
                                    Message msg = mHandler.obtainMessage(404);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("404", getResources().getString(R.string.server_offline));
                                    idlingResource.decrement();
                                    msg.setData(bundle);
                                    msg.sendToTarget();
                                }
                            }
                            //Thread.sleep(5000);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "Error!!");
                            Message msg = mHandler.obtainMessage(404);
                            Bundle bundle = new Bundle();
                            bundle.putString("404", getResources().getString(R.string.server_offline));
                            idlingResource.decrement();
                            msg.setData(bundle);
                            msg.sendToTarget();
                        }
                        progressDialog.dismiss();

                    }
                }).start();
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!new Utilities(getApplicationContext()).isNetworkAvailable()) {
            setLAYOUT();
            displaySnackBar(R.string.offline, getColor(R.color.red));
        }
        else{
            if(LAYOUT!=null) {
                setLAYOUT();
                displaySnackBar(R.string.online, getColor(R.color.green));
            }
        }
        //gets current logged in entry from the shared preference file.
        String naam = new Utilities(this).getCurrentIdentity();
        //Log.i(TAG, "in on Resume:" + naam);
        // if the current user information is not found in the shared preference then redirect to the Login Activity
        if (naam.equals("No Email ID") || naam == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();

        }
        WildbookApplication.getmInstance().setNetworkStatusChangeListener(this);
    }

    protected void signOut() {
        final Context ctx = getApplicationContext();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        mGoogleApiClient.disconnect();
                        mGoogleApiClient = null;
                        finish();

                        Intent intent = new Intent(UserContributionsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                        Log.i(TAG, "Logging out from UserContributionsActivity");
                        new Utilities(UserContributionsActivity.this).setCurrentIdentity("");
                        UserContributionsActivity.this.finish();
                        UserContributionsActivity.this.finishAffinity();

                    }
                });
        }
    public void setLAYOUT(){
        LAYOUT=findViewById(R.id.display_history_layout);//setupLayout;
    }
    public void onPause(){
        super.onPause();
        WildbookApplication.getmInstance().setNetworkStatusChangeListener(null);
    }
    /**********************************************
     * Method that displays a snackbar
     * @param message to be displayed in the snackbar
     * @param bgcolor background color of the snackbar
     ***********************************************/
    public void  displaySnackBar(int message,int bgcolor){
        Snackbar snack=null;
        View snackView;
        setLAYOUT();
        if(message == com.ibeis.wildbook.wildbook.R.string.offline)
            snack=Snackbar.make(LAYOUT,message,Snackbar.LENGTH_INDEFINITE);
        else
            snack=Snackbar.make(LAYOUT,message,Snackbar.LENGTH_SHORT);
        snackView = snack.getView();
        snackView.setBackgroundColor(bgcolor);
        snack.show();
    }

    @Override
    public void onNetworkStatusChanged(boolean isConnected) {
            setLAYOUT();
            if(isConnected){
                new Utilities(this).displaySnackBar(LAYOUT,R.string.online,MainActivity.POS_FEEDBACK);
            }
            else{
                new Utilities(this).displaySnackBar(LAYOUT,R.string.offline, Color.RED);
            }

    }
    @VisibleForTesting
    @NonNull
    public CountingIdlingResource getIdlingResource(){
        if(idlingResource==null)
            idlingResource = new CountingIdlingResource("TEST");
        return idlingResource;
    }
}
