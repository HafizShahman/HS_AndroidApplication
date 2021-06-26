package com.caluli.proposalapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.caluli.proposalapp.R;
import com.caluli.proposalapp.model.GroupUser;
import com.caluli.proposalapp.util.ConstantVar;

import java.util.ArrayList;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.RecyclerVH> {

    ArrayList<GroupUser> groupUsers;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    Context c;

    public GroupMemberAdapter(Context c, FirebaseAuth mAuth, DatabaseReference mDatabase, ArrayList<GroupUser> groupUsers) {
        this.c = c;
        this.mAuth = mAuth;
        this.mDatabase = mDatabase;
        this.groupUsers = groupUsers;
    }

    @Override
    public RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerVH(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(final RecyclerVH holder, final int position) {

        if (groupUsers.get(position).group_user_is_leader) {
            holder.tv_desc.setText("Group leader");
        } else {
            holder.tv_desc.setText("Group member");
        }

        getUserData(holder, groupUsers.get(position).getGroup_user_matric());
    }


    private void getUserData(RecyclerVH holder, String user_matric) {
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
                            user_class = snapshot1.child(ConstantVar.COLUMN_USER_CLASS).getValue().toString();
                        }
                        if (snapshot1.hasChild(ConstantVar.COLUMN_USER_PHONE)) {
                            user_phone = snapshot1.child(ConstantVar.COLUMN_USER_PHONE).getValue().toString();
                        }

                        holder.tv_matric.setText(user_matric);
                        holder.tv_name.setText(user_name);
                        holder.tv_class.setText(user_class);
                        holder.tv_phone.setText(user_phone);

                        break;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return groupUsers.size();
    }

    public class RecyclerVH extends RecyclerView.ViewHolder {

        public TextView tv_desc;
        public TextView tv_matric;
        public TextView tv_name;
        public TextView tv_class;
        public TextView tv_phone;

        public RecyclerVH(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.row_group_member, parent,
                    false));

            tv_desc = itemView.findViewById(R.id.tv_desc);
            tv_matric = itemView.findViewById(R.id.tv_matric);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_class = itemView.findViewById(R.id.tv_class);
            tv_phone = itemView.findViewById(R.id.tv_phone);

        }

    }
}
