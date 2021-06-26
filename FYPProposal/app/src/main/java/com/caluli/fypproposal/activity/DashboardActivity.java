package com.caluli.fypproposal.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lailyyana.proposalapp.R;
import com.lailyyana.proposalapp.util.ConstantVar;
import com.lailyyana.proposalapp.util.TinyDB;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    //user details
    TextView tv_name;
    TextView tv_matric;
    TextView tv_logout;

    //circle progress
    ImageView circle1;
    ImageView circle2;
    ImageView circle3;

    //linear progress
    View view1;
    View view2;
    View view3;
    View view4;

    //card view
    CardView cv_member_registration;
    CardView cv_proposal;
    CardView cv_status;

    //text view
    TextView tv_desc1;
    TextView tv_desc2;
    TextView tv_desc3;
    TextView tv_btn1;
    TextView tv_btn2;
    TextView tv_btn3;
    TextView tv_group_no;
    TextView tv_group_member;
    TextView tv_proposal_title;
    TextView tv_status;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //tiny db
    TinyDB tinyDB;

    String group_id = "";
    String group_ref = "";

    String proposal_id = "";
    String proposal_title = "";
    String prposal_status = "";

    StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        tinyDB = new TinyDB(this);

        setToolbar();
        createReference();

        //init ui
        setProgressTrack(1);

        getUserMatricNo();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dashboard");
    }

    private void createReference() {
        tv_name = findViewById(R.id.tv_name);
        tv_matric = findViewById(R.id.tv_matric);
        tv_logout = findViewById(R.id.tv_logout);
        circle1 = findViewById(R.id.circlel);
        circle2 = findViewById(R.id.circle2);
        circle3 = findViewById(R.id.circle3);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        cv_member_registration = findViewById(R.id.cv_member_registration);
        cv_proposal = findViewById(R.id.cv_proposal);
        cv_status = findViewById(R.id.cv_status);
        tv_desc1 = findViewById(R.id.tv_desc1);
        tv_desc2 = findViewById(R.id.tv_desc2);
        tv_desc3 = findViewById(R.id.tv_desc3);
        tv_btn1 = findViewById(R.id.tv_btn1);
        tv_btn2 = findViewById(R.id.tv_btn2);
        tv_btn3 = findViewById(R.id.tv_btn3);
        tv_group_no = findViewById(R.id.tv_group_no);
        tv_group_member = findViewById(R.id.tv_group_member);
        tv_proposal_title = findViewById(R.id.tv_proposal_title);
        tv_status = findViewById(R.id.tv_status);

    }

    private void getUserMatricNo() {
        mDatabase.child(ConstantVar.TABLE_USER).child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    String user_name = snapshot.child(ConstantVar.COLUMN_USER_NAME).getValue().toString();
                    String user_matric = snapshot.child(ConstantVar.COLUMN_USER_MATRIC).getValue().toString();

                    tv_name.setText(user_name);
                    tv_matric.setText(user_matric);

                    tv_logout.setOnClickListener(DashboardActivity.this);

                    tinyDB.putString(ConstantVar.TNB_USER_MATRIC, user_matric);
                    checkGroupRegistrationData();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkGroupRegistrationData() {
        mDatabase.child(ConstantVar.TABLE_GROUP).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        if (snapshot1.child(ConstantVar.COLUMN_GROUP_USERS).hasChild(tinyDB.getString(ConstantVar.TNB_USER_MATRIC))) {
                            // already registered
                            group_id = snapshot1.getKey();
                            group_ref = snapshot1.child(ConstantVar.COLUMN_GROUP_USERS).getValue().toString();

                            tinyDB.putString(ConstantVar.TNB_GROUP_ID, group_id);

                            //get member data
                            sb = new StringBuilder();

                            int i = 2;
                            for (DataSnapshot snapshot2 : snapshot1.child(ConstantVar.COLUMN_GROUP_USERS).getChildren()) {
                                String group_user_matric = snapshot2.getKey();
                                Boolean group_user_is_leader = (Boolean) snapshot2.getValue();

                                if (group_user_is_leader) {
                                    getUserData(1, group_user_matric);
                                } else {
                                    getUserData(i, group_user_matric);
                                    i++;
                                }
                            }

                            //set progress track
                            setProgressTrack(2);

                            //check for proposal
                            checkProposalData(group_id, group_ref);
                            break;
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserData(int user_position, String user_matric) {
        mDatabase.child(ConstantVar.TABLE_USER).orderByChild(ConstantVar.COLUMN_USER_MATRIC).equalTo(user_matric).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String user_name = "";

                        if (snapshot1.hasChild(ConstantVar.COLUMN_USER_NAME)) {
                            user_name = snapshot1.child(ConstantVar.COLUMN_USER_NAME).getValue().toString();
                        }

                        if (user_position == 1) {
                            sb.insert(0, "Leader: " + user_name);

                        } else {
                            sb.append("\nMember #" + (user_position - 1) + ":" + user_name);
                        }
                    }

                    tv_group_member.setText(sb);
                    tv_group_member.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkProposalData(String group_id, String group_ref) {
        mDatabase.child(ConstantVar.TABLE_PROPOSAL).orderByChild(ConstantVar.COLUMN_PROPOSAL_GROUP).equalTo(group_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //has submitted proposal

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        proposal_id = snapshot1.getKey();
                        proposal_title = snapshot1.child(ConstantVar.COLUMN_PROPOSAL_TITLE).getValue().toString();
                        prposal_status = snapshot1.child(ConstantVar.COLUMN_PROPOSAL_STATUS).getValue().toString();

                        tinyDB.putString(ConstantVar.TNB_PROPOSAL_ID, proposal_id);

                        if (!prposal_status.equals(ConstantVar.PROPOSAL_STATUS_APPROVED)) {
                            //set program track
                            setProgressTrack(3);
                        }
                        break;

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setProgressTrack(int position) {

        switch (position) {
            case 1:
                cv_member_registration.setAlpha(1.0f);
                cv_proposal.setAlpha(0.8f);
                cv_status.setAlpha(0.8f);

                tv_btn1.setVisibility(View.VISIBLE);
                tv_btn2.setVisibility(View.VISIBLE);
                tv_btn3.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    circle1.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple_700)));
                    circle2.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.teal_200)));
                    circle3.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.teal_200)));
                } else {
                    circle1.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_700), PorterDuff.Mode.MULTIPLY);
                    circle2.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.teal_200), PorterDuff.Mode.MULTIPLY);
                    circle3.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.teal_200), PorterDuff.Mode.MULTIPLY);
                }

                view1.setBackgroundColor(getResources().getColor(R.color.purple_700));
                view2.setBackgroundColor(getResources().getColor(R.color.purple_700));
                view3.setBackgroundColor(getResources().getColor(R.color.purple_700));
                view4.setBackgroundColor(getResources().getColor(R.color.purple_700));

                //set listener
                cv_member_registration.setOnClickListener(this);

                break;

            case 2:
                cv_member_registration.setAlpha(1.0f);
                cv_proposal.setAlpha(1.0f);
                cv_status.setAlpha(0.8f);

                tv_btn1.setVisibility(View.GONE);
                tv_btn2.setVisibility(View.VISIBLE);
                tv_btn3.setVisibility(View.GONE);

                tv_desc1.setVisibility(View.GONE);
                tv_desc2.setVisibility(View.VISIBLE);
                tv_desc3.setVisibility(View.VISIBLE);

                tv_group_no.setText("Group No. " + group_ref);
                tv_group_no.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    circle1.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple_700)));
                    circle2.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple_700)));
                    circle3.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.teal_200)));
                } else {
                    circle1.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_700), PorterDuff.Mode.MULTIPLY);
                    circle2.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_700), PorterDuff.Mode.MULTIPLY);
                    circle3.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.teal_200), PorterDuff.Mode.MULTIPLY);
                }

                view1.setBackgroundColor(getResources().getColor(R.color.purple_700));
                view2.setBackgroundColor(getResources().getColor(R.color.purple_700));
                view3.setBackgroundColor(getResources().getColor(R.color.teal_200));
                view4.setBackgroundColor(getResources().getColor(R.color.teal_200));

                //set listener
                cv_member_registration.setOnClickListener(this);
                cv_proposal.setOnClickListener(this);

                break;

            case 3:
                cv_member_registration.setAlpha(1.0f);
                cv_proposal.setAlpha(1.0f);
                cv_status.setAlpha(1.0f);

                tv_btn1.setVisibility(View.GONE);
                tv_btn2.setVisibility(View.GONE);
                tv_btn3.setVisibility(View.VISIBLE);

                tv_desc1.setVisibility(View.GONE);
                tv_desc2.setVisibility(View.GONE);
                tv_desc3.setVisibility(View.GONE);

                tv_proposal_title.setText("Title: " + proposal_title);
                tv_proposal_title.setVisibility(View.VISIBLE);
                tv_status.setText("Status:" + prposal_status);
                tv_status.setVisibility(View.VISIBLE);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    circle1.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple_700)));
                    circle2.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple_700)));
                    circle3.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple_700)));
                } else {
                    circle1.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_700), PorterDuff.Mode.MULTIPLY);
                    circle2.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_700), PorterDuff.Mode.MULTIPLY);
                    circle3.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_700), PorterDuff.Mode.MULTIPLY);
                }

                view1.setBackgroundColor(getResources().getColor(R.color.purple_700));
                view2.setBackgroundColor(getResources().getColor(R.color.purple_700));
                view3.setBackgroundColor(getResources().getColor(R.color.purple_700));
                view4.setBackgroundColor(getResources().getColor(R.color.purple_700));

                //set listener
                cv_member_registration.setOnClickListener(this);
                cv_proposal.setOnClickListener(this);
                cv_status.setOnClickListener(this);

                break;

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_logout:
                attemptLogout();
                break;
            case R.id.cv_member_registration:
                startActivity(new Intent(this, GroupMemberActivity.class));
                break;

            case R.id.cv_proposal:
            case R.id.cv_status:
                startActivity(new Intent(this, ProposalActivity.class));
                break;
        }
    }

    private void attemptLogout() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Logout confirmation")
                .setMessage("Are you sure want to logout?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //clear tinydb
                        tinyDB.clear();

                        mAuth.signOut();

                        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
}
