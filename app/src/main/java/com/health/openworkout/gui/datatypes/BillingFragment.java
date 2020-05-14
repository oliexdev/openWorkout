/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.datatypes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
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
import com.health.openworkout.core.OpenWorkout;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class BillingFragment extends Fragment {
    private Button purchaseView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_billing, container, false);

        purchaseView = root.findViewById(R.id.purchaseView);

        purchaseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenWorkout.getInstance().startInAppPurchaseFlow(getActivity());
            }
        });

        OpenWorkout.getInstance().setOnSkuListener(new OpenWorkout.OnSkuListener() {
            @Override
            public void onSkuDetailsResponse(SkuDetails skuDetails) {
                purchaseView.setText(String.format(getString(R.string.label_info_purchase_for), skuDetails.getPrice()));
            }
        });

        OpenWorkout.getInstance().querySkuDetails();

        return root;
    }

}
