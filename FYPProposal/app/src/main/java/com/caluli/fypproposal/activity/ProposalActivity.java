package com.caluli.fypproposal.activity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lailyyana.proposalapp.R;
import com.lailyyana.proposalapp.model.Proposal;
import com.lailyyana.proposalapp.util.ConstantVar;
import com.lailyyana.proposalapp.util.DialogUtil;
import com.lailyyana.proposalapp.util.TinyDB;

public class ProposalActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_title;
    TextView tv_title;
    EditText et_objective;
    TextView tv_objective;
    EditText et_problem_statement;
    TextView tv_problem_statement;
    EditText et_organization;
    TextView tv_organization;
    EditText et_organization_address;
    TextView tv_organization_address;
    EditText et_supervisor_name;
    TextView tv_supervisor_name;
    LinearLayout ll_status;
    TextView tv_status;
    TextView tv_comment;
    Button btn_cancel;
    Button btn_submit;
    Button btn_unsumbit;

    android.app.AlertDialog loadingDialog;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //tiny db
    TinyDB tinyDB;

    String proposal_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposal);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        tinyDB = new TinyDB(this);

        setToolbar();
        createReference();
        addListener();

        setData();

    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Project Proposal");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }

    private void createReference() {
        et_title = findViewById(R.id.et_title);
        tv_title = findViewById(R.id.tv_title);
        et_objective = findViewById(R.id.et_objective);
        tv_objective = findViewById(R.id.tv_objective);
        et_problem_statement = findViewById(R.id.et_problem_statement);
        tv_problem_statement = findViewById(R.id.tv_problem_statement);
        et_organization = findViewById(R.id.et_organization);
        tv_organization = findViewById(R.id.tv_organization);
        et_organization_address = findViewById(R.id.et_organization_address);
        tv_organization_address = findViewById(R.id.tv_organization_address);
        et_supervisor_name = findViewById(R.id.et_supervisor_name);
        tv_supervisor_name = findViewById(R.id.tv_supervisor_name);
        ll_status = findViewById(R.id.ll_status);
        tv_status = findViewById(R.id.tv_status);
        tv_comment = findViewById(R.id.tv_comment);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_submit = findViewById(R.id.btn_submit);
        btn_unsumbit = findViewById(R.id.btn_unsubmit);
    }

    private void addListener() {
        btn_cancel.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_unsumbit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                promptDiscardDialog();

            case R.id.btn_submit:
                attemptSubmit();
                break;

            case R.id.btn_unsubmit:
                attemptUnsubmit();
                break;

        }
    }

    private void setData() {
        mDatabase.child(ConstantVar.TABLE_PROPOSAL).orderByChild(ConstantVar.COLUMN_PROPOSAL_GROUP).equalTo(tinyDB.getString(ConstantVar.TNB_GROUP_ID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    //has submitted proposal
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        proposal_id = snapshot1.getKey();

                        String proposal_title = "";
                        String proposal_objective = "";
                        String proposal_problem_statement = "";
                        String proposal_organization = "";
                        String proposal_organization_address = "";
                        String proposal_supervisor = "";
                        String proposal_comments = "";
                        String proposal_status = "";

                        if (snapshot1.hasChild(ConstantVar.COLUMN_PROPOSAL_TITLE)) {
                            proposal_title = snapshot1.child(ConstantVar.COLUMN_PROPOSAL_TITLE).getValue().toString();
                        }
                        if (snapshot1.hasChild(ConstantVar.COLUMN_PROPOSAL_OBJECTIVE)) {
                            proposal_objective = snapshot1.child(ConstantVar.COLUMN_PROPOSAL_OBJECTIVE).getValue().toString();
                        }
                        if (snapshot1.hasChild(ConstantVar.COLUMN_PROPOSAL_PROBLEM_STATEMENT)) {
                            proposal_problem_statement = snapshot1.child(ConstantVar.COLUMN_PROPOSAL_PROBLEM_STATEMENT).getValue().toString();
                        }
                        if (snapshot1.hasChild(ConstantVar.COLUMN_PROPOSAL_ORGANIZATION)) {
                            proposal_organization = snapshot1.child(ConstantVar.COLUMN_PROPOSAL_ORGANIZATION).getValue().toString();
                        }
                        if (snapshot1.hasChild(ConstantVar.COLUMN_PROPOSAL_ORGANIZATION_ADDRESS)) {
                            proposal_organization_address = snapshot1.child(ConstantVar.COLUMN_PROPOSAL_ORGANIZATION_ADDRESS).getValue().toString();
                        }
                        if (snapshot1.hasChild(ConstantVar.COLUMN_PROPOSAL_SUPERVISOR)) {
                            proposal_supervisor = snapshot1.child(ConstantVar.COLUMN_PROPOSAL_SUPERVISOR).getValue().toString();
                        }
                        if (snapshot1.hasChild(ConstantVar.COLUMN_PROPOSAL_COMMENTS)) {
                            proposal_comments = snapshot1.child(ConstantVar.COLUMN_PROPOSAL_COMMENTS).getValue().toString();
                        }
                        if (snapshot1.hasChild(ConstantVar.COLUMN_PROPOSAL_STATUS)) {
                            proposal_status = snapshot1.child(ConstantVar.COLUMN_PROPOSAL_STATUS).getValue().toString();
                        }

                        //set data
                        et_title.setText(proposal_title);
                        tv_title.setText(proposal_title);
                        et_objective.setText(proposal_objective);
                        tv_objective.setText(proposal_objective);
                        et_problem_statement.setText(proposal_problem_statement);
                        tv_problem_statement.setText(proposal_problem_statement);
                        et_organization.setText(proposal_organization);
                        tv_organization.setText(proposal_organization);
                        et_organization_address.setText(proposal_organization_address);
                        tv_organization_address.setText(proposal_organization_address);
                        et_supervisor_name.setText(proposal_supervisor);
                        tv_supervisor_name.setText(proposal_supervisor);

                        if (proposal_status.equals(ConstantVar.PROPOSAL_STATUS_PENDING)) {
                            //status still pending. can unsubmit proposal
                            ll_status.setVisibility(View.VISIBLE);

                            tv_status.setText(proposal_status);
                            tv_status.setTextColor(getResources().getColor(R.color.status_pending));
                            tv_status.setVisibility(View.VISIBLE);

                            tv_comment.setText("-");
                            tv_comment.setVisibility(View.VISIBLE);

                            setEditTextState(false);

                            btn_cancel.setVisibility(View.GONE);
                            btn_submit.setVisibility(View.GONE);
                            btn_unsumbit.setVisibility(View.VISIBLE);

                        } else if (proposal_status.equals(ConstantVar.PROPOSAL_STATUS_NOT_APPROVED)) {
                            //status not approved. enable all edittext
                            ll_status.setVisibility(View.VISIBLE);

                            tv_status.setText(proposal_status);
                            tv_status.setTextColor(getResources().getColor(R.color.status_not_approved));
                            tv_status.setVisibility(View.VISIBLE);

                            tv_comment.setText(proposal_comments);
                            tv_comment.setVisibility(View.VISIBLE);

                            setEditTextState(true);

                            btn_cancel.setVisibility(View.VISIBLE);
                            btn_submit.setText("Resubmit");
                            btn_submit.setVisibility(View.VISIBLE);
                            btn_unsumbit.setVisibility(View.GONE);

                        } else if (proposal_status.equals(ConstantVar.PROPOSAL_STATUS_APPROVED)) {
                            //status approved. disable all edittext
                            ll_status.setVisibility(View.VISIBLE);

                            tv_status.setText(proposal_status);
                            tv_status.setTextColor(getResources().getColor(R.color.status_approved));
                            tv_status.setVisibility(View.VISIBLE);

                            tv_comment.setText(proposal_comments);
                            tv_comment.setVisibility(View.VISIBLE);

                            setEditTextState(false);

                            btn_cancel.setVisibility(View.GONE);
                            btn_submit.setVisibility(View.GONE);
                            btn_unsumbit.setVisibility(View.GONE);

                        } else if (proposal_status.equals(ConstantVar.PROPOSAL_STATUS_ARCHIVED)) {
                            //status archived. enable all edittext
                            ll_status.setVisibility(View.GONE);


                            setEditTextState(false);

                            btn_cancel.setVisibility(View.VISIBLE);
                            btn_submit.setText("Resubmit");
                            btn_submit.setVisibility(View.VISIBLE);
                            btn_unsumbit.setVisibility(View.GONE);
                        }

                        break;
                    }
                } else {

                    // has not submitted proposal
                    ll_status.setVisibility(View.GONE);

                    btn_cancel.setVisibility(View.VISIBLE);
                    btn_submit.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setEditTextState(boolean isEnabled) {
        if (isEnabled) {

            et_title.setVisibility(View.VISIBLE);
            tv_title.setVisibility(View.GONE);
            et_objective.setVisibility(View.VISIBLE);
            tv_objective.setVisibility(View.GONE);
            et_problem_statement.setVisibility(View.VISIBLE);
            tv_problem_statement.setVisibility(View.GONE);
            et_organization.setVisibility(View.VISIBLE);
            tv_organization.setVisibility(View.GONE);
            et_organization_address.setVisibility(View.VISIBLE);
            tv_organization_address.setVisibility(View.GONE);
            et_supervisor_name.setVisibility(View.VISIBLE);
            tv_supervisor_name.setVisibility(View.GONE);

        } else {

            et_title.setVisibility(View.GONE);
            tv_title.setVisibility(View.VISIBLE);
            et_objective.setVisibility(View.GONE);
            tv_objective.setVisibility(View.VISIBLE);
            et_problem_statement.setVisibility(View.GONE);
            tv_problem_statement.setVisibility(View.VISIBLE);
            et_organization.setVisibility(View.GONE);
            tv_organization.setVisibility(View.VISIBLE);
            et_organization_address.setVisibility(View.GONE);
            tv_organization_address.setVisibility(View.VISIBLE);
            et_supervisor_name.setVisibility(View.GONE);
            tv_supervisor_name.setVisibility(View.VISIBLE);
        }
    }

    private void promptDiscardDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Confirmation")
                .setMessage("Are you sure want to discard the changes?")
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void attemptSubmit() {
        String title = et_title.getText().toString();
        String objective = et_objective.getText().toString();
        String problem_statement = et_problem_statement.getText().toString();
        String organization = et_organization.getText().toString();
        String organization_address = et_organization_address.getText().toString();
        String supervisor_name = et_supervisor_name.getText().toString();

        if (title.equals("") || objective.equals("") || problem_statement.equals("") || organization.equals("") || organization_address.equals("") || supervisor_name.equals("")) {
            new DialogUtil().promptDialog(this, "Ops!", "All fields cannot be empty.", "OK");

        } else {

            loadingDialog = new DialogUtil().promptLoading(this, "Submitting proposal", "Please wait...");
            Proposal proposal = new Proposal();
            proposal.setProposal_title(title);
            proposal.setProposal_objective(objective);
            proposal.setProposal_problem_statement(problem_statement);
            proposal.setProposal_organization(organization);
            proposal.setProposal_organization_address(organization_address);
            proposal.setProposal_supervisor(supervisor_name);
            proposal.setProposal_group(tinyDB.getString(ConstantVar.TNB_GROUP_ID));
            proposal.setProposal_status(ConstantVar.PROPOSAL_STATUS_PENDING);

            submitProposal(proposal);
        }
    }

    private void submitProposal(Proposal proposal) {

        if (proposal_id.equals("")) {
            proposal_id = mDatabase.child(ConstantVar.TABLE_PROPOSAL).push().getKey();
        }

        mDatabase.child(ConstantVar.TABLE_PROPOSAL).child(proposal_id).setValue(proposal);

        loadingDialog.dismiss();
    }

    private void attemptUnsubmit() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Confirmation")
                .setMessage("Are you sure want to unsubmit your project proposal?")
                .setPositiveButton("Unsubmit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase.child(ConstantVar.TABLE_PROPOSAL).child(tinyDB.getString(ConstantVar.TNB_PROPOSAL_ID)).child(ConstantVar.COLUMN_PROPOSAL_STATUS).setValue(ConstantVar.PROPOSAL_STATUS_ARCHIVED);
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                })
                .show();
    }

}