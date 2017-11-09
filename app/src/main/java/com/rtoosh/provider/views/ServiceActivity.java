package com.rtoosh.provider.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.Order;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.views.adapters.OrdersAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ServiceActivity extends AppBaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerOrders) RecyclerView recyclerOrders;
    ArrayList<Order> listOrders;
    OrdersAdapter ordersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_cancel);

        listOrders = new ArrayList<>();
        listOrders.add(new Order("2 Blowout", 2, 80));
        listOrders.add(new Order("1 Hair cut", 1, 90));
        listOrders.add(new Order("1 Nail polish", 1, 55));

        recyclerOrders.setLayoutManager(new LinearLayoutManager(mContext));
        ordersAdapter = new OrdersAdapter(mContext, listOrders);
        recyclerOrders.setAdapter(ordersAdapter);

    }

    @OnClick(R.id.tvServiceCompleted)
    public void serviceCompleted(View view) {
        startActivity(new Intent(mContext, PurchaseDetailsActivity.class));
        Utils.gotoNextActivityAnimation(mContext);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
