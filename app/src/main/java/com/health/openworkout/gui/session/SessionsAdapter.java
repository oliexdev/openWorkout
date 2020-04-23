/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.session;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutSession;
import com.health.openworkout.gui.datatypes.GenericAdapter;

import java.util.List;

public class SessionsAdapter extends GenericAdapter<SessionsAdapter.ViewHolder> {
    private GenericAdapter.FRAGMENT_MODE mode;
    private List<WorkoutSession> workoutSessionList;
    private Context context;

    public SessionsAdapter(Context aContext, List<WorkoutSession> workoutSessionList, GenericAdapter.FRAGMENT_MODE mode) {
        super(aContext, mode);
        this.mode = mode;
        this.context = aContext;
        this.workoutSessionList = workoutSessionList;
    }

    @Override
    public SessionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SessionsAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        WorkoutSession workoutSession = workoutSessionList.get(position);
        holder.nameView.setText(workoutSession.getName());

        if (workoutSession.isFinished()) {
            holder.imgView.setImageResource(R.drawable.ic_session_done);
        } else {
            holder.imgView.setImageResource(R.drawable.ic_session_undone);
        }
    }

    @Override
    public long getItemId(int position) {
        return workoutSessionList.get(position).getWorkoutSessionId();
    }

    @Override
    public int getItemCount() {
        return workoutSessionList.size();
    }

    static class ViewHolder extends GenericAdapter.ViewHolder {
        ImageView imgView;
        TextView nameView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgView = itemView.findViewById(R.id.imgView);
            nameView = itemView.findViewById(R.id.nameView);
        }
    }
}
