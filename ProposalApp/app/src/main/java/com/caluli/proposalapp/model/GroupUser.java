package com.caluli.proposalapp.model;

import java.io.Serializable;

public class GroupUser extends android.app.Activity implements Serializable {

    public String group_user_matric;
    public Boolean group_user_is_leader;

    public GroupUser() {

    }

    public GroupUser(String group_user_matric, Boolean group_user_is_leader) {
        this.group_user_matric = group_user_matric;
        this.group_user_is_leader = group_user_is_leader;
    }

    public  String getGroup_user_matric() {
        return group_user_matric;
    }

    public  void setGroup_user_matric (String group_user_matric) {
      this.group_user_matric = group_user_matric;
    }

    public Boolean getGroup_user_is_leader() {
        return group_user_is_leader;
    }

    public void setGroup_user_is_leader(Boolean group_user_is_leader) {
        this.group_user_is_leader = group_user_is_leader;
    }

    public void add(int i, GroupUser groupUser) {
    }
}
