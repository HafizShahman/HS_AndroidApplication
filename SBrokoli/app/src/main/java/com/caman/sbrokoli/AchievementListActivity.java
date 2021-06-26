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
public class AchievementListActivity extends AppCompatActivity {
    /**
     * step 2
     */
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    RecyclerView rv_achievement;
    TextView tv_no_data;
    FloatingActionButton fb_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_list);
        /**
         * step 1
         */
        getSupportActionBar().setTitle("Achievement List");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * step 3
         */
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        /**
         * step 4
         */
        rv_achievement = findViewById(R.id.rv_achievement);
        tv_no_data = findViewById(R.id.tv_no_data);
        fb_add = findViewById(R.id.fb_add);

        fb_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AchievementListActivity.this, AchievementActivity.class));
            }
        });

        /**
         * step 5
         */
        getAchievementList();
    }
    /**
     * step 6
     */
    private void getAchievementList() {

        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("achievement").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    setAdapter(snapshot);
                } else {
                    rv_achievement.setVisibility(View.GONE);
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
        ArrayList<Achievement> achievements = new ArrayList<>();
        achievements.clear();
        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Achievement achievement = new Achievement();
            achievement.setId(snapshot.getKey());
            achievement.setTitle(snapshot.child("title").getValue().toString().trim());
            achievement.setLevel(snapshot.child("level").getValue().toString().trim());
            achievement.setYear(snapshot.child("year").getValue().toString().trim());
            achievement.setType(snapshot.child("type").getValue().toString().trim());
            achievements.add(achievement);
        }
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(AchievementListActivity.this);
        rv_achievement.setLayoutManager(linearLayoutManager);
        rv_achievement.setHasFixedSize(true);
        AchievementAdapter adapter = new AchievementAdapter(AchievementListActivity.this, achievements, mDatabase, mAuth);
        rv_achievement.setAdapter(adapter);
        rv_achievement.setVisibility(View.VISIBLE);
        tv_no_data.setVisibility(View.GONE);
    }
}