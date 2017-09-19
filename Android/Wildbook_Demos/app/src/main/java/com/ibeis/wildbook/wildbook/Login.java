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
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

    }
    public void onStart(){
      super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
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
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    public void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
        // [START auth_with_google]
        private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
            Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
            // [START_EXCLUDE silent]
           progressDialog.show();
            // [END_EXCLUDE]

            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
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
                    });

        }
    private void updateUI(FirebaseUser user) {
        progressDialog.dismiss();
        if (user != null) {
           Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
        } else {

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
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
