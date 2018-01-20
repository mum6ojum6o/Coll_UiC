package com.ibeis.wildbook.wildbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/************************************************
 * Activity to preview the images selected from the Image Gallery of the device.
 ************************************************/
public class DisplaySelectedImages extends BaseActivity implements View.OnClickListener{
    final static public String TAG= "DisplaySelectedImages";
    protected ArrayList<String> selectedImages = new ArrayList<String>();
    protected  ArrayList<Uri> imageUri = new ArrayList<Uri>();
    protected Button UploadBtn,DiscardBtn,SelectAllBtn,UnSelectAllBtn;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter ;
    private ArrayList<String> mSelectedImages=new ArrayList<String>(); //represents the images selected after long press.
    //protected StorageReference mStorage;
    //protected FirebaseAuth mAuth;
    //protected DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_grid_view);
        setContentView(R.layout.activity_camera_upload_preview);
        /*getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.wildbook2);
        getSupportActionBar().setTitle(R.string.imagePreviewString);*/
        /*action.setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.action_bar,null)));*/
       // mAuth = FirebaseAuth.getInstance();
        //FirebaseUser user = mAuth.getCurrentUser();
       // mStorage= FirebaseStorage.getInstance().getReference();
       // databaseReference = FirebaseDatabase.getInstance().getReference(MainActivity.databasePath);

        ArrayList<String> stringToUri = new ArrayList<String>();
        selectedImages = getIntent().getStringArrayListExtra("selectedImages");
        stringToUri = getIntent().getStringArrayListExtra("ImageUris");
        if (stringToUri != null && selectedImages.get(0) != null) {
            for (String aUri : stringToUri) {
                imageUri.add(Uri.parse(aUri));
            }
        } else {
            for (String aUri : stringToUri) {
                imageUri.add(Uri.parse(aUri));
            }
        }

