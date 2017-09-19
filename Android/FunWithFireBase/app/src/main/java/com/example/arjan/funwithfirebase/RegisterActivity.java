package com.example.arjan.funwithfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    //private String email,password;
    private EditText EdTxtEmail,EdTxtPassword;
    private Button BtnSignUp;
    private ProgressDialog progressDialog;
    private TextView TxtVwSignIn;
private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();//initialize Firebase Auth
        if (firebaseAuth.getCurrentUser()   !=null){//if user already logged in.x
            finish();
        }
        EdTxtEmail = (EditText)findViewById(R.id.emailEdText);
        EdTxtPassword = (EditText)findViewById(R.id.passEdText);
        TxtVwSignIn = (TextView)findViewById(R.id.textViewSignin);
        BtnSignUp = (Button)findViewById(R.id.SignUpBtn);
        progressDialog = new ProgressDialog(this);
        BtnSignUp.setOnClickListener(this);
        TxtVwSignIn.setOnClickListener(this);
    }

    public void onClick(View view){
        if (view== BtnSignUp){
            registerUser();
        }
        else if (view == TxtVwSignIn){
            startActivity(new Intent(this,OnlyActivity.class));
        }
    }
    protected void registerUser(){
        String email = EdTxtEmail.getText().toString().trim();
        String password = EdTxtPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter a email!", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter a password!", Toast.LENGTH_LONG).show();
            return;
        }
progressDialog.setMessage("Registering User");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(),OnlyActivity.class));
                        }
                        else{
                            Toast.makeText(RegisterActivity.this,"Registration Error",Toast.LENGTH_LONG).show();

                        }
                        progressDialog.dismiss();
                    }
                });

    }
}
