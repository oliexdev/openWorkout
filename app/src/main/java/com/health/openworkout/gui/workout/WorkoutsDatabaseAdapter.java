/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.workout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.gui.datatypes.GenericAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import timber.log.Timber;

public class WorkoutsDatabaseAdapter extends RecyclerView.Adapter<WorkoutsDatabaseAdapter.ViewHolder> {
    private final List<WorkoutItem> workoutItemList;
    private Context context;
    private static GenericAdapter.OnGenericClickListener onItemClickListener;

    public WorkoutsDatabaseAdapter(Context aContext, List<WorkoutItem> workoutItemList) {
        this.context = aContext;
        this.workoutItemList = workoutItemList;
    }

    public void setOnItemClickListener(GenericAdapter.OnGenericClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public WorkoutsDatabaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workoutdatabase, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        WorkoutItem workoutItem = workoutItemList.get(position);
        holder.nameView.setText(workoutItem.getName());
        holder.detailedView.setText(workoutItem.getDescription());

        if (workoutItem.isImagePathExternal()) {
            holder.imgView.setImageURI(Uri.parse(workoutItem.getImagePath()));
        } else {
            try {
                String subFolder;
                if (OpenWorkout.getInstance().getCurrentUser().isMale()) {
                    subFolder = "male";
                } else {
                    subFolder = "female";
                }

                InputStream ims = context.getAssets().open("image/" + subFolder + "/" + workoutItem.getImagePath());
                holder.imgView.setImageDrawable(Drawable.createFromStream(ims, null));

                ims.close();
            } catch (IOException ex) {
                holder.imgView.setImageResource(0);
                Timber.e(ex);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return workoutItemList.get(position).getWorkoutItemId();
    }

    @Override
    public int getItemCount() {
        return workoutItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView;
        TextView nameView;
        TextView detailedView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgView = itemView.findViewById(R.id.imgView);
            nameView = itemView.findViewById(R.id.nameView);
            detailedView = itemView.findViewById(R.id.detailedView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }
    }
}
