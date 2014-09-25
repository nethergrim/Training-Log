package com.nethergrim.combogymdiary.model;

import com.nethergrim.combogymdiary.tools.BaseDbObjectInterface;

/**
 * Created by Andrey Drobyazko on 22.09.2014.
 */
public class TrainingDay extends BaseDbObject {

    private String trainingName = "";
    private DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
    private String imageUrl = "";
    private Integer color;

    public interface Columns extends BaseDbObjectInterface{
        public static final String TABLE = "trainings_tab";

        public static final String FIELD_TRAINING_NAME = "training_name";
        public static final String FIELD_DAY_OF_WEEK = "day_of_week";
        public static final String FIELD_IMAGE_URL = "image_url";
        public static final String FIELD_COLOR = "color";
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }
}
