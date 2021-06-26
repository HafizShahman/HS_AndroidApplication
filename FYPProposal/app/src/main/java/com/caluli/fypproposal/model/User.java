package com.caluli.fypproposal.model;


import java.io.Serializable;

public class User implements Serializable {


    public String user_id;
    public String user_name;
    public String user_matric_no;
    public String user_class;
    public String user_email;
    public String user_phone;
    public String user_image;
    public String user_created_on;
    public Boolean user_is_admin;

    public User() {

    }

    public User(String user_id, String user_name, String user_class, String user_email, String user_phone, String user_image, String user_created_on, Boolean user_is_admin) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_matric_no = user_matric_no;
        this.user_class = user_class;
        this.user_email = user_email;
        this.user_phone = user_phone;
        this.user_image = user_image;
        this.user_created_on = user_created_on;
        this.user_is_admin = user_is_admin;

    }

    public String getUser_id() { return user_id; }

    public void setUser_id(String user_id) {this.user_id = user_id;}

    public String getUser_name() { return user_name;}

    public void setUser_name(String user_name) {this.user_name = user_name;}

    public String getUser_matric_no() { return user_matric_no;}

    public void setUser_matric_no(String user_matric_no) {this.user_matric_no = user_matric_no;}

    public String getUser_class() { return user_class; }

    public void setUser_class(String user_class) { this.user_class = user_class;}

    public String getUser_email() { return user_email;}

    public String getUser_phone() {return user_phone;}

    public void setUser_phone(String user_phone) { this.user_phone = user_phone;}

    public String getUser_image() { return user_image;}

    public void setUser_image(String user_image) { this.user_image = user_image;}

    public String getUser_created_on() { return user_created_on;}

    public void setUser_created_on(String user_created_on) { this.user_created_on = user_created_on;}

    public Boolean getUser_is_admin() {return  user_is_admin;}

    public void setUser_is_admin(Boolean user_is_admin) { this.user_is_admin = user_is_admin;}

}
