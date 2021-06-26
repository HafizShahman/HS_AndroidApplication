package com.caluli.proposalapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.caluli.proposalapp.R;
import com.caluli.proposalapp.adapter.GroupMemberAdapter;
import com.caluli.proposalapp.model.GroupUser;
import com.caluli.proposalapp.util.ConstantVar;

import java.util.ArrayList;

public class GroupDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_group_no;
    ProgressBar progress_bar;
    RecyclerView rv;
    TextView tv_no_data;
    Button btn_view_proposal;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    Context c;

    String hold_group;
    String hold_proposal;
    ArrayList<GroupUser> groupUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_group_details);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        createReference();
        getExtraDataFromIntent(saveInstanceState);
        setToolbar();
        addListener();

        //init ui
        progress_bar.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.VISIBLE);

        setData();

    }

    private void createReference() {

        tv_group_no = findViewById(R.id.tv_group_no);
        progress_bar = findViewById(R.id.progress_bar);
        rv = findViewById(R.id.rv);
        tv_no_data = findViewById(R.id.tv_no_data);
        btn_view_proposal = findViewById(R.id.btn_view_proposal);
    }

    private void getExtraDataFromIntent(Bundle saveInstanceState) {
        if (saveInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                hold_group = null;
                hold_proposal = null;
            } else {
                hold_group = extras.getString("HOLD_GROUP");
                hold_proposal = extras.getString("HOLD_PROPOSAL");
            }
        } else {
            hold_group = (String) saveInstanceState.getSerializable("HOLD_GROUP");
            hold_proposal = (String) saveInstanceState.getSerializable("HOLD_PROPOSAL");

        }
    }

    private void setToolbar() {

        //setToolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar().setTitle("Group details");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
    }

    private ActionBar setSupportActionBar() {
        return null;
    }

    private void addListener() {btn_view_proposal.setOnClickListener(this); }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_view_proposal:
                Intent intent = new Intent(this,ProposalDetailsActivity. class);
                Intent.putExtra("HOLD_PROPOSAL", proposals.get(position));
                startActivity(intent);
                break;
        }
    }

    private void setData() {
        mDatabase.child(ConstantVar.TABLE_GROUP).child(hold_group).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String group_id = snapshot.getKey();
                    String group_ref = snapshot.child(ConstantVar.COLUMN_GROUP_REF).getValue().toString();

                    tv_group_no.setText(group_ref);

                    groupUsers.clear();

                    for (DataSnapshot snapshot1 : snapshot.child(ConstantVar.COLUMN_GROUP_USERS).getChildren()) {

                        String group_user_matric = snapshot1.getKey();
                        Boolean group_user_is_leader = (Boolean) snapshot1.getValue();

                        GroupUser groupUser = new GroupUser(group_user_matric, group_user_is_leader);

                        if (group_user_is_leader) {
                            groupUser.add(0, groupUser);
                        } else {
                            groupUser.add(0, groupUser);
                        }

                    }

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    rv.setLayoutManager(linearLayoutManager);
                    rv.setHasFixedSize(true);
                    GroupMemberAdapter adapter = new GroupMemberAdapter(GroupDetailsActivity.this, mAuth, mDatabase, groupUsers);
                    rv.setAdapter(adapter);

                    progress_bar.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                    tv_no_data.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}


