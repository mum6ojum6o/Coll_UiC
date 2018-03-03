package com.mum6ojumbo.locateme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Authentication extends AppCompatActivity implements View.OnClickListener{
    private static final int RC_SIGN_IN = 9072;
    private static final String TAG = "AutheticateAct";
    private GoogleSignInOptions mGso;
private static GoogleSignInClient  mGoogleSignInClient;
private GoogleSignInAccount mGoogleSignInAccount;
private FirebaseAuth mFirebaseAuth;
private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_authentication);
        mGso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,mGso);
        findViewById(R.id.sign_in_button).setOnClickListener((View.OnClickListener) this);
    }
    @Override
    protected void onStart(){
        super.onStart();

        mCurrentUser = mFirebaseAuth.getCurrentUser();
        if(mCurrentUser!=null)
            redirect();
    }
    private void redirect(){
        startActivity(new Intent(Authentication.this,MainActivity.class));
    }
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode){
            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);

                }
                break;
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        Log.d(TAG,"firebaseAuthWithGoogle:"+acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG,"SignInSuccess");
                            redirect();
                        }else{
                            Log.w(TAG,"signInWithCredential:failure",task.getException());
                            Toast.makeText(getApplicationContext(),"Authentication Failed!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        signIn();
    }
    public static GoogleSignInClient getGoogleSignInClient(){

       if(mGoogleSignInClient==null){
           GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                   .requestIdToken(Resources.getSystem().getString(R.string.default_web_client_id))
                   .requestEmail()
                   .build();
           //return GoogleSignInClient.getClient(MainActivity.class,gso);
           mGoogleSignInClient=  GoogleSignIn.getClient(new MainActivity(),gso);
           return mGoogleSignInClient;
       }else{
           return mGoogleSignInClient;
        }
    }
}
