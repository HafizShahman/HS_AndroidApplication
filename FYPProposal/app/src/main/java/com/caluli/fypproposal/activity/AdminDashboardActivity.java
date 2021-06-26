package com.caluli.fypproposal.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lailyyana.proposalapp.R;
import com.lailyyana.proposalapp.util.ConstantVar;
import com.lailyyana.proposalapp.util.TinyDB;

public class AdminDashboardActivity extends AppCompatActivity implements View.OnClickListener {

    //user details
    TextView tv_name;
    TextView tv_logout;

    CardView cv_pending;
    CardView cv_not_approved;
    CardView cv_approved;

    TextView tv_pending_count;
    TextView tv_not_approved_count;
    TextView tv_approved_count;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //tiny db
    TinyDB tinyDB;


    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_admin_dashboard);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        tinyDB = new TinyDB(this);

        setToolbar();
        createReferences();
        addlistener();

        setUserData();
        setData();

    }

    private void setUserData() {
        mDatabase.child(ConstantVar.TABLE_USER).child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String user_name = snapshot.child(ConstantVar.COLUMN_USER_NAME).getValue().toString();
                    tv_name.setText(user_name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setData() {
        mDatabase.child(ConstantVar.TABLE_PROPOSAL).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    int pending_count = 0;
                    int not_approved_count = 0;
                    int approved_count = 0;

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String proposal_status = "";

                        if (snapshot1.hasChild(ConstantVar.COLUMN_PROPOSAL_STATUS)) {
                            proposal_status = snapshot1.child(ConstantVar.COLUMN_PROPOSAL_STATUS).getValue().toString();
                        }

                        if (proposal_status.equals(ConstantVar.PROPOSAL_STATUS_PENDING)){
                            pending_count++;

                        } else if (proposal_status.equals(ConstantVar.PROPOSAL_STATUS_NOT_APPROVED)) {
                            not_approved_count++;

                        } else if (proposal_status.equals(ConstantVar.PROPOSAL_STATUS_APPROVED)) {
                            approved_count++;

                        }

                    }

                    tv_pending_count.setText(String.valueOf(pending_count));
                    tv_not_approved_count.setText(String.valueOf(not_approved_count));
                    tv_approved_count.setText(String.valueOf(approved_count));

                } else {

                    tv_pending_count.setText("0");
                    tv_not_approved_count.setText("0");
                    tv_approved_count.setText("0");
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
        getSupportActionBar().setTitle("Dashboard");
    }

    private void createReferences() {
        tv_name = findViewById(R.id.tv_name);
        tv_logout = findViewById(R.id.tv_logout);
        cv_pending= findViewById(R.id.cv_pending);
        cv_not_approved = findViewById(R.id.cv_not_approved);
        cv_approved = findViewById(R.id.cv_approved);
        tv_pending_count = findViewById(R.id.tv_pending_count);
        tv_not_approved_count = findViewById(R.id.tv_not_approved_count);
        tv_approved_count = findViewById(R.id.tv_approved_count);

    }

    private void addlistener() {
        tv_logout.setOnClickListener(this);
        cv_pending.setOnClickListener(this);
        cv_not_approved.setOnClickListener(this);
        cv_approved.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent(this,DashboardActivity.class);

        switch (view.getId()) {

            case R.id.tv_logout:
                attemptLogout();
                break;

            case R.id.cv_pending:
                intent.putExtra("HOLD STATUS", ConstantVar.PROPOSAL_STATUS_PENDING);
                break;

            case R.id.cv_not_approved:
                intent.putExtra("HOLD STATUS", ConstantVar.PROPOSAL_STATUS_NOT_APPROVED);
                break;

            case R.id.cv_approved:
                intent.putExtra("HOLD STATUS", ConstantVar.PROPOSAL_STATUS_APPROVED);
                break;
        }
    }

    private void attemptLogout() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert) ;
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Logout confirmation")
                .setMessage("Are you sure want to logout?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) { dialog.dismiss();}

                })
                .setPositiveButton("logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        //clear tinydb
                        tinyDB.clear();

                        mAuth.signOut();

                        Intent intent = new Intent(AdminDashboardActivity.this,LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }
                })
                .show();

    }
}