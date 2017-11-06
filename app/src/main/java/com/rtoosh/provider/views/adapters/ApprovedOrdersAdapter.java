package com.rtoosh.provider.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.Order;

import java.util.ArrayList;


public class ApprovedOrdersAdapter extends RecyclerView.Adapter<ApprovedOrdersAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<Order> listOrders;

    public ApprovedOrdersAdapter(Context mContext, ArrayList<Order> listOrders) {
        this.mContext = mContext;
        this.listOrders = listOrders;
    }

    @Override
    public ApprovedOrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_approved_orders, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ApprovedOrdersAdapter.ViewHolder holder, int position) {
        Order order = listOrders.get(position);
        holder.tvService.setText(order.getService());
    }

    @Override
    public int getItemCount() {
        return listOrders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvService;

        private ViewHolder(View itemView) {
            super(itemView);

            tvService = itemView.findViewById(R.id.tvService);
        }
    }
}
