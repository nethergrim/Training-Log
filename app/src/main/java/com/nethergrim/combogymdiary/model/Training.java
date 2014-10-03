package com.nethergrim.combogymdiary.model;

import com.nethergrim.combogymdiary.tools.BaseDbObjectInterface;

import java.io.Serializable;

/**
 * Created by Andrey Drobyazko on 28.09.2014.
 */
public class Training extends BaseDbObject implements Serializable {

    private String date;
    private String comment;
    @Deprecated
    private String totalTime;
    private Long totalWeight;
    private String trainingDayName;
    private Long trainingDayId;
    private Long timeOfEnd;


    public interface Columns extends BaseDbObjectInterface{
        public static final String TABLE  = "comment_table";

        public static final String FIELD_DATE = "Date";
        public static final String FIELD_COMMENT = "comment_to_training";
        @Deprecated
        public static final String FIELD_TOTAL_TIME = "time_of_training";
        public static final String FIELD_TOTAL_WEIGHT = "total_weight";

        public static final String FIELD_TRAINING_DAY_NAME = "training_name";
        public static final String FIELD_TRAINING_DAY_ID = "training_day_id";
        public static final String FIELD_TIME_OF_END = "end_time";
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public Long getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Long totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getTrainingDayName() {
        return trainingDayName;
    }

    public void setTrainingDayName(String trainingDayName) {
        this.trainingDayName = trainingDayName;
    }

    public Long getTrainingDayId() {
        return trainingDayId;
    }

    public void setTrainingDayId(Long trainingDayId) {
        this.trainingDayId = trainingDayId;
    }

    public Long getTimeOfEnd() {
        return timeOfEnd;
    }

    public void setTimeOfEnd(Long timeOfEnd) {
        this.timeOfEnd = timeOfEnd;
    }
}
