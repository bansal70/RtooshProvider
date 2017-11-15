package com.rtoosh.provider.views;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.RequestDetailsResponse;
import com.rtoosh.provider.views.adapters.OrdersAdapter;

import java.util.ArrayList;
import java.util.List;

public class RequestsDetailActivity extends AppBaseActivity {

    Toolbar toolbar;
    RecyclerView recyclerOrders;
    List<RequestDetailsResponse.OrderItem> listOrders;
    OrdersAdapter ordersAdapter;
    TextView tvCustomerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_detail);

        initViews();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listOrders = new ArrayList<>();
        /*listOrders.add(new Order("2 Blowout", 2, 80));
        listOrders.add(new Order("1 Hair cut", 1, 90));
        listOrders.add(new Order("1 Nail polish", 1, 55));*/
        recyclerOrders = findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(mContext));
        ordersAdapter = new OrdersAdapter(mContext, listOrders);
        recyclerOrders.setAdapter(ordersAdapter);

        tvCustomerName = findViewById(R.id.tvCustomerName);
        findViewById(R.id.cardServiceDetail).setBackgroundResource(R.mipmap.ic_purchase_bg);
    }
}
