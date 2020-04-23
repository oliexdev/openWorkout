/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.training;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    private TrainingFragment.TRAINING_MODE mode;
    private List<TrainingPlan> trainingPlanList;
    private Context context;
    private static OnTrainingClickListener onDefaultClickListener;
    private static OnTrainingClickListener onEditClickListener;
    private static OnTrainingClickListener onDeleteClickListener;
    private static OnTrainingClickListener onReorderClickListener;

    public TrainingsAdapter(Context aContext, List<TrainingPlan> trainingPlanList, TrainingFragment.TRAINING_MODE mode) {
        this.mode = mode;
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

        switch (mode) {
            case VIEW:
                holder.trophyView.setVisibility(View.VISIBLE);
                holder.reorderView.setVisibility(View.GONE);
                holder.deleteView.setVisibility(View.GONE);
                holder.editView.setVisibility(View.GONE);
                break;
            case EDIT:
                holder.trophyView.setVisibility(View.GONE);
                holder.reorderView.setVisibility(View.VISIBLE);
                holder.deleteView.setVisibility(View.VISIBLE);
                holder.editView.setVisibility(View.VISIBLE);
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

    public void setOnItemClickListener(OnTrainingClickListener onWorkoutClickListener) {
        this.onDefaultClickListener = onWorkoutClickListener;
    }

    public void setOnItemEditClickListener(OnTrainingClickListener onEditClickListener) {
        this.onEditClickListener = onEditClickListener;
    }

    public void setOnItemDeleteClickListener(OnTrainingClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void setOnItemReorderClickListener(OnTrainingClickListener onReorderClickListener) {
        this.onReorderClickListener = onReorderClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView;
        TextView nameView;
        TextView detailedView;
        TextView trophyView;
        ImageView reorderView;
        ImageView deleteView;
        ImageView editView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgView = itemView.findViewById(R.id.imgView);
            nameView = itemView.findViewById(R.id.nameView);
            detailedView = itemView.findViewById(R.id.detailedView);
            trophyView = itemView.findViewById(R.id.trophyView);
            reorderView = itemView.findViewById(R.id.reorderView);
            deleteView = itemView.findViewById(R.id.deleteView);
            editView = itemView.findViewById(R.id.editView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDefaultClickListener != null) {
                        onDefaultClickListener.onItemClick(getAdapterPosition(), v);
                    }
                }
            });

            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.onItemClick(getAdapterPosition(), v);
                    }
                }
            });

            editView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onEditClickListener != null) {
                        onEditClickListener.onItemClick(getAdapterPosition(), v);
                    }
                }
            });

            reorderView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        if (onReorderClickListener != null) {
                            onReorderClickListener.onItemClick(getAdapterPosition(), v);
                        }
                    }
                    return false;
                }
            });
        }
    }

    public interface OnTrainingClickListener {
        public void onItemClick(int position, View v);
    }
}
