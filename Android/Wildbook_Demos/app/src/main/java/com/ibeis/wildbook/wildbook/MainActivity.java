package com.ibeis.wildbook.wildbook;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"MainActivity onCreate");
        super.onCreate(savedInstanceState);
       // Log.i(TAG,"Username:"+FirebaseAuth.getInstance().getCurrentUser().getEmail());
        setContentView(R.layout.activity_main);
        //mAuth = FirebaseAuth.getInstance();
        mAuth=FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(storagePath.equals("Photos/"))
            storagePath = storagePath+firebaseUser.getEmail();
        Log.i(TAG,"storagePath"+storagePath);
        if(databasePath.equals("Photos/"))
            databasePath = databasePath+firebaseUser.getUid();
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

        UserName=(TextView)findViewById(R.id.username);
        UserName.setText(firebaseUser.getEmail()+"!");
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

    }
    /*@Override
    public void onStart(){
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            //showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    //hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    */@Override
    public void onResume(){
        super.onResume();

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
                else
                    displayToasts(INTERNET_NOT_CONNECTED);
                break;
            case R.id.SingOut:
               /* mGoogleApiClient.disconnect();
                mAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),Login.class));*/

               signOut();
                break;
            case R.id.syncBtn:
                new Utilities(this).startSyncing();
                break;

        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        mGoogleApiClient.disconnect();
                        //mAuth.signOut();
                        mGoogleApiClient=null;
                        mAuth.signOut();
                        mAuth=null;
                        finish();
                        databasePath="Photos/";
                        storagePath="Photos/";
                        //mAuth=null;
                        startActivity(new Intent(MainActivity.this,Login.class));
                    }
                });

        //startActivity(new Intent(MainActivity.this,Login.class));
    }

    /*
    *fetch the pictures from Firebase.
     */
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
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            // mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
        } else {
            //Nothing.
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        startActivity(new Intent(MainActivity.this,Login.class));
    }

}
