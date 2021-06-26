package com.caluli.proposalapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.caluli.proposalapp.R;
import com.caluli.proposalapp.adapter.GroupAdapter;
import com.caluli.proposalapp.model.Proposal;
import com.caluli.proposalapp.util.ConstantVar;

import java.util.ArrayList;

public class GroupListActivity extends AppCompatActivity {

    ProgressBar progress_bar;
    RecyclerView rv;
    TextView tv_no_data;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    String hold_status;
    ArrayList<Proposal> proposals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_group_list);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        createReference();
        getExtraDataFromIntent(saveInstanceState);
        setToolbar();

        //init ui
        progress_bar.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.VISIBLE);

    }

    private void createReference() {

        progress_bar = findViewById(R.id.progress_bar);
        rv = findViewById(R.id.rv);
        tv_no_data = findViewById(R.id.tv_no_data);

    }

    private void getExtraDataFromIntent(Bundle saveInstanceState) {
        if (saveInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                hold_status = null;
            } else {
                hold_status = extras.getString("HOLD_STATUS");
            }
        } else {
            hold_status = (String) saveInstanceState.getSerializable("HOLD_STATUS");
        }
    }

    private void setToolbar() {
        //setToolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(hold_status);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setData() {
        mDatabase.child(ConstantVar.TABLE_PROPOSAL).orderByChild(ConstantVar.COLUMN_PROPOSAL_STATUS).equalTo(hold_status).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    proposals.clear();

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String proposal_id = snapshot1.getKey();
                        String proposal_group = "";
                        String proposal_title = "";

                        if (snapshot1.hasChild(ConstantVar.COLUMN_PROPOSAL_GROUP)) {
                            proposal_group = snapshot1.child(ConstantVar.COLUMN_PROPOSAL_GROUP).getValue().toString();
                        }
                        if (snapshot1.hasChild(ConstantVar.COLUMN_PROPOSAL_TITLE)) {
                            proposal_group = snapshot1.child(ConstantVar.COLUMN_PROPOSAL_TITLE).getValue().toString();
                        }

                        Proposal proposal = new Proposal();
                        proposal.setProposal_id(proposal_id);
                        proposal.setProposal_group(proposal_group);
                        proposal.setProposal_id(proposal_title);

                        proposals.add(proposal);
                    }

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    rv.setLayoutManager(linearLayoutManager);
                    rv.setHasFixedSize(true);
                    GroupAdapter adapter = new GroupAdapter(GroupListActivity.this, mAuth, mDatabase, proposals);
                    rv.setAdapter(adapter);

                    progress_bar.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                    tv_no_data.setVisibility(View.GONE);

                } else {
                    tv_no_data.setText("No record of " + hold_status.toLowerCase() + "proposal found");

                    progress_bar.setVisibility(View.GONE);
                    rv.setVisibility(View.GONE);
                    tv_no_data.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
