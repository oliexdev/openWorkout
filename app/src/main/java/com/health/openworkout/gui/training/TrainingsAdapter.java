/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.training;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.gui.datatypes.GenericAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import timber.log.Timber;

public class TrainingsAdapter extends GenericAdapter<TrainingsAdapter.ViewHolder> {
    private List<TrainingPlan> trainingPlanList;
    private Context context;

    public TrainingsAdapter(Context aContext, List<TrainingPlan> trainingPlanList) {
        super(aContext);
        this.context = aContext;
        this.trainingPlanList = trainingPlanList;
    }

    @Override
    public TrainingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_training, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        TrainingPlan trainingPlan = trainingPlanList.get(position);
        holder.nameView.setText(trainingPlan.getName());

        if (trainingPlan.isImagePathExternal()) {
            holder.imgView.setImageURI(Uri.parse(trainingPlan.getImagePath()));
        } else {
            try {
                InputStream ims = context.getAssets().open("image/" + trainingPlan.getImagePath());
                holder.imgView.setImageDrawable(Drawable.createFromStream(ims, null));
                ims.close();
            } catch (IOException ex) {
                Timber.e(ex);
            }
        }

        if (!trainingPlan.getWorkoutSessions().isEmpty() &&
                trainingPlan.getWorkoutSessionSize() == trainingPlan.finishedSessionSize()) {
            trainingPlan.setCountFinishedTraining(trainingPlan.getCountFinishedTraining() + 1);
            trainingPlan.resetFinishedSessions();
            OpenWorkout.getInstance().updateTrainingPlan(trainingPlan);
        }

        if (trainingPlan.getCountFinishedTraining() == 0) {
            holder.trophyView.setBackgroundResource(R.drawable.ic_trophy_disabled);
        } else {
            holder.trophyView.setBackgroundResource(R.drawable.ic_trophy_enabled);
            holder.trophyView.setText(Integer.toString(trainingPlan.getCountFinishedTraining()));
        }

        holder.detailedView.setText(String.format(context.getString(R.string.label_session_size_completed), trainingPlan.finishedSessionSize(), trainingPlan.getWorkoutSessionSize()));

        switch (getMode()) {
            case VIEW:
                holder.trophyView.setVisibility(View.VISIBLE);
                break;
            case EDIT:
                holder.trophyView.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public long getItemId(int position) {
        return trainingPlanList.get(position).getTrainingPlanId();
    }

    @Override
    public int getItemCount() {
        return trainingPlanList.size();
    }

    static class ViewHolder extends GenericAdapter.ViewHolder {
        ImageView imgView;
        TextView nameView;
        TextView detailedView;
        TextView trophyView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgView = itemView.findViewById(R.id.imgView);
            nameView = itemView.findViewById(R.id.nameView);
            detailedView = itemView.findViewById(R.id.detailedView);
            trophyView = itemView.findViewById(R.id.trophyView);
        }
    }
}
