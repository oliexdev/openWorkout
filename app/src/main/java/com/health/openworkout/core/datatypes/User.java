/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.datatypes;

import androidx.annotation.Keep;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Keep
@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    private long userId;
    @ColumnInfo
    private long trainingsPlanId;
    @ColumnInfo
    private boolean isMale;

    public User() {
        isMale = true;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTrainingsPlanId() {
        return trainingsPlanId;
    }

    public void setTrainingsPlanId(long trainingsPlanId) {
        this.trainingsPlanId = trainingsPlanId;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }
}
