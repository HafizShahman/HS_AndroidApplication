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
public class ReferencesAdapter extends RecyclerView.Adapter<ReferencesAdapter.RecyclerVH> {
    ArrayList<References> referencess;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    Context c;
    public ReferencesAdapter(Context c, ArrayList<References> referencess, DatabaseReference
            mDatabase, FirebaseAuth mAuth) {
        this.c = c;
        this.referencess = referencess;
        this.mDatabase = mDatabase;
        this.mAuth = mAuth;
    }
    @Override
    public RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerVH(LayoutInflater.from(parent.getContext()), parent);
    }
    @Override
    public void onBindViewHolder(final RecyclerVH holder, final int position) {
        holder.tv_name.setText(referencess.get(position).getName());
        holder.tv_designation.setText(referencess.get(position).getDesignation());
        holder.tv_company.setText(referencess.get(position).getCompany());
        holder.tv_phone.setText(referencess.get(position).getPhone());
        holder.tv_emailAddress.setText(referencess.get(position).getEmail());
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
                    Intent intent = new Intent(c, ReferencesActivity.class);
                    intent.putExtra("EXTRA_VAL", referencess.get(position));
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
                        +referencess.get(position).getName() + " from list?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        mDatabase.child("cv").child(mAuth.getCurrentUser().getUid()).child("references").child(referencess.get(position).getId()).removeValue();
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
        return referencess.size();
    }
    public class RecyclerVH extends RecyclerView.ViewHolder
    {
        public TextView tv_name, tv_designation, tv_company, tv_phone, tv_emailAddress;
        public RecyclerVH(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.row_references, parent,
                    false));
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_designation = itemView.findViewById(R.id.tv_designation);
            tv_company = itemView.findViewById(R.id.tv_company);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            tv_emailAddress = itemView.findViewById(R.id.tv_emailAddress);
        }
    }
}
