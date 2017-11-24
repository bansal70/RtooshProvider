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
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.POJO.HistoryResponse;
import com.rtoosh.provider.model.POJO.RequestDetailsResponse;
import com.rtoosh.provider.model.custom.DateUtils;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.views.RequestsDetailActivity;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApprovedRequestAdapter extends RecyclerView.Adapter<ApprovedRequestAdapter.ViewHolder>{
    private Context context;
    private List<HistoryResponse.Data> approvedRequestsList;
    private String orderId, serverTime;

    public ApprovedRequestAdapter(Context context, List<HistoryResponse.Data> approvedRequestsList, String serverTime) {
        this.context = context;
        this.approvedRequestsList = approvedRequestsList;
        this.serverTime = serverTime;
    }

    @Override
    public ApprovedRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_approved_requests, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ApprovedRequestAdapter.ViewHolder holder, int position) {
        HistoryResponse.Data data = approvedRequestsList.get(position);
        RequestDetailsResponse.Order order = data.order;
        holder.tvOrderDate.setText(DateUtils.getDateFormat(order.created));
        holder.tvOrderTime.setText(DateUtils.getTimeFormat(order.created));

        List<RequestDetailsResponse.OrderItem> orderItemList = approvedRequestsList.get(position).orderItem;

        int persons = 0, price = 0;
        for (RequestDetailsResponse.OrderItem orderItem : orderItemList) {

            persons += Integer.parseInt(orderItem.noOfPerson);
            orderId = orderItem.orderId;

            RequestDetailsResponse.Service service = orderItem.service;
            if (service.price != null)
                price += Integer.parseInt(service.price);
        }


        //DateUtils.getTimeDifference(serverTime, order.created);
       // Timber.e("Time-- "+DateUtils.twoDatesBetweenTime(order.created, serverTime));

        holder.tvTotalPersons.setText(String.valueOf(persons));
        holder.tvOrderId.setText(String.format("#%s", orderId));
        holder.tvTotalPrice.setText(String.format("%s %s", String.valueOf(price), Constants.CURRENCY));

        ApprovedOrdersAdapter ordersAdapter = new ApprovedOrdersAdapter(context, orderItemList);
        holder.recyclerOrders.setAdapter(ordersAdapter);
    }

    @Override
    public int getItemCount() {
        return approvedRequestsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recyclerOrders) RecyclerView recyclerOrders;
        @BindView(R.id.tvTotalPersons) TextView tvTotalPersons;
        @BindView(R.id.tvOrderId) TextView tvOrderId;
        @BindView(R.id.tvTotalPrice) TextView tvTotalPrice;
        @BindView(R.id.tvOrderDate) TextView tvOrderDate;
        @BindView(R.id.tvOrderTime) TextView tvOrderTime;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            recyclerOrders.setLayoutManager(new LinearLayoutManager(context));
        }

        @OnClick(R.id.tvDetails)
        public void serviceDetails(View v) {
            context.startActivity(new Intent(context, RequestsDetailActivity.class)
            .putExtra("position", getAdapterPosition())
            .putExtra("data", Parcels.wrap(approvedRequestsList.get(getAdapterPosition()))));
            Utils.gotoNextActivityAnimation(context);
        }
    }
}
