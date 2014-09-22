package com.nethergrim.combogymdiary.row;

import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.nethergrim.combogymdiary.MyApp;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.model.TrainingDay;

/**
 * Created by Andrey Drobyazko on 22.09.2014.
 */
public class TrainingDayRow implements ExpandableRow {

    private TrainingDay trainingDay;
    private OnTrainingDayRowPressed listener;
    private ViewHolder holder;
    private boolean expanded = false;
    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.layoutExpandable.getLayoutParams();
            Float value = (Float) valueAnimator.getAnimatedValue();
            params.height = (int) (value * 256 * MyApp.density);
            holder.layoutExpandable.setLayoutParams(params);
        }
    };
    private View.OnClickListener expandListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    public TrainingDayRow(TrainingDay trainingDay, OnTrainingDayRowPressed callback) {
        this.trainingDay = trainingDay;
        this.listener = callback;
    }

    @Override
    public RowType getType() {
        return RowType.TRAINING_DAY;
    }

    @Override
    public View getView(View convertView, LayoutInflater inflater) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.row_training_day, null);
            ViewHolder holder = new ViewHolder();
            holder.textName = (TextView) view.findViewById(R.id.text_training_day_name);
            holder.textDayOfWeek = (TextView) view.findViewById(R.id.text_day_of_week);
            holder.image = (SmartImageView) view.findViewById(R.id.image);
            holder.btnStartTraining = (Button) view.findViewById(R.id.btn_start_training);
            holder.layoutExpandable = (RelativeLayout) view.findViewById(R.id.layout_expandable);
            view.setTag(holder);
        }
        holder = (ViewHolder) view.getTag();
        holder.btnStartTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onTrainingDayPressed(TrainingDayRow.this.trainingDay);
            }
        });
        holder.textName.setText(trainingDay.getTrainingName());
        holder.textDayOfWeek.setText(trainingDay.getDayOfWeek().getName(holder.textDayOfWeek.getContext()));

        holder.image.setImageUrl("http://www.bodybuilding.com/exercises/exerciseImages/sequences/64/Male/m/64_2.jpg");
        return view;
    }

    @Override
    public void toggle() {
        ValueAnimator valueAnimator;
        if (expanded) {
            valueAnimator = ValueAnimator.ofFloat(1f, 0f);
        } else {
            valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        }
        valueAnimator.setDuration(350);
        valueAnimator.addUpdateListener(animatorUpdateListener);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.start();
        expanded = !expanded;
    }

    @Override
    public long getId() {
        return trainingDay.getId();
    }

    public interface OnTrainingDayRowPressed {
        public void onTrainingDayPressed(TrainingDay trainingDay1);
    }

    private class ViewHolder {
        TextView textName;
        TextView textDayOfWeek;
        SmartImageView image;
        Button btnStartTraining;
        RelativeLayout layoutExpandable;
    }


}
