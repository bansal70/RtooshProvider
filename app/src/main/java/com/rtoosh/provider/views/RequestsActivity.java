package com.rtoosh.provider.views;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.Order;
import com.rtoosh.provider.views.adapters.ApprovedRequestAdapter;
import com.rtoosh.provider.views.adapters.NewRequestsAdapter;
import com.rtoosh.provider.views.adapters.OrdersAdapter;

import java.util.ArrayList;

public class RequestsActivity extends AppBaseActivity {

    Toolbar toolbar;
    RecyclerView recyclerNewRequests, recyclerApprovedRequests;
    NewRequestsAdapter newRequestsAdapter;
    ApprovedRequestAdapter approvedRequestAdapter;
    ArrayList<Order> listOrders;
    RecyclerView recyclerOrders;
    OrdersAdapter ordersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        initViews();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listOrders = new ArrayList<>();
        listOrders.add(new Order("2 Blowout", 2, 80));
        listOrders.add(new Order("1 Hair cut", 1, 90));
        listOrders.add(new Order("1 Nail polish", 1, 55));

        recyclerNewRequests = findViewById(R.id.recyclerNewRequests);
        recyclerApprovedRequests = findViewById(R.id.recyclerApprovedRequests);

        recyclerNewRequests.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerApprovedRequests.setLayoutManager(new LinearLayoutManager(mContext));

        newRequestsAdapter = new NewRequestsAdapter(mContext);
        approvedRequestAdapter = new ApprovedRequestAdapter(mContext, listOrders);

        recyclerNewRequests.setAdapter(newRequestsAdapter);
        recyclerApprovedRequests.setAdapter(approvedRequestAdapter);
    }

}
