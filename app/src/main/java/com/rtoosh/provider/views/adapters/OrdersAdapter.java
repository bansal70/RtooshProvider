package com.rtoosh.provider.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.RequestDetailsResponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder>{
    private Context mContext;
    private List<RequestDetailsResponse.OrderItem> listOrders;

    public OrdersAdapter(Context mContext, List<RequestDetailsResponse.OrderItem> listOrders) {
        this.mContext = mContext;
        this.listOrders = listOrders;
    }

    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_orders, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrdersAdapter.ViewHolder holder, int position) {
        RequestDetailsResponse.OrderItem orderItem = listOrders.get(position);
        RequestDetailsResponse.Service service = orderItem.service;

        holder.tvService.setText(service.serviceName);


        if (orderItem.noOfPerson.equals("1"))
            holder.tvQuantity.setVisibility(View.GONE);
        else
            holder.tvQuantity.setVisibility(View.VISIBLE);


        String quantity = Integer.parseInt(orderItem.noOfPerson) + " x " + Double.parseDouble(orderItem.amount) + " = ";
        holder.tvQuantity.setText(quantity);

        double price = Integer.parseInt(orderItem.noOfPerson) * Double.parseDouble(orderItem.amount);
        holder.tvPrice.setText(String.valueOf(price));
    }

    @Override
    public int getItemCount() {
        return listOrders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvService) TextView tvService;
        @BindView(R.id.tvQuantity) TextView tvQuantity;
        @BindView(R.id.tvPrice) TextView tvPrice;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
