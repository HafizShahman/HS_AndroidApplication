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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ForgotPasswordActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    AlertDialog loading_dialog;
    EditText et_email;
    Button btn_reset;
    TextView tv_success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setTitle("Forgot Password");
        /**
         * step 3
         */
        mAuth = FirebaseAuth.getInstance();
        /**
         * step 4
         */
        et_email = findViewById(R.id.et_email);
        btn_reset = findViewById(R.id.btn_reset);
        tv_success = findViewById(R.id.tv_success);

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptReset();
            }
        });
    }

    /**
     * step 8
     */
    private void attemptReset() {
        String email = et_email.getText().toString().trim();
        if (email.equals("")) {
            promptDialog("Ops!", "Please enter your email before proceed.", "OK");
        } else {
            loading_dialog = promptLoading("Verifying email", "Please wait..");
            sendVerificationEmail(email);
        }
    }

    /**
     * step 9
     *
     * @param email
     */
    private void sendVerificationEmail(final String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            loading_dialog.dismiss();
                            tv_success.setVisibility(View.VISIBLE);
                            tv_success.setText("An email has been sent to " + email + " with instructions to reset your password.");
                        } else {
                            loading_dialog.dismiss();
                            tv_success.setVisibility(View.INVISIBLE);
                            promptDialog("", task.getException().getMessage(), "OK");
                        }
                    }
                });
    }
    private AlertDialog promptLoading(String title, String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(ForgotPasswordActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
        }
        builder.setTitle(title)
                .setMessage(message);
        final AlertDialog ad = builder.show();
        return ad;
    }

    private void promptDialog(String title, String message, String button) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(ForgotPasswordActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
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