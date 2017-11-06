package com.rtoosh.provider.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.Order;
import com.rtoosh.provider.views.RequestsDetailActivity;

import java.util.ArrayList;

public class ApprovedRequestAdapter extends RecyclerView.Adapter<ApprovedRequestAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Order> listOrders;

    public ApprovedRequestAdapter(Context context, ArrayList<Order> listOrders) {
        this.context = context;
        this.listOrders = listOrders;
    }

    @Override
    public ApprovedRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_approved_requests, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ApprovedRequestAdapter.ViewHolder holder, int position) {
        ApprovedOrdersAdapter ordersAdapter = new ApprovedOrdersAdapter(context, listOrders);
        holder.recyclerOrders.setAdapter(ordersAdapter);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private RecyclerView recyclerOrders;
        private TextView tvDetails;

        private ViewHolder(View itemView) {
            super(itemView);

            recyclerOrders = itemView.findViewById(R.id.recyclerOrders);
            recyclerOrders.setLayoutManager(new LinearLayoutManager(context));
            tvDetails = itemView.findViewById(R.id.tvDetails);

            tvDetails.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvDetails:
                    context.startActivity(new Intent(context, RequestsDetailActivity.class));
            }
        }
    }
}
