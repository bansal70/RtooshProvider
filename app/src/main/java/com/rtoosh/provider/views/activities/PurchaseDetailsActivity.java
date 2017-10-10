package com.rtoosh.provider.views.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.Order;
import com.rtoosh.provider.views.adapters.OrdersAdapter;

import java.util.ArrayList;

public class PurchaseDetailsActivity extends AppBaseActivity {

    Toolbar toolbar;
    RecyclerView recyclerOrders;
    ArrayList<Order> listOrders;
    OrdersAdapter ordersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_details);

        initViews();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listOrders = new ArrayList<>();
        listOrders.add(new Order("2 Blowout", 2, 80));
        listOrders.add(new Order("1 Hair cut", 1, 90));
        listOrders.add(new Order("1 Nail polish", 1, 55));
        recyclerOrders = (RecyclerView) findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(mContext));
        ordersAdapter = new OrdersAdapter(mContext, listOrders);
        recyclerOrders.setAdapter(ordersAdapter);
    }
}
