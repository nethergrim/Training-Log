package com.nethergrim.combogymdiary.model;

import java.io.Serializable;


public class TrainingProgram implements Serializable {

    private static final long serialVersionUID = 0L;
    private long id;
    private String name;

    public TrainingProgram(int id, String name){
        setId(id);
        setName(name);
    }

    public TrainingProgram(int id){
        setId(id);
        setName("");
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
