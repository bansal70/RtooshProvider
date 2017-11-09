package com.rtoosh.provider.views;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.Order;
import com.rtoosh.provider.views.adapters.ApprovedRequestAdapter;
import com.rtoosh.provider.views.adapters.NewRequestsAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequestsActivity extends AppBaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerNewRequests) RecyclerView recyclerNewRequests;
    @BindView(R.id.recyclerApprovedRequests) RecyclerView recyclerApprovedRequests;

    NewRequestsAdapter newRequestsAdapter;
    ApprovedRequestAdapter approvedRequestAdapter;
    ArrayList<Order> listOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listOrders = new ArrayList<>();
        listOrders.add(new Order("2 Blowout", 2, 80));
        listOrders.add(new Order("1 Hair cut", 1, 90));
        listOrders.add(new Order("1 Nail polish", 1, 55));

        recyclerNewRequests.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerApprovedRequests.setLayoutManager(new LinearLayoutManager(mContext));

        newRequestsAdapter = new NewRequestsAdapter(mContext);
        approvedRequestAdapter = new ApprovedRequestAdapter(mContext, listOrders);

        recyclerNewRequests.setAdapter(newRequestsAdapter);
        recyclerApprovedRequests.setAdapter(approvedRequestAdapter);
    }

}
