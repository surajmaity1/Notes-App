package com.example.android.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText mForgotPassword;
    private Button mPasswordRecoverButton;
    private TextView mGoBackToLogIn;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //hide action bar
        getSupportActionBar().hide();

        mForgotPassword = (EditText) findViewById(R.id.forgotpassword);
        mPasswordRecoverButton = (Button) findViewById(R.id.passwordrecoverbutton);
        mGoBackToLogIn = (TextView) findViewById(R.id.gobacktologin);

        firebaseAuth = FirebaseAuth.getInstance();

        mGoBackToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPassword.this, MainActivity.class));

            }
        });

        mPasswordRecoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mForgotPassword.getText().toString();
                if(mail.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter Your Email First", Toast.LENGTH_SHORT).show();
                }
                else {
                    // we have to send password recover mail
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),
                                        "Mail Send, You Now Can Recover Your Password Using Same Mail" , Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                            }
                            else {
                                Toast.makeText(getApplicationContext(),
                                        "Invalid Email Or Account Not Exist" , Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
            }
        });
    }
}