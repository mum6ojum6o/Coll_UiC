package com.ibeis.wildbook.wildbook;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
    protected static View LAYOUT;
    protected static int WARNING,POS_FEEDBACK;
    protected static final int ONLINE=1;
    protected static final int OFFLINE=0;
    protected static final int IMAGE_GALLERY_REQUEST = 20;
    protected static final int CAMERA_PERMISSION_REQUEST_CODE=88;
    protected static final int READ_STORAGE_PERMISSION_REQUEST=99;
    protected  static final int INTERNET_NOT_CONNECTED =11;
    public static final String TAG ="MainActivity";
    protected Button RepEncounter,SignOutBut,UploadFromGallery,History,mSync;
    private FirebaseAuth mAuth;
    protected TextView UserName;
    protected ArrayList<String> selectedImages= new ArrayList<String>();
    protected static String storagePath="Photos/";
    protected static String databasePath="Photos/";
    private GoogleApiClient mGoogleApiClient;
    public static final int SYNC_COMPLETE=1;
    public static final int SYNC_STARTED=2;
    public static boolean MAIN_ACTIVITY_IS_RUNNING;
    public static Handler handler;
    private GoogleSignInAccount googleSignInAccount;
    public final Handler mUiHandler = new Handler(){
       @Override
        public void handleMessage(Message message){
           switch(message.what){
               case SYNC_COMPLETE:
                   Log.i(TAG,"SYNC_COMPLETED");
                   mSync.setEnabled(true);
                   break;
               case SYNC_STARTED:
                   Log.i(TAG,"SYNC_STARTED");
                   mSync.setEnabled(false);
                   break;
               case 200:
                   dispSnackBar(R.string.imageUploadedString,POS_FEEDBACK);
                   break;
               case 404:
                   dispSnackBar(R.string.imagesNotUploaded,WARNING);
                   break;
           }
       }
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        MAIN_ACTIVITY_IS_RUNNING=true;
        handler = mUiHandler;
        NetworkScanner scanner = new NetworkScanner();
        /*IntentFilter filter = new IntentFilter("android.net.wifi.STATE_CHANGE");
        IntentFilter filter2 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        */
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        getApplicationContext().registerReceiver(scanner,filter);
        Log.i(TAG,"MainActivity onCreate");
        super.onCreate(savedInstanceState);
       // Log.i(TAG,"Username:"+FirebaseAuth.getInstance().getCurrentUser().getEmail());
        Log.i("Logging window","Orientation is"+getResources().getConfiguration().orientation);

            setContentView(R.layout.activity_main);
            setLAYOUT();
        mAuth=FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(storagePath.equals("Photos/"))
            storagePath = storagePath+firebaseUser.getEmail();
         if(databasePath.equals("Photos/"))
            databasePath = databasePath+firebaseUser.getUid();
        Log.i(TAG,"storagePath"+storagePath);

        Log.i(TAG,"databasePath"+databasePath);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        if(firebaseUser==null){
            finish();
            startActivity(new Intent(this,Login.class));
        }
        WARNING=getColor(R.color.red);
        POS_FEEDBACK=getColor(R.color.green);
        UserName=(TextView)findViewById(R.id.username);
        //UserName.setText(firebaseUser.getEmail()+"!");
        RepEncounter = (Button) findViewById(R.id.RepEnc);
        UploadFromGallery=(Button)findViewById(R.id.GallUpload);
        mSync = (Button)findViewById(R.id.syncBtn);
        SignOutBut = (Button)findViewById(R.id.SingOut);
        History = (Button)findViewById(R.id.historyBtn);
        SignOutBut.setOnClickListener(this);
        RepEncounter.setOnClickListener(this);
        History.setOnClickListener(this);
        UploadFromGallery.setOnClickListener(this);
        mSync.setOnClickListener(this);
       Log.i("Login",Auth.GOOGLE_SIGN_IN_API.getName());
        mGoogleApiClient.connect();


    }

    @Override
    public void onStart() {

        super.onStart();
        MAIN_ACTIVITY_IS_RUNNING=true;
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            mAuth = FirebaseAuth.getInstance();
            googleSignInAccount = result.getSignInAccount();
            Log.i("SilentLogin",googleSignInAccount.getEmail());
            UserName.setText(googleSignInAccount.getEmail()+"!");
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            //showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    //hideProgressDialog();
                    if (googleSignInResult.getStatus().isSuccess() ) {
                        googleSignInAccount = googleSignInResult.getSignInAccount();
                        Log.i("SilentLogin", googleSignInAccount.getEmail());
                        UserName.setText(googleSignInAccount.getEmail() + "!");
                    } else {
                        Snackbar snack=Snackbar.make(LAYOUT,R.string.offline,Snackbar.LENGTH_LONG);
                        View snackView = snack.getView();
                        snackView.setBackgroundColor(WARNING);
                        snack.show();
                    }//else closes
                }
            });
        }
    }
    @Override
    public void onStop(){
        super.onStop();
        MAIN_ACTIVITY_IS_RUNNING=false;
    }
    @Override
    public void onResume(){

        super.onResume();
        MAIN_ACTIVITY_IS_RUNNING=true;
        if(SyncerService.IsRunning){
            Log.i(TAG,"Service is running");
            mSync.setEnabled(false);
        }
        else{
            Log.i(TAG,"Service is not running");
            mSync.setEnabled(true);
        }
        //UserName.setText(googleSignInAccount.getEmail()+"!");
        //selectedImages = new ArrayList<String>();
        Log.i(TAG,"OnResume selectedImagesCreated n size");//+selectedImages.size());
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.RepEnc:
               if(ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.CAMERA")
                       == PackageManager.PERMISSION_GRANTED &&
                       ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.READ_EXTERNAL_STORAGE")
                       == PackageManager.PERMISSION_GRANTED &&
                       ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.WRITE_EXTERNAL_STORAGE")
                       == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.ACCESS_FINE_LOCATION")
                        == PackageManager.PERMISSION_GRANTED){
                    startActivity(new Intent(getApplicationContext(), CameraMainActivity.class));
                }
                else{
                   ActivityCompat.requestPermissions(this,new String[]{"android.permission.CAMERA",
                           "android.permission.READ_EXTERNAL_STORAGE",
                           "android.permission.WRITE_EXTERNAL_STORAGE",
                           "android.permission.ACCESS_FINE_LOCATION"},
                           CAMERA_PERMISSION_REQUEST_CODE);

                }
                break;
            case R.id.GallUpload:
                //Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                if(ContextCompat.checkSelfPermission(getApplicationContext(),"android.permission.READ_EXTERNAL_STORAGE")==PackageManager.PERMISSION_GRANTED) {
                    requestPictureGalleryUpload();
                }
                else{
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"},READ_STORAGE_PERMISSION_REQUEST);
                }
                break;
            case R.id.historyBtn:
                if(new Utilities(this).isNetworkAvailable())
                    startActivity(new Intent(MainActivity.this, DisplayImagesUsingRecyclerView.class));
                else {
                    MainActivity.displayOnlineStatus(MainActivity.OFFLINE);
                    //displayToasts(INTERNET_NOT_CONNECTED);
                }
                break;
            case R.id.SingOut:
                signOut();
                break;
            case R.id.syncBtn:
                if(!SyncerService.IsRunning) {
                    mSync.setEnabled(false);
                    new Utilities(this).startSyncing();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Service is already Running!!",Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        mGoogleApiClient.disconnect();
                        mAuth.signOut(); //comment for production
                        mGoogleApiClient=null;
                        mAuth.signOut();
                        mAuth=null;
                        finish();
                        databasePath="Photos/"; //comment for production
                        storagePath="Photos/";//comment for production

                        startActivity(new Intent(MainActivity.this,Login.class));
                    }
                });


    }

    /***********************************************************
    *fetch the pictures from Firebase.
     ***********************************************************/
    public void retrieveFirebaseData(){

    }
