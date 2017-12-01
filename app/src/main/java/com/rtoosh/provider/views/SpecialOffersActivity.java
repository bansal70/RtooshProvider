package com.rtoosh.provider.views;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.POJO.ProfileResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.views.adapters.SpecialOffersAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpecialOffersActivity extends AppBaseActivity {

    private final String SERVICES_TAG = "PROVIDER_SERVICES";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbarTitle) TextView toolbarTitle;
    @BindView(R.id.recyclerOffers) RecyclerView recyclerOffers;

    SpecialOffersAdapter specialOffersAdapter;

    String lang, user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_offers);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText(getString(R.string.special_offers));

        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        user_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);

        recyclerOffers.setLayoutManager(new LinearLayoutManager(mContext));

        showDialog();
        ModelManager.getInstance().getProfileManager().profileTask(mContext, SERVICES_TAG, user_id, lang);
    }

    @Subscribe(sticky = true)
    public void onEvent(ProfileResponse profileResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();

        switch (profileResponse.getRequestTag()) {
            case SERVICES_TAG:
                ProfileResponse.Data profileData = profileResponse.data;
                List<ProfileResponse.Service> serviceList = profileData.service;
                specialOffersAdapter = new SpecialOffersAdapter(mContext, serviceList);
                recyclerOffers.setAdapter(specialOffersAdapter);

                break;

            default:
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorWithMessageEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        showToast(event.getResultMsgUser());
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        showToast(getString(R.string.something_went_wrong));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
