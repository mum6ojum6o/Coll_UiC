package com.ibeis.wildbook.wildbook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
    protected View LAYOUT;
    protected static int WARNING,POS_FEEDBACK;
    protected static final int ONLINE=1;
    // request code  for identifying the results obtained from location settings client
    protected static final int  REQUEST_CHECK_SETTINGS=562;
    protected static final int OFFLINE=0;
    //request code for identifying the results obtained from images gallery
    protected static final int IMAGE_GALLERY_REQUEST = 20;
    //request code for identifying the results for camera permission
    protected static final int CAMERA_PERMISSION_REQUEST_CODE=88;
    //request code for identifying the results for Internal_Storage permission
    protected static final int READ_STORAGE_PERMISSION_REQUEST=99;
    protected  static final int INTERNET_NOT_CONNECTED =11;
    public static final String TAG ="MainActivity";
    protected Button RepEncounter,SignOutBut,UploadFromGallery,History,mSync;
    //private FirebaseAuth mAuth;
    protected TextView UserName;
    protected ArrayList<String> selectedImages= new ArrayList<String>();
   // protected static String storagePath="Photos/";
   // protected static String databasePath="Photos/";
    //private GoogleApiClient mGoogleApiClient;
    public static final int SYNC_COMPLETE=1;
    public static final int SYNC_STARTED=2;
    public static boolean MAIN_ACTIVITY_IS_RUNNING;
    //private GoogleSignInAccount googleSignInAccount;
    //private ImageView mCricleImageView;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //MAIN_ACTIVITY_IS_RUNNING=true;
        super.onCreate(savedInstanceState);
        NetworkScanner scanner = new NetworkScanner();
       /* getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.wildbook2);
        getSupportActionBar().setTitle(R.string.welcomeString);
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.action_bar,null)));*/


        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        getApplicationContext().registerReceiver(scanner,filter);
        Log.i(TAG,"MainActivity onCreate");

       // Log.i(TAG,"Username:"+FirebaseAuth.getInstance().getCurrentUser().getEmail());
        Log.i("Logging window","Orientation is"+getResources().getConfiguration().orientation);

            setContentView(R.layout.activity_main);
            setLAYOUT();
       /* mAuth=FirebaseAuth.getInstance();
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar_main);

        final FirebaseUser firebaseUser = mAuth.getCurrentUser();*/
        /*if(storagePath.equals("Photos/"))
            storagePath = storagePath+firebaseUser.getEmail();
         if(databasePath.equals("Photos/"))
            databasePath = databasePath+firebaseUser.getUid();*/
        /*Log.i(TAG,"storagePath"+storagePath);

        Log.i(TAG,"databasePath"+databasePath);*/

        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this *//* FragmentActivity *//*, this *//* OnConnectionFailedListener *//*)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/
       /* if(firebaseUser==null){
            finish();
            startActivity(new Intent(this,Login.class));
        }*/
        WARNING=getColor(R.color.red);
        POS_FEEDBACK=getColor(R.color.green);
        UserName=(TextView)findViewById(R.id.username);
        //UserName.setText(firebaseUser.getEmail()+"!");
        RepEncounter = (Button) findViewById(R.id.RepEnc);
        UploadFromGallery=(Button)findViewById(R.id.GallUpload);
        //mSync = (Button)findViewById(R.id.syncBtn);
       // SignOutBut = (Button)findViewById(R.id.SingOut);
        History = (Button)findViewById(R.id.historyBtn);

       // SignOutBut.setOnClickListener(this);
        RepEncounter.setOnClickListener(this);
        History.setOnClickListener(this);
        UploadFromGallery.setOnClickListener(this);
        //mSync.setOnClickListener(this);
       Log.i("Login",Auth.GOOGLE_SIGN_IN_API.getName());
       // mGoogleApiClient.connect();
       /*ActionBar action =getSupportActionBar();
       action.setCustomView(R.layout.circular_imageview_for_action_bar);
        LinearLayout ll = (LinearLayout)findViewById(R.id.circular_image_view_layout);
        mCricleImageView = action.getCustomView().findViewById(R.id.circle_imgeview);
        action.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_SHOW_HOME);*/

    }

    @Override
    public void onStart() {

        super.onStart();
        MAIN_ACTIVITY_IS_RUNNING=true;
       /* OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            //mAuth = FirebaseAuth.getInstance();
            googleSignInAccount = result.getSignInAccount();
            Log.i("SilentLogin",googleSignInAccount.getEmail());
            UserName.setText(googleSignInAccount.getEmail());
            Uri uri = googleSignInAccount.getPhotoUrl();
            *//*Glide.with(this)
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mCricleImageView);*//*
            Glide.with(getApplicationContext())
                    .load(uri)
                    .asBitmap()
                    .fitCenter()
                    .into(new BitmapImageViewTarget(mCricleImageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);

                            circularBitmapDrawable.setCircular(true);
                            mCricleImageView.setImageDrawable(circularBitmapDrawable);
                        }
                    });

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
        }*/
    }
    @Override
    public void onStop(){
        super.onStop();
        MAIN_ACTIVITY_IS_RUNNING=false;
    }
    @Override
    public void onResume(){
        super.onResume();

        ActivityUpdater.activeActivity = this;
        MAIN_ACTIVITY_IS_RUNNING=true;
        /*if(SyncerService.IsRunning){
            Log.i(TAG,"Service is running");
            //mSync.setEnabled(false);
        }
        else{
            Log.i(TAG,"Service is not running");
            mSync.setEnabled(true);
        }*/
        String naam=new Utilities(this).getCurrentIdentity();
        /*String naam=null;
        if(googleSignInAccount!=null)
             naam = googleSignInAccount.getEmail();*/
        Log.i(TAG,""+new Utilities(this).getCurrentIdentity());
        if(new Utilities(this).getCurrentIdentity().equals("")){
            Log.i(TAG,"zzxxyz not set!!");
            signOut();
            finish();
        }
        Log.i(TAG,"in on Resume:"+ naam);
       /* if(naam.equals("No Email ID")||naam==null||(naam.equals("")&& !(mGoogleApiClient.isConnected()))){
         signOut();
            finish();

        }*/

        Log.i(TAG,"OnResume selectedImagesCreated n size");//+selectedImages.size());
    }

    /****************
     *
     * Button click Callback
     * @param v
     */
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
                   //createLocationRequest();
                   checkLocationSettings();
                    //startActivity(new Intent(getApplicationContext(), CameraMainActivity.class));
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
                    //MainActivity.displayOnlineStatus(MainActivity.OFFLINE);
                    displaySnackBar(R.string.offline,MainActivity.WARNING);

                }
                break;
           /* case R.id.SingOut:
                signOut();
                break;*/
           /* case R.id.syncBtn:
                if(!SyncerService.IsRunning) {
                    mSync.setEnabled(false);
                    new Utilities(this).startSyncing();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Service is already Running!!",Toast.LENGTH_SHORT).show();
                }
                break;*/

        }
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
                    createLocationRequest();
                    checkLocationSettings();
                //startActivity(new Intent(getApplicationContext(), CameraMainActivity.class));
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
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String selectedImage = null;


        if (resultCode == RESULT_OK) {
            Log.i(TAG, "OnActivityResult OK");

            if (requestCode == IMAGE_GALLERY_REQUEST) {  //Handling images selected by the users....
                Uri dataUri = data.getData();
                selectedImages = new ArrayList<String>();
                //ontentResolver contentResolver = getContentResolver();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                //Log.i(TAG,"Scheme:" +data.getData().getScheme());
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    if (mClipData.getItemCount() > 10) {
                        Log.i(TAG, "More than 10 images selected");
                        Toast.makeText(getApplicationContext(), R.string.maxuploadString, Toast.LENGTH_LONG).show();
                    } else {
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
                            try {
                                selectedImages.add(getFilePath(this, uri));
                            }catch(Exception e){
                                Toast.makeText(this,"Something went wrong!",Toast.LENGTH_LONG).show();
                            }

                          /*  if(selectedImages.get(0)==null){
                                File f;
                                getContentResolver().openInputStream(uri)
                            }*/
                            cursor.close();

                        }

                            showGallPicturesPreview(selectedImages, mArrayUri,dataUri);

                    }
                } else if (data.getData() != null) {
                    Uri uri = data.getData();
                         /*final int takeFlags = data.getFlags()
                                 & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                 | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        //
                         //getContentResolver().takePersistableUriPermission(uri,takeFlags);
                         Cursor cursor = getContentResolver().query(uri,filePathColumn,null,null, null);

                         Log.i(TAG,"Cursor Size:"+ cursor.getCount());
                        cursor.moveToFirst();
                        while(!cursor.isAfterLast()) {
                            int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
                            String imagePath = cursor.getString(columnIndex);
                            if (null==imagePath){
                                File f = new File(uri.getPath());
                                imagePath=f.getAbsolutePath();
                            }
                            selectedImages.add(imagePath);
                            cursor.moveToNext();
                        }
                        cursor.close();
                }*/

                    /*try {
                        selectedImages.add(getFilePath(this, uri));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Something went wrong!!", Toast.LENGTH_LONG).show();
                    }*/
                    ArrayList<Uri> uris = new ArrayList<Uri>();
                    uris.add(uri);
                    try{
                        selectedImages.add(getFilePath(this,uri));
                    }catch(Exception e){
                        e.printStackTrace();
                        Toast.makeText(this,"Something went wrong!",Toast.LENGTH_LONG).show();
                    }
                    showGallPicturesPreview(selectedImages, uris,uri);
                }
            }
        }
        if(requestCode == REQUEST_CHECK_SETTINGS){
            Log.i(TAG,"REQUEST_CHECK_SETTTINGS");
           /*LocationSettingsStates locationSettingsStates = LocationSettingsStates.fromIntent(data);
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            // All required changes were successfully made
                            Toast.makeText(getApplicationContext(), "Location Settings were selected", Toast.LENGTH_SHORT).show();
                            break;
                        case Activity.RESULT_CANCELED:
                            // The user was asked to change settings, but chose not to
                            Toast.makeText(getApplicationContext(), "Location Settings were not selected", Toast.LENGTH_SHORT).show();
                            break;
                        default:

                            break;
                    }*/
                    startActivity(new Intent(MainActivity.this,CameraMainActivity.class));



        }

    }

    @SuppressLint("NewApi")
    public  String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4),
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    //method to indentify if the Uri is an External Storage document
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    //method to indentify if the Uri is an downloads document
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    //method to indentify if the Uri is a media document
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    //method that redirects user to preview the list of images selected
    public void showGallPicturesPreview(ArrayList<String> images,ArrayList<Uri> uris,Uri uriData){
        Intent intent = new Intent(MainActivity.this,DisplaySelectedImages.class);
        if(uriData!=null)
            intent.setData(uriData);
        intent.putExtra("selectedImages",images);
        ArrayList<String> uriToString = new ArrayList<String>();
        if(uris!=null) {
            for (Uri uri : uris) {
                uriToString.add(uri.toString());
            }
        }
        if(uriToString!=null && uriToString.size()>0)
            intent.putExtra("ImageUris",uriToString);

        startActivity(intent);

    }
    //this method enables users to select multiple pictures from the mobile device.
    public void requestPictureGalleryUpload(){

        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

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

    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG,"OnResume");
        selectedImages=null;
        MAIN_ACTIVITY_IS_RUNNING=false;
        ActivityUpdater.activeActivity = this;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        startActivity(new Intent(MainActivity.this,Login.class));
    }

    /*******************
     * Method that displays a snackbar
     * @param message to be displayed in the snackbar
     * @param bgcolor background color of the snackbar
     */
    public void  displaySnackBar(int message,int bgcolor){
        Snackbar snack=null;
        View snackView;
        snack=Snackbar.make(LAYOUT,message,Snackbar.LENGTH_SHORT);
        snackView = snack.getView();
        snackView.setBackgroundColor(bgcolor);
        snack.show();
    }

    /********************************************
     * Setup the LAYOUT
     * Configures the layout for the snackbar depending on the device orientation.
     ********************************************/
    public void setLAYOUT(){
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            LAYOUT=findViewById(R.id.mainLinearLayout);//setupLayout;

        }
        else{
            LAYOUT=findViewById(R.id.myCoordinatorLayout);
        }
    }

    /********
     * performs User Logout operations
     */
    protected void signOut() {
       final Context ctx = getApplicationContext();
        //Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
        if(mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {

                            mGoogleApiClient.disconnect();
                            mGoogleApiClient = null;
                            finish();
                            ActivityUpdater.activeActivity = null;
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Log.i(TAG,"Logging out from DisplayImagesUsingRecyclerView");
                            new Utilities(MainActivity.this).setCurrentIdentity("");
                            MainActivity.this.finish();
                        }
                    }
            );
        }
    }

    /***********************
     * Method to initialize loaction request
     *********************/
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /******************
     * Method to check if Location settings are satisfied
     *****************/
    protected void checkLocationSettings(){
    createLocationRequest();
    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                                .addLocationRequest(mLocationRequest);

    //SettingsClient client =
    //check whether current location services are satisfied.
    Task<LocationSettingsResponse> task =LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
    task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
        @Override
        public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
            try{
                LocationSettingsResponse response = task.getResult(ApiException.class);
                if(!response.getLocationSettingsStates().isLocationUsable()){
                    throw new ResolvableApiException(new Status(LocationSettingsStatusCodes.RESOLUTION_REQUIRED));
                }
                else{
                    startActivity(new Intent(MainActivity.this,CameraMainActivity.class));
                }
                }
            catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG,"LOCATION_RESOLUTION_REQUIRED");
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                    MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        } catch (ClassCastException e) {
                            // Ignore, should be an impossible error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        }
    });

    }
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
}
