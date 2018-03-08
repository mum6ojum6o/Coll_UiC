package com.ibeis.wildbook.wildbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/*************************************************************************
 * Class to view full screen preview of the images
 *********************************************************************/
public class ImageViewActivity extends BaseActivity implements GestureDetector.OnGestureListener,View.OnClickListener{
    private static final String TAG="ImageViewActivity";
    private GestureDetectorCompat mDetector;
    double mPrimaryXCoord= 0.0,mSecondaryXCoord= 0.0;
    ImageView mImageView;
    int mCurrentPicPreviewIndex=-1;
    ArrayList<String> mAllImagePaths;
    ArrayList<Uri>mAllImageUris;
    String mFilePath;
    Uri mUri;
    String mEncounterUrl,mIndividualName;
    boolean mIsReportEncRequest;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetector = new GestureDetectorCompat(this,this);
        RecyclerView recyclerView =null;
        if(getIntent().getIntExtra("SelectedFilePosition",-1)!=-1)
            mCurrentPicPreviewIndex=getIntent().getIntExtra("SelectedFilePosition",-1);

        Log.i(TAG,"current preview pic index:"+mCurrentPicPreviewIndex);

        String filePath=null;

        if(getIntent().getStringArrayListExtra("assets")==null) { //image upload scenario
            setContentView(R.layout.activity_imageview_activity);
            LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout_imageview_act);
            ll.setBackgroundColor(getColor(R.color.font));
            if(getIntent().hasExtra("POS"))
                mUri= Uri.parse(getIntent().getStringExtra("POS"));
            if(getIntent().hasExtra("OtherImageUris")) {
                mAllImagePaths=getIntent().getStringArrayListExtra("OtherImageUris");
                mAllImageUris=new ArrayList<>();
                for(String aUri:mAllImagePaths){
                    mAllImageUris.add(Uri.parse(aUri));
                }
            }
            if(getIntent().hasExtra("filePath")) {
                mFilePath = getIntent().getStringExtra("filePath");
                mAllImagePaths=getIntent().getStringArrayListExtra("AllFiles");
                Log.i(TAG,"Images in this file:"+mAllImagePaths.size());
            }
            ImageView imageView = new ImageView(getApplicationContext());
            mImageView = (ImageView) findViewById(R.id.imgView);
            Display display = getWindowManager().getDefaultDisplay();
            if (!getIntent().getStringExtra("Adapter").equals("DispPic")) {// images from report encounter.
                Glide
                        .with(ImageViewActivity.this)
                        .load(mUri)// the uri you got from Firebase
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                        .crossFade()
                        .into(mImageView);
            } else {

                //Note:- Loading an image through uri with Glide can sometimes lead an issue.
                //thats because,sometime the uri may not contain headers like (Uri:, http:, https:)

                if(mFilePath!=null) { //determines if the preview is from UploadCamPics (images captured from Camera)
                    Log.i(TAG,"ImagePath position in mAllImagesPath:"+mAllImagePaths.indexOf(filePath));
                    Glide
                            .with(ImageViewActivity.this)
                            .load(new File(mFilePath).getPath())
                            .placeholder(R.mipmap.wildbook2)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fitCenter()
                            .crossFade()
                            .into(mImageView);
                }
                else if(mUri!=null) { // this means this is from DisplaySelectedImages.java (Images selected from Gallery)
                Glide
                        .with(this)
                        .load(mUri)
                        .placeholder(R.mipmap.wildbook2)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                        .crossFade()
                        .into(mImageView);
                }
            }
        }
        // to Display the image(s) belonging to an Encounter
        else if(getIntent().getStringArrayListExtra("assets") !=null){
            setContentView(R.layout.activity_display_images_using_recycler_view);
            recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
            findViewById(R.id.encounter_details).setVisibility(View.VISIBLE);
            TextView longitude,latitude,date,encounterId,individualId,individualName;
            longitude=(TextView)findViewById(R.id.encounter_long);
            latitude=(TextView)findViewById(R.id.encounter_lat);
            date=(TextView)findViewById(R.id.encounter_date);
            encounterId=(TextView)findViewById(R.id.encounter_id);
            individualId=(TextView)findViewById(R.id.individual_name);
            individualId.setVisibility(View.INVISIBLE);
            individualName=(TextView)findViewById(R.id.individual_name_string);
            individualName.setVisibility(View.INVISIBLE);
            date.setText(getIntent().getStringExtra("encounter_date"));
            longitude.setText(getIntent().getStringExtra("longitude")!=null? getIntent().getStringExtra("longitude").substring(0,10):"");
            latitude.setText(getIntent().getStringExtra("latitude")!=null?getIntent().getStringExtra("latitude").substring(0,10):"");
            mEncounterUrl = getIntent().getStringExtra("encounterId");
            encounterId.setText(mEncounterUrl);
            encounterId.setPaintFlags(encounterId.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            encounterId.setOnClickListener(this);
            if(getIntent().hasExtra("individualId")){
                individualId.setVisibility(View.VISIBLE);
                mIndividualName=getIntent().getStringExtra("individualId");
                individualName.setText(mIndividualName);
                individualName.setTypeface(null,Typeface.BOLD);
                individualName.setVisibility(View.VISIBLE);
                individualName.setOnClickListener(this);
                individualName.setPaintFlags(individualName.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

            }


            recyclerView.setHasFixedSize(true);
            if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE)
                recyclerView.setLayoutManager(new GridLayoutManager(ImageViewActivity.this,2));
            else
                recyclerView.setLayoutManager(new GridLayoutManager(ImageViewActivity.this,3));
            //JSONArray jsonObject = new JSONObject(getIntent().getStringArrayListExtra("assets"));
            ArrayList<String> imagePaths = getIntent().getStringArrayListExtra("assets");
            //ArrayList<Uri> imageUris = new ArrayList<Uri>();
            mAllImageUris=new ArrayList<>();
            for (String aUri:imagePaths)
                mAllImageUris.add(Uri.parse(aUri));
            RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(ImageViewActivity.this,mAllImageUris);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
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
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        new Utilities(ImageViewActivity.this).setCurrentIdentity("");
                        startActivity(intent);
                        Log.i(TAG,"Logging out from ImageViewActivity");
                        new Utilities(ImageViewActivity.this).setCurrentIdentity("");
                        finishAffinity();
                    }
                });


    }
    //This method will inflate the image depending on the position
    protected void inflateImage(int position){
        Log.i(TAG,"position:"+position+" mCuurenPicPreview:"+mCurrentPicPreviewIndex);
        if(mAllImagePaths!=null && position<mAllImagePaths.size()&& position>=0 && mUri==null) {
            Glide
                    .with(ImageViewActivity.this)
                    .load(new File(mAllImagePaths.get(position)).getPath())
                    .placeholder(R.mipmap.wildbook2)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .crossFade()
                    .into(mImageView);
        }
        else if(mAllImagePaths!=null && position<mAllImagePaths.size()&& position>=0 && mUri!=null){
            Glide
                    .with(this)
                    .load(mAllImageUris.get(position))
                    .placeholder(R.mipmap.wildbook2)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .crossFade()
                    .into(mImageView);
        }
    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        Log.d(TAG,"onDown: " + motionEvent.toString());
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        Log.d(TAG, "onShowPress: " + motionEvent.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        Log.d(TAG, "onSingleTapUp: " + motionEvent.toString());
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Log.d(TAG, "onScroll: " + motionEvent.toString() + motionEvent1.toString());
        mPrimaryXCoord=motionEvent.getX();
        mSecondaryXCoord=motionEvent1.getX();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        Log.d(TAG, "onLongPress: " + motionEvent.toString());
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Log.d(TAG, "onFling: " + motionEvent.toString() + motionEvent1.toString());
        if(mSecondaryXCoord>mPrimaryXCoord){
            //move Left
            if(mCurrentPicPreviewIndex>0)
                mCurrentPicPreviewIndex--;

            inflateImage(mCurrentPicPreviewIndex);
            //Toast.makeText(this,"Left -> Right",Toast.LENGTH_SHORT).show();
        }
        else if(mPrimaryXCoord > mSecondaryXCoord){
            //move right
            Log.i(TAG,"Current Picture Index:"+mCurrentPicPreviewIndex);
            if(mCurrentPicPreviewIndex<mAllImagePaths.size()-1 )
                mCurrentPicPreviewIndex++;

            inflateImage(mCurrentPicPreviewIndex);
            //Toast.makeText(this,"Right -> Left",Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(mUri!=null || mFilePath!=null)
            this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        // Toast.makeText(this,"in onTouchEvent!",Toast.LENGTH_SHORT).show();
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.individual_name_string:
                if(mIndividualName!=null){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://uidev.scribble.com/v2/individuals.jsp?number="+mIndividualName));
                    startActivity(intent);
                }
                break;
            case R.id.encounter_id:
                if(mEncounterUrl!=null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://uidev.scribble.com/v2/encounters/encounter.jsp?number="+mEncounterUrl));
                    startActivity(intent);
                }
                break;
        }
    }
}
