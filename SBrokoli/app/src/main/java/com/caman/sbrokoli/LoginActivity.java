package com.caman.sbrokoli;

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
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
public class LoginActivity extends AppCompatActivity {
    /**
     * step 1
     */
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    AlertDialog loading_dialog;
    EditText et_email, et_password;
    Button btn_login;
    TextView tv_forgot_password, tv_register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         * step 2
         */
        getSupportActionBar().setTitle("Login");
        /**
         * step 3
         */
        mAuth = FirebaseAuth.getInstance();
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
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        tv_forgot_password = findViewById(R.id.tv_forgot_password);
        tv_register = findViewById(R.id.tv_register);
        /**
         * step 9
         */
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
    /**
     * step 5
     */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
    /**
     * step 6
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    /**
     * step 10
     */
    private void attemptLogin() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (email.equals("") || password.equals("")) {
            promptDialog("", "Please enter your email and password.", "OK");
        } else {
            loading_dialog = promptLoading("Authentication process", "Please wait..");
            loginUser(email, password);
        }
    }
    /**
     * step 11
     */
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(">>>>>>>", "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            loading_dialog.dismiss();
                            Log.w(">>>>>>>>", "signInWithEmail:failed", task.getException());
                            promptDialog("", task.getException().getMessage(), "OK");
                        } else {
                            loading_dialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
    /**
     * step 7
     */
    private AlertDialog promptLoading(String title, String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(LoginActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(LoginActivity.this);
        }
        builder.setTitle(title)
                .setMessage(message);
        final AlertDialog ad = builder.show();
        return ad;
    }

    /**
     * step 8
     */
    private void promptDialog(String title, String message, String button) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(LoginActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {

            builder = new AlertDialog.Builder(LoginActivity.this);
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