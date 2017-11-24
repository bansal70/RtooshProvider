package com.rtoosh.provider.views;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.RatingsResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.views.adapters.RatingsAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsActivity extends AppBaseActivity {

    private final String RATINGS_TAG = "PROVIDER_RATINGS";

    @BindView(R.id.recyclerRatings) RecyclerView recyclerRatings;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbarTitle) TextView toolbarTitle;

    String lang, user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText(getString(R.string.ratings));

        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        user_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);

        showDialog();

        ModelManager.getInstance().getRatingsManager().ratingsTask(mContext, RATINGS_TAG,
                Operations.ratingsParams(user_id, lang));
    }

    private void setRatings(RatingsResponse ratingsResponse) {
        recyclerRatings.setLayoutManager(new LinearLayoutManager(mContext));
        RatingsAdapter ratingsAdapter = new RatingsAdapter(mContext, ratingsResponse.data);
        recyclerRatings.setAdapter(ratingsAdapter);
    }

    @Subscribe(sticky = true)
    public void onEvent(RatingsResponse ratingsResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (ratingsResponse.getRequestTag()) {
            case RATINGS_TAG:
                setRatings(ratingsResponse);
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
}
