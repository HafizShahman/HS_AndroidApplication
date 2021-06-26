package com.caman.sbrokoli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import com.bumptech.glide.load.engine.Resource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
public class ReferencesActivity extends AppCompatActivity {
    /**
     * step 2
     */
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    AlertDialog loading_dialog;
    EditText et_name, et_designation, et_company, et_phone, et_emailAddress;
    Button btn_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_references);
        /**
         * step 1
         */
        getSupportActionBar().setTitle("References");
        /**
         * step 12
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /**
         * step 3
         */
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        /**
         * step 4
         */
        et_name = findViewById(R.id.et_name);
        et_designation = findViewById(R.id.et_designation);
        et_company = findViewById(R.id.et_company);
        et_phone = findViewById(R.id.et_phone);
        et_emailAddress = findViewById(R.id.et_emailAddress);
        btn_save = findViewById(R.id.btn_save);
        if (getIntent().getSerializableExtra("EXTRA_VAL") != null) {
            /**
             * step 13
             */
            final References references = (References)
                    getIntent().getSerializableExtra("EXTRA_VAL");
            setPrefillData(references);
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptUpdate(references);
                }
            });
        } else {
            /**
             * step 7
             */
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptSave();
                }
            });
        }
    }
    /**
     * step 14
     */
    private void setPrefillData(final References references) {
        et_name.setText(references.getName());
        et_designation.setText(references.getDesignation());
        et_company.setText(references.getCompany());
        et_phone.setText(references.getPhone());
        et_emailAddress.setText(references.getEmail());
    }
    /**
     * step 15
     */
    private void attemptUpdate(References references) {
        String name = et_name.getText().toString().trim();
        String desingnation = et_designation.getText().toString().trim();
        String company = et_company.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        String email = et_emailAddress.getText().toString().trim();
        if (name.equals("") || desingnation.equals("") || company.equals("") ||
                phone.equals("") || email.equals("")) {
            promptDialog("Ops!", "All field cannot be empty.", "OK");
        } else {
            loading_dialog = promptLoading("Saving information", "Please wait...");
            updateData(references.getId(), name, desingnation, company, phone, email);
        }
    }
    /**
     * step 16
     */
    private void updateData(String id, String name, String designation, String company, String phone, String email) {
        References references = new References();
        references.setName(name);
        references.setDesignation(designation);
        references.setCompany(company);
        references.setPhone(phone);
        references.setEmail(email);

        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("references").child(id).setValue(references).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    loading_dialog.dismiss();
                    promptDialog("Ops!", "Cannot save data. Make sure you have an internet connection. Please try again later.", "OK");
                }
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(ReferencesActivity.this,
                            android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(ReferencesActivity.this);
                }
                builder.setTitle("Success")
                        .setMessage("You data has been successfully saved.")
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .show();
            }
        });
    }
    /**
     * step 8
     */
    private void attemptSave() {
        String name = et_name.getText().toString().trim();
        String designation = et_designation.getText().toString().trim();
        String company = et_company.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        String email = et_emailAddress.getText().toString().trim();
        if (name.equals("") || designation.equals("") || company.equals("") ||
                phone.equals("") || email.equals("")) {
            promptDialog("Ops!", "All field cannot be empty.", "OK");
        } else {
            loading_dialog = promptLoading("Saving information", "Please wait...");
            saveData(name, designation, company, phone, email);
        }
    }
    /**
     * step 11
     */
    private void saveData(String name, String designation, String company, String phone, String email) {
        References references = new References();
        references.setName(name);
        references.setDesignation(designation);
        references.setCompany(company);
        references.setPhone(phone);
        references.setEmail(email);
        String key =
                mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("references").push().getKey(
                );

        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("references").child(key).setValue(references).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    loading_dialog.dismiss();
                    promptDialog("Ops!", "Cannot save data. Make sure you have an internet connection. Please try again later.", "OK");
                }
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(ReferencesActivity.this,
                            android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(ReferencesActivity.this);
                }
                builder.setTitle("Success")
                        .setMessage("You data has been successfully saved.")
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .show();
            }
        });
    }
    /**
     * step 9
     */
    private AlertDialog promptLoading(String title, String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(ReferencesActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(ReferencesActivity.this);
        }
        builder.setTitle(title)
                .setMessage(message);
        final AlertDialog ad = builder.show();
        return ad;
    }
    /**
     * step 10
     */
    private void promptDialog(String title, String message, String button) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(ReferencesActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(ReferencesActivity.this);
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