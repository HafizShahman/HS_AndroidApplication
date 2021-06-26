package com.caluli.fypproposal.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lailyyana.proposalapp.R;
import com.lailyyana.proposalapp.util.ConstantVar;
import com.lailyyana.proposalapp.util.DialogUtil;
import com.lailyyana.proposalapp.util.TinyDB;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity  implements  View.OnClickListener {

    EditText et_email;
    EditText et_password;
    Button btn_login;
    TextView tv_register;

    AlertDialog loadingDialog;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        tinyDB = new TinyDB(this);

        createReference();
        addListener();
        iniAuth();
    }

    private void createReference() {

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        tv_register = findViewById(R.id.tv_register);
    }

    private void addListener() {
        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);

    }

    private void iniAuth() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(">>>>>>>", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(">>>>>>>", "onAuthStateChanged:signed_out");
                }

            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                attemptLogin();
                break;

            case R.id.tv_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
        }
    }

    private void attemptLogin() {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        if (email.equals("") || password.equals("")) {
            new DialogUtil().promptDialog(LoginActivity.this,"Ops!", "Please enter your email and password.", "OK");
        } else {
            loadingDialog = new DialogUtil().promptLoading(LoginActivity.this, "Authentication process", "Please wait..");
            loginUser(email, password);
        }
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(">>>>>>>", "signInWithEmail:onComplete:" + task.isSuccessful());

                        loadingDialog.dismiss();

                        if (!task.isSuccessful()) {

                            new DialogUtil().promptDialog(LoginActivity.this, "Opps!", Objects.requireNonNull(task.getException()).getMessage(), "OK");
                            Log.w(">>>>>>>>", "signInWithEmail:failed", task.getException());

                        } else {
                            checkIfUserAdmin();
                        }
                    }
                });
    }

    private void checkIfUserAdmin() {
        mDatabase.child(ConstantVar.TABLE_USER).child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    if (snapshot.hasChild(ConstantVar.COLUMN_USER_IS_ADMIN)) {
                        Boolean is_admin = (Boolean) snapshot.child(ConstantVar.COLUMN_USER_IS_ADMIN).getValue();

                        if (is_admin) {
                            // is admin
                            TinyDB.putBoolean(ConstantVar.TNB_IS_ADMIN, true);
                            launchDashboard();

                        } else {
                            //not admin
                            TinyDB.putBoolean(ConstantVar.TNB_IS_ADMIN, false);
                            launchDashboard();
                        }

                    } else {
                        //not an admin
                        tinyDB.putBoolean(ConstantVar.TNB_IS_ADMIN, false);
                        launchDashboard();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void launchDashboard() {
        if (tinyDB.getBoolean(ConstantVar.TNB_IS_ADMIN)) {
            Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } else {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            launchDashboard();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}

