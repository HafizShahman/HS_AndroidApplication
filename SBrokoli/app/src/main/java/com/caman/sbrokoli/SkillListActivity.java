package com.caman.sbrokoli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
public class SkillListActivity extends AppCompatActivity {
    /**
     * step 2
     */
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    RecyclerView rv_skill;
    TextView tv_no_data;
    FloatingActionButton fb_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_list);
        /**
         * step 1
         */
        getSupportActionBar().setTitle("Skill List");
        /**
         * step 12
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
        rv_skill = findViewById(R.id.rv_skill);
        tv_no_data = findViewById(R.id.tv_no_data);
        fb_add = findViewById(R.id.fb_add);

        /**
         * step 9
         */
        fb_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view){
                startActivity(new Intent(SkillListActivity.this, SkillActivity.class));
            }
        });
        /**
         * step 5
         */
        getSkillList();
    }
    /**
     * step 6
     */
    private void getSkillList() {

        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("skill").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    setAdapter(snapshot);
                } else {
                    rv_skill.setVisibility(View.GONE);
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
        ArrayList<Skill> skills = new ArrayList<>();
        skills.clear();
        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Skill skill = new Skill();
            skill.setId(snapshot.getKey());
            skill.setSkill(snapshot.child("skill").getValue().toString().trim());
            skill.setLevel(Integer.parseInt(snapshot.child("level").getValue().toString()));
            skills.add(skill);
        }
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(SkillListActivity.this);
        rv_skill.setLayoutManager(linearLayoutManager);
        rv_skill.setHasFixedSize(true);
        SkillAdapter adapter = new SkillAdapter(SkillListActivity.this, skills, mDatabase,
                mAuth);
        rv_skill.setAdapter(adapter);
        rv_skill.setVisibility(View.VISIBLE);
        tv_no_data.setVisibility(View.GONE);
    }
}