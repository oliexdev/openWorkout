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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.TrainingPlan;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import timber.log.Timber;

public class TrainingsAdapter extends RecyclerView.Adapter<TrainingsAdapter.ViewHolder> {
    private List<TrainingPlan> trainingPlanList;
    private Context context;
    private static OnTrainingClickListener onTrainingClickListener;

    public TrainingsAdapter(Context aContext, List<TrainingPlan> trainingPlanList) {
        this.context = aContext;
        this.trainingPlanList = trainingPlanList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_training, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

    }

    @Override
    public long getItemId(int position) {
        return trainingPlanList.get(position).getTrainingPlanId();
    }

    @Override
    public int getItemCount() {
        return trainingPlanList.size();
    }

    public void setOnItemClickListener(OnTrainingClickListener onTrainingClickListener) {
        this.onTrainingClickListener = onTrainingClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTrainingClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }
    }

    public interface OnTrainingClickListener {
        public void onItemClick(int position, View v);
    }
}
