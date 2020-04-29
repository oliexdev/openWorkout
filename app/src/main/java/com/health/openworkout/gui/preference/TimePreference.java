/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */
package com.health.openworkout.gui.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.DateFormat;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import com.health.openworkout.R;

import java.util.Calendar;

public class TimePreference extends DialogPreference {

    private long timeInMillis = Calendar.getInstance().getTimeInMillis();

    public TimePreference(Context context) {
        this(context, null);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.preferenceStyle);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;

        persistLong(this.timeInMillis);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.preference_timepicker;
    }


    @Override
    protected void onSetInitialValue(Object defaultValue) {
        setTimeInMillis(getPersistedLong(timeInMillis));
    }

    @Override
    public CharSequence getSummary() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);

        return (DateFormat.getTimeFormat(getContext()).format(calendar.getTime()));
    }

}