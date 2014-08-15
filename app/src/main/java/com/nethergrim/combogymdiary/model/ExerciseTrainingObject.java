package com.nethergrim.combogymdiary.model;


import java.io.Serializable;

public class ExerciseTrainingObject implements Serializable{

    private static final long serialVersionUID = 0L;

    private int id;
    private int trainingProgramId;
    private int exercise;
    private int positionAtTraining;
    private boolean superset;
    private int positionAtSuperset;
    private int supersetFirstItemId;

    public ExerciseTrainingObject(){}

    public ExerciseTrainingObject(int id){
        this.id = id;
        positionAtSuperset = 0;
        superset = false;
        supersetFirstItemId = 0;
        positionAtTraining = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrainingProgramId() {
        return trainingProgramId;
    }

    public void setTrainingProgramId(int trainingProgramId) {
        this.trainingProgramId = trainingProgramId;
    }

    public int getExerciseId() {
        return exercise;
    }

    public void setExerciseId(int exercise) {
        this.exercise = exercise;
    }

    public int getPositionAtTraining() {
        return positionAtTraining;
    }

    public void setPositionAtTraining(int positionAtTraining) {
        this.positionAtTraining = positionAtTraining;
    }

    public boolean isSuperset() {
        return superset;
    }

    public void setSuperset(boolean superset) {
        this.superset = superset;
    }

    public int getPositionAtSuperset() {
        return positionAtSuperset;
    }

    public void setPositionAtSuperset(int positionAtSuperset) {
        this.positionAtSuperset = positionAtSuperset;
    }

    public int getSupersetFirstItemId() {
        return supersetFirstItemId;
    }

    public void setSupersetFirstItemId(int supersetFirstItemId) {
        this.supersetFirstItemId = supersetFirstItemId;
    }
}
