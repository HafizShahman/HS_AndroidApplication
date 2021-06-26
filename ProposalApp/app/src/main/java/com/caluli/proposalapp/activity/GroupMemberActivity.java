package com.caluli.proposalapp.activity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import com.caluli.proposalapp.R;
import com.caluli.proposalapp.model.Group;
import com.caluli.proposalapp.model.User;
import com.caluli.proposalapp.util.ConstantVar;
import com.caluli.proposalapp.util.DateTimeUtil;
import com.caluli.proposalapp.util.DialogUtil;
import com.caluli.proposalapp.util.TinyDB;

public class GroupMemberActivity extends AppCompatActivity implements View.OnClickListener , TextWatcher {

    //group no
    LinearLayout ll_group_no;
    TextView tv_group_no;

    //leader
    EditText et_leader_matric;
    EditText et_leader_name;
    MaterialSpinner sp_leader_class;
    EditText et_leader_phone;

    //number #1
    LinearLayout ll_member1;
    LinearLayout ll_member1_form;
    EditText et_member1_matric;
    EditText et_member1_name;
    MaterialSpinner sp_member1_class;
    EditText et_member1_phone;

    //number #2
    LinearLayout ll_member2;
    LinearLayout ll_member2_form;
    EditText et_member2_matric;
    EditText et_member2_name;
    MaterialSpinner sp_member2_class;
    EditText et_member2_phone;

    Button btn_cancel;
    Button btn_save;

    android.app.AlertDialog loadingDialog;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //tiny db
    TinyDB tinyDB;

    String group_id = "";
    String group_ref = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        tinyDB = new TinyDB(this);

        setToolbar();
        createReference();
        addListener();

        //init ui
        ll_group_no.setVisibility(View.GONE);
        ll_member1_form.setVisibility(View.GONE);
        ll_member2_form.setVisibility(View.GONE);

