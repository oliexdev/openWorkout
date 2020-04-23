/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.health.openworkout.core.database.AppDatabase;
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.core.datatypes.User;
import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.core.datatypes.WorkoutSession;
import com.health.openworkout.core.training.BeginnersTraining;
import com.health.openworkout.core.training.SevenMinutesTraining;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class OpenWorkout {
    public static boolean DEBUG_MODE = false;
    public static final String DATABASE_NAME = "openWorkout.db";

    private static OpenWorkout instance;
    private final Context context;

    private AppDatabase appDB;
    private User user;

    private OpenWorkout(Context aContext) {
        context = aContext;

        openDB();
    }

    public static void createInstance(Context aContext) {
        if (instance != null) {
            return;
        }

        instance = new OpenWorkout(aContext);
    }

    public static OpenWorkout getInstance() {
        if (instance == null) {
            throw new RuntimeException("No openWorkout instance created");
        }

        return instance;
    }

    public final Context getContext() {
        return context;
    }

    private void openDB() {
        appDB = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onOpen(SupportSQLiteDatabase db) {
                        super.onOpen(db);
                        db.setForeignKeyConstraintsEnabled(true);
                    }
                })
                .build();
    }

    public void initTrainingPlans() {
        List<TrainingPlan> trainingPlanList = appDB.trainingPlanDAO().getAll();

        if (trainingPlanList.isEmpty()) {
            insertTrainingPlan(new BeginnersTraining());
            long trainingPlanId = insertTrainingPlan(new SevenMinutesTraining());

            user = new User();
            user.setTrainingsPlanId(trainingPlanId);
            appDB.userDAO().insert(user);
        }

        user = appDB.userDAO().getAll().get(0);
    }

    public User getCurrentUser() {
        return user;
    }

    public void printTrainingPlans() {
        Timber.d("################ TRAINING PLAN PRINTOUT #####################");
        List<TrainingPlan> trainingPlanList = appDB.trainingPlanDAO().getAll();

        for (TrainingPlan singleTrainingPlan : trainingPlanList) {
            Timber.d("- Training Plan " + singleTrainingPlan.getName() + " Id " + singleTrainingPlan.getTrainingPlanId());
            List<WorkoutSession> workoutSessionList = appDB.workoutSessionDAO().getAll(singleTrainingPlan.getTrainingPlanId());

            for (WorkoutSession singleWorkoutSession : workoutSessionList) {
                Timber.d("-- WorkoutSession " + singleWorkoutSession.getName() + " Id " + singleWorkoutSession.getWorkoutSessionId());
                List<WorkoutItem> workoutItemList = appDB.workoutItemDAO().getAll(singleWorkoutSession.getWorkoutSessionId());

                for (WorkoutItem singleWorkItem : workoutItemList) {
                    Timber.d("---- WorkoutItem " + singleWorkItem.getName() + " Id " + singleWorkItem.getWorkoutItemId());
                }
            }
        }
    }

    public List<TrainingPlan> getTrainingPlans() {
        List<TrainingPlan> trainingPlanList = new ArrayList<>();

        List<TrainingPlan> dbTrainingPlanList = appDB.trainingPlanDAO().getAll();
        for (TrainingPlan dbTrainingPlan : dbTrainingPlanList) {
            trainingPlanList.add(getTrainingPlan(dbTrainingPlan.getTrainingPlanId()));
        }

        return trainingPlanList;
    }

    public TrainingPlan getTrainingPlan(long trainingPlanId) {
        TrainingPlan singleTrainingPlan = appDB.trainingPlanDAO().get(trainingPlanId);

        if (singleTrainingPlan != null) {
            List<WorkoutSession> workoutSessionList = appDB.workoutSessionDAO().getAll(singleTrainingPlan.getTrainingPlanId());
            singleTrainingPlan.setWorkoutSessions(workoutSessionList);

            for (WorkoutSession singleWorkoutSession : workoutSessionList) {
                List<WorkoutItem> workoutItemList = appDB.workoutItemDAO().getAll(singleWorkoutSession.getWorkoutSessionId());
                singleWorkoutSession.setWorkoutItems(workoutItemList);
            }
        }

        return singleTrainingPlan;
    }

    public WorkoutSession getWorkoutSession(long workoutSessionId) {
        WorkoutSession singleWorkoutSession = appDB.workoutSessionDAO().get(workoutSessionId);

        List<WorkoutItem> workoutItemList = appDB.workoutItemDAO().getAll(singleWorkoutSession.getWorkoutSessionId());
        singleWorkoutSession.setWorkoutItems(workoutItemList);

        return singleWorkoutSession;
    }

    public WorkoutItem getWorkoutItem(long workoutItemId) {
        return appDB.workoutItemDAO().get(workoutItemId);
    }

    public long insertTrainingPlan(TrainingPlan trainingPlan) {
        long trainingPlanId = appDB.trainingPlanDAO().insert(trainingPlan);
        for (WorkoutSession workoutSession : trainingPlan.getWorkoutSessions()) {
            workoutSession.setTrainingPlanId(trainingPlanId);
            long workoutSessionId = appDB.workoutSessionDAO().insert(workoutSession);

            for (WorkoutItem workoutItem : workoutSession.getWorkoutItems()) {
                workoutItem.setWorkoutSessionId(workoutSessionId);
                appDB.workoutItemDAO().insert(workoutItem);
            }
        }

        return trainingPlanId;
    }

    public long insertWorkoutSession(WorkoutSession workoutSession) {
        long workoutSessionId = appDB.workoutSessionDAO().insert(workoutSession);

        for (WorkoutItem workoutItem : workoutSession.getWorkoutItems()) {
            workoutItem.setWorkoutSessionId(workoutSessionId);
            appDB.workoutItemDAO().insert(workoutItem);
        }

        return workoutSessionId;
    }

    public long insertWorkoutItem(WorkoutItem workoutItem) {
        long workoutItemId = appDB.workoutItemDAO().insert(workoutItem);

        return workoutItemId;
    }

    public void deleteTrainingPlan(TrainingPlan trainingPlan) {
        for (WorkoutSession workoutSession : trainingPlan.getWorkoutSessions()) {
            for (WorkoutItem workoutItem : workoutSession.getWorkoutItems()) {
                appDB.workoutItemDAO().delete(workoutItem);
            }
            appDB.workoutSessionDAO().delete(workoutSession);
        }

        appDB.trainingPlanDAO().delete(trainingPlan);
    }

    public void deleteWorkoutSession(WorkoutSession workoutSession) {
        for (WorkoutItem workoutItem : workoutSession.getWorkoutItems()) {
            appDB.workoutItemDAO().delete(workoutItem);
        }

        appDB.workoutSessionDAO().delete(workoutSession);
    }

    public void updateWorkoutItem(WorkoutItem workoutItem) {
        appDB.workoutItemDAO().update(workoutItem);
    }

    public void updateWorkoutSession(WorkoutSession workoutSession) {
        for (WorkoutItem workoutItem : workoutSession.getWorkoutItems()) {
            appDB.workoutItemDAO().update(workoutItem);
        }

        appDB.workoutSessionDAO().update(workoutSession);
    }

    public void updateTrainingPlan(TrainingPlan trainingPlan) {
        for (WorkoutSession workoutSession : trainingPlan.getWorkoutSessions()) {
            for (WorkoutItem workoutItem : workoutSession.getWorkoutItems()) {
                appDB.workoutItemDAO().update(workoutItem);
            }
            appDB.workoutSessionDAO().update(workoutSession);
        }

        appDB.trainingPlanDAO().update(trainingPlan);
    }

    public void updateUser(User user) {
        appDB.userDAO().update(user);
    }
}
