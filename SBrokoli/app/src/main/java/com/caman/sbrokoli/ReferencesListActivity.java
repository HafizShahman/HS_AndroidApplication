package com.caman.sbrokoli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.ref.Reference;
import java.util.ArrayList;
public class ReferencesListActivity extends AppCompatActivity {
    /**
     * step 2
     */
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    RecyclerView rv_references;
    TextView tv_no_data;
    FloatingActionButton fb_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_references_list);
        /**
         * step 1
         */
        getSupportActionBar().setTitle("References List");

        /**
         * step 8
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * step 3
         */
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        /**
         * step 4
         */
        rv_references = findViewById(R.id.rv_references);
        tv_no_data = findViewById(R.id.tv_no_data);
        fb_add = findViewById(R.id.fb_add);

        /**
         * step 9
         */
        fb_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReferencesListActivity.this, ReferencesActivity.class));
            }
        });

        /**
         * step 5
         */
        getReferencesList();
    }
    /**
     * step 6
     */
    private void getReferencesList() {

        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("references").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    setAdapter(snapshot);
                } else {
                    rv_references.setVisibility(View.GONE);
                    tv_no_data.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * step 7
     */
    private void setAdapter(DataSnapshot dataSnapshot) {
        ArrayList<References> referencess = new ArrayList<>();
        referencess.clear();
        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
            References references = new References();
            references.setId(snapshot.getKey());
            references.setName(snapshot.child("name").getValue().toString().trim());
            references.setDesignation(snapshot.child("designation").getValue().toString().trim());
            references.setCompany(snapshot.child("company").getValue().toString().trim());
            references.setPhone(snapshot.child("phone").getValue().toString().trim());
            references.setEmail(snapshot.child("email").getValue().toString().trim());
            referencess.add(references);
        }
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(ReferencesListActivity.this);
        rv_references.setLayoutManager(linearLayoutManager);
        rv_references.setHasFixedSize(true);
        ReferencesAdapter adapter = new ReferencesAdapter(ReferencesListActivity.this, referencess, mDatabase, mAuth);
        rv_references.setAdapter(adapter);
        rv_references.setVisibility(View.VISIBLE);
        tv_no_data.setVisibility(View.GONE);
    }
}