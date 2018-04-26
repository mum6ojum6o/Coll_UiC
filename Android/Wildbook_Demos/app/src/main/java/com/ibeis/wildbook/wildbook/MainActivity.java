package com.ibeis.wildbook.wildbook;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends BaseActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        NetworkScannerBroadcastReceiver.NetworkChangeReceiverListener,
        ImageUploaderTaskRunnable.ImageUploadStatusListener{

    LocalBroadcastManager mLocalBroadcastManager;
    NetworkScannerBroadcastReceiver mReceiver;
   // protected View LAYOUT;
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
    //constant used to display sncakbar if Internet not Connected.
    protected  static final int INTERNET_NOT_CONNECTED =11;
    public static final String TAG ="MainActivity";
    public int mCount=0;
    protected Button RepEncounter,SignOutBut,UploadFromGallery,History,mSync;
    //private FirebaseAuth mAuth;
    protected TextView UserName;
    protected ArrayList<String> mSelectedImages = new ArrayList<String>();
    protected ArrayList<Uri> mUris = new ArrayList<>();
   // protected static String storagePath="Photos/";
   // protected static String databasePath="Photos/";
    //private GoogleApiClient mGoogleApiClient;
    public static final int SYNC_COMPLETE=1;
    public static final int SYNC_STARTED=2;
    //public static boolean MAIN_ACTIVITY_IS_RUNNING;
    //private GoogleSignInAccount googleSignInAccount;
    //private ImageView mCricleImageView;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private static int syncRunCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //MAIN_ACTIVITY_IS_RUNNING=true;
        super.onCreate(savedInstanceState);
        Log.i(TAG,"MainActivity onCreate");

       // Log.i(TAG,"Username:"+FirebaseAuth.getInstance().getCurrentUser().getEmail());
        Log.i("Logging window","Orientation is"+getResources().getConfiguration().orientation);

            setContentView(R.layout.activity_main);
            setLAYOUT();
        WARNING=getColor(R.color.red);
        POS_FEEDBACK=getColor(R.color.green);
        UserName=(TextView)findViewById(R.id.username);
        RepEncounter = (Button) findViewById(R.id.RepEnc);
        UploadFromGallery=(Button)findViewById(R.id.GallUpload);
        History = (Button)findViewById(R.id.historyBtn);

        RepEncounter.setOnClickListener(this);
        History.setOnClickListener(this);
        UploadFromGallery.setOnClickListener(this);
        //mSync.setOnClickListener(this);
       Log.i("LoginActivity",Auth.GOOGLE_SIGN_IN_API.getName());
       Log.i(TAG,"IsUploaded:"+getIntent().hasExtra("UploadRequested"));
        if(getIntent().hasExtra("UploadRequested"))
            displayDialogue();
    }

    @Override
    public void onStart() {
        super.onStart();
        //MAIN_ACTIVITY_IS_RUNNING=true;
    }

    @Override
    public void onStop(){
        super.onStop();
        //MAIN_ACTIVITY_IS_RUNNING=false;
    }
    @Override
    public void onResume(){
        super.onResume();
        Utilities utilities = new Utilities(this);
        //ActivityUpdaterBroadcastReceiver.activeActivity = this;
        //MAIN_ACTIVITY_IS_RUNNING=true;
        String naam=utilities.getCurrentIdentity();
        Log.i(TAG,""+new Utilities(this).getCurrentIdentity());
        if(utilities.getCurrentIdentity().equals("")){
            Log.i(TAG,"zzxxyz not set!!");
            signOut();
            finish();
        }
        Log.i(TAG,"in on Resume:"+ naam);
        Log.i(TAG,"uploadRequested?:"+getIntent().hasExtra("UploadRequested"));

        //registering local listeners
        WildbookApplication.getmInstance().setNetworkStatusChangeListener(this);
        WildbookApplication.getmInstance().setImageStatusChangedListener(this);

        if(utilities.isNetworkAvailable()){
            setLAYOUT();
            utilities.displaySnackBar(LAYOUT,R.string.online,MainActivity.POS_FEEDBACK);
        }
        else{
            setLAYOUT();
            utilities.displaySnackBar(LAYOUT,R.string.offline,MainActivity.WARNING);
        }


        if(getIntent().hasExtra("UploadRequested"))
            displayDialogue();
        Log.i(TAG,"OnResume selectedImagesCreated n size");//+mSelectedImages.size());


        //startSync();

    }

    /*************************
     *
     * Button click Callback
     * @param v
     *************************/
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
                Log.i(TAG,"R.id.GallUpload clicked");
                if(ContextCompat.checkSelfPermission(getApplicationContext(),"android.permission.READ_EXTERNAL_STORAGE")==PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), R.string.maxuploadString, Toast.LENGTH_LONG).show();
                    requestPictureGalleryUpload();
                }
                else{
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"},READ_STORAGE_PERMISSION_REQUEST);
                }
                break;
            case R.id.historyBtn:
                if(new Utilities(this).isNetworkAvailable())
                    startActivity(new Intent(MainActivity.this, UserContributionsActivity.class));
                else {
                    new Utilities(this).displaySnackBar(LAYOUT,R.string.offline,MainActivity.WARNING);
                }
                break;
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
                mSelectedImages = new ArrayList<String>();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    if (mClipData.getItemCount() > 10) {
                        Log.i(TAG, "More than 10 images selected");
                        Toast.makeText(getApplicationContext(), R.string.maxuploadString, Toast.LENGTH_LONG).show();
                        requestPictureGalleryUpload();
                    } else {
                        //ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mUris.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            selectedImage = cursor.getString(columnIndex);
                            try {
                                mSelectedImages.add(getFilePath(this, uri));
                            }catch(Exception e){
                                Toast.makeText(this,"Something went wrong!",Toast.LENGTH_LONG).show();
                            }
                            cursor.close();
                        }

                            //showGallPicturesPreview(mSelectedImages, mUris,dataUri);
                        showGallPicturesPreview();

                    }
                } else if (data.getData() != null) {
                    Uri uri = data.getData();
                    ArrayList<Uri> uris = new ArrayList<Uri>();
                    mUris.add(uri);
                    uris.add(uri);
                    try{
                        mSelectedImages.add(getFilePath(this,uri));
                    }catch(Exception e){
                        e.printStackTrace();
                        Toast.makeText(this,"Something went wrong!",Toast.LENGTH_LONG).show();
                    }
                    //showGallPicturesPreview(mSelectedImages, uris,uri);
                    showGallPicturesPreview();
                }
            }
        }
        if(requestCode == REQUEST_CHECK_SETTINGS){
            Log.i(TAG,"REQUEST_CHECK_SETTTINGS");
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

    //method uploads the images selected or caches them in the absence of network.
    //public void showGallPicturesPreview(ArrayList<String> images,ArrayList<Uri> uris,Uri uriData){
    public void showGallPicturesPreview(){
       //check the network availability....
        if (new Utilities(this).isNetworkAvailable()) {
            if (areValidPaths(mSelectedImages)) {
                startUpload();
            } else {//download image from uri and then upload it....
                mSelectedImages.clear();
                new Utilities(getApplicationContext()).displaySnackBar(LAYOUT, R.string.imagesWillBeUploadedShortly, POS_FEEDBACK);
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        mSelectedImages = returnDownloadedFilePath(mUris);
                        if (mSelectedImages == null) { //error while downloading file from other sources....
                           runOnUiThread(new Runnable (){
                               @Override
                               public void run() {
                                   Toast.makeText(getApplicationContext(),
                                           "Something went wrong while Downloading the image!!",
                                           Toast.LENGTH_LONG).show();
                               }
                           });
                        } else {
                            //start download....
                            startUpload();
                        }
                    }
                }).start();

            }
        }
        else {//no connectivity
            if (areValidPaths(mSelectedImages)) {
                saveToDb();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mSelectedImages = returnDownloadedFilePath(mUris); //download the files
                        if (mSelectedImages != null)
                            saveToDb();
                        else {
                            runOnUiThread(new Runnable (){
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Something went wrong while Downloading the image!!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        }


        }
    //this method enables users to select multiple pictures from the mobile device.
    public void requestPictureGalleryUpload(){
        Log.i(TAG,"Launch Upload Encounter(s) images");
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT); //Set Action
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
        mSelectedImages =null;
        //MAIN_ACTIVITY_IS_RUNNING=false;
        WildbookApplication.getmInstance().setNetworkStatusChangeListener(null);
        WildbookApplication.getmInstance().setImageStatusChangedListener(null);

        //ActivityUpdaterBroadcastReceiver.activeActivity = null;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
    }

    /*******************
     * Method that displays a snackbar
     * @param message to be displayed in the snackbar
     * @param bgcolor background color of the snackbar
     */
    /*public void  displaySnackBar(int message,int bgcolor){
        Snackbar snack=null;
        View snackView;
        if(message == com.ibeis.wildbook.wildbook.R.string.offline)
            snack=Snackbar.make(LAYOUT,message,Snackbar.LENGTH_INDEFINITE);
        else
            snack=Snackbar.make(LAYOUT,message,Snackbar.LENGTH_SHORT);
        snackView = snack.getView();
        snackView.setBackgroundColor(bgcolor);
        snack.show();
    }*/

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

    /****************************************
     * performs User Logout operations
     *****************************************/
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

                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Log.i(TAG,"Logging out from MainActivity");
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

    //????
    protected void onNewIntent(Intent newIntent){
        super.onNewIntent(newIntent);
        setIntent(newIntent);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
    //Displays the Hurray! dialog on the iniatiaing an upload through Report encounter
    protected void displayDialogue(){
        getIntent().removeExtra("UploadRequested");
        onNewIntent(getIntent());
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setTitle(R.string.thankyou);
        TextView tv = (TextView)dialog.findViewById(R.id.dialog_txt);
        tv.setText(R.string.thankyou);
        TextView tv1 = (TextView)dialog.findViewById(R.id.dialog_txt1);
        tv1.setText(R.string.whatnext);
        dialog.show();

    }
    /***************************************
     *
     * Method to check if all filesPaths that are to be previewed/uploaded are valid and exist in the device.
     * @param selectedImages
     * @return
     *************************************/
    public boolean areValidPaths(ArrayList<String> selectedImages){

        try{
            for(String aPath:selectedImages){
                File f= new File(aPath);
                if(!f.exists())
                    return false;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**************************************
     * Downloads a file from the internet if downloading from google drive etc
     * provides the path of the file.
     * @param selectedImages
     * @return
     * DO NOT CALL THIS METHOD FROM THE UI THREAD!!
     *************************************/

    public ArrayList<String> returnDownloadedFilePath(final ArrayList<Uri> selectedImages){
        ArrayList<String> downloadedImagePaths = new ArrayList<String>();
        File imageFolder=null;
        File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        imageFolder = new File(imageFile, "Wildbook");
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }

        for(Uri aUri: selectedImages){
            mCount++;
            Bitmap bitmap=null;
            runOnUiThread(new Runnable (){

                @Override
                public void run() {
                    String message = getApplicationContext().getResources().getString(R.string.downloading_images);
                   new Utilities(getApplicationContext())
                           .displaySnackBar(LAYOUT,
                                   message+mCount+"/"+selectedImages.size(),
                                   MainActivity.POS_FEEDBACK);
                }
            });
            if(getContentResolver().getType(aUri).contains("Image")|| getContentResolver().getType(aUri).contains("image")) {
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(aUri));
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    File f = new File(imageFolder + "/" + timestamp + ".jpg");
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                    fo.flush();
                    fo.close();
                    downloadedImagePaths.add(f.getAbsolutePath());
                }
                catch(Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
            else{
                return null;
            }
        }
        return downloadedImagePaths;

    }
    //Method that starts the upload process
    public void startUpload(){
        ImageUploaderTaskRunnable task = new ImageUploaderTaskRunnable(this, mSelectedImages,new Utilities(this).getCurrentIdentity());
        Log.i(TAG, "Starting fileupload request using worker thread!!");
        new Thread(task).start();
        Log.i(TAG, "redirecting....to mainActivity");
        //Toast.makeText(this,R.string.uploading_images,Toast.LENGTH_SHORT).show();

    }
    //Method that stores the details of images to be uploaded in the device sqlite database instance.
    public void saveToDb(){
        ImageRecorderDatabaseSQLiteOpenHelper dbHelper = new ImageRecorderDatabaseSQLiteOpenHelper(this);
        Utilities utility = new Utilities(this, mSelectedImages,dbHelper);
        utility.prepareBatchForInsertToDB(false);
        Log.i(TAG,"prepared");
        if(!(new Utilities(this).checkSharedPreference(new Utilities(this).getCurrentIdentity()))) {
            utility.connectivityAlert().show();
        }
        else {

            utility.insertRecords();
            Log.i(TAG,"Results saved to SQLite3");
            runOnUiThread(new Runnable (){
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),R.string.uploadRequestOnNoNetwork,Toast.LENGTH_LONG).show();
                }
            });
            /*startActivity(new Intent(GalleryUploadImagePreviewRecyclerViewActivity.this,MainActivity.class));
            finish();*/
            // redirect(0, 0);
        }
    }

    //Displays network status using SnackBar
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

    /***********************************************************
     *  This method displays the status of Image Upload using snackbar
     *
     * @param imagesUploadedStatus - the status of the ImageUpload is represented using the following:-
     *                             -1 ->error,
     *                             1 -> Upload Started,
     *                             2 -> Upload Completed Succesfully
     *                             TBT
     ************************************************************/
    @Override
    public void onImageUploadStatusChangedListener(int imagesUploadedStatus) {
        Utilities utilities = new Utilities(this);
        switch(imagesUploadedStatus){
            case ImageUploaderTaskRunnable.UPLOAD_STARTED:
                utilities.displaySnackBar(LAYOUT,R.string.uploading_images,MainActivity.POS_FEEDBACK);
                break;
            case ImageUploaderTaskRunnable.UPLOAD_COMPLETED_SUCCESSFULLY:
                utilities.displaySnackBar(LAYOUT,R.string.imageUploadedString,MainActivity.POS_FEEDBACK);
                break;
            case ImageUploaderTaskRunnable.UPLOAD_ERROR:
                utilities.displaySnackBar(LAYOUT,R.string.imagesNotUploaded,MainActivity.WARNING);
        }
    }

    /**************************
     * This method is deprecated.
     *************************/
    protected void localBroadcast(){
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mReceiver = new NetworkScannerBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mLocalBroadcastManager.registerReceiver(mReceiver,intentFilter);
        Intent intent = new Intent("android.net.conn.CONNECTIVITY_CHANGE");
        intent.putExtra("string",com.ibeis.wildbook.wildbook.R.string.online);
        mLocalBroadcastManager.sendBroadcast(intent);
    }
}
