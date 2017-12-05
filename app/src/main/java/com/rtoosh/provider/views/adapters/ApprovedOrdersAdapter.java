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


public class ApprovedOrdersAdapter extends RecyclerView.Adapter<ApprovedOrdersAdapter.ViewHolder>{
    private Context mContext;
    private List<RequestDetailsResponse.OrderItem> orderItemList;

    ApprovedOrdersAdapter(Context mContext, List<RequestDetailsResponse.OrderItem> orderItemList) {
        this.mContext = mContext;
        this.orderItemList = orderItemList;
    }

    @Override
    public ApprovedOrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_approved_orders, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ApprovedOrdersAdapter.ViewHolder holder, int position) {
        RequestDetailsResponse.OrderItem orderItem = orderItemList.get(position);
        //RequestDetailsResponse.Service service = orderItem.service;

        holder.tvService.setText(orderItem.serviceName);
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvService) TextView tvService;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
