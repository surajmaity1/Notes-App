package com.example.android.notes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesActivity extends AppCompatActivity {

    private FloatingActionButton mCreateNotesFab;

    private FirebaseAuth firebaseAuth;

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    private FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        mCreateNotesFab = (FloatingActionButton) findViewById(R.id.createNoteFab);
        firebaseAuth = FirebaseAuth.getInstance();


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getSupportActionBar().setTitle("All Notes");

        mCreateNotesFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesActivity.this, CreateNote.class));
            }
        });


        // load data from FireStore for Specific User
        Query query = firebaseFirestore.collection("notes")
                        .document(firebaseUser.getUid())
                        .collection("mynotes")
                        .orderBy("title", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<FirebaseModel> allUserNotes = new FirestoreRecyclerOptions
                                                                .Builder<FirebaseModel>()
                                                                .setQuery(query, FirebaseModel.class)
                                                                .build();
        noteAdapter = new FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(allUserNotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull FirebaseModel firebaseModel) {

                ImageView popUpButton = noteViewHolder.itemView.findViewById(R.id.menuPopButton);

                int colourCode = getRandomColor();
                noteViewHolder.mNote.
                        setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colourCode, null));

                noteViewHolder.noteTitle.setText(firebaseModel.getTitle());
                noteViewHolder.noteContent.setText(firebaseModel.getContent());

                String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // have to open note detail activity

                        Intent intent = new Intent(v.getContext(), NoteDetails.class);

                        intent.putExtra("title", firebaseModel.getTitle());
                        intent.putExtra("content", firebaseModel.getContent());
                        intent.putExtra("noteId", docId);


                        v.getContext().startActivity(intent);

                        //Toast.makeText(getApplicationContext(), "This is Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                popUpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                        popupMenu.setGravity(Gravity.END);


                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent = new Intent(v.getContext(), EditNoteActivity.class);
                                intent.putExtra("title", firebaseModel.getTitle());
                                intent.putExtra("content", firebaseModel.getContent());
                                intent.putExtra("noteId", docId);
                                v.getContext().startActivity(intent);
                                return false;
                            }
                        });


                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference documentReference = firebaseFirestore
                                        .collection("notes")
                                        .document(firebaseUser.getUid())
                                        .collection("mynotes")
                                        .document(docId);

                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(v.getContext(), "This note is deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(v.getContext(), "Failed to delete Note", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                return false;
                            }
                        });

                        popupMenu.show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.notes_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setAdapter(noteAdapter);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{

        private TextView noteTitle;
        private TextView noteContent;
        private LinearLayout mNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteContent = itemView.findViewById(R.id.noteContent);
            mNote = itemView.findViewById(R.id.note);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logOut:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(NotesActivity.this, MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter != null){
            noteAdapter.stopListening();
        }

    }
    private int getRandomColor(){
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.gray);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.skyBlue);
        colorCode.add(R.color.color1);
        colorCode.add(R.color.color2);
        colorCode.add(R.color.color3);
        colorCode.add(R.color.color4);
        colorCode.add(R.color.color5);
        colorCode.add(R.color.green);

        Random random = new Random();
        int number = random.nextInt(colorCode.size());
        return colorCode.get(number);
    }
}