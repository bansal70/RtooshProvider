package com.rtoosh.provider.views;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.POJO.HistoryResponse;
import com.rtoosh.provider.model.POJO.RequestDetailsResponse;
import com.rtoosh.provider.model.custom.DateUtils;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.views.adapters.OrdersAdapter;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RequestsDetailActivity extends AppBaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerOrders) RecyclerView recyclerOrders;
    @BindView(R.id.cardServiceDetail) CardView cardView;
    @BindView(R.id.tvTotalPrice) TextView tvTotalPrice;
    @BindView(R.id.tvCustomerName) TextView tvCustomerName;
    @BindView(R.id.tvCall) TextView tvCall;
    @BindView(R.id.tvSms) TextView tvSms;
    @BindView(R.id.tvTotalPersons) TextView tvTotalPersons;
    @BindView(R.id.tvHour) TextView tvHour;
    @BindView(R.id.tvMinutes) TextView tvMinutes;
    @BindView(R.id.tvOrderId) TextView tvOrderId;
    @BindView(R.id.tvPersons) TextView tvPersons;
    @BindView(R.id.tvTimeLeft) TextView tvTimeLeft;
    @BindView(R.id.tvTime) TextView tvTime;

    OrdersAdapter ordersAdapter;
    HistoryResponse.Data data;
    int totalPersons = 0, hour = 0, minutes = 0;
    double amount = 0, price = 0;
    String phone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_detail);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cardView.setBackgroundResource(R.mipmap.ic_purchase_bg);

        data = Parcels.unwrap(getIntent().getParcelableExtra("data"));

        setData();
    }

    private void setData() {
        RequestDetailsResponse.Order order = data.order;
        RequestDetailsResponse.Client client = data.client;
        List<RequestDetailsResponse.OrderItem> listOrders = data.orderItem;

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

        tvCustomerName.setText(client.fullName);
        tvTotalPersons.setText(String.format("%s %s", String.valueOf(totalPersons), getString(R.string.persons)));
        tvHour.setText(String.valueOf(hour));
        tvMinutes.setText(String.valueOf(mMinutes));
        tvPersons.setText(String.valueOf(totalPersons));
        tvOrderId.setText(data.order.id);
        tvTotalPrice.setText(String.format("%s %s", String.valueOf(price), Constants.CURRENCY));
        tvTime.setText(String.format("%s %s %s",
                DateUtils.getTimeFormat(order.created),
                DateUtils.getDayOfWeek(order.created),
                DateUtils.getDateFormat(order.created)));

        recyclerOrders.setLayoutManager(new LinearLayoutManager(mContext));
        ordersAdapter = new OrdersAdapter(mContext, listOrders);
        recyclerOrders.setAdapter(ordersAdapter);
        ordersAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.tvSms)
    public void smsUser() {
        Utils.smsIntent(mContext, phone);
    }

    @OnClick(R.id.tvCall)
    public void callUser() {
        Utils.callIntent(mContext, phone);
    }
}
