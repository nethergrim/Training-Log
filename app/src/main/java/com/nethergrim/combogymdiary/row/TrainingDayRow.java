package com.nethergrim.combogymdiary.row;

import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.nethergrim.combogymdiary.MyApp;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.model.TrainingDay;
import com.nethergrim.combogymdiary.row.interfaces.TrainingDayRowInterface;

/**
 * Created by Andrey Drobyazko on 22.09.2014.
 */
public class TrainingDayRow implements ExpandableRow, View.OnClickListener {

    private TrainingDay trainingDay;
    private TrainingDayRowInterface listener;
    private ViewHolder holder;
    private boolean expanded = false;

    public TrainingDayRow(TrainingDay trainingDay, TrainingDayRowInterface callback) {
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
            holder.btnDelete = (ImageButton) view.findViewById(R.id.btn_delete);
            holder.btnEdit = (ImageButton) view.findViewById(R.id.btn_edit);
            view.setTag(holder);
        }
        holder = (ViewHolder) view.getTag();
        holder.btnStartTraining.setOnClickListener(this);
        holder.btnDelete.setOnClickListener(this);
        holder.btnEdit.setOnClickListener(this);
        holder.textName.setText(trainingDay.getTrainingName());
        holder.textDayOfWeek.setText(trainingDay.getDayOfWeek().getName(holder.textDayOfWeek.getContext()));
        holder.image.setImageUrl(trainingDay.getImageUrl());
        if (expanded){
            changeLayoutHeight(1);
        } else {
            changeLayoutHeight(0);
        }
        return view;
    }

    private void changeLayoutHeight(float f){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.layoutExpandable.getLayoutParams();
        params.height = (int) (f * MyApp.context.getResources().getInteger(R.integer.training_day_row_expanded_height) * MyApp.density);
        holder.layoutExpandable.setAlpha(f);
        holder.layoutExpandable.setLayoutParams(params);
        RelativeLayout.LayoutParams textParams = (RelativeLayout.LayoutParams) holder.textName.getLayoutParams();
        textParams.leftMargin = (int) (f * (  (holder.layoutExpandable.getWidth() / 2) - (holder.textName.getWidth() / 2) ));
        holder.textName.setLayoutParams(textParams);

        RelativeLayout.LayoutParams dayOkWeekTextParams = (RelativeLayout.LayoutParams) holder.textDayOfWeek.getLayoutParams();
        dayOkWeekTextParams.leftMargin = (int) (f * (  (holder.layoutExpandable.getWidth() / 2) - (holder.textDayOfWeek.getWidth() / 2) ));
        holder.textDayOfWeek.setLayoutParams(dayOkWeekTextParams);
    }

    @Override
    public void toggle() {
        ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float value = (Float) valueAnimator.getAnimatedValue();
                changeLayoutHeight(value);
            }
        };
        ValueAnimator valueAnimator;
        if (expanded) {
            valueAnimator = ValueAnimator.ofFloat(1f, 0f);
        } else {
            valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        }
        valueAnimator.setDuration(MyApp.context.getResources().getInteger(R.integer.animation_expand_time));
        valueAnimator.addUpdateListener(animatorUpdateListener);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.start();
        expanded = !expanded;
    }

    @Override
    public boolean isOpened() {
        return expanded;
    }

    @Override
    public long getId() {
        return trainingDay.getId();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_delete:
                toggle();
                listener.onDeletePressed(trainingDay);
                break;
            case R.id.btn_edit:
                listener.onEditPressed(trainingDay);
                break;
            case R.id.btn_start_training:
                listener.onTrainingStartPressed(trainingDay);
                break;
        }
    }

    private class ViewHolder {
        TextView textName;
        TextView textDayOfWeek;
        SmartImageView image;
        Button btnStartTraining;
        RelativeLayout layoutExpandable;
        ImageButton btnDelete;
        ImageButton btnEdit;
    }
}