@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int []grantResults){
        switch(requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                //
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1]== PackageManager.PERMISSION_GRANTED &&
                    grantResults[2]== PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(getApplicationContext(), CameraMainActivity.class));
            } else {
                displayToasts(PackageManager.PERMISSION_DENIED);
                //Toast.makeText(getApplicationContext(), "In order to contribute to cause we would encourage you to grant us access in the future! ", Toast.LENGTH_LONG).show();
            }
            break;
            case READ_STORAGE_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestPictureGalleryUpload();
                }
                else{
                    displayToasts(PackageManager.PERMISSION_DENIED);
                }
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        String selectedImage=null;
        if(resultCode==RESULT_OK){
            Log.i(TAG,"OnActivityResult OK");
            if(requestCode==IMAGE_GALLERY_REQUEST) {
                selectedImages=new ArrayList<String>();
                ContentResolver contentResolver = getContentResolver();
                String [] filePathColumn = {MediaStore.Images.Media.DATA};

                    if (data.getClipData() != null ) {

                    ClipData mClipData = data.getClipData();
                    if (mClipData.getItemCount()>10){
                        Log.i(TAG, "More than 10 images selected");
                        Toast.makeText(getApplicationContext(), "Please select upto 10 images only", Toast.LENGTH_LONG).show();
                    }
                    else {
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            selectedImage = cursor.getString(columnIndex);
                            selectedImages.add(selectedImage);
                            cursor.close();

                        }
                        showGallPicturesPreview(selectedImages);
                    }
                }
                else if (data.getData()!=null){
                    Cursor cursor = getContentResolver().query(data.getData(),filePathColumn,null,null, null);
                    cursor.moveToFirst();
                    while(!cursor.isAfterLast()) {
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        selectedImages.add(cursor.getString(columnIndex));
                        cursor.moveToNext();
                    }
                    cursor.close();
                    showGallPicturesPreview(selectedImages);
                }

            }
        }
    }
    //metho
    public void showGallPicturesPreview(ArrayList<String> images){
        Intent intent = new Intent(MainActivity.this,DisplaySelectedImages.class);
        intent.putExtra("selectedImages",images);
        startActivity(intent);
    }
    //this method enables users to select multiple pictures from the mobile device.
    public void requestPictureGalleryUpload(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        photoPickerIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), IMAGE_GALLERY_REQUEST);
    }

    public void displayToasts(int type){
        switch (type){
            case PackageManager.PERMISSION_DENIED:
                Toast.makeText(getApplicationContext(), "In order to contribute to cause we would encourage you to grant us access in the future! ", Toast.LENGTH_LONG).show();
                break;
            case INTERNET_NOT_CONNECTED:
                Toast.makeText(getApplicationContext(), "Internet connection is needed to perform this operation ", Toast.LENGTH_LONG).show();
        }
    }
   /* private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }*/
    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG,"OnResume");
        selectedImages=null;
        MAIN_ACTIVITY_IS_RUNNING=false;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        startActivity(new Intent(MainActivity.this,Login.class));
    }
