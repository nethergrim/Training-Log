package com.nethergrim.combogymdiary.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ExerciseGroup implements Serializable {

    private String name;
    private int positionInGlobalArray = 0;
    private List<Exercise> list = new ArrayList<Exercise>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPositionInGlobalArray() {
        return positionInGlobalArray;
    }

    public void setPositionInGlobalArray(int positionInGlobalArray) {
        this.positionInGlobalArray = positionInGlobalArray;
    }

    public List<Exercise> getExercisesList() {
        return list;
    }

    public void setList(List<Exercise> list) {
        this.list = list;
    }


}
