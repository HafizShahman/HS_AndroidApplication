package com.caman.sbrokoli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    /**
     * step 5
     */
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    CardView cv_dashboard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * step 4
         */
        getSupportActionBar().setTitle("Home");
        /**
         * step 6
         */
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        /**
         * step 7
         */
        cv_dashboard = findViewById(R.id.cv_dashboard);
        /**
         * step 8
         */
        cv_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ViewCvActivity.class));
            }
        });
    }

    /**
     * step 1
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_logout, menu);
        return super.onCreateOptionsMenu(menu);
    }
    /**
     * step 2
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(">>>>>>", "masuk selected");
        switch (item.getItemId()) {
            case R.id.action_logout:
                attemptLogout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * step 3
     */
    private void attemptLogout() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Are you sure want to logout?");
        builder1.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder1.show();
    }
}