package com.caman.sbrokoli;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.RecyclerVH> {
    ArrayList<Achievement> achievements;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    Context c;
    public AchievementAdapter(Context c, ArrayList<Achievement> achievements,
                              DatabaseReference mDatabase, FirebaseAuth mAuth) {
        this.c = c;
        this.achievements = achievements;
        this.mDatabase = mDatabase;
        this.mAuth = mAuth;
    }
    @Override
    public RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerVH(LayoutInflater.from(parent.getContext()), parent);
    }
    @Override
    public void onBindViewHolder(final RecyclerVH holder, final int position) {
        holder.tv_level.setText(achievements.get(position).getLevel());
        holder.tv_title.setText(achievements.get(position).getTitle());
        holder.tv_year.setText(achievements.get(position).getYear());
        holder.tv_type.setText(achievements.get(position).getType());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptActionDialog(position);
            }
        });
    }

    private void promptActionDialog(final int position) {
        final CharSequence[] items={"Edit", "Delete"};
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(c, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(c);
        }
        builder.setTitle("Select Action");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Edit")) {
                    /**
                     * step 3
                     */
                    Intent intent = new Intent(c, AchievementActivity.class);
                    intent.putExtra("EXTRA_VAL", achievements.get(position));
                    c.startActivity(intent);
                } else if (items[i].equals("Delete")) {
                    /**
                     * step 4
                     */
                    promptDeleteDialog(position);
                }
            }
        });
        builder.show();
    }
    /**
     * step 5
     */
    private void promptDeleteDialog(final int position) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(c, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(c);
        }
        builder.setTitle("Delete data")
                .setMessage("Are you sure want to delete "
                        +achievements.get(position).getLevel() + " from list?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("achievement").child(achievements.get(position).getId()).removeValue();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }
    public class RecyclerVH extends RecyclerView.ViewHolder
    {
        public TextView tv_level, tv_title, tv_year, tv_type;
        public RecyclerVH(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.row_achievement, parent,
                    false));
            tv_level = itemView.findViewById(R.id.tv_level);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_year = itemView.findViewById(R.id.tv_year);
            tv_type = itemView.findViewById(R.id.tv_type);
        }
    }
}
