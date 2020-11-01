/*
 * Copyright (C) 2020 olie.xdev <olie.xdev@googlemail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.health.openworkout.gui.workout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.gui.datatypes.GenericAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import timber.log.Timber;

public class WorkoutsAdapter extends GenericAdapter<WorkoutsAdapter.ViewHolder> {
    private final List<WorkoutItem> workoutItemList;
    private Context context;

    public WorkoutsAdapter(Context aContext, List<WorkoutItem> workoutItemList) {
        super(aContext);
        this.context = aContext;
        this.workoutItemList = workoutItemList;
    }

    @Override
    public WorkoutsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        WorkoutItem workoutItem = workoutItemList.get(position);
        holder.nameView.setText(workoutItem.getName());

        if (workoutItem.isFinished()) {
            holder.doneView.setVisibility(View.VISIBLE);
        } else {
            holder.doneView.setVisibility(View.INVISIBLE);
        }

        if (workoutItem.isTimeMode()) {
            holder.detailedView.setText(String.format(context.getString(R.string.label_work_duration_item_info), workoutItem.getWorkoutTime()));
        } else {
            holder.detailedView.setText(String.format(context.getString(R.string.label_repetition_item_info), workoutItem.getRepetitionCount()));
        }

        holder.prepView.setText(String.format(context.getString(R.string.label_prep_duration_item_info), workoutItem.getPrepTime()));
        holder.breakView.setText(String.format(context.getString(R.string.label_break_duration_item_info), workoutItem.getBreakTime()));

        try {
            if (workoutItem.isImagePathExternal()) {
                holder.imgView.setImageURI(Uri.parse(workoutItem.getImagePath()));
            } else {
                String subFolder;
                if (OpenWorkout.getInstance().getCurrentUser().isMale()) {
                    subFolder = "male";
                } else {
                    subFolder = "female";
                }

                InputStream ims = context.getAssets().open("image/" + subFolder + "/" + workoutItem.getImagePath());
                holder.imgView.setImageDrawable(Drawable.createFromStream(ims, null));

                ims.close();
            }
        } catch (IOException ex) {
            holder.imgView.setImageResource(0);
            Timber.e(ex);
        } catch (SecurityException ex) {
            holder.imgView.setImageResource(0);
            Toast.makeText(context, context.getString(R.string.error_no_access_to_file) + " " + workoutItem.getImagePath(), Toast.LENGTH_SHORT).show();
            Timber.e(ex);
        }

        switch (getMode()) {
            case VIEW:
                break;
            case EDIT:
                holder.doneView.setVisibility(View.GONE);
                break;
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

    static class ViewHolder extends GenericAdapter.ViewHolder {
        TextView prepView;
        ImageView imgView;
        TextView nameView;
        TextView detailedView;
        ImageView doneView;
        TextView breakView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            prepView = itemView.findViewById(R.id.prepView);
            imgView = itemView.findViewById(R.id.imgView);
            nameView = itemView.findViewById(R.id.nameView);
            detailedView = itemView.findViewById(R.id.detailedView);
            doneView = itemView.findViewById(R.id.doneView);
            breakView = itemView.findViewById(R.id.breakView);
        }
    }
}
