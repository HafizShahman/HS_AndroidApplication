package com.caman.sbrokoli;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AchievementActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    Calendar myCalendar;
    AlertDialog loading_dialog;
    MaterialSpinner sp_level;
    RadioGroup radioGroup;
    RadioButton radioButton;
    EditText et_title, et_year;
    Button btn_save;
    /**
     * step 5
     */
    String[] arr_title = {"Department", "Institution", "National", "International"};
    String selected_title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        getSupportActionBar().setTitle("Achievement");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        myCalendar = Calendar.getInstance();

        sp_level = findViewById(R.id.sp_level);
        et_title = findViewById(R.id.et_title);
        et_year = findViewById(R.id.et_year);
        radioGroup = findViewById(R.id.radioGroup);
        btn_save = findViewById(R.id.btn_save);

        sp_level.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,
                arr_title
        ));
        sp_level.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object
                    item) {
                selected_title = view.getText().toString();
            }
        });
        /**
         * step 22
         */

        if (getIntent().getSerializableExtra("EXTRA_VAL") != null) {
            /**
             * step 13
             */
            final Achievement achievement = (Achievement)
                    getIntent().getSerializableExtra("EXTRA_VAL");
            setPrefillData(achievement);
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptUpdate(achievement);
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

        et_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AchievementActivity.this, new
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
                                et_year.setText(sdf.format(myCalendar.getTime()));
                            }
                        }, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

    }

    private void setPrefillData(final Achievement achievement) {
        sp_level.setText(achievement.getLevel());
        et_title.setText(achievement.getTitle());
        et_year.setText(achievement.getYear());
    }

    private void attemptUpdate(Achievement achievement) {
        String level = sp_level.getText().toString().trim();
        String title = et_title.getText().toString().trim();
        String year = et_year.getText().toString().trim();
        String type = radioButton.getText().toString().trim();
        if (level.equals("") || title.equals("") || year.equals("") ||
                type.equals("")) {
            promptDialog("Ops!", "All field cannot be empty.", "OK");
        } else {
            loading_dialog = promptLoading("Saving information", "Please wait...");
            updateData(achievement.getId(), level, title, year, type);
        }
    }

    private void updateData(String id, String level, String title, String year, String type) {
        Achievement achievement = new Achievement();
        achievement.setLevel(level);
        achievement.setTitle(title);
        achievement.setYear(year);
        achievement.setType(type);

        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("achievement").child(id).setValue(achievement).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    loading_dialog.dismiss();
                    promptDialog("Ops!", "Cannot save data. Make sure you have an internet connection. Please try again later.", "OK");
                }
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(AchievementActivity.this,
                            android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(AchievementActivity.this);
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

    private void attemptSave() {
        String level = sp_level.getText().toString().trim();
        String title = et_title.getText().toString().trim();
        String year = et_year.getText().toString().trim();
        String type = radioButton.getText().toString().trim();

        int radioid = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioid);

        if (level.equals("") || title.equals("") || year.equals("")) {
            promptDialog("Ops!", "All field cannot be empty.", "OK");
        } else {
            loading_dialog = promptLoading("Saving information", "Please wait...");
            saveData(level, title, year, type);
        }
    }

    private void saveData(String level, String title, String year, String type) {
        Achievement achievement = new Achievement();
        achievement.setLevel(level);
        achievement.setTitle(title);
        achievement.setYear(year);
        achievement.setType(type);

        String key =
                mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("achievement").push().getKey();

        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("achievement").child(key).setValue(achievement).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    loading_dialog.dismiss();
                    promptDialog("Ops!", "Cannot save data. Make sure you have an internet connection. Please try again later.", "OK");
                }
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(AchievementActivity.this,
                            android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(AchievementActivity.this);
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

    private AlertDialog promptLoading(String title, String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(AchievementActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(AchievementActivity.this);
        }
        builder.setTitle(title)
                .setMessage(message);
        final AlertDialog ad = builder.show();
        return ad;
    }

    private void promptDialog(String title, String message, String button) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(AchievementActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(AchievementActivity.this);
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

    public void checkButton(View view){
        int radioid = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioid);
    }

}