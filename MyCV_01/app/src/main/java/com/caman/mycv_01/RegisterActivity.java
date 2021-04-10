package com.caman.mycv_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mDatabase;
    AlertDialog loading_dialog;
    EditText et_name, et_email, et_password, et_retype_password;
    Button btn_register;
    TextView tv_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        /**
         * step 2
         */
        getSupportActionBar().setTitle("Register");
        /**
         * step 3
         */
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
        /**
         * step 4
         */
        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_retype_password = findViewById(R.id.et_retype_password);
        btn_register = findViewById(R.id.btn_register);
        tv_login = findViewById(R.id.tv_login);
        /**
         * step 9
         */
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attempRegister();
            }
        });
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void attempRegister() {
        String full_name = et_name.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String retype_password = et_retype_password.getText().toString().trim();
        if (full_name.equals("") || email.equals("") || password.equals("") ||
                retype_password.equals("")) {
            promptDialog("Ops!", "Please enter all information to continue.", "OK");
        } else if (password.length() < 6) {
            promptDialog("Ops!", "Password must be at least 6 characters.", "OK");
        } else if (!retype_password.equals(password)) {
            promptDialog("Ops!", "Password did not match.", "OK");
        } else {
            loading_dialog = promptLoading("Registration process", "Please wait..");
            registerUser(full_name, email, password);
        }
    }

    /**
     * step 11
     */
    private void registerUser(final String full_name, final String email, String
            password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(">>>>>>>", "createUserWithEmail:onComplete:" +
                                task.isSuccessful());
                        if (!task.isSuccessful()) {
                            loading_dialog.dismiss();
                            promptDialog("", task.getException().getMessage(), "OK");
                        } else {
                            FirebaseUser user =
                                    FirebaseAuth.getInstance().getCurrentUser();
                            writeNewUser(
                                    full_name,
                                    email,
                                    "",
                                    user.getUid()
                            );
                            loading_dialog.dismiss();
                            finish();
                        }
                    }
                });
    }

    /**
     * step 12
     */
    private void writeNewUser(String full_name, String email, String image_url, String
            user_id) {
        User user = new User();
        user.setName(full_name);
        user.setEmail(email);
        mDatabase.child("users").child(user_id).setValue(user);
    }

    /**
     * step 5
     */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /** step 6
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * step 7
     */
    private AlertDialog promptLoading(String title, String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(RegisterActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(RegisterActivity.this);
        }
        builder.setTitle(title)
                .setMessage(message);
        final AlertDialog ad = builder.show();
        return ad;
    }

    private void promptDialog(String title, String message, String button) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(RegisterActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(RegisterActivity.this);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}