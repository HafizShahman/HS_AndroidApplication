package com.caluli.proposalapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

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
import com.caluli.proposalapp.R;
import com.caluli.proposalapp.model.User;
import com.caluli.proposalapp.util.ConstantVar;
import com.caluli.proposalapp.util.DateTimeUtil;
import com.caluli.proposalapp.util.DialogUtil;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements  View.onClickListener {

    EditText et_matric;
    EditText et_full_name;
    EditText et_email;
    EditText et_password;
    EditText et_confirm_password;
    Button btn_register;
    TextView tv_login;

    AlertDialog loadingDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setToolbar();
        createReference();
        addListener();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        getSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }

    private void createReference() {

        et_matric = findViewById(R.id.et_matric);
        et_full_name = findViewById(R.id.et_full_name);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);
        btn_register = findViewById(R.id.btn_register);
        tv_login = findViewById(R.id.tv_login);

    }

    private void addListener() {
        btn_register.setOnClickListener(this);
        tv_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                attemptRegister();
                break;

            case R.id.tv_login:
                finish();
                break;
        }
    }

    private void attemptRegister() {
        String matric_no = et_matric.getText().toString();
        String full_name = et_full_name.getText().toString();
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        String confirm_password = et_confirm_password.getText().toString();

        if (matric_no.equals("") || full_name.equals("") || email.equals("") || password.equals("")) {
            new DialogUtil().promptDialog(RegisterActivity.this, "Ops!", "Please enter all information to continue.", "OK");

        } else if (password.length() < 6) {
            new DialogUtil().promptDialog(RegisterActivity.this, "Ops!", "Password must be at least 6 characters.", "OK");

        } else if (!password.equals(confirm_password)) {
            new DialogUtil().promptDialog(RegisterActivity.this, "Ops!", "Password did not match.", "OK");

        } else {
            loadingDialog = new DialogUtil().promptLoading(RegisterActivity.this, "Authentication process", "Please wait..");
            registerUser(matric_no, full_name, email, password);
        }
    }

    private void registerUser(String matric_no, final String full_name, final String email, String
            password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(">>>>>>>", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            loadingDialog.dismiss();
                            new DialogUtil().promptDialog(RegisterActivity.this, "Ops!", Objects.requireNonNull(task.getException()).getMessage(), "OK");
                        } else {
                            checkIfUserExists(matric_no, full_name, email);
                        }
                    }
                });
    }

    private void checkIfUserExists(String matric_no, String full_name, String email) {
        mDatabase.child(ConstantVar.TABLE_USER).orderByChild(ConstantVar.COLUMN_USER_MATRIC).equalTo(matric_no).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        mDatabase.child(ConstantVar.TABLE_USER).child(snapshot.getKey()).removeValue();
                        break;
                    }
                }

                //add new user data
                writeNewUser(
                        matric_no,
                        full_name,
                        email,
                        mAuth.getCurrentUser().getUid()
                );

                loadingDialog.dismiss();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void writeNewUser(String matric_no, String full_name, String user_id) {

        String ts = new DateTimeUtil().getCurrentTimestamp();

        User user = new User();
        user.setUser_matric_no(matric_no);
        user.setUser_name(full_name);
        user.setUser_email(email);
        user.setUser_created_on(ts);
       


        mDatabase.child(ConstantVar.TABLE_USER).child(user_id).setValue(user);
    }

}

