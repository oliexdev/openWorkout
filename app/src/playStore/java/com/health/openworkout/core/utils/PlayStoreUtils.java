/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.health.openworkout.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class PlayStoreUtils {
    private static final String SKU_AD_REMOVAL = "sku_ad_removal";

    private static PlayStoreUtils instance;

    private Context context;
    private BillingClient billingClient;
    private SkuDetails adRemoval;
    private static OnSkuListener onSkuListener;

    private PlayStoreUtils(Context aContext) {
        context = aContext;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean("adRemoval", false).commit();

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

        instance = new PlayStoreUtils(aContext);
    }

    public static PlayStoreUtils getInstance() {
        if (instance == null) {
            throw new RuntimeException("No PlayStoreUtils instance created");
        }

        return instance;
    }

    public void initMobileAds(Activity activity) {
        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                for (Map.Entry<String, AdapterStatus> adNetwork : initializationStatus.getAdapterStatusMap().entrySet()) {
                    Timber.d("Ad sense " + adNetwork.getKey() + " initialization status " + adNetwork.getValue().getInitializationState() + " (" + adNetwork.getValue().getDescription() + ")");
                }
            }
        });
    }

    public AdView getAdView(Context aContext) {
        AdView adView = new AdView(aContext);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(context.getString(R.string.MOB_ID));

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Timber.d("Ad successful loaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Timber.e("Ad failed to load with error code " + errorCode);
            }

        });

        return adView;
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
                    Timber.d("Ad removal purchased");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    sharedPreferences.edit().putBoolean("adRemoval", true).commit();

                    if (!purchase.isAcknowledged()) {
                        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();

                        billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                            @Override
                            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
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
                    Timber.e("Ad removal purchase is pending " + purchase);
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
