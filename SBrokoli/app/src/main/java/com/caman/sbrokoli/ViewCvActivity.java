package com.caman.sbrokoli;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
public class ViewCvActivity extends AppCompatActivity {
    /**
     * step 2
     */
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    StorageReference storageReference;
    FirebaseStorage storage;
    CardView cv_profile, cv_education, cv_skill, cv_achievement, cv_references;
    TextView tv_profile_no_data, tv_education_no_data, tv_skill_no_data, tv_achievement_no_data, tv_references_no_data;
    LinearLayout ll_profile_data, ll_education_data, ll_skill_data, ll_achievement_data, ll_references_data;
    TextView tv_edit_profile, tv_edit_education, tv_edit_skill, tv_edit_achievement, tv_edit_references;
    ImageView iv_profile;
    TextView tv_full_name, tv_email, tv_address;
    RecyclerView rv_education, rv_skill, rv_achievement, rv_references;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cv);
        /**
         * step 1
         */
        getSupportActionBar().setTitle("My CV");
        /**
         * step 7
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /**
         * step 3
         */
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        /**
         * step 4
         */
        cv_profile = findViewById(R.id.cv_profile);
        cv_education = findViewById(R.id.cv_education);
        cv_skill = findViewById(R.id.cv_skill);
        cv_achievement = findViewById(R.id.cv_achievement);
        cv_references = findViewById(R.id.cv_references);

        tv_profile_no_data = findViewById(R.id.tv_profile_no_data);
        tv_education_no_data = findViewById(R.id.tv_education_no_data);
        tv_skill_no_data = findViewById(R.id.tv_skill_no_data);
        tv_achievement_no_data = findViewById(R.id.tv_achievement_no_data);
        tv_references_no_data = findViewById(R.id.tv_references_no_data);

        ll_profile_data = findViewById(R.id.ll_profile_data);
        ll_education_data = findViewById(R.id.ll_education_data);
        ll_skill_data = findViewById(R.id.ll_skill_data);
        ll_achievement_data = findViewById(R.id.ll_achievement_data);
        ll_references_data = findViewById(R.id.ll_references_data);

        tv_edit_profile = findViewById(R.id.tv_edit_profile);
        tv_edit_education = findViewById(R.id.tv_edit_education);
        tv_edit_skill = findViewById(R.id.tv_edit_skill);
        tv_edit_achievement = findViewById(R.id.tv_edit_achievement);
        tv_edit_references = findViewById(R.id.tv_edit_references);

        iv_profile = findViewById(R.id.iv_profile);
        tv_full_name = findViewById(R.id.tv_full_name);
        tv_email = findViewById(R.id.tv_email);
        tv_address = findViewById(R.id.tv_address);

        rv_education = findViewById(R.id.rv_education);
        rv_skill = findViewById(R.id.rv_skill);
        rv_achievement = findViewById(R.id.rv_achievement);
        rv_references = findViewById(R.id.rv_references);
        /**
         * step 5
         */
        getCvData();
    }
    /**
     * step 6
     */
    private void getCvData() {
        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean profile = snapshot.child("profile").exists();
                    boolean education = snapshot.child("education").exists();
                    boolean skill = snapshot.child("skill").exists();
                    boolean achievement = snapshot.child("achievement").exists();
                    boolean references = snapshot.child("references").exists();
                    if (profile) {
                        /**
                         * step 8
                         */
                        //exists
                        ll_profile_data.setVisibility(View.VISIBLE);
                        tv_profile_no_data.setVisibility(View.GONE);
                        setupProfile(snapshot.child("profile"));
                        tv_edit_profile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(ViewCvActivity.this, ProfileActivity.class));
                            }
                        });
                    } else {
                        //not exists
                        ll_profile_data.setVisibility(View.GONE);
                        tv_profile_no_data.setVisibility(View.VISIBLE);
                        cv_profile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(ViewCvActivity.this, ProfileActivity.class));
                            }
                        });
                    }
                    if (education) {
                        /**
                         * step 10
                         */
                        //exists
                        ll_education_data.setVisibility(View.VISIBLE);
                        tv_education_no_data.setVisibility(View.GONE);
                        setupEducation(snapshot.child("education"));
                        tv_edit_education.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(ViewCvActivity.this, EducationListActivity.class));
                            }
                        });
                    } else {
                        //not exists
                        ll_education_data.setVisibility(View.GONE);
                        tv_education_no_data.setVisibility(View.VISIBLE);
                        cv_education.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(ViewCvActivity.this,
                                        EducationListActivity.class));
                            }
                        });
                    }
                    if (skill) {
                        /**
                         * step 12
                         */
                        //exists
                        ll_skill_data.setVisibility(View.VISIBLE);
                        tv_skill_no_data.setVisibility(View.GONE);
                        setupSkill(snapshot.child("skill"));
                        tv_edit_skill.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(ViewCvActivity.this,
                                        SkillListActivity.class));
                            }
                        });
                    } else {
                        //not exists
                        rv_skill.setVisibility(View.GONE);
                        tv_skill_no_data.setVisibility(View.VISIBLE);
                        cv_skill.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(ViewCvActivity.this,
                                        SkillListActivity.class));
                            }
                        });
                    }
                    if (achievement){

                        ll_achievement_data.setVisibility(View.VISIBLE);
                        tv_achievement_no_data.setVisibility(View.GONE);
                        setupAchievement(snapshot.child("achievement"));
                        tv_edit_achievement.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(ViewCvActivity.this, AchievementListActivity.class));
                            }
                        });
                    } else {

                        rv_achievement.setVisibility(View.GONE);
                        tv_achievement_no_data.setVisibility(View.VISIBLE);
                        cv_achievement.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(ViewCvActivity.this, AchievementListActivity.class));
                            }
                        });
                    }
                    if (references){

                        ll_references_data.setVisibility(View.VISIBLE);
                        tv_references_no_data.setVisibility(View.GONE);
                        setupReferences(snapshot.child("references"));
                        tv_edit_references.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(ViewCvActivity.this, ReferencesListActivity.class));
                            }
                        });
                    } else {

                        rv_references.setVisibility(View.GONE);
                        tv_references_no_data.setVisibility(View.VISIBLE);
                        cv_references.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(ViewCvActivity.this, ReferencesListActivity.class));
                            }
                        });
                    }
                } else {
                    // no data at all
// maybe new registered user
                    ll_profile_data.setVisibility(View.GONE);
                    tv_profile_no_data.setVisibility(View.VISIBLE);
                    cv_profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(ViewCvActivity.this,
                                    ProfileActivity.class));
                        }
                    });
                    rv_education.setVisibility(View.GONE);
                    tv_education_no_data.setVisibility(View.VISIBLE);
                    cv_education.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(ViewCvActivity.this,
                                    EducationListActivity.class));
                        }
                    });
                    rv_skill.setVisibility(View.GONE);
                    tv_skill_no_data.setVisibility(View.VISIBLE);
                    cv_skill.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(ViewCvActivity.this,
                                    SkillListActivity.class));
                        }
                    });
                    rv_achievement.setVisibility(View.GONE);
                    tv_achievement_no_data.setVisibility(View.VISIBLE);
                    cv_achievement.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(ViewCvActivity.this, AchievementListActivity.class));
                        }
                    });
                    rv_references.setVisibility(View.GONE);
                    tv_references_no_data.setVisibility(View.VISIBLE);
                    cv_references.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(ViewCvActivity.this, ReferencesListActivity.class));
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    /**
     * step 9
     */
    private void setupProfile(DataSnapshot snapshot) {
        if (snapshot.child("image_url").exists()) {
            String image_url = snapshot.child("image_url").getValue().toString().trim();
            if (!image_url.equals("")) {
                Glide.with(getApplicationContext())
                        .load(image_url)
                        .circleCrop()
                        .into(iv_profile);
            }
        }
        String title = snapshot.child("title").getValue().toString().trim();
        String first_name = snapshot.child("first_name").getValue().toString().trim();
        String last_name = snapshot.child("last_name").getValue().toString().trim();
        String email = snapshot.child("email").getValue().toString().trim();
        String address = snapshot.child("address").getValue().toString().trim();
        String city = snapshot.child("city").getValue().toString().trim();
        String postcode = snapshot.child("postcode").getValue().toString().trim();
        String state = snapshot.child("state").getValue().toString().trim();
        tv_full_name.setText(title +" " +first_name +" " +last_name);
        tv_email.setText(email);
        tv_address.setText(address +"\n" +postcode +" " +city +"\n" +state);
    }
    /**
     * step 11
     */
    private void setupEducation(DataSnapshot dataSnapshot) {
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
                LinearLayoutManager(ViewCvActivity.this);
        rv_education.setLayoutManager(linearLayoutManager);
        rv_education.setHasFixedSize(true);
        EducationAdapter adapter = new EducationAdapter(ViewCvActivity.this, educations,
                mDatabase, mAuth);
        rv_education.setAdapter(adapter);
    }
    /**
     * step 13
     */
    private void setupSkill(DataSnapshot dataSnapshot) {
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
                LinearLayoutManager(ViewCvActivity.this);
        rv_skill.setLayoutManager(linearLayoutManager);
        rv_skill.setHasFixedSize(true);
        SkillAdapter adapter = new SkillAdapter(ViewCvActivity.this, skills, mDatabase,
                mAuth);
        rv_skill.setAdapter(adapter);
    }

    private void setupAchievement(DataSnapshot dataSnapshot){
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewCvActivity.this);
        rv_achievement.setLayoutManager(linearLayoutManager);
        rv_achievement.setHasFixedSize(true);
        AchievementAdapter adapter = new AchievementAdapter(ViewCvActivity.this, achievements, mDatabase, mAuth);
        rv_achievement.setAdapter(adapter);
    }

    private void setupReferences(DataSnapshot dataSnapshot){
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewCvActivity.this);
        rv_references.setLayoutManager(linearLayoutManager);
        rv_references.setHasFixedSize(true);
        ReferencesAdapter adapter = new ReferencesAdapter(ViewCvActivity.this, referencess, mDatabase, mAuth);
        rv_references.setAdapter(adapter);
    }
}