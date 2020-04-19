/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.training;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.TrainingPlan;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import timber.log.Timber;

public class TrainingsAdapter extends BaseAdapter {
    private List<TrainingPlan> trainingPlanList;
    private LayoutInflater layoutInflater;
    private Context context;

    public TrainingsAdapter(Context aContext, List<TrainingPlan> trainingPlanList) {
        this.context = aContext;
        this.trainingPlanList = trainingPlanList;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return trainingPlanList.size();
    }

    @Override
    public Object getItem(int position) {
        return trainingPlanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_item_training, null);
            holder = new ViewHolder();
            holder.imgView = convertView.findViewById(R.id.imgView);
            holder.nameView = convertView.findViewById(R.id.nameView);
            holder.detailedView = convertView.findViewById(R.id.detailedView);
            holder.trophyView = convertView.findViewById(R.id.trophyView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TrainingPlan trainingPlan = trainingPlanList.get(position);
        holder.nameView.setText(trainingPlan.getName());

        try {
            InputStream ims = context.getAssets().open("image/" + trainingPlan.getImagePath());
            holder.imgView.setImageDrawable(Drawable.createFromStream(ims, null));
            ims.close();
        }
        catch(IOException ex) {
            Timber.e(ex);
        }

        if (trainingPlan.getWorkoutSessionSize() == trainingPlan.finishedSessionSize()) {
            trainingPlan.setCountFinishedTraining(trainingPlan.getCountFinishedTraining() + 1);
            trainingPlan.resetFinishedSessions();
            OpenWorkout.getInstance().updateTrainingPlan(trainingPlan);
        }

        if (trainingPlan.getCountFinishedTraining() == 0) {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            holder.trophyView.getBackground().setColorFilter(filter);
        } else {
            holder.trophyView.setText(Integer.toString(trainingPlan.getCountFinishedTraining()));
        }

        holder.detailedView.setText(String.format(context.getString(R.string.label_session_size_completed), trainingPlan.finishedSessionSize(), trainingPlan.getWorkoutSessionSize()));

        return convertView;
    }

    static class ViewHolder {
        ImageView imgView;
        TextView nameView;
        TextView detailedView;
        TextView trophyView;
    }
}
