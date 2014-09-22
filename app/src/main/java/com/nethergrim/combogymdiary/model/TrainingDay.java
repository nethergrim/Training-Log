package com.nethergrim.combogymdiary.model;

/**
 * Created by Andrey Drobyazko on 22.09.2014.
 */
public class TrainingDay {

    private String trainingName = "";

    private long id = 0;

    private DayOfWeek dayOfWeek = DayOfWeek.MONDAY;

    private String imageUrl = "";

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
