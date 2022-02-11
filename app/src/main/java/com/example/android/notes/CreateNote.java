package com.example.android.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateNote extends AppCompatActivity {

    private EditText mCreateTitleOfNote;
    private EditText mCreateContentOfNote;
    private FloatingActionButton mSaveNote;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private Toolbar toolbar;
    private ProgressBar mProgressBarOfCreateNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);


        mSaveNote = (FloatingActionButton) findViewById(R.id.saveNote);
        mCreateContentOfNote = (EditText) findViewById(R.id.createContentOfNote);
        mCreateTitleOfNote = (EditText) findViewById(R.id.createTitleOfNote);
        mProgressBarOfCreateNote = (ProgressBar) findViewById(R.id.progressBarOfCreateNote);

        toolbar = (Toolbar) findViewById(R.id.toolBarOfCreateNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        mSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mCreateTitleOfNote.getText().toString();
                String content = mCreateContentOfNote.getText().toString();

                if (title.isEmpty() || content.isEmpty()){
                    Toast.makeText(getApplicationContext(),
                            "Both Fields are Required", Toast.LENGTH_SHORT).show();

                }
                else {

                    mProgressBarOfCreateNote.setVisibility(View.VISIBLE);
                    DocumentReference documentReference = firebaseFirestore
                            .collection("notes")
                            .document(firebaseUser.getUid())
                            .collection("mynotes")
                            .document();

                    Map<String, Object> note =  new HashMap<>();
                    note.put("title", title);
                    note.put("content", content);

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(),
                                    "Note Created Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreateNote.this, NotesActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    "Failed To Create Note", Toast.LENGTH_SHORT).show();

                            mProgressBarOfCreateNote.setVisibility(View.INVISIBLE);

                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}