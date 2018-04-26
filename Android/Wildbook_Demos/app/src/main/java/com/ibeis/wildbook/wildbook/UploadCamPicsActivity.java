package com.ibeis.wildbook.wildbook;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.File;
import java.util.ArrayList;

import static com.ibeis.wildbook.wildbook.GalleryUploadImagePreviewRecyclerViewActivity.SAVED_LAYOUT_MANAGER;


/*********************************
This Activity is used for displaying the preview of images captured by the user using the
cameraMainActivity.
This activity also enables user to either Upload or Discard the pictures clicked by the user.
 *******************************/
public class UploadCamPicsActivity extends BaseActivity implements View.OnClickListener {
    public static final String url="http://uidev.scribble.com/v2/EncounterForm";
    final static public String TAG= "GalleryUploadImagePreviewRecyclerViewActivity";
    private RecyclerView recyclerView;
    private DispPicAdapter adapter ;
    private Button mUploadBtn,mDiscardBtn,mSelectAll,mUnselectAll;
    //private FirebaseAuth mAuth;

    private ArrayList<Uri> imagesUris=new ArrayList<Uri>();
    private ArrayList<String> imagesNames = new ArrayList<String>();
    private ArrayList<String>mSelectedImages = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_camera_upload_preview);
        mSelectAll = (Button) findViewById(R.id.SelectAllBtn);
        mUploadBtn = (Button) findViewById(R.id.UploadBtn2);
        mDiscardBtn = (Button) findViewById(R.id.DiscardBtn2);
        mUnselectAll = (Button) findViewById(R.id.UnselectAll);
        Intent intent = getIntent();
        Log.i(TAG, ""+intent.hasExtra("Files"));
        imagesNames =intent.getStringArrayListExtra("Files");
        for(String file: imagesNames){
            imagesUris.add(Uri.parse(new File(file).toString()));
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new  GridLayoutManager(UploadCamPicsActivity.this,3));
        adapter = new DispPicAdapter(this, imagesUris,imagesNames);
        //adapter = new RecyclerViewAdapter(getApplicationContext(),imagesList);
        recyclerView.setAdapter(adapter);
        mUploadBtn.setOnClickListener(this);
        mDiscardBtn.setOnClickListener(this);
        mSelectAll.setOnClickListener(this);
        mUnselectAll.setOnClickListener(this);
        super.onCreate(savedInstanceState);

    }

    public void getSelectedImages(){
        for (int i=0;i<imagesNames.size();i++){
            if(recyclerView.getChildAt(i).findViewById(R.id.image_selected_icon).getVisibility()==View.VISIBLE){
                mSelectedImages.add(imagesNames.get(i));
            }
        }
    }

    @Override
    public void onClick(View view){

        switch (view.getId()){
            case R.id.UploadBtn2:
                getSelectedImages();
                //check if any image(s) are selected
                if(mSelectedImages.size()>0 && mSelectedImages.size()<=10) {
                    //check if network is available
                    if (new Utilities(this).isNetworkAvailable()) {
                        int uploadCount = 0;
                        //adapter.getSelectedImages();
                        //start the image upload.
                        ImageUploaderTaskRunnable task = new ImageUploaderTaskRunnable(this, mSelectedImages, new Utilities(this).getCurrentIdentity());
                        new Thread(task).start();
                        Log.i(TAG, "redirecting....");
                        //redirect(imagesNames.size(), imagesNames.size());
                        Toast.makeText(this,R.string.uploading_images,Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UploadCamPicsActivity.this,MainActivity.class);
                        intent.putExtra("UploadRequested",true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent); // proceed to the MainActivity
                        finish();
                    }
                    else {
                        //If no network is available prepare to insert records to the SQLite database
                        Log.i(TAG,"No Network prepare data for caching");
                        ImageRecorderDatabaseSQLiteOpenHelper dbHelper = new ImageRecorderDatabaseSQLiteOpenHelper(this);
                        Utilities utility = new Utilities(this, mSelectedImages, dbHelper);
                        utility.prepareBatchForInsertToDB(false);
                        // if the user's preferences are not found on the device
                        if (!(utility.checkSharedPreference(utility.getCurrentIdentity()))) {
                            utility.connectivityAlert().show(); //show preferences dialog.
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.uploadRequestOnNoNetwork, Toast.LENGTH_LONG).show();
                            utility.insertRecords(); //insert records to cache.
                            Log.i(TAG, "Results saved to cache");
                            Intent intent = new Intent(UploadCamPicsActivity.this,MainActivity.class);
                            intent.putExtra("UploadRequested",true);
                            startActivity(intent);
                            finish();
                        }


                    }
                }
                else if(mSelectedImages.size()>10){
                    //more than 10 images selected, display dialogue.
                    displayDialog(R.string.maxuploadHeaderString,R.string.maxuploadHeaderString);
                }
                else{//no images selected, display dialogue.
                    displayDialog(R.string.noimageselected,R.string.requestimageselection);
                }

                break;
            case R.id.DiscardBtn2:
                startActivity(new Intent(UploadCamPicsActivity.this , CameraMainActivity.class));
                finish();
                break;
            case R.id.SelectAllBtn:
                    selectAll();
                    break;
            case R.id.UnselectAll:
                unselectAll();
                break;
        }

    }
    //method to select all images
