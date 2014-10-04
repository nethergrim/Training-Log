package com.nethergrim.combogymdiary.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.nethergrim.combogymdiary.storage.DB;
import com.nethergrim.combogymdiary.model.TrainingDay;

import java.util.List;

/**
 * Created by Andrey Drobyazko on 28.09.2014.
 */
public class TrainingDayLoader extends AsyncTaskLoader<List<TrainingDay>> {

    public TrainingDayLoader(Context context) {
        super(context);
    }

    @Override
    public List<TrainingDay> loadInBackground() {
        return new DB(getContext()).getTrainingDays();
    }




}
