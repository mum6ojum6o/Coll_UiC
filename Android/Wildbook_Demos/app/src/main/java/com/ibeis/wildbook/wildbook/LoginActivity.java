package com.ibeis.wildbook.wildbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
/*import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;*/

import static com.google.android.gms.auth.api.signin.GoogleSignIn.getLastSignedInAccount;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG ="WildbookDemo";
    final static  private  int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"in OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               // .requestIdToken(getString(R.string.default_web_client_id))  /*disabled intentionally*/
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener) this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        findViewById(R.id.sign_in_button).setOnClickListener(this);

    }
    @Override
    public void onStart(){
        super.onStart();
        GoogleSignInAccount lastSignedInAccount =  getLastSignedInAccount(this);
        if(lastSignedInAccount!=null){
            firebaseAuthWithGoogle(lastSignedInAccount);
        }
        else {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);


            if (opr.isDone()) {
               // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                // and the GoogleSignInResult will be available instantly.
                Log.d(TAG, "Got cached sign-in");
                GoogleSignInResult result = opr.get();
                //mAuth = FirebaseAuth.getInstance();
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

                        Log.i(TAG, "Result Callback in else!!");
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        }
    }

    /**********
     * Handles silent signIn results of the GoogleSignInApi
     * @param result
     */
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            Log.i(TAG,"Successful Authentication!!");
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.i(TAG,"LOGGED IN AS:"+acct.getEmail());
            redirect(acct.getEmail());
        } else {
            Log.i(TAG,"Result is NOT Success!!");
        }
    }

    /*************
     * Handles results obtained from launching the intent from GoogleSignInApi.getSignInIntent
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.i(TAG,"in onActivityResult:"+ result.getSignInAccount().getEmail());
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                redirect(account.getEmail());
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                if(result==null)
                    Toast.makeText(getApplicationContext(),"Unable to LoginActivity. Please ensure your username and password are correct!",Toast.LENGTH_LONG).show();
            }
        }
    }

    /*************************************
     *method to prompt user to select one of google accounts added on the device
     ************************************/

    private void signIn(){
        Log.i(TAG,"Signing In!!");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
        // [START auth_with_google]
        private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

                Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
                // [START_EXCLUDE silent]
                progressDialog.show();
                // [END_EXCLUDE]
                //AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                redirect(acct.getEmail());
        }

    /***********************
     *Reidrects the user to the MainActivity
     * @param user
     ***************************************/
    private void redirect(String user) {
        progressDialog.dismiss();
        if (user != null) {

           Intent i = new Intent(this,MainActivity.class);
            new Utilities(this).setCurrentIdentity(user);
            startActivity(i);

            //finishAndRemoveTask();
            finish();
        } else {

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }
/***************************************
* Handles errors when Google APIs are not available.
 *******************************/
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    /***********************************************
     * Button click Callback method
     * @param view
     *******************************************/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    }