public static void displayOnlineStatus(int status){
    Snackbar snack=null;
    View snackView=null;
        switch(status){
            case OFFLINE://make a function out of it.
                /*snack=Snackbar.make(LAYOUT,R.string.offline,Snackbar.LENGTH_LONG);
                 snackView = snack.getView();
                snackView.setBackgroundColor(WARNING);
                snack.show();*/
                dispSnackBar(R.string.offline,WARNING);
                break;
            case ONLINE:
               /* snack=Snackbar.make(LAYOUT,R.string.online,Snackbar.LENGTH_LONG);
                 snackView = snack.getView();
                snackView.setBackgroundColor(POS_FEEDBACK);
                snack.show();*/
                dispSnackBar(R.string.online,POS_FEEDBACK);
                break;
        }

    }
    public static void  dispSnackBar(int message,int bgcolor){
        Snackbar snack=null;
        View snackView;
        snack=Snackbar.make(LAYOUT,message,Snackbar.LENGTH_LONG);
        snackView = snack.getView();
        snackView.setBackgroundColor(bgcolor);
        snack.show();
    }

    /********************************************
     * Setup the LAYOUT
     ********************************************/
    public void setLAYOUT(){
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            LAYOUT=findViewById(R.id.mainLinearLayout);//setupLayout;
        }
        else{
            LAYOUT=findViewById(R.id.myCoordinatorLayout);
        }
    }

}