protected void selectAll(){
        ArrayList<Integer> selectedImagesIndices = new ArrayList<>();
    for (int i=0;i<imagesNames.size();i++) {
        if (recyclerView.getChildAt(i).findViewById(R.id.image_selected_icon).getVisibility() != View.VISIBLE) {
            recyclerView.getChildAt(i).findViewById(R.id.image_selected_icon).setVisibility(View.VISIBLE);
            selectedImagesIndices.add(i);
        }

    }
    //adapter.updateAllViewHolderVisibilityStatus(true);
    adapter.setmSelectedImages(selectedImagesIndices); //update the list of selected images for the adapter
    adapter.setmLongClicked(true); //inform adapter to set mLongClicked member variable

}
    //method to unselcet all images
    protected void unselectAll(){
    for (int i=0;i<imagesNames.size();i++) {
        if (recyclerView.getChildAt(i).findViewById(R.id.image_selected_icon).getVisibility() == View.VISIBLE) {
            recyclerView.getChildAt(i).findViewById(R.id.image_selected_icon).setVisibility(View.INVISIBLE);
        }
    }
    //adapter.updateAllViewHolderVisibilityStatus(false);
    //mSelectedImages.clear();
    adapter.setmSelectedImages(new ArrayList<Integer>()); // clear the list of selected images for the adapter
    adapter.setmLongClicked(false);     // inform the adapter that longClick operation is reset.
}

    protected void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        mGoogleApiClient.disconnect();
                        mGoogleApiClient=null;
                        finish();

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Log.i("ImagePreviewActivity","Logging out from ImagePreviewActivity");
                        new Utilities(UploadCamPicsActivity.this).setCurrentIdentity("");
                        startActivity(intent);

                        finishAffinity();
                    }
                });


    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        Bundle b = new Bundle();
        adapter.onSaveState(b);
        outState.putBundle(SAVED_LAYOUT_MANAGER,b);
    }
    @Override
    protected void onRestoreInstanceState(Bundle inState ){
        super.onRestoreInstanceState(inState);
        if(inState.containsKey(SAVED_LAYOUT_MANAGER) && inState.getBundle(SAVED_LAYOUT_MANAGER).containsKey("SELECTED_IMAGES"))
            adapter.setmSelectedImages(inState.getBundle(SAVED_LAYOUT_MANAGER).getIntegerArrayList("SELECTED_IMAGES"));
    }
    //Method to display a Dialog in the event User clicks on the Upload button while no Image(s) have been seleced.
    public void displayDialog(int heading,int body){
        Dialog dialog = new Dialog(UploadCamPicsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setTitle(getResources().getString(heading));
        TextView tv = (TextView)dialog.findViewById(R.id.dialog_txt);
        tv.setText(getResources().getString(body));
        TextView tv1 = (TextView)dialog.findViewById(R.id.dialog_txt1);
        tv1.setText(getResources().getString(body));
        dialog.show();
    }
}
