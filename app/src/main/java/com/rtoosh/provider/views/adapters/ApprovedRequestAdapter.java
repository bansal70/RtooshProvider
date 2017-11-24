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
import com.rtoosh.provider.views.MinuteTimer;
import com.rtoosh.provider.views.RequestsDetailActivity;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

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

        if (order.orderType.equals(Constants.ORDER_ONLINE)) {
            holder.tvOrderDate.setText(DateUtils.getDateFormat(order.created));
            holder.tvOrderTime.setText(DateUtils.getTimeFormat(order.created));
        } else  {
            holder.tvOrderDate.setText(DateUtils.getDateFormat(order.scheduleDate));
            holder.tvOrderTime.setText(DateUtils.getTimeFormat(order.scheduleDate));
        }

        String timeOut = "";
        int seconds, minutes, hours, days;
        long totalTime;

        if (order.orderType.equals(Constants.ORDER_ONLINE)) {
            switch (order.status) {
                case Constants.ORDER_ACCEPTED:
                    holder.tvTimeRemaining.setText(R.string.order_accepted);
                    break;
                case Constants.ORDER_INITIATED:
                    holder.tvTimeRemaining.setText(R.string.order_initiated);
                    break;
                case Constants.ORDER_STARTED:
                    holder.tvTimeRemaining.setText(R.string.order_started);
                    break;
            }
        } else {
            timeOut = DateUtils.printDifference(order.scheduleDate, serverTime);
            Timber.e("timeout- "+timeOut);
            String[] split = timeOut.split(":");
            days = Integer.parseInt(split[0]);
            hours = Integer.parseInt(split[1]);
            minutes = Integer.parseInt(split[2]);
            seconds = Integer.parseInt(split[3]);
            if (days == 0 && hours == 0) {
                totalTime = minutes * 60 + seconds;
            } else {
                totalTime = 60 * 60 * 24 * days + hours * 60 * 60 + minutes * 60 + seconds;
            }
            MinuteTimer minuteTimer = new MinuteTimer(totalTime * 1000,
                    1000, minutes, seconds, holder.tvTimeRemaining);
            minuteTimer.start();
        }

        List<RequestDetailsResponse.OrderItem> orderItemList = approvedRequestsList.get(position).orderItem;
        int persons = 0, price = 0;
        for (RequestDetailsResponse.OrderItem orderItem : orderItemList) {

            persons += Integer.parseInt(orderItem.noOfPerson);
            orderId = orderItem.orderId;

            RequestDetailsResponse.Service service = orderItem.service;
            if (service.price != null)
                price += Integer.parseInt(service.price);
        }

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
        @BindView(R.id.tvTimeRemaining) TextView tvTimeRemaining;

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
