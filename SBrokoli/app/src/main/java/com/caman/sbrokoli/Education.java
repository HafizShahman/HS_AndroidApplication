package com.caman.sbrokoli;

import java.io.Serializable;
public class Education implements Serializable {
    private String id;
    private String school;
    private String level;
    private String start_date;

    private String end_date;
    public String getSchool() {
        return school;
    }
    public void setSchool(String school) {
        this.school = school;
    }
    public String getLevel() {
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }
    public String getStart_date() {
        return start_date;
    }
    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }
    public String getEnd_date() {
        return end_date;
    }
    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
