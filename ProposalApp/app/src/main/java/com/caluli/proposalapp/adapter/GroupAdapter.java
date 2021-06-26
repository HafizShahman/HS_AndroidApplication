package com.caluli.proposalapp.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.caluli.proposalapp.R;
import com.caluli.proposalapp.activity.GroupDetailsActivity;
import com.caluli.proposalapp.model.Proposal;
import com.caluli.proposalapp.util.ConstantVar;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.RecyclerVH> {

    ArrayList<Proposal> proposals;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    Context c;

    public GroupAdapter(Context c, FirebaseAuth mAuth, DatabaseReference mDatabase, ArrayList<Proposal> proposals) {

        this.c = c;
        this.mAuth = mAuth;
        this.mDatabase = mDatabase;
        this.proposals = proposals;
    }

    @Override
    public RecyclerVH onCreateViewHolder(ViewGroup PARENT, int viewType) {
        return new RecyclerVH(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(final RecyclerVH holder, final int position) {

        getGroupDetails(holder, position, proposals.get(position).getProposal_group());

        holder.tv_proposal_title.setText(proposals.get(position).getProposal_title());

    }

    private void getGroupDetails(RecyclerVH holder, int position, String group_id) {
        mDatabase.child(ConstantVar.TABLE_GROUP).child(group_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String group_id = snapshot.getKey();
                    String grooup_ref = snapshot.child(ConstantVar.COLUMN_GROUP_REF).getValue().toString();
                    holder.tv_group_no.setText("Group " + group_ref);

                    holder.ll_item.setonClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(c, GroupDetailsActivity.class);
                            intent.putExtra("HOLD_GROUP", group_id);
                            intent.putExtra("HOLD_PROPOSAL", proposals.get(position).getProposal_id());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public  int getItemCount() { return proposals.size();}

    public class RecyclerVH extends RecyclerView.ViewHolder {

        public LinearLayout ll_item;
        public TextView tv_group_no;
        public  TextView tv_proposal_title;

        public  RecyclerVH(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.row_group, parent, false));

            ll_item = itemView.findViewById(R.id.ll_item);
            tv_group_no = itemView.findViewById(R.id.tv_group_no);
            tv_proposal_title = itemView.findViewById(R.id.tv_proposal_title);


        }
    }


}
