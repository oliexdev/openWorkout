/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.datatypes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.billingclient.api.SkuDetails;
import com.health.openworkout.R;
import com.health.openworkout.core.utils.PlayStoreUtils;


public class BillingFragment extends Fragment {
    private Button purchaseView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_billing, container, false);

        purchaseView = root.findViewById(R.id.purchaseView);

        purchaseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayStoreUtils.getInstance().startInAppPurchaseFlow(getActivity());
            }
        });

        PlayStoreUtils.getInstance().setOnSkuListener(new PlayStoreUtils.OnSkuListener() {
            @Override
            public void onSkuDetailsResponse(SkuDetails skuDetails) {
                purchaseView.setText(String.format(getString(R.string.label_info_purchase_for), skuDetails.getPrice()));
            }
        });

        PlayStoreUtils.getInstance().querySkuDetails();

        return root;
    }

}
