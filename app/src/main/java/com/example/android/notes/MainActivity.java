package com.example.android.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.installations.internal.FidListener;

public class MainActivity extends AppCompatActivity {

    private EditText mLogInEmail;
    private EditText mLogInPassword;
    private RelativeLayout mLogIn;
    private RelativeLayout mGoToSignUp;
    private TextView mGoToForgotPassword;
    private ProgressBar mProgressBarOfMainActivity;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        mLogInEmail = (EditText) findViewById(R.id.loginemail);
        mLogInPassword = (EditText) findViewById(R.id.loginpassword);
        mLogIn = (RelativeLayout) findViewById(R.id.login);
        mGoToSignUp = (RelativeLayout) findViewById(R.id.gotosignup);
        mGoToForgotPassword = (TextView) findViewById(R.id.goToForgotPassword);
        mProgressBarOfMainActivity = (ProgressBar)findViewById(R.id.progressBarOfMainActivity);
        
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            finish();
            startActivity(new Intent(MainActivity.this, NotesActivity.class));
        }


        mGoToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });

        mGoToForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
            }
        });

        mLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mLogInEmail.getText().toString().trim();
                String password = mLogInPassword.getText().toString().trim();

                if(mail.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "All Fields are Required", Toast.LENGTH_SHORT).show();
                }
                else{
                    // Log In the user

                    mProgressBarOfMainActivity.setVisibility(View.VISIBLE);

                    firebaseAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                checkMailVerification();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Account Doesn't Exist", Toast.LENGTH_SHORT).show();
                                mProgressBarOfMainActivity.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void checkMailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser.isEmailVerified()){
            Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(MainActivity.this, NotesActivity.class));
        }
        else{
            mProgressBarOfMainActivity.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Mail Verification Required", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
}