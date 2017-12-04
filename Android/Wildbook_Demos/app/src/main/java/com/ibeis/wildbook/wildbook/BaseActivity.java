package com.ibeis.wildbook.wildbook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public abstract class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    protected GoogleApiClient mGoogleApiClient;
    protected GoogleSignInAccount googleSignInAccount;
    private ImageView mCircleImageView;
    protected ActionBar action;
    private MenuItem menuItem;
    private SubMenu subMenuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //mGoogleApiClient.connect();
         action =getSupportActionBar();
        action.setCustomView(R.layout.circular_imageview_for_action_bar);
        action.setBackgroundDrawable(getDrawable(R.drawable.actionbar_portrait));
        LinearLayout ll = (LinearLayout)findViewById(R.id.circular_image_view_layout);
        mCircleImageView = action.getCustomView().findViewById(R.id.circle_imgeview);
        action.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_SHOW_HOME);



    }
@Override
public void onStart(){
        super.onStart();
    Log.i("BASE","in BASE OnStart");


}


@Override
public void onResume() {
    super.onResume();


        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                // and the GoogleSignInResult will be available instantly.
                Log.d("BASEACTIVITY", "Got cached sign-in");
                GoogleSignInResult result = opr.get();

                googleSignInAccount = result.getSignInAccount();
                Log.i("SilentLogin", googleSignInAccount.getEmail());

                Uri uri = googleSignInAccount.getPhotoUrl();

                Glide.with(getApplicationContext())
                        .load(uri)
                        .asBitmap()
                        .fitCenter()
                        .into(new BitmapImageViewTarget(mCircleImageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);

                                circularBitmapDrawable.setCircular(true);
                                mCircleImageView.setImageDrawable(circularBitmapDrawable);
                            }
                        });
                if (menuItem != null)
                    menuItem.setTitle(googleSignInAccount.getDisplayName());



            } else {

                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        //hideProgressDialog();
                        if (googleSignInResult.getStatus().isSuccess()) {
                            googleSignInAccount = googleSignInResult.getSignInAccount();
                            Log.i("SilentLogin", "SUCCESSSS!!!"+googleSignInAccount.getEmail());
                            Glide.with(getApplicationContext())
                                    .load(googleSignInAccount.getPhotoUrl())
                                    .asBitmap()
                                    .fitCenter()
                                    .into(new BitmapImageViewTarget(mCircleImageView) {
                                        @Override
                                        protected void setResource(Bitmap resource) {
                                            RoundedBitmapDrawable circularBitmapDrawable =
                                                    RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);

                                            circularBitmapDrawable.setCircular(true);
                                            mCircleImageView.setImageDrawable(circularBitmapDrawable);
                                        }
                                    });
                            if (menuItem != null)
                                menuItem.setTitle(googleSignInAccount.getDisplayName());

                        } else {

                        }//else closes
                    }
                });
            }
            Log.i("BASE","in BASE OnResume");

        }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("BASEACTIVITY", "onConnectionFailed:" + connectionResult);
        startActivity(new Intent(getApplicationContext(),Login.class));
    }
    protected void signOut() {
        final Context ctx=getApplicationContext();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        mGoogleApiClient.disconnect();
                        mGoogleApiClient=null;
                        finish();
                        ActivityUpdater.activeActivity=null;
                        Intent intent = new Intent(ctx, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        BaseActivity.this.finish();
                        startActivity(intent);

                        finishAffinity();
                    }
                });


    }

     public boolean onCreateOptionsMenu(Menu menu){
        //String name=googleSignInAccount.getDisplayName();
        getMenuInflater().inflate(R.menu.actionbar_options,menu);
        if(menuItem ==null){
            Log.i("BASE","menuItem is null");
        }
        menuItem = menu.findItem(R.id.menu_name);

        if(googleSignInAccount!=null)
            menuItem.setTitle(googleSignInAccount.getDisplayName());
        //item_name.setTitle("Arjan");
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Log.i("VideoPlaylist:","In onOptionsItemsSelected");
        switch(item.getItemId()){
            case R.id.menu_name:
                return true;

            case R.id.menu_sync:
                new Utilities(this).startSyncing();
                return true;

            case R.id.menu_logout:
                signOut();
                return true;

        }
        return false;
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i("BASE", "in BASE onSTOP");
        //Log.i("BASE","googleApiClient is connected?"+(mGoogleApiClient.isConnected()));
        Log.i("BASE", "bye bye!! "+ new Utilities(this).getCurrentIdentity());

    }
}
