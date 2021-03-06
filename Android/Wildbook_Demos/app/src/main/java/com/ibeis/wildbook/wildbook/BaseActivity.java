package com.ibeis.wildbook.wildbook;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener
{
    protected View LAYOUT;
    protected GoogleApiClient mGoogleApiClient;
    protected GoogleSignInAccount googleSignInAccount;
    private ImageView mCircleImageView;
    protected ActionBar mActionBar;
    private MenuItem menuItem;
    private RadioGroup mRadioGroup ;
    private static final String TAG="BaseActivity";
    protected AlertDialog.Builder mAlertDialogBuilder;
    protected String mFirstName=null,mLastName=null,mEmail=null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //mGoogleApiClient.connect();
        mActionBar =getSupportActionBar();
        mActionBar.setCustomView(R.layout.circular_imageview_for_action_bar);
        mActionBar.setBackgroundDrawable(getDrawable(R.drawable.actionbar_portrait));
        LinearLayout ll = (LinearLayout)findViewById(R.id.circular_image_view_layout);
        mCircleImageView = (ImageView)mActionBar.getCustomView().findViewById(R.id.circle_imgeview);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_SHOW_HOME);



    }
@Override
public void onStart(){
        super.onStart();
    Log.i(TAG,"in BASE OnStart");
}