        if(isImage(imageUri)) {
            Log.i(TAG,"Buttons to be activated");
            UploadBtn = (Button) findViewById(R.id.UploadBtn2);
            DiscardBtn = (Button) findViewById(R.id.DiscardBtn2);
            SelectAllBtn = (Button) findViewById(R.id.SelectAllBtn);
            UnSelectAllBtn =(Button)findViewById(R.id.UnselectAll);
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(DisplaySelectedImages.this, 3));
            adapter = new DispPicAdapter(this, imageUri, selectedImages);
            recyclerView.setAdapter(adapter);
            //  GridView gv = (GridView)findViewById(R.id.gridview);
            //gv.setAdapter(new ImageAdapter(this,selectedImages));
            UploadBtn.setOnClickListener(this);
            DiscardBtn.setOnClickListener(this);
            SelectAllBtn.setOnClickListener(this);
            UnSelectAllBtn.setOnClickListener(this);
        }
        else{
            Toast.makeText(getApplicationContext(),"Unable to Load Image",Toast.LENGTH_LONG).show();
            startActivity(new Intent(DisplaySelectedImages.this, MainActivity.class));
        }
    }
    public void getSelectedImages(){
        for (int i=0;i<selectedImages.size();i++){
            if(recyclerView.getChildAt(i).findViewById(R.id.imageView2).getVisibility()==View.VISIBLE){
                mSelectedImages.add(selectedImages.get(i));
            }
        }
    }
    public void onClick(View view){
        getSelectedImages();
        switch(view.getId()){
            case R.id.UploadBtn2:
                if(mSelectedImages.size()>0) {
                    if (new Utilities(this).isNetworkAvailable()) {
                        //check for network availability
                   /* Utilities util = new Utilities(getApplicationContext(),selectedImages,new ImageRecorderDatabase(this));
                    util.uploadPictures(selectedImages);

                    redirect(selectedImages.size(),selectedImages.size());*/
                   /*Requestor request = new Requestor(UploadCamPics.url,"UTF-8","POST");
                    request.addFormField("jsonResponse","true");
                    for(String file:selectedImages){
                        try {
                            request.addFile("theFiles", file);
                        }catch(Exception e){
                            e.printStackTrace();
                            Log.i(TAG,"case UploadBtn2: exception occured while building request");
                            break;
                        }
                    }
                    try{
                        request.finishRequesting();
                        Toast.makeText(this," Images uploaded!",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(DisplaySelectedImages.this,MainActivity.class));
                    }catch(Exception e){
                        Log.i(TAG,"Server response error!!");
                        Toast.makeText(getApplicationContext(),"The images were not uploaded!!",Toast.LENGTH_LONG).show();
                    }finally{
                        redirect(selectedImages.size(),selectedImages.size());
                    }*/
                   /* Bitmap bitmap=null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(selectedImages.get(0))));
                    }catch(Exception e){e.printStackTrace();}
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,50,bytes);
                    byte[] imageBytes = bytes.toByteArray();
                    //String encodedImage= Base64.encodeToString(imageBytes,Base64.DEFAULT);

                    selectedImages.clear();
                    selectedImages.add(encodedImage);*/
                        if (areValidPaths(mSelectedImages)) {
                        /*ImageUploaderTask task = new ImageUploaderTask(this, selectedImages);
                        Log.i(TAG, "Starting fileupload request using worker thread!!");
                        new Thread(task).start();
                        Log.i(TAG, "redirecting....to mainActivity");
                        //redirect(selectedImages.size(), selectedImages.size());
                        startActivity(new Intent(DisplaySelectedImages.this, MainActivity.class));
                        finish();*/
                            startUpload();
                        } else {//download image from uri and then upload it....
                            mSelectedImages.clear();
                            mSelectedImages = returnDownloadedFilePath(imageUri);
                            if (mSelectedImages == null) { //error while downloading file from other sources....
                                Toast.makeText(getApplicationContext(),
                                        "Something went wrong while Downloading the image!!",
                                        Toast.LENGTH_LONG).show();
                            } else {//start download....
                           /* ImageUploaderTask task = new ImageUploaderTask(this, selectedImages);
                            Log.i(TAG, "Starting fileupload request using worker thread!!");
                            new Thread(task).start();
                            Log.i(TAG, "redirecting....to mainActivity");
                            //redirect(selectedImages.size(), selectedImages.size());
                            startActivity(new Intent(DisplaySelectedImages.this, MainActivity.class));
                            finish();*/
                                startUpload();
                            }
                        }


                    } else {//no connectivity
                        if (areValidPaths(mSelectedImages)) {
                            saveToDb();
                        } else {
                            mSelectedImages = returnDownloadedFilePath(imageUri); //download the files
                            if (mSelectedImages != null)
                                saveToDb();
                            else {
                                Toast.makeText(getApplicationContext(), "Something went wrong! Please try again!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(DisplaySelectedImages.this, MainActivity.class));
                            }
                        }
                    /*ImageRecorderDatabase dbHelper = new ImageRecorderDatabase(this);
                    Utilities utility = new Utilities(this,selectedImages,dbHelper);
                    utility.prepareBatchForInsertToDB(false);
                    Log.i(TAG,"prepared");
                    if(!(new Utilities(this).checkSharedPreference(new Utilities(this).getUserEmail()))) {
                        utility.connectivityAlert().show();
                    }
                    else {

                        utility.insertRecords();
                        Log.i(TAG,"Results saved to SQLite3");
                        Toast.makeText(getApplicationContext(),R.string.uploadRequestOnNoNetwork,Toast.LENGTH_LONG).show();
                        startActivity(new Intent(DisplaySelectedImages.this,MainActivity.class));
                        finish();
                       // redirect(0, 0);
                    }*/
                    }
                }
                else{
                    /*final AlertDialog.Builder builder = new AlertDialog.Builder(DisplaySelectedImages.this);
                    builder.setTitle(getResources().getString(R.string.noimageselected));
                    builder.setMessage(getResources().getString(R.string.requestimageselection));

                    AlertDialog dialog = builder.create();
                    dialog.setInverseBackgroundForced(true);
                    */
                    Dialog dialog = new Dialog(DisplaySelectedImages.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_layout);
                    dialog.setTitle(getResources().getString(R.string.noimageselected));
                    TextView tv = (TextView)dialog.findViewById(R.id.dialog_txt);
                    tv.setText(getResources().getString(R.string.noimageselected));
                    TextView tv1 = (TextView)dialog.findViewById(R.id.dialog_txt1);
                    tv1.setText(getResources().getString(R.string.requestimageselection));
                    dialog.show();

                }
                break;
            case R.id.DiscardBtn2:
                startActivity(new Intent(DisplaySelectedImages.this , MainActivity.class));
                finish();
                break;
            case R.id.SelectAllBtn:
                    selectAll();
                break;
            case R.id.UnselectAll:
                    unSelectAll();
                break;
        }
    }

    /*************************
     * Method that selects all images
     **************************/
    public void selectAll(){
        for(int i=0;i<selectedImages.size();i++) {
            if (recyclerView.getChildAt(i).findViewById(R.id.imageView2).getVisibility() != View.VISIBLE) {
                recyclerView.getChildAt(i).findViewById(R.id.imageView2).setVisibility(View.VISIBLE);
            }
        }
}

    //method to unselcet all images
    protected void unSelectAll(){
        for (int i=0;i<selectedImages.size();i++) {
            if (recyclerView.getChildAt(i).findViewById(R.id.imageView2).getVisibility() == View.VISIBLE) {
                recyclerView.getChildAt(i).findViewById(R.id.imageView2).setVisibility(View.INVISIBLE);
                //mSelectedImages.add(imagesNames.get(i));
            }
        }
        mSelectedImages.clear();
    }

    //Method that captures details of images in the device sqlite database instance.
    public void saveToDb(){
        ImageRecorderDatabase dbHelper = new ImageRecorderDatabase(this);
        Utilities utility = new Utilities(this,mSelectedImages,dbHelper);
        utility.prepareBatchForInsertToDB(false);
        Log.i(TAG,"prepared");
        if(!(new Utilities(this).checkSharedPreference(new Utilities(this).getCurrentIdentity()))) {
            utility.connectivityAlert().show();
        }
        else {

            utility.insertRecords();
            Log.i(TAG,"Results saved to SQLite3");
            Toast.makeText(getApplicationContext(),R.string.uploadRequestOnNoNetwork,Toast.LENGTH_LONG).show();
            startActivity(new Intent(DisplaySelectedImages.this,MainActivity.class));
            finish();
            // redirect(0, 0);
        }
    }

    public void onResume(){
        super.onResume();

    }
    //Method that starts the upload process
    public void startUpload(){
        ImageUploaderTask task = new ImageUploaderTask(this, mSelectedImages,new Utilities(this).getCurrentIdentity());
        Log.i(TAG, "Starting fileupload request using worker thread!!");
        new Thread(task).start();
        Log.i(TAG, "redirecting....to mainActivity");
        //redirect(selectedImages.size(), selectedImages.size());
        startActivity(new Intent(DisplaySelectedImages.this, MainActivity.class));
        finish();
    }

    /***************************************
     *
     * Method to check if all filesPaths that are to be previewed/uploaded are valied and exist in the device.
     * @param selectedImages
     * @return
     *************************************/
    public boolean areValidPaths(ArrayList<String> selectedImages){
        ArrayList<File> files = new ArrayList<File>();
        try{
            for(String aPath:selectedImages){
                File f= new File(aPath);
                if(!f.exists())
                    return false;
                else
                    files.add(f);
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /***************
     * Downloads a file from the internet if downloading from google drive etc
     * provides the path of the file.
     * @param selectedImages
     * @return
     */

    public ArrayList<String> returnDownloadedFilePath(ArrayList<Uri> selectedImages){
        ArrayList<String> downloadedImagePaths = new ArrayList<String>();
        File imageFolder=null;
        File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        imageFolder = new File(imageFile, "Wildbook");
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }
        for(Uri aUri: selectedImages){
            Bitmap bitmap=null;

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
    // Method that determines if the mentioned Uri is an Image
    public boolean isImage(ArrayList<Uri> uris){
        for (Uri aUri:uris){
            if(getContentResolver().getType(aUri).contains("Image")|| getContentResolver().getType(aUri).contains("image")) {
                return true;
            }
            else{
                return false;
            }
        }
        return false;
    }
    protected void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        mGoogleApiClient.disconnect();
                        mGoogleApiClient=null;
                        finish();
                        ActivityUpdater.activeActivity=null;
                        Intent intent = new Intent(DisplaySelectedImages.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Log.i(TAG,"Logging out from DisplayImagesUsingRecyclerView");
                        new Utilities(DisplaySelectedImages.this).setCurrentIdentity("");
                        DisplaySelectedImages.this.finish();
                    }
                });


    }
}
