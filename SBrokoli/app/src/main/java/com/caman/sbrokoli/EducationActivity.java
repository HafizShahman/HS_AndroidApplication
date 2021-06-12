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
public class EducationActivity extends AppCompatActivity {
    /**
     * step 2
     */
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    Calendar myCalendar;
    AlertDialog loading_dialog;
    EditText et_level, et_school, et_start_date, et_end_date;
    Button btn_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);
        /**
         * step 1
         */
        getSupportActionBar().setTitle("Education");
        /**
         * step 12
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /**
         * step 3
         */
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        myCalendar = Calendar.getInstance();

        /**
         * step 4
         */
        et_level = findViewById(R.id.et_level);
        et_school = findViewById(R.id.et_school);
        et_start_date = findViewById(R.id.et_start_date);
        et_end_date = findViewById(R.id.et_end_date);
        btn_save = findViewById(R.id.btn_save);
        if (getIntent().getSerializableExtra("EXTRA_VAL") != null) {
            /**
             * step 13
             */
            final Education education = (Education)
                    getIntent().getSerializableExtra("EXTRA_VAL");
            setPrefillData(education);
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptUpdate(education);
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
        /**
         * step 5
         */
        et_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EducationActivity.this, new
                        DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String myFormat = "MMMM yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat,
                                        Locale.getDefault());
                                et_start_date.setText(sdf.format(myCalendar.getTime()));
                            }
                        }, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });
        /**
         * step 6
         */
        et_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EducationActivity.this, new
                        DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String myFormat = "MMMM yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat,
                                        Locale.getDefault());
                                et_end_date.setText(sdf.format(myCalendar.getTime()));
                            }
                        }, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });
    }
    /**
     * step 14
     */
    private void setPrefillData(final Education education) {
        et_level.setText(education.getLevel());
        et_school.setText(education.getSchool());
        et_start_date.setText(education.getStart_date());
        et_end_date.setText(education.getEnd_date());
    }
    /**
     * step 15
     */
    private void attemptUpdate(Education education) {
        String level = et_level.getText().toString().trim();
        String school = et_school.getText().toString().trim();
        String start_date = et_start_date.getText().toString().trim();
        String end_date = et_end_date.getText().toString().trim();
        if (level.equals("") || school.equals("") || start_date.equals("") ||
                end_date.equals("")) {
            promptDialog("Ops!", "All field cannot be empty.", "OK");
        } else {
            loading_dialog = promptLoading("Saving information", "Please wait...");
            updateData(education.getId(), level, school, start_date, end_date);
        }
    }
    /**
     * step 16
     */
    private void updateData(String id, String level, String school, String start_date, String
            end_date) {
        Education education = new Education();
        education.setLevel(level);
        education.setSchool(school);
        education.setStart_date(start_date);
        education.setEnd_date(end_date);

        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("education").child(id).setValue(education).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    loading_dialog.dismiss();
                    promptDialog("Ops!", "Cannot save data. Make sure you have an internetconnection. Please try again later.", "OK");
                }
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(EducationActivity.this,
                            android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(EducationActivity.this);
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
        String level = et_level.getText().toString().trim();
        String school = et_school.getText().toString().trim();
        String start_date = et_start_date.getText().toString().trim();
        String end_date = et_end_date.getText().toString().trim();
        if (level.equals("") || school.equals("") || start_date.equals("") ||
                end_date.equals("")) {
            promptDialog("Ops!", "All field cannot be empty.", "OK");
        } else {
            loading_dialog = promptLoading("Saving information", "Please wait...");
            saveData(level, school, start_date, end_date);
        }
    }
    /**
     * step 11
     */
    private void saveData(String level, String school, String start_date, String end_date) {
        Education education = new Education();
        education.setLevel(level);
        education.setSchool(school);
        education.setStart_date(start_date);
        education.setEnd_date(end_date);
        String key =
                mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("education").push().getKey(
                );

        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("education").child(key).setValue(education).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    loading_dialog.dismiss();
                    promptDialog("Ops!", "Cannot save data. Make sure you have an internet connection. Please try again later.", "OK");
                }
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(EducationActivity.this,
                            android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(EducationActivity.this);
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
            builder = new AlertDialog.Builder(EducationActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(EducationActivity.this);
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
            builder = new AlertDialog.Builder(EducationActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(EducationActivity.this);
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