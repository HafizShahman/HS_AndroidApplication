package com.caluli.proposalapp.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;
import android.util.Log;

import com.caluli.proposalapp.util.DialogUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.caluli.proposalapp.util.DialogUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.caluli.proposalapp.R;
import com.caluli.proposalapp.util.ConstantVar;
import com.caluli.proposalapp.util.TinyDB;

public class ProposalDetailsActivity extends AppCompatActivity implements View.OnClickListener {




    TextView tv_title;
    TextView tv_objective;
    TextView tv_problem_statement;
    TextView tv_organization;
    TextView tv_organization_address;
    TextView tv_supervisor_name;


    RadioButton rb_approved;
    RadioButton rb_not_approved;

    EditText et_comment;
    Button btn_cancel;
    Button btn_update;

    AlertDialog loadingDialog;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //tinyDB
    TinyDB tinyDB;

    String hold_proposal;


    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposal_details);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        tinyDB = new TinyDB(this);

        setToolbar();
        createReference();
        getExtraDataFromIntent(savedInstanceState);
        addListener();

        setData();
    }

    private void setData() {
        mDatabase.child(ConstantVar.TABLE_PROPOSAL).child(hold_proposal).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String proposal_title = "";
                    String proposal_objective = "";
                    String proposal_problem_statement = "";
                    String proposal_organization = "";
                    String proposal_organization_address = "";
                    String proposal_supervisor = "";
                    String proposal_comments = "";
                    String proposal_status = "";


                    if (snapshot.hasChild(ConstantVar.COLUMN_PROPOSAL_TITLE)) {
                        proposal_title = snapshot.child(ConstantVar.COLUMN_PROPOSAL_TITLE).getValue().toString();
                    }

                    if (snapshot.hasChild(ConstantVar.COLUMN_PROPOSAL_OBJECTIVE)) {
                        proposal_objective = snapshot.child(ConstantVar.COLUMN_PROPOSAL_OBJECTIVE).getValue().toString();
                    }

                    if (snapshot.hasChild(ConstantVar.COLUMN_PROPOSAL_PROBLEM_STATEMENT)) {
                        proposal_problem_statement = snapshot.child(ConstantVar.COLUMN_PROPOSAL_PROBLEM_STATEMENT).getValue().toString();
                    }

                    if (snapshot.hasChild(ConstantVar.COLUMN_PROPOSAL_ORGANIZATION)) {
                        proposal_organization = snapshot.child(ConstantVar.COLUMN_PROPOSAL_ORGANIZATION).getValue().toString();
                    }

                    if (snapshot.hasChild(ConstantVar.COLUMN_PROPOSAL_ORGANIZATION_ADDRESS)) {
                        proposal_organization_address = snapshot.child(ConstantVar.COLUMN_PROPOSAL_ORGANIZATION_ADDRESS).getValue().toString();
                    }

                    if (snapshot.hasChild(ConstantVar.COLUMN_PROPOSAL_SUPERVISOR)) {
                        proposal_supervisor = snapshot.child(ConstantVar.COLUMN_PROPOSAL_SUPERVISOR).getValue().toString();
                    }

                    if (snapshot.hasChild(ConstantVar.COLUMN_PROPOSAL_COMMENTS)) {
                        proposal_comments = snapshot.child(ConstantVar.COLUMN_PROPOSAL_COMMENTS).getValue().toString();
                    }

                    if (snapshot.hasChild(ConstantVar.COLUMN_PROPOSAL_STATUS)) {
                        proposal_status = snapshot.child(ConstantVar.COLUMN_PROPOSAL_STATUS).getValue().toString();
                    }

                    //setData

                    tv_title.setText(proposal_title);
                    tv_objective.setText(proposal_objective);
                    tv_problem_statement.setText(proposal_objective);
                    tv_organization.setText(proposal_organization);
                    tv_organization_address.setText(proposal_organization_address);
                    tv_supervisor_name.setText(proposal_supervisor);

                    et_comment.setText(proposal_comments);

                    if (proposal_status.equals(ConstantVar.PROPOSAL_STATUS_NOT_APPROVED)) {
                        rb_not_approved.setChecked(true);
                        rb_approved.setChecked(false);

                    } else if (proposal_status.equals(ConstantVar.PROPOSAL_STATUS_APPROVED)) {
                        rb_not_approved.setChecked(false);
                        rb_approved.setChecked(true);
                    } else {
                        rb_not_approved.setChecked(false);
                        rb_approved.setChecked(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

            private void setToolbar() {
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle("Proposal Details");
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish(); }

                });
            }

    private void setSupportActionBar(Toolbar toolbar) {
    }


    private void createReference() {

                tv_title = findViewById(R.id.tv_title);
                tv_objective = findViewById(R.id.tv_objective);
                tv_problem_statement = findViewById(R.id.tv_problem_statement);
                tv_organization = findViewById(R.id.tv_organization);
                tv_organization_address = findViewById(R.id.tv_organization_address);
                tv_supervisor_name = findViewById(R.id.tv_supervisor_name);


                rb_approved = findViewById(R.id.rb_approved);
                rb_not_approved = findViewById(R.id.rb_not_approved);
                et_comment = findViewById(R.id.et_comment);
                btn_cancel = findViewById(R.id.btn_cancel);
                btn_update = findViewById(R.id.btn_update);
            }

            private void getExtraDataFromIntent(Bundle savedInstanceState) {
                if (savedInstanceState == null) {
                    Bundle extras = getIntent().getExtras();
                    if (extras == null) {
                        hold_proposal = null;
                    } else {
                        hold_proposal = extras.getString("HOLD_PROPOSAL");
                    }
                } else {
                    hold_proposal = (String) savedInstanceState.getSerializable("HOLD_PROPOSAL");
                }

            }
                private void addListener() {
                    btn_cancel.setOnClickListener(this);
                    btn_update.setOnClickListener(this);

                }

                @Override
                public void onClick (View view){
                    switch (view.getId()) {
                        case R.id.btn_cancel:
                            promptDiscardDialog();
                            break;

                        case R.id.btn_update:
                            attemptUpdate();
                            break;
                    }
                }

                private void attemptUpdate() {

                if (!rb_approved.isChecked() && !rb_not_approved.isChecked()) {
                    new DialogUtil().promptDialog(ProposalDetailsActivity.this, "Oops!", "Please select one approval status.", "OK");

                } else if (et_comment.getText().toString().equals("")) {
                    new DialogUtil().promptDialog(ProposalDetailsActivity.this, "Oops!", "Comment cannot be empty.", "OK");
                } else {
                    updateProposalStatus();
                }
            }

            private void updateProposalStatus() {
                if (rb_approved.isChecked()) {
                    mDatabase.child(ConstantVar.TABLE_PROPOSAL).child(hold_proposal).child(ConstantVar.COLUMN_PROPOSAL_STATUS).setValue(ConstantVar.PROPOSAL_STATUS_APPROVED);

                } else if (rb_not_approved.isChecked()) {
                    mDatabase.child(ConstantVar.TABLE_PROPOSAL).child(hold_proposal).child(ConstantVar.COLUMN_PROPOSAL_STATUS).setValue(ConstantVar.PROPOSAL_STATUS_NOT_APPROVED);

                }
                mDatabase.child(ConstantVar.TABLE_PROPOSAL).child(hold_proposal).child(ConstantVar.COLUMN_PROPOSAL_COMMENTS).setValue(et_comment.getText().toString());

                Toast.makeText(getApplicationContext(), "Proposal approval status has been updated.", Toast.LENGTH_SHORT).show();
            }

            private void promptDiscardDialog() {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("Confirmation")
                        .setMessage("Are you sure want to discard this changer?")
                        .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }




