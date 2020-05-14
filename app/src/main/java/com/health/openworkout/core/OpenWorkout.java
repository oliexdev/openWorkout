/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.health.openworkout.R;
import com.health.openworkout.core.database.AppDatabase;
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.core.datatypes.User;
import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.core.datatypes.WorkoutSession;
import com.health.openworkout.core.training.AbdominalMuscleTraining;
import com.health.openworkout.core.training.BeginnersTraining;
import com.health.openworkout.core.training.SevenMinutesTraining;
import com.health.openworkout.core.workout.WorkoutFactory;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class OpenWorkout {
    public static boolean DEBUG_MODE = false;
    private static final String DATABASE_NAME = "openWorkout.db";
    private static final String SKU_AD_REMOVAL = "android.test.purchased";

    private static OpenWorkout instance;
    private final Context context;

    private AppDatabase appDB;
    private User user;

    private BillingClient billingClient;
    private SkuDetails adRemoval;
    private static OnSkuListener onSkuListener;

    private OpenWorkout(Context aContext) {
        context = aContext;

        openDB();

        billingClient = BillingClient.newBuilder(context)
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
                        onBillingPurchasesUpdated(billingResult, list);
                    }
                })
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    Timber.d("Billing setup successful finished");
                    // The BillingClient is ready. You can query purchases here.
                    Purchase.PurchasesResult purchaseResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
                    for (Purchase purchase : purchaseResult.getPurchasesList()) {
                        handlePurchase(purchase);
                    }
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
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
            appDB.workoutItemDAO().clear();

            long trainingPlanId = insertTrainingPlan(new SevenMinutesTraining());
            insertTrainingPlan(new BeginnersTraining());
            insertTrainingPlan(new AbdominalMuscleTraining());

            WorkoutFactory workoutFactory = new WorkoutFactory();
            appDB.workoutItemDAO().insertAll(workoutFactory.getAllWorkoutItems());

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

    public List<WorkoutItem> getAllUniqueWorkoutItems() {
        return appDB.workoutItemDAO().getAllUnique();
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
            appDB.workoutItemDAO().deleteAll(workoutSession.getWorkoutSessionId());
        }

        appDB.workoutSessionDAO().deleteAll(trainingPlan.getTrainingPlanId());
        appDB.trainingPlanDAO().delete(trainingPlan);
    }

    public void deleteWorkoutSession(WorkoutSession workoutSession) {
        appDB.workoutItemDAO().deleteAll(workoutSession.getWorkoutSessionId());

        appDB.workoutSessionDAO().delete(workoutSession);
    }

    public void deleteWorkoutItem(WorkoutItem workoutItem) {
        appDB.workoutItemDAO().delete(workoutItem);
    }

    public void updateWorkoutItem(WorkoutItem workoutItem) {
        appDB.workoutItemDAO().update(workoutItem);
    }

    public void updateWorkoutSession(WorkoutSession workoutSession) {
        appDB.workoutSessionDAO().update(workoutSession);
    }

    public void updateTrainingPlan(TrainingPlan trainingPlan) {
        appDB.trainingPlanDAO().update(trainingPlan);
    }

    public void updateUser(User user) {
        appDB.userDAO().update(user);
    }

    public void querySkuDetails() {
        List<String> skuList = new ArrayList<>();
        skuList.add(SKU_AD_REMOVAL);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            for (SkuDetails skuDetails : skuDetailsList) {
                                String sku = skuDetails.getSku();

                                if (SKU_AD_REMOVAL.equals(sku)) {
                                    adRemoval = skuDetails;
                                    onSkuListener.onSkuDetailsResponse(skuDetails);
                                }
                            }
                        }
                    }
                });

    }

    public void startInAppPurchaseFlow(Activity activity) {
        if (adRemoval != null) {
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(adRemoval)
                    .build();
            billingClient.launchBillingFlow(activity, flowParams);
        }
    }

    private void onBillingPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        switch (billingResult.getResponseCode()) {
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                Toast.makeText(context, context.getString(R.string.label_already_purchased), Toast.LENGTH_LONG).show();
                break;
            case BillingClient.BillingResponseCode.OK:
                if (purchases != null){
                    for (Purchase purchase : purchases) {
                        Toast.makeText(context, context.getString(R.string.label_successful_purchased), Toast.LENGTH_LONG).show();
                        handlePurchase(purchase);
                    }
                }
                break;
            case BillingClient.BillingResponseCode.USER_CANCELED:
                // Handle an error caused by a user cancelling the purchase flow.
                Timber.e("User purchase canceled");
                Toast.makeText(context, context.getString(R.string.label_purchase_canceled), Toast.LENGTH_LONG).show();
                break;
            default:
                // Handle any other error codes.
                Timber.e("Unexpected error abort purchasing");
                Toast.makeText(context, context.getString(R.string.label_purchase_unexpected_error), Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getSku().equals(SKU_AD_REMOVAL)) {
            switch (purchase.getPurchaseState()) {
                case Purchase.PurchaseState.PURCHASED:
                    Timber.d("ad removal purchased");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    sharedPreferences.edit().putBoolean("adRemoval", true).commit();

                    if (!purchase.isAcknowledged()) {
                        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();

                        billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                            @Override
                            public void onConsumeResponse(BillingResult billingResult, String s) {
                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    Timber.d("Purchase acknowledge successful");
                                } else {
                                    Timber.e("Purchase acknowledge unsuccessful");
                                }
                            }
                        });
                    }
                    break;
                case Purchase.PurchaseState.PENDING:
                    Timber.e("ad removal purchase is pending " + purchase);
                    Toast.makeText(context, String.format(context.getString(R.string.label_pending_purchase), purchase.getOrderId()), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    public boolean isAdRemovalPaid() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getBoolean("adRemoval", false);
    }

    public void setOnSkuListener(OnSkuListener onSkuListener) {
        this.onSkuListener = onSkuListener;
    }

    public interface OnSkuListener {
        public void onSkuDetailsResponse(SkuDetails skuDetails);
    }
}
