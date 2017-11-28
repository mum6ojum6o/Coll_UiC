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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public Button GoogleSignIn,EmailSignIn;
    public static final String TAG ="WildbookDemo";
    final static  private  int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        GoogleSignIn = (Button)findViewById(R.id.sign_in_button);
        EmailSignIn = (Button) findViewById(R.id.email_Button);
        //GoogleSignIn.setOnClickListener(this);
        EmailSignIn.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener) this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        // [START initialize_auth]
           //mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        /*if(null !=mAuth.getCurrentUser()){
            updateUI(mAuth.getCurrentUser());
        }*/


    }
    @Override
    public void onStart(){
        super.onStart();
        // more understanding on OptionalPendingResult required....
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.i(TAG,"Result is Done!!");
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
                    Log.i(TAG,"Result Callback in else!!");
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            Log.i(TAG,"Result is Success!!");
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
           /* if(acct!=null)
                updateUI(true);*/  //UNCOMMENT in PRODUCTIONS
           firebaseAuthWithGoogle(acct);
            // mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
        } else {
            Log.i(TAG,"Result is NOT Success!!");
            //Nothing.
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                //updateUI(true);
                firebaseAuthWithGoogle(account); //Comment in Production....
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                Toast.makeText(getApplicationContext(),"Unable to Login. Please ensure you are connected to the Internet!",Toast.LENGTH_LONG).show();
                    //updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    public void signIn(){
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
                updateUI(acct.getEmail());

                //Below Commented Code will help authentication user in Firebase...
               /* mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithCredential:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Log.i(TAG, "Login Username:" + user.getEmail());
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }

                                // [START_EXCLUDE]
                                progressDialog.dismiss();
                                // [END_EXCLUDE]
                            }
                        });*/
           /* }
            else{
                Toast.makeText(getApplicationContext(),"Unable to Login. Please ensure you are connected to the Internet!",Toast.LENGTH_LONG).show();
            }*/

        }

    /***********************
     * This method would be deprecated.
     * @param user
     */
    private void updateUI(String user) {
        progressDialog.dismiss();
        if (user != null) {

           Intent i = new Intent(this,MainActivity.class);

            startActivity(i);
            //finishAndRemoveTask();
            finish();
        } else {

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }
    private void updateUI(boolean isLoggedIn){
        if (isLoggedIn){
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
            //finishAndRemoveTask();
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_LONG);
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.email_Button:
                break;
        }
    }

    }

