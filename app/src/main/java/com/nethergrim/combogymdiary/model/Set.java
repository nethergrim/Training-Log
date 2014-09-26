package com.nethergrim.combogymdiary.model;


/**
 * Created by andrey_drobyazko on 21.08.14.
 */
public class Set extends TrainingExerciseObject {

    private String exerciseName;
    private int setsCount;

    public Set(){
        this.setsCount = 0;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public int getSetsCount() {
        return setsCount;
    }

    public void incrementSetsCount(){
        ++setsCount;
    }

    public void setSetsCount(int setsCount) {
        this.setsCount = setsCount;
    }
}
