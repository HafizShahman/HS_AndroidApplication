package com.caman.sbrokoli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;
public class SkillActivity extends AppCompatActivity {
    /**
     * step 2
     */
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    AlertDialog loading_dialog;
    EditText et_skill;
    SeekBar sb_level;
    Button btn_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill);


        /**
         * step 1
         */
        getSupportActionBar().setTitle("Skill");
        /**
         * step 10
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
        et_skill = findViewById(R.id.et_skill);
        sb_level = findViewById(R.id.sb_level);
        btn_save = findViewById(R.id.btn_save);
        if (getIntent().getSerializableExtra("EXTRA_VAL") != null) {
            /**
             * step 11
             */
            final Skill skill = (Skill) getIntent().getSerializableExtra("EXTRA_VAL");
            setPrefillData(skill);
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptUpdate(skill);
                }
            });
        } else {
            /**
             * step 5
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
     * step 12
     */
    private void setPrefillData(final Skill skill) {
        et_skill.setText(skill.getSkill());
        sb_level.setProgress(skill.getLevel());
    }
    /**
     * step 13
     */
    private void attemptUpdate(Skill skill) {
        String skill_name = et_skill.getText().toString().trim();
        int level = sb_level.getProgress();
        if (skill_name.equals("") || level==0) {
            promptDialog("Ops!", "All field cannot be empty.", "OK");
        } else {
            loading_dialog = promptLoading("Saving information", "Please wait...");
            updateData(skill.getId(), skill_name, level);
        }
    }
    /**
     * step 14
     */
    private void updateData(String id, String skill_name, int level) {
        Skill skill = new Skill();
        skill.setSkill(skill_name);
        skill.setLevel(level);

        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("skill").child(id).setValue
                (skill).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    loading_dialog.dismiss();
                    promptDialog("Ops!", "Cannot save data. Make sure you have an internet connection. Please try again later.", "OK");
                }
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(SkillActivity.this,
                            android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(SkillActivity.this);
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
     * step 6
     */
    private void attemptSave() {
        String skill = et_skill.getText().toString().trim();
        int level = sb_level.getProgress();
        if (skill.equals("") || level == 0) {
            promptDialog("Ops!", "All field cannot be empty.", "OK");
        } else {
            loading_dialog = promptLoading("Saving information", "Please wait...");
            saveData(skill, level);
        }
    }
    /**
     * step 9
     */
    private void saveData(String skill_name, int level) {
        Skill skill = new Skill();
        skill.setSkill(skill_name);
        skill.setLevel(level);
        String key =
                mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("skill").push().getKey();

        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("skill").child(key).setValue(skill).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    loading_dialog.dismiss();
                    promptDialog("Ops!", "Cannot save data. Make sure you have an internet connection. Please try again later.", "OK");
                }
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(SkillActivity.this,
                            android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(SkillActivity.this);
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
     * step 7
     */
    private AlertDialog promptLoading(String title, String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(SkillActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(SkillActivity.this);
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
            builder = new AlertDialog.Builder(SkillActivity.this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(SkillActivity.this);
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