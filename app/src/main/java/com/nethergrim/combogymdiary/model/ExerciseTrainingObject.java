package com.nethergrim.combogymdiary.model;


import com.nethergrim.combogymdiary.tools.BaseDbObjectInterface;

import java.io.Serializable;

public class ExerciseTrainingObject extends BaseDbObject implements Serializable {


    private Long trainingProgramId;
    private Long exerciseId;
    private Integer positionAtTraining;
    private Boolean superset;
    private Integer positionAtSuperset;
    private Long supersetId;
    private Integer supersetColor;

    public interface Columns extends BaseDbObjectInterface{
        public static final String TABLE = "";

        public static final String FIELD_TRAINING_PROGRAM_ID = "training_program_id";
        public static final String FIELD_EXERCISE_ID = "training_exercise_id";
        public static final String FIELD_POSITION_AT_TRAINING = "position_at_training";
        public static final String FIELD_SUPERSET_PRESENTS = "superset";
        public static final String FIELD_SUPERSET_POSITION = "superset_position";
        public static final String FIELD_SUPERSET_ID = "superset_id";
        public static final String FIELD_SUPERSET_COLOR = "superset_color";
    }

    public Long getTrainingProgramId() {
        return trainingProgramId;
    }

    public void setTrainingProgramId(Long trainingProgramId) {
        this.trainingProgramId = trainingProgramId;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
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

    public Long getSupersetId() {
        return supersetId;
    }

    public void setSupersetId(Long supersetId) {
        this.supersetId = supersetId;
    }

    public Integer getSupersetColor() {
        return supersetColor;
    }

    public void setSupersetColor(Integer supersetColor) {
        this.supersetColor = supersetColor;
    }
}
