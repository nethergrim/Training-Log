package com.nethergrim.combogymdiary.row.interfaces;

import com.nethergrim.combogymdiary.model.TrainingDay;

/**
 * Created by andrey_drobyazko on 26.09.14.
 */
public interface TrainingDayRowInterface {

    public void onTrainingStartPressed(TrainingDay trainingDay);
    public void onDeletePressed(TrainingDay trainingDay);
    public void onEditPressed(TrainingDay trainingDay);
}
