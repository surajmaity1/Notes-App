package com.example.android.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteDetails extends AppCompatActivity {

    private TextView mTitleOfNoteDetail;
    private TextView mContentOfNoteDetail;
    private FloatingActionButton mGoToEditNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        mTitleOfNoteDetail = (TextView) findViewById(R.id.titleOfNoteDetail);
        mContentOfNoteDetail = (TextView) findViewById(R.id.contentOfNoteDetail);
        mGoToEditNote = (FloatingActionButton) findViewById(R.id.goToEditNote);
        Toolbar toolbar = findViewById(R.id.toolBarOfNoteDetail);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent data = getIntent();

        mGoToEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditNoteActivity.class);
                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("content", data.getStringExtra("content"));
                intent.putExtra("noteId", data.getStringExtra("noteId"));
                v.getContext().startActivity(intent);
            }
        });

        mContentOfNoteDetail.setText(data.getStringExtra("content"));
        mTitleOfNoteDetail.setText(data.getStringExtra("title"));
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}