@Override
public void onResume() {
    super.onResume();

    googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
    if(googleSignInAccount!=null){
        Log.i(TAG,"LastSignedInAccount");
        Uri uri = googleSignInAccount.getPhotoUrl();
        mFirstName = googleSignInAccount.getGivenName();
        mLastName = googleSignInAccount.getFamilyName();
        mEmail = googleSignInAccount.getEmail();
        displayProfilePic(uri,mCircleImageView);
    }
    else {
        Log.i(TAG,"LastSignedInAccount no present");
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();

            googleSignInAccount = result.getSignInAccount();
            Log.i(TAG, "SilentLogin"+googleSignInAccount.getEmail());
            Uri uri = googleSignInAccount.getPhotoUrl();
            displayProfilePic(uri,mCircleImageView);
            if (menuItem != null)
                menuItem.setTitle(googleSignInAccount.getDisplayName());
        }
        else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                if (googleSignInResult.getStatus().isSuccess()) {
                    googleSignInAccount = googleSignInResult.getSignInAccount();
                    Log.i(TAG, "SilentLogin"+" SUCCESSSS!!!" + googleSignInAccount.getEmail());
                    displayProfilePic(googleSignInAccount.getPhotoUrl(),mCircleImageView);
                        if (menuItem != null)
                            menuItem.setTitle(googleSignInAccount.getDisplayName());

                    } else {
                        Log.i(TAG, "LoginActivity unsuccessful");
                    }//else closes
                }
            });
        }
    }
            Log.i(TAG,"in BASE OnResume");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }

    /*******************************************
     * Signs User out of the application
     *******************************************/
    protected void signOut() {
        final Context ctx=getApplicationContext();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        mGoogleApiClient.disconnect();
                        mGoogleApiClient=null;
                        finish();
                        Intent intent = new Intent(ctx, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        BaseActivity.this.finish();
                        startActivity(intent);
                        finishAffinity();
                    }
                });


    }

    /****************
     * Inflates the menu options to be displayed in the ActionBar
     * @param menu
     * @return
     *****************/
     public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.actionbar_options,menu);
        if(menuItem ==null){
            Log.i(TAG,"menuItem is null");
        }
        menuItem = menu.findItem(R.id.menu_name);

        if(googleSignInAccount!=null)
            menuItem.setTitle(googleSignInAccount.getDisplayName());
        //item_name.setTitle("Arjan");
        return super.onCreateOptionsMenu(menu);
    }

    /***********************************
     * performs mActionBar on the basis of the option selected in the actionbar
     * @param item
     * @return
     ************************************/
    public boolean onOptionsItemSelected(MenuItem item){
        Log.i(TAG,"In onOptionsItemsSelected");
        switch(item.getItemId()){
            case R.id.menu_name:
                showUserInfoDialog();
                return true;

            case R.id.menu_sync:
                new Utilities(this).startSyncing();
                return true;

            case R.id.menu_logout:
                signOut();
                return true;
            case R.id.menu_preferences:
                showPreferencesDialog();
                return true;

        }
        return false;
    }
    /*************************
     * Inflates the dialog to view user information
     * ******************************/

   public void  showUserInfoDialog(){
       final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.user_profile);
        dialog.setTitle(R.string.userdetails);
        final ImageView userDisplayPic = (ImageView)dialog.findViewById(R.id.user_disp_pic);
        TextView userNameDetails= (TextView)dialog.findViewById(R.id.user_name_details);
        TextView userEmailDetails=(TextView)dialog.findViewById(R.id.user_email_details);
        displayProfilePic(googleSignInAccount.getPhotoUrl(),userDisplayPic);
        userNameDetails.setText(mFirstName+" "+mLastName);
        userEmailDetails.setText(mEmail);
        dialog.show();
    }
    /*************************
     * Inflates the dialog to enable user select their Syncing preferences
     * ******************************/
    public void showPreferencesDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.preferences_radio);
        dialog.setTitle(R.string.syncpreferences);
       final Utilities util = new Utilities(this);
        ArrayList<String> preferences = new ArrayList<>();
        preferences.add(getResources().getString(R.string.wifiString));
        preferences.add(getResources().getString(R.string.mobile_data_wifi_string));
        //preferences.add(getResources().getString(R.string.anyString));
        final String getPref = util.getSyncSharedPreference(util.getCurrentIdentity());
        Log.i(TAG, "selected pref="+getPref);
        mRadioGroup = (RadioGroup) dialog.findViewById(R.id.radio_group);
        RadioButton rb = new RadioButton(this);
        rb.setText(getResources().getString(R.string.wifiString));
        RadioButton rb2 = new RadioButton(this);
        rb2.setText(getResources().getString(R.string.mobile_data_wifi_string));
        //RadioButton rb3 = new RadioButton(this);
        //rb3.setText(getResources().getString(R.string.anyString));
        switch(preferences.indexOf(getPref)){
            case 0:
                rb.setChecked(true);
                break;
            case 1:
                rb2.setChecked(true);
                break;
        }
        mRadioGroup.addView(rb2);
        mRadioGroup.addView(rb);
        //mRadioGroup.addView(rb3);
        dialog.show();
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int childCount = radioGroup.getChildCount();
                Log.i(TAG,"radioGroup="+childCount);
                for (int x = 0; x < childCount; x++) {
                    final RadioButton btn = (RadioButton) radioGroup.getChildAt(x);
                    if (btn.getId() == i) {
                        Log.i(TAG, "buttonn clicked"+i);
                        Log.e(TAG,"selected RadioButton->"+btn.getText().toString());
                        if(btn.getText().toString().equals(getString(R.string.mobile_data_wifi_string))){
                            Log.i(TAG,"mobile data selected!!");
                            mAlertDialogBuilder = new AlertDialog.Builder(BaseActivity.this);
                            mAlertDialogBuilder.setMessage(R.string.mobiledatacost);
                            mAlertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    btn.setChecked(true);
                                    new Utilities(getApplicationContext()).writeSyncPreferences(btn.getText().toString());
                                }
                            });
                            mAlertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Do Nothing
                                }
                            });
                            mAlertDialogBuilder.show();
                        }
                        else {
                            btn.setChecked(true);
                            new Utilities(getApplicationContext()).writeSyncPreferences(btn.getText().toString());
                        }
                        dialog.hide();
                    }
                }
            }
        });
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.i(TAG, "in BASE onSTOP");
        //Log.i("BASE","googleApiClient is connected?"+(mGoogleApiClient.isConnected()));
        //Log.i(TAG, "bye bye!! "+ new Utilities(this).getCurrentIdentity());

    }

    public void setLAYOUT(){
        //LAYOUT=findViewById(R.id.display_history_layout);//setupLayout;
        Log.i(TAG,"BaseActivity setLAYOUT!!");
    }

    /**********************************************************
     * fucntion to render the profile picture of the user.
     *  @param uri - Uri of the users' profile picture.
     **********************************************************/
    public void displayProfilePic(Uri uri, final ImageView anImageView){
        Glide.with(getApplicationContext())
                .load(uri)
                .asBitmap()
                .fitCenter()
                .into(new BitmapImageViewTarget(anImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);

                        circularBitmapDrawable.setCircular(true);
                        anImageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }
}
