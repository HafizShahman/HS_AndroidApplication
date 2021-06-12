package com.caman.sbrokoli;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.RecyclerVH> {
    ArrayList<Skill> skills;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    Context c;
    public SkillAdapter(Context c, ArrayList<Skill> skills, DatabaseReference mDatabase,
                        FirebaseAuth mAuth) {
        this.c = c;
        this.skills = skills;
        this.mDatabase = mDatabase;
        this.mAuth = mAuth;
    }
    @Override
    public SkillAdapter.RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SkillAdapter.RecyclerVH(LayoutInflater.from(parent.getContext()), parent);
    }
    @Override
    public void onBindViewHolder(final SkillAdapter.RecyclerVH holder, final int position) {
        holder.tv_skill.setText(skills.get(position).getSkill());
        holder.progress_bar.setProgress(skills.get(position).getLevel());
        /**
         * step 1
         */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptActionDialog(position);
            }
        });
    }
    /**
     * step 2
     */
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
                    Intent intent = new Intent(c, SkillActivity.class);
                    intent.putExtra("EXTRA_VAL", skills.get(position));
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
                .setMessage("Are you sure want to delete " +skills.get(position).getSkill() +
                        " from list?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("skill").child(skills.get(position).getId()).removeValue();
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
        return skills.size();
    }
    public class RecyclerVH extends RecyclerView.ViewHolder
    {
        public TextView tv_skill;
        public ProgressBar progress_bar;
        public RecyclerVH(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.row_skill, parent,
                    false));
            tv_skill = itemView.findViewById(R.id.tv_skill);
            progress_bar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
