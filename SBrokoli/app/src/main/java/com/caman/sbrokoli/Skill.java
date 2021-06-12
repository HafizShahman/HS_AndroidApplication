package com.caman.sbrokoli;

import java.io.Serializable;
public class Skill implements Serializable {
    private String id;
    private String skill;
    private int level;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSkill() {
        return skill;
    }
    public void setSkill(String skill) {
        this.skill = skill;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
}