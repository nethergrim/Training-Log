package com.nethergrim.combogymdiary.model;


import java.io.Serializable;

public class ExerciseTrainingObject implements Serializable{

    private static final long serialVersionUID = 0L;

    private int id;
    private int trainingProgramId;
    private int exerciseid;
    private int positionAtTraining;
    private boolean superset;
    private int positionAtSuperset;
    private int supersetId;
    private int supersetColor;

    public ExerciseTrainingObject(){}

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
        return exerciseid;
    }

    public void setExerciseId(int exercise) {
        this.exerciseid = exercise;
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

    public int getSupersetId() {
        return supersetId;
    }

    public void setSupersetId(int supersetId) {
        this.supersetId = supersetId;
    }

    public int getSupersetColor() {
        return supersetColor;
    }

    public void setSupersetColor(int supersetColor) {
        this.supersetColor = supersetColor;
    }
}
