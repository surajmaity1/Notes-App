package com.example.android.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditNoteActivity extends AppCompatActivity {

    private Intent data;
    private EditText mEditTitleOfNote;
    private EditText mEditContentOfNote;
    private FloatingActionButton mSaveEditNote;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        mEditTitleOfNote = (EditText) findViewById(R.id.editTitleOfNote);
        mEditContentOfNote = (EditText) findViewById(R.id.editContentOfNote);
        mSaveEditNote = (FloatingActionButton) findViewById(R.id.saveEditNote);
        data = getIntent();


        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        Toolbar toolbar = findViewById(R.id.toolBarOfEditNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSaveEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newTitle = mEditTitleOfNote.getText().toString();
                String newContent = mEditContentOfNote.getText().toString();

                if(newTitle.isEmpty() || newContent.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Both Fields are Required", Toast.LENGTH_SHORT).show();
                }
                else{
                    DocumentReference documentReference = firebaseFirestore
                            .collection("notes")
                            .document(firebaseUser.getUid())
                            .collection("mynotes")
                            .document(data.getStringExtra("noteId"));

                    Map<String, Object> note = new HashMap<>();
                    note.put("title", newTitle);
                    note.put("content", newContent);
                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Note is Updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditNoteActivity.this, NotesActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to Update Note", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //Toast.makeText(getApplicationContext(), "Saved Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });


        // Need to set and Display data
        String noteTitle = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");

        mEditContentOfNote.setText(noteContent);
        mEditTitleOfNote.setText(noteTitle);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}