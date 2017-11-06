package com.rtoosh.provider.views;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.Order;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.views.adapters.OrdersAdapter;

import java.util.ArrayList;

public class PurchaseDetailsActivity extends AppBaseActivity implements View.OnClickListener{

    Toolbar toolbar;
    RecyclerView recyclerOrders;
    ArrayList<Order> listOrders;
    OrdersAdapter ordersAdapter;
    private Dialog dialogService, dialogFeedback;
    private TextView tvYes, tvNo;
    boolean isService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_details);

        initViews();
        initServiceDialog();
        initFeedBackDialog();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvYes = (TextView) findViewById(R.id.tvYes);
        tvNo = (TextView) findViewById(R.id.tvNo);

        listOrders = new ArrayList<>();
        listOrders.add(new Order("2 Blowout", 2, 80));
        listOrders.add(new Order("1 Hair cut", 1, 90));
        listOrders.add(new Order("1 Nail polish", 1, 55));
        recyclerOrders = (RecyclerView) findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(mContext));
        ordersAdapter = new OrdersAdapter(mContext, listOrders);
        recyclerOrders.setAdapter(ordersAdapter);
        findViewById(R.id.cardPurchase).setBackgroundResource(R.mipmap.ic_purchase_bg);

        findViewById(R.id.tvDone).setOnClickListener(this);
        tvYes.setOnClickListener(this);
        tvNo.setOnClickListener(this);
    }

    private void initServiceDialog() {
        dialogService = Utils.createDialog(this, R.layout.dialog_additional_services);
        dialogService.findViewById(R.id.tvSend).setOnClickListener(this);
    }

    private void initFeedBackDialog() {
        dialogFeedback = Utils.createDialog(this, R.layout.dialog_feedback);
        dialogFeedback.findViewById(R.id.tvSendFeedback).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvYes:
                setBackgroundColor(R.drawable.custom_basic_gradient, R.color.white);
                setTextColor(R.color.white, R.color.colorAccent);
                setDrawable(R.mipmap.ic_check_white, R.mipmap.ic_check_pink);
                isService = true;
                break;

            case R.id.tvNo:
                setBackgroundColor(R.color.white, R.drawable.custom_basic_gradient);
                setTextColor(R.color.colorAccent, R.color.white);
                setDrawable(R.mipmap.ic_check_pink, R.mipmap.ic_check_white);
                isService = false;
                break;

            case R.id.tvDone:
                if (isService)
                    dialogService.show();
                else
                    dialogFeedback.show();
                break;

            case R.id.tvSend:
                dialogService.dismiss();
                dialogFeedback.show();
                break;

            case R.id.tvSendFeedback:
                dialogFeedback.dismiss();
                Toast.makeText(mContext, R.string.feedback_submitted, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setBackgroundColor(int a, int b) {
        tvYes.setBackgroundResource(a);
        tvNo.setBackgroundResource(b);
    }

    private void setDrawable(int a, int b) {
        tvYes.setCompoundDrawablesWithIntrinsicBounds(0, 0, a, 0);
        tvNo.setCompoundDrawablesWithIntrinsicBounds(0, 0, b, 0);
    }

    private void setTextColor(int a, int b) {
        tvYes.setTextColor(ContextCompat.getColor(mContext, a));
        tvNo.setTextColor(ContextCompat.getColor(mContext, b));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