        setSpinnerData();
        setData();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Group members");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void createReference() {
        ll_group_no = findViewById(R.id.ll_group_no);
        tv_group_no = findViewById(R.id.tv_group_no);
        et_leader_matric = findViewById(R.id.et_leader_matric);
        et_leader_name = findViewById(R.id.et_leader_name);
        sp_leader_class = findViewById(R.id.sp_leader_class);
        et_leader_phone = findViewById(R.id.et_leader_phone);
        ll_member1 = findViewById(R.id.ll_member1);
        ll_member1_form = findViewById(R.id.ll_member1_form);
        et_member1_matric = findViewById(R.id.et_member1_matric);
        et_member1_name = findViewById(R.id.et_member1_name);
        sp_member1_class = findViewById(R.id.sp_member1_class);
        et_member1_phone = findViewById(R.id.et_member1_phone);
        ll_member2 = findViewById(R.id.ll_member2);
        ll_member2_form = findViewById(R.id.ll_member2_form);
        et_member2_matric = findViewById(R.id.et_member2_matric);
        et_member2_name = findViewById(R.id.et_member2_name);
        sp_member2_class = findViewById(R.id.sp_member2_class);
        et_member2_phone = findViewById(R.id.et_member2_phone);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);

    }

    private void addListener() {
        et_leader_matric.addTextChangedListener(this);
        et_member1_matric.addTextChangedListener(this);
        et_member2_matric.addTextChangedListener(this);
        ll_member1.setOnClickListener(this);
        ll_member2.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_member1:
                if (ll_member1_form.getVisibility() == View.GONE) {
                    ll_member1_form.setVisibility(View.VISIBLE);
                } else {
                    ll_member1_form.setVisibility(View.GONE);
                }
                break;

            case R.id.ll_member2:
                if (ll_member2_form.getVisibility() == View.GONE) {
                    ll_member2_form.setVisibility(View.VISIBLE);
                } else {
                    ll_member2_form.setVisibility(View.GONE);
                }
                break;

            case R.id.btn_cancel:
                promptDiscardDialog();
                break;

            case R.id.btn_save:
                attemptSave();
                break;

        }
    }

    private void setSpinnerData() {
        //set adapter
        sp_leader_class.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,
                ConstantVar.ARRAY_CLASS
        ));
        sp_member1_class.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,
                ConstantVar.ARRAY_CLASS
        ));
        sp_member2_class.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,
                ConstantVar.ARRAY_CLASS
        ));
    }

    private void setData() {
        mDatabase.child(ConstantVar.TABLE_GROUP).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        if (snapshot1.child(ConstantVar.COLUMN_GROUP_USERS).hasChild(tinyDB.getString(ConstantVar.TNB_USER_MATRIC))) {
                            //already registered
                            group_id = snapshot1.getKey();
                            group_ref = snapshot1.child(ConstantVar.COLUMN_GROUP_REF).getValue().toString();

                            ll_group_no.setVisibility(View.VISIBLE);
                            tv_group_no.setText(group_ref);

                            int i = 2;
                            for (DataSnapshot snapshot2 : snapshot1.child(ConstantVar.COLUMN_GROUP_REF).getChildren()) {
                                String group_user_matric = snapshot2.getKey();
                                Boolean group_user_is_leader = (Boolean) snapshot2.getValue();

                                if (group_user_is_leader) {
                                    getUserData(1, group_user_matric);

                                } else {
                                    getUserData(i, group_user_matric);
                                    i++;
                                }

                            }

                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void attemptSave() {
        //leader
        String leader_matric = et_leader_matric.getText().toString();
        String leader_name = et_leader_name.getText().toString();
        String leader_class = sp_leader_class.getText().toString();
        String leader_phone = et_leader_phone.getText().toString();

        //member 1
        String member1_matric = et_member1_matric.getText().toString();
        String member1_name = et_member1_name.getText().toString();
        String member1_class = sp_member1_class.getText().toString();
        String member1_phone = et_member1_phone.getText().toString();

        //member 2
        String member2_matric = et_member2_matric.getText().toString();
        String member2_name = et_member2_name.getText().toString();
        String member2_class = sp_member2_class.getText().toString();
        String member2_phone = et_member2_phone.getText().toString();

        if (leader_matric.equals("") || leader_name.equals("") || leader_class.equals("") || leader_phone.equals("")) {
            new DialogUtil().promptDialog(this, "Ops!", "All visible field cannot be empty", "OK");

        } else if (ll_member1_form.getVisibility() == View.VISIBLE &&
                (member1_matric.equals("") || member1_name.equals("") || member1_class.equals("") || member1_phone.equals(""))
        ) {
            new DialogUtil().promptDialog(this, "Ops!", "All visible field cannot be empty", "OK");

        } else if (ll_member2_form.getVisibility() == View.VISIBLE &&
                (member2_matric.equals("") || member2_name.equals("") || member2_class.equals("") || member2_phone.equals(""))
        ) {
            new DialogUtil().promptDialog(this, "Ops!", "All visible field cannot be empty", "OK");

        } else if (ll_member2_form.getVisibility() == View.VISIBLE && ll_member1_form.getVisibility() == View.GONE) {
            new DialogUtil().promptDialog(this, "Ops!", "Group member #1 information cannot be empty", "OK");

        } else {

            loadingDialog = new DialogUtil().promptLoading(this, "Saving group information", "Please wait...");

            User leader = new User();
            leader.setUser_matric_no(leader_matric);
            leader.setUser_name(leader_name);
            leader.setUser_class(leader_class);
            leader.setUser_phone(leader_phone);

            User member1 = new User();
            leader.setUser_matric_no(member1_matric);
            leader.setUser_name(member1_name);
            leader.setUser_class(member1_class);
            leader.setUser_phone(member1_phone);

            User member2 = new User();
            leader.setUser_matric_no(member2_matric);
            leader.setUser_name(member2_name);
            leader.setUser_class(member2_class);
            leader.setUser_phone(member2_phone);

            /**
             * 1.save new user if not exists
             * 2.update user data if exists
             * 3.generate group no if save new record
             * 4.save group information
             */
            saveNewUser(leader, member1, member2);
        }
    }

    private void saveNewUser(User leader, User member1, User member2) {
        //check for leader
        checkLeader(leader, member1, member2);
    }

    private void checkLeader(User leader, User member1, User member2) {
        mDatabase.child(ConstantVar.TABLE_USER).orderByChild(ConstantVar.COLUMN_USER_MATRIC).equalTo(leader.getUser_matric_no()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    //exists. update user data
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        mDatabase.child(ConstantVar.TABLE_USER).child(snapshot1.getKey()).child(ConstantVar.COLUMN_USER_NAME).setValue(leader.getUser_name());
                        mDatabase.child(ConstantVar.TABLE_USER).child(snapshot1.getKey()).child(ConstantVar.COLUMN_USER_CLASS).setValue(leader.getUser_class());
                        mDatabase.child(ConstantVar.TABLE_USER).child(snapshot1.getKey()).child(ConstantVar.COLUMN_USER_PHONE).setValue(leader.getUser_class());
                        break;
                    }

                } else {
                    //not exists. save new user
                    writeNewUser(leader);
                }

                if (ll_member1_form.getVisibility() == View.VISIBLE) {
                    //check for member 1
                    checkMember1(leader, member1, member2);

                } else if (ll_member2_form.getVisibility() == View.VISIBLE) {
                    //check for member 2
                    checkMember2(leader, member1, member2);

                } else {
                    //done check
                    generateGroupNo(leader, member1, member2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkMember1(User leader, User member1, User member2) {
        mDatabase.child(ConstantVar.TABLE_USER).orderByChild(ConstantVar.COLUMN_USER_MATRIC).equalTo(member1.getUser_matric_no()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    //exist.update user data
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        mDatabase.child(ConstantVar.TABLE_USER).child(snapshot1.getKey()).child(ConstantVar.COLUMN_USER_NAME).setValue(member1.getUser_name());
                        mDatabase.child(ConstantVar.TABLE_USER).child(snapshot1.getKey()).child(ConstantVar.COLUMN_USER_CLASS).setValue(member1.getUser_class());
                        mDatabase.child(ConstantVar.TABLE_USER).child(snapshot1.getKey()).child(ConstantVar.COLUMN_USER_PHONE).setValue(member1.getUser_phone());
                        break;
                    }
                } else {
                    //not exits.save new user
                    writeNewUser(member1);
                }
                if (ll_member2_form.getVisibility() == View.VISIBLE) {
                    //check for member 1
                    checkMember2(leader, member1, member2);

                } else {
                    //done check
                    generateGroupNo(leader, member1, member2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkMember2(User leader, User member1, User member2) {
        mDatabase.child(ConstantVar.TABLE_USER).orderByChild(ConstantVar.COLUMN_USER_MATRIC).equalTo(member2.getUser_matric_no()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    //exist.update user data
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        mDatabase.child(ConstantVar.TABLE_USER).child(snapshot1.getKey()).child(ConstantVar.COLUMN_USER_NAME).setValue(member2.getUser_name());
                        mDatabase.child(ConstantVar.TABLE_USER).child(snapshot1.getKey()).child(ConstantVar.COLUMN_USER_CLASS).setValue(member2.getUser_class());
                        mDatabase.child(ConstantVar.TABLE_USER).child(snapshot1.getKey()).child(ConstantVar.COLUMN_USER_PHONE).setValue(member2.getUser_phone());
                        break;
                    }
                } else {

                    //not exists.save new user
                    writeNewUser(member2);
                }

                //done check
                generateGroupNo(leader, member1, member2);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void writeNewUser(User user1) {
        String ts = new DateTimeUtil().getCurrentTimestamp();

        User user = new User();
        user.setUser_matric_no(user1.getUser_matric_no());
        user.setUser_name(user1.getUser_name());
        user.setUser_class(user1.getUser_class());
        user.setUser_phone(user1.getUser_phone());
        user.setUser_created_on(ts);

        String push = mDatabase.child(ConstantVar.TABLE_USER).push().getKey();
        mDatabase.child(ConstantVar.TABLE_USER).child(push).setValue(user);
    }

    private void generateGroupNo(User leader, User member1, User member2) {
        if (group_ref.equals(" ")) {
            //generate
            mDatabase.child(ConstantVar.TABLE_GROUP).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        int count = (int) snapshot.getChildrenCount();
                        int group_no = count + 1;

                        saveGroupInformation(String.valueOf(group_no), leader, member1, member2);
                    } else {
                        saveGroupInformation("1", leader, member1, member2);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            saveGroupInformation(group_ref, leader, member1, member2);
        }
    }

    private void saveGroupInformation(String group_no, User leader, User member1, User member2) {

        //add new group

        Group group = new Group();
        group.setGroup_ref(group_no);
        group.setGroup_registered_on(new DateTimeUtil().getCurrentTimestamp());

        if (group_id.equals("")) {
            group_id = mDatabase.child(ConstantVar.TABLE_GROUP).push().getKey();

        }

        mDatabase.child(ConstantVar.TABLE_GROUP).child(group_id).setValue(group);

        //remove current group members
        mDatabase.child(ConstantVar.TABLE_GROUP).child(group_id).child(ConstantVar.COLUMN_GROUP_USERS).removeValue();

        //update group members
        mDatabase.child(ConstantVar.TABLE_GROUP).child(group_id).child(ConstantVar.COLUMN_GROUP_USERS).child(leader.getUser_matric_no()).setValue(true);
        if (ll_member1_form.getVisibility() == View.VISIBLE) {
            mDatabase.child(ConstantVar.TABLE_GROUP).child(group_id).child(ConstantVar.COLUMN_GROUP_USERS).child(member1.getUser_matric_no()).setValue(false);
        }
        if (ll_member2_form.getVisibility() == View.VISIBLE) {
            mDatabase.child(ConstantVar.TABLE_GROUP).child(group_id).child(ConstantVar.COLUMN_GROUP_USERS).child(member1.getUser_matric_no()).setValue(false);
        }
        loadingDialog.dismiss();
        finish();
    }

    private void promptDiscardDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Confirmation")
                .setMessage("Are you sure want to discard the chages?")
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable == et_leader_matric.getEditableText()) {
            getUserData(1,et_leader_matric.getText().toString());

        } else if (editable == et_member1_matric.getEditableText()) {
            getUserData(2,et_member1_matric.getText().toString());

        } else if (editable == et_member2_matric.getEditableText());{
            getUserData(3, et_member2_matric.getText().toString());
        }

    }

    private void getUserData(int user_position, String user_matric) {
        mDatabase.child(ConstantVar.TABLE_USER).orderByChild(ConstantVar.COLUMN_USER_MATRIC).equalTo(user_matric).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String user_name = "";
                        String user_class = "";
                        String user_phone = "";

                        if (snapshot1.hasChild(ConstantVar.COLUMN_USER_NAME)) {
                            user_name = snapshot1.child(ConstantVar.COLUMN_USER_NAME).getValue().toString();
                        }

                        if (snapshot1.hasChild(ConstantVar.COLUMN_USER_CLASS)) {
                            user_name = snapshot1.child(ConstantVar.COLUMN_USER_CLASS).getValue().toString();
                        }
                        if (snapshot1.hasChild(ConstantVar.COLUMN_USER_PHONE)) {
                            user_name = snapshot1.child(ConstantVar.COLUMN_USER_PHONE).getValue().toString();
                        }

                        setAutocompleteEdittext(user_position, user_matric, user_name, user_class, user_phone);
                    }

                } else {
                    setAutocompleteEdittext(user_position, user_matric, "",  "", " ");
                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    private void setAutocompleteEdittext(int user_position,String user_matric,String user_name,String user_class,String user_phone) {
        switch (user_position) {

            case 1:
                if (et_leader_matric.getText().toString().equals("")) {
                    et_leader_matric.setText(user_matric);
                }
                et_leader_name.setText(user_name);
                sp_leader_class.setText(user_class);
                et_leader_phone.setText(user_phone);
                break;


            case 2:
                if (et_member1_matric.getText().toString().equals("")) {
                    et_member1_matric.setText(user_matric);
                }
                et_member1_name.setText(user_name);
                sp_member1_class.setText(user_class);
                et_member1_phone.setText(user_phone);

                ll_member1_form.setVisibility(View.VISIBLE);
                break;

            case 3:
                if (et_member2_matric.getText().toString().equals("")) {
                    et_member2_matric.setText(user_matric);
                }
                et_member2_name.setText(user_name);
                sp_member2_class.setText(user_class);
                et_member2_phone.setText(user_phone);

                ll_member2_form.setVisibility(View.VISIBLE);
                break;



        }
    }
}