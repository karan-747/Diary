package com.example.mydiary;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FloatingActionButton mcreatenotesfab;

    RecyclerView mrecyclerview;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;


    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirestoreRecyclerAdapter <firebasemodel,NoteViewHolder> noteAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        mcreatenotesfab=findViewById(R.id.createnotefab);
        getSupportActionBar().setTitle("All Entries");
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();


        mcreatenotesfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesActivity.this,CreateNotes.class));
            }
        });

        Query query= firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<firebasemodel> allusernotes =new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query,firebasemodel.class).build();



        noteAdapter =new FirestoreRecyclerAdapter <firebasemodel,NoteViewHolder>(allusernotes){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolderholder, int i, @NonNull firebasemodel firebasemodel) {
                ImageView popupbutton= noteViewHolderholder.itemView.findViewById(R.id.menupopupbutton);
                int colourcode=getRandomColor();
                noteViewHolderholder.mnote.setBackgroundColor(noteViewHolderholder.itemView.getResources().getColor(colourcode,null));


                noteViewHolderholder.notetitle.setText(firebasemodel.getTitle());
                noteViewHolderholder.noteContent.setText(firebasemodel.getContent());


                noteViewHolderholder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //open node detail

                        Intent intent=new Intent(v.getContext(),NodeDetails.class);
                        v.getContext().startActivity(intent);



                       // Toast.makeText(getApplicationContext(),"Node Will Open",Toast.LENGTH_SHORT).show();
                    }
                });
                popupbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu=new PopupMenu(v.getContext(),v );
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                Intent intent=new Intent(v.getContext(),EditNoteActivity.class);
                                v.getContext().startActivity(intent);
                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Toast.makeText(getApplicationContext(),"Note Deleted",Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        });
                    }
                });

            }


            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };
        mrecyclerview=findViewById(R.id.recyclerview);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(noteAdapter);






    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{
        private TextView notetitle;
        private TextView noteContent;
        private LinearLayout mnote;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle=itemView.findViewById(R.id.notetitle);
            noteContent=itemView.findViewById(R.id.notecontent);
            mnote=itemView.findViewById(R.id.note);

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                firebaseAuth.signOut();finish();
                startActivity(new Intent(NotesActivity.this,MainActivity.class));

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
        if(noteAdapter!=null){
            noteAdapter.stopListening();
        }

    }

    private int getRandomColor() {
        List<Integer>colourcode=new ArrayList<>( );
        colourcode.add(R.color.colour1);
        colourcode.add(R.color.colour2);
        colourcode.add(R.color.colour3);
        colourcode.add(R.color.colour4);
        colourcode.add(R.color.colour5);
        colourcode.add(R.color.colour6);
        colourcode.add(R.color.colour7);
        colourcode.add(R.color.colour8);
        colourcode.add(R.color.colour9);
        colourcode.add(R.color.colour10);
        colourcode.add(R.color.colour11);


        Random random= new Random();
        int number=random.nextInt(colourcode.size());
        return colourcode.get(number);

    }

}