package com.rtoosh.provider.views;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
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
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

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
    @BindView(R.id.tvTimeText) TextView tvTimeText;
    @BindView(R.id.tvTimeLeft) TextView tvTimeLeft;
    @BindView(R.id.tvTime) TextView tvTime;
    @BindView(R.id.rlDiscount) RelativeLayout rlDiscount;
    @BindView(R.id.tvDiscount) TextView tvDiscount;

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

        phone = client.phone;
        if (phone.isEmpty()) {
            tvCall.setVisibility(View.GONE);
            tvSms.setVisibility(View.GONE);
        }

        if (order.discount.equals("0") || order.discount.isEmpty()) {
            rlDiscount.setVisibility(View.GONE);
        }
        else {
            rlDiscount.setVisibility(View.VISIBLE);
        }

        List<RequestDetailsResponse.OrderItem> listOrders = data.orderItem;
        for (int i=0; i<listOrders.size(); i++) {
            RequestDetailsResponse.Service service = listOrders.get(i).service;

            int persons = Integer.parseInt(listOrders.get(i).noOfPerson);
            totalPersons += persons;
            Timber.e("duration-- "+service.duration);
            if (service.duration != null && service.duration.contains(":")) {
                String[] time = service.duration.split(":");
                hour += persons * Integer.parseInt(time[0]);
                minutes += persons * Integer.parseInt(time[1]);
            }

            amount = Double.parseDouble(listOrders.get(i).amount);
            price += persons * amount;
        }

        price -= Integer.parseInt(order.discount);

        int mHours = minutes / 60;
        int mMinutes = minutes % 60;
        hour += mHours;

        tvCustomerName.setText(client.fullName);
        tvTotalPersons.setText(String.format("%s %s", String.valueOf(totalPersons), getString(R.string.persons)));
        tvHour.setText(String.valueOf(hour));
        tvMinutes.setText(String.valueOf(mMinutes));
        tvDiscount.setText(order.discount);
        tvPersons.setText(String.valueOf(totalPersons));
        tvOrderId.setText(data.order.id);
        tvTotalPrice.setText(String.format("%s %s", String.valueOf(price), Constants.CURRENCY));

        if (data.order.orderType.equals(Constants.ORDER_ONLINE)) {
            tvTime.setText(String.format("%s %s %s",
                    DateUtils.getTimeFormat(order.created),
                    DateUtils.getDayOfWeek(order.created),
                    DateUtils.getDateFormat(order.created)));
        } else {
            tvTime.setText(String.format("%s %s %s",
                    DateUtils.getTimeFormat(order.scheduleDate),
                    DateUtils.getDayOfWeek(order.scheduleDate),
                    DateUtils.getDateFormat(order.scheduleDate)));
        }

        int seconds, minutes, hours, days;
        long totalTime;
        String timeOut;

        if (order.orderType.equals(Constants.ORDER_ONLINE)) {
            timeOut = order.timeRemains;
            /*time = DateUtils.twoDatesBetweenTime(order.created, serverTime);
            timeOut = DateUtils.getTimeout(time);*/
            String[] split = timeOut.split(":");
            minutes = Integer.parseInt(split[0]);
            seconds = Integer.parseInt(split[1]);
            totalTime = minutes *60 + seconds;
        } else {
            /*timeOut = DateUtils.printDifference(order.scheduleDate, serverTime);
            Timber.e("timeout- "+timeOut);*/
            timeOut = order.timeRemains;
            String[] split = timeOut.split(":");
            days = Integer.parseInt(split[0]);
            hours = Integer.parseInt(split[1]);
            minutes = Integer.parseInt(split[2]);
            seconds = Integer.parseInt(split[3]);
            if (days == 0 && hours == 0) {
                totalTime = minutes * 60 + seconds;
            } else {
                totalTime = 60 * 60 * 24 * days + hours * 60 * 60 + minutes * 60 + seconds;
            }
        }

        CountDownTimer countDownTimer = new CountDownTimer(totalTime*1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                String days = TimeUnit.HOURS.toDays(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))+"";
                String hours = (TimeUnit.MILLISECONDS.toHours(millisUntilFinished) -
                        TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished)))+"";
                String minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))+"";
               /* String seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))+"";*/

               tvTimeLeft.setText(String.format("%s %s %s %s %s %s", days, getString(R.string.days),
                       hours, getString(R.string.hours), minutes, getString(R.string.minutes)));
            }

            @Override
            public void onFinish() {

            }
        }.start();

        if (order.status.equals(Constants.ORDER_COMPLETED) || order.status.equals(Constants.ORDER_REVIEWED)) {
            if (countDownTimer != null)
                countDownTimer.cancel();

            tvTimeLeft.setText(R.string.message_completed);
            tvTimeText.setText(String.format("%s:", getString(R.string.status)));
        }

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
