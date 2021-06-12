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
import java.util.ArrayList;
public class EducationListActivity extends AppCompatActivity {
    /**
     * step 2
     */
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    RecyclerView rv_education;
    TextView tv_no_data;
    FloatingActionButton fb_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_list);
        /**
         * step 1
         */
        getSupportActionBar().setTitle("Education List");
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
        rv_education = findViewById(R.id.rv_education);
        tv_no_data = findViewById(R.id.tv_no_data);
        fb_add = findViewById(R.id.fb_add);

        /**
         * step 9
         */
        fb_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view){
                startActivity(new Intent(EducationListActivity.this, EducationActivity.class));
            }
        });

        /**
         * step 5
         */
        getEducationList();
    }
    /**
     * step 6
     */
    private void getEducationList() {

        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("education").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    setAdapter(snapshot);
                } else {
                    rv_education.setVisibility(View.GONE);
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
        ArrayList<Education> educations = new ArrayList<>();
        educations.clear();

        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Education education = new Education();
            education.setId(snapshot.getKey());
            education.setSchool(snapshot.child("school").getValue().toString().trim());
            education.setLevel(snapshot.child("level").getValue().toString().trim());
            education.setStart_date(snapshot.child("start_date").getValue().toString().trim());
            education.setEnd_date(snapshot.child("end_date").getValue().toString().trim());
            educations.add(education);
        }
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(EducationListActivity.this);
        rv_education.setLayoutManager(linearLayoutManager);
        rv_education.setHasFixedSize(true);
        EducationAdapter adapter = new EducationAdapter(EducationListActivity.this,
                educations, mDatabase, mAuth);
        rv_education.setAdapter(adapter);
        rv_education.setVisibility(View.VISIBLE);
        tv_no_data.setVisibility(View.GONE);
    }
}
