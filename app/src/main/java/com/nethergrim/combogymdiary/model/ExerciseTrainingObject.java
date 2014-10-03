package com.nethergrim.combogymdiary.model;


import java.io.Serializable;

public class ExerciseTrainingObject extends BaseDbObject implements Serializable {


    private Integer trainingProgramId;
    private Integer exerciseId;
    private Integer positionAtTraining;
    private Boolean superset;
    private Integer positionAtSuperset;
    private Integer supersetId;
    private Integer supersetColor;

    public Integer getTrainingProgramId() {
        return trainingProgramId;
    }

    public void setTrainingProgramId(Integer trainingProgramId) {
        this.trainingProgramId = trainingProgramId;
    }

    public Integer getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Integer exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Integer getPositionAtTraining() {
        return positionAtTraining;
    }

    public void setPositionAtTraining(Integer positionAtTraining) {
        this.positionAtTraining = positionAtTraining;
    }

    public Boolean getSuperset() {
        return superset;
    }

    public void setSuperset(Boolean superset) {
        this.superset = superset;
    }

    public Integer getPositionAtSuperset() {
        return positionAtSuperset;
    }

    public void setPositionAtSuperset(Integer positionAtSuperset) {
        this.positionAtSuperset = positionAtSuperset;
    }

    public Integer getSupersetId() {
        return supersetId;
    }

    public void setSupersetId(Integer supersetId) {
        this.supersetId = supersetId;
    }

    public Integer getSupersetColor() {
        return supersetColor;
    }

    public void setSupersetColor(Integer supersetColor) {
        this.supersetColor = supersetColor;
    }
}
