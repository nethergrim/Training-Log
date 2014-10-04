package com.nethergrim.combogymdiary.model;

import java.io.Serializable;

/**
 * Created by nethergrim on 04.10.2014.
 */
public class Set extends ExerciseTrainingObject implements Serializable {

    private String exerciseName;
    private Integer setCount;
    private String trainingName;
    private String date;
    private Integer weight;
    private Integer reps;


    public interface Columns extends ExerciseTrainingObject.Columns{
        public static final String TABLE = "main_tab";

        public static final String FIELD_TRAINING_NAME = "training_name";
        public static final String FIELD_EXERCISE_NAME = "exercise_name";
        public static final String FIELD_DATE = "Date";
        public static final String FIELD_WEIGHT = "Weight";
        public static final String FIELD_REPS = "Reps";
        public static final String FIELD_SET_COUNT = "SetsN";
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public Integer getSetCount() {
        return setCount;
    }

    public void setSetCount(Integer setCount) {
        this.setCount = setCount;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }
}
