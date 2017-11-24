package com.rtoosh.provider.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.RequestDetailsResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.views.adapters.OrdersAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ServiceActivity extends AppBaseActivity {

    private final String TAG = "ServiceActivity";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerOrders) RecyclerView recyclerOrders;
    @BindView(R.id.tvTotalPrice) TextView tvTotalPrice;
    @BindView(R.id.tvCustomerName) TextView tvCustomerName;
    @BindView(R.id.tvCall) TextView tvCall;
    @BindView(R.id.tvSms) TextView tvSms;
    @BindView(R.id.tvTotalPersons) TextView tvTotalPersons;
    @BindView(R.id.tvHour) TextView tvHour;
    @BindView(R.id.tvMinutes) TextView tvMinutes;

    OrdersAdapter ordersAdapter;

    String user_id, lang, request_id, phone = "";
    RequestDetailsResponse requestDetailsResponse;
    int totalPersons = 0, hour = 0, minutes = 0;
    double amount = 0, price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(R.mipmap.ic_cancel);

        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        user_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);
        request_id = getIntent().getStringExtra("request_id");

        requestDetailsResponse = (RequestDetailsResponse) getIntent().getSerializableExtra("requestDetails");

        setData();
    }

    private void setData() {
        RequestDetailsResponse.Data data = requestDetailsResponse.data;
        RequestDetailsResponse.Client client = data.client;
        List<RequestDetailsResponse.OrderItem> listOrders = data.orderItem;
        tvCustomerName.setText(client.fullName);
        phone = client.phone;
        if (phone.isEmpty()) {
            tvCall.setVisibility(View.GONE);
            tvSms.setVisibility(View.GONE);
        }

        for (int i=0; i<listOrders.size(); i++) {
            RequestDetailsResponse.Service service = listOrders.get(i).service;

            int persons = Integer.parseInt(listOrders.get(i).noOfPerson);
            totalPersons += persons;
            String[] time = service.duration.split(":");
            hour += persons * Integer.parseInt(time[0]);
            minutes += persons * Integer.parseInt(time[1]);

            amount = Double.parseDouble(listOrders.get(i).amount);
            price += persons * amount;
        }

        int mHours = minutes / 60;
        int mMinutes = minutes % 60;
        hour += mHours;

        tvTotalPersons.setText(String.format("%s %s", String.valueOf(totalPersons), getString(R.string.persons)));
        tvHour.setText(String.valueOf(hour));
        tvMinutes.setText(String.valueOf(mMinutes));

        recyclerOrders.setLayoutManager(new LinearLayoutManager(mContext));
        ordersAdapter = new OrdersAdapter(mContext, listOrders);
        recyclerOrders.setAdapter(ordersAdapter);
        ordersAdapter.notifyDataSetChanged();
        tvTotalPrice.setText(String.format("%s %s", String.valueOf(price), Constants.CURRENCY));
    }

    @OnClick(R.id.tvSms)
    public void smsUser() {
        Utils.smsIntent(mContext, phone);
    }

    @OnClick(R.id.tvCall)
    public void callUser() {
        Utils.callIntent(mContext, phone);
    }

    @OnClick(R.id.tvServiceCompleted)
    public void serviceCompleted(View view) {
        showDialog();
        ModelManager.getInstance().getServiceCompletedManager().completeServiceTask(mContext, TAG,
                Operations.serviceCompletedParams(request_id, lang));
    }

    @Subscribe(sticky = true)
    public void onEvent(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (apiResponse.getRequestTag()) {
            case TAG:
                startActivity(new Intent(mContext, PurchaseDetailsActivity.class)
                        .putExtra("requestDetails", requestDetailsResponse)
                        .putExtra("request_id", request_id));
                Utils.gotoNextActivityAnimation(mContext);
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
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
