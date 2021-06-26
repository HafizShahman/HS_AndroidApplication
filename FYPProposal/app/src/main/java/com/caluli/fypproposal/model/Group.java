package com.caluli.fypproposal.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable {

    public String group_id;
    public String group_ref;
    public ArrayList<GroupUser> groupUsers;
    public String group_registered_on;

    public Group() {

    }

    public Group(String group_id, String group_ref, ArrayList<GroupUser> groupUsers, String group_registered_on) {

        this.group_id = group_id;
        this.group_ref = group_ref;
        this.group_registered_on = group_registered_on;

    }

    public String getGroup_id() {
        return group_id;
    }

    public  void setGroup_id( String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_ref() {
        return group_ref;
    }

    public  void setGroup_ref( String group_ref) {
        this.group_ref = group_ref;
    }

    public ArrayList<GroupUser> getGroupUsers() {
        return groupUsers;
    }

    public void setGroupUsers(ArrayList<GroupUser> groupUsers) {
        this.groupUsers = groupUsers;
    }

    public String getGroup_registered_on() {
        return group_registered_on;
    }

    public void setGroup_registered_on(String group_registered_on) {
        this.group_registered_on = group_registered_on;
    }
}
