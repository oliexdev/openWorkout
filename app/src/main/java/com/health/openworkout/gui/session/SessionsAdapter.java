/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.session;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutSession;

import java.util.List;

public class SessionsAdapter extends BaseAdapter {
    private List<WorkoutSession> workoutSessionList;
    private LayoutInflater layoutInflater;
    private Context context;

    public SessionsAdapter(Context aContext, List<WorkoutSession> workoutSessionList) {
        this.context = aContext;
        this.workoutSessionList = workoutSessionList;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return workoutSessionList.size();
    }

    @Override
    public Object getItem(int position) {
        return workoutSessionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_session, null);
            holder = new ViewHolder();
            holder.imgView = (ImageView) convertView.findViewById(R.id.imgView);
            holder.nameView = (TextView) convertView.findViewById(R.id.nameView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WorkoutSession workoutSession = workoutSessionList.get(position);
        holder.nameView.setText(workoutSession.getName());

        if (workoutSession.isFinished()) {
            holder.imgView.setImageResource(R.drawable.ic_session_done);
        } else {
            holder.imgView.setImageResource(R.drawable.ic_session_undone);
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView imgView;
        TextView nameView;
    }
}
