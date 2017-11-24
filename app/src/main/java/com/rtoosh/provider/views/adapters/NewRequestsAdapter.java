package com.rtoosh.provider.views.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.HistoryResponse;
import com.rtoosh.provider.model.POJO.RequestDetailsResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.DateUtils;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.views.OrderDetailsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class NewRequestsAdapter extends RecyclerView.Adapter<NewRequestsAdapter.ViewHolder> {
    private final String ACCEPTED_REQUEST_TAG = "ACCEPTED_REQUEST";
    private final String DECLINED_REQUEST_TAG = "DECLINED_REQUEST";

    private Context context;
    private List<HistoryResponse.Data> pendingRequestsList, approvedRequestsList;
    private String lang, serverTime;
    private Dialog dialogDecline, dialog;
    private EditText editReason;
    private ApprovedRequestAdapter approvedRequestAdapter;
    private int pos;
    private OnDataChangeListener mOnDataChangeListener;

    public NewRequestsAdapter(Context context, List<HistoryResponse.Data> pendingRequestsList,
                              List<HistoryResponse.Data> approvedRequestsList,
                              ApprovedRequestAdapter approvedRequestAdapter, String serverTime) {
        this.context = context;
        this.pendingRequestsList = pendingRequestsList;
        this.approvedRequestsList = approvedRequestsList;
        this.approvedRequestAdapter = approvedRequestAdapter;
        this.serverTime = serverTime;

        lang = RPPreferences.readString(context, Constants.LANGUAGE_KEY);
        dialog = Utils.showDialog(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public NewRequestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_new_requests, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewRequestsAdapter.ViewHolder holder, int position) {
        HistoryResponse.Data data = pendingRequestsList.get(position);
        RequestDetailsResponse.Order order = data.order;
        if (order.orderType.equals(Constants.ORDER_ONLINE)) {
            holder.tvOrderDate.setText(DateUtils.getDateFormat(order.created));
            holder.tvOrderTime.setText(DateUtils.getTimeFormat(order.created));
        } else  {
            holder.tvOrderDate.setText(DateUtils.getDateFormat(order.scheduleDate));
            holder.tvOrderTime.setText(DateUtils.getTimeFormat(order.scheduleDate));
        }

        List<RequestDetailsResponse.OrderItem> orderItemList = pendingRequestsList.get(position).orderItem;
        int persons = 0;
        for (RequestDetailsResponse.OrderItem orderItem : orderItemList) {
            persons += Integer.parseInt(orderItem.noOfPerson);
        }
        String totalRequests = orderItemList.size() + " " + context.getString(R.string.services_request);
        holder.tvTotalPersons.setText(String.valueOf(persons));
        holder.tvTotalRequests.setText(totalRequests);

        String time = "", timeOut = "";
        int seconds, minutes, hours, days;
        long totalTime;

        if (order.orderType.equals(Constants.ORDER_ONLINE)) {
            timeOut = order.timeRemains;
            /*time = DateUtils.twoDatesBetweenTime(order.created, serverTime);
            timeOut = DateUtils.getTimeout(time);*/
            String[] split = timeOut.split(":");
            minutes = Integer.parseInt(split[2]);
            seconds = Integer.parseInt(split[3]);
            totalTime = minutes *60 + seconds;
        } else {
            /*timeOut = DateUtils.printDifference(order.scheduleDate, serverTime);
            Timber.e("timeout- "+timeOut);*/
            timeOut = order.timeRemains;
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
        }

        new CountDownTimer(totalTime*1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                String days = TimeUnit.HOURS.toDays(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))+"";
                String hours = (TimeUnit.MILLISECONDS.toHours(millisUntilFinished) -
                        TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished)))+"";
                String minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))+"";
                String seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))+"";

                holder.tvTimeRemaining.setText(String.format("%s:%s:%s:%s", days, hours, minutes, seconds));
            }

            @Override
            public void onFinish() {

            }
        }.start();

       /* MinuteTimer minuteTimer = new MinuteTimer(totalTime * 1000,
                1000, minutes, seconds, holder.tvTimeRemaining);
        minuteTimer.start();*/

       /* MyCountDownTimer myCountDownTimer = new MyCountDownTimer(totalTime * 1000,
                1000, days, hours, minutes, seconds, holder.tvTimeRemaining);
        myCountDownTimer.start();*/

       /* new CountDownTimer(minutes * 60 * 1000 + seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String sec = String.valueOf(--seconds);

                if (seconds < 10)
                    sec = "0" + sec;

                holder.tvTimeRemaining.setText(String.format("%s:%s", String.valueOf(minutes), sec));

                if (seconds < 1) {
                    seconds = 60;
                    if (minutes > 0) {
                        String min = String.valueOf(minutes--);
                        holder.tvTimeRemaining.setText(String.format("%s:%s", min, sec));
                    }
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();*/
    }

    @Override
    public int getItemCount() {
        return pendingRequestsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTotalPersons) TextView tvTotalPersons;
        @BindView(R.id.tvTotalRequests) TextView tvTotalRequests;
        @BindView(R.id.tvOrderDate) TextView tvOrderDate;
        @BindView(R.id.tvOrderTime) TextView tvOrderTime;
        @BindView(R.id.tvTimeRemaining) TextView tvTimeRemaining;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.tvAccept)
        public void acceptRequest() {
            pos = getAdapterPosition();
            dialog.show();
            ModelManager.getInstance().getRequestManager().acceptRequestTask(context, ACCEPTED_REQUEST_TAG,
                    Operations.acceptRequestParams(pendingRequestsList.get(pos).order.id, lang));
        }

        @OnClick(R.id.tvDecline)
        public void declineRequest() {
            pos = getAdapterPosition();
            initDeclineDialog(pos);
        }
    }

    private void initDeclineDialog(int position) {
        dialogDecline = Utils.createDialog(context, R.layout.dialog_decline_request);

        editReason = dialogDecline.findViewById(R.id.editReason);
        dialogDecline.findViewById(R.id.tvDeclineRequest).setOnClickListener(v -> {
            dialog.show();
            ModelManager.getInstance().getRequestManager().declineRequestTask(context, DECLINED_REQUEST_TAG,
                    Operations.declineRequestParams(pendingRequestsList.get(position).order.id,
                            editReason.getText().toString(), lang));
        });

        dialogDecline.findViewById(R.id.tvDeclineCancel).setOnClickListener(v -> dialogDecline.dismiss());

        dialogDecline.show();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        EventBus.getDefault().unregister(this);
    }

    public interface OnDataChangeListener{
        void onDataChanged(int size);
    }

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }

    @Subscribe(sticky = true)
    public void onEvent(RequestDetailsResponse detailsResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dialog.dismiss();
        switch (detailsResponse.getRequestTag()) {
            case ACCEPTED_REQUEST_TAG:
                approvedRequestsList.add(pendingRequestsList.get(pos));
                approvedRequestAdapter.notifyDataSetChanged();

                HistoryResponse.Data data = pendingRequestsList.get(pos);
                RequestDetailsResponse.Order order = data.order;

                Timber.e("Order type-- "+order.orderType);
                if (order.orderType.equals(Constants.ORDER_ONLINE)) {
                    context.startActivity(new Intent(context, OrderDetailsActivity.class)
                            .putExtra("requestDetails", detailsResponse)
                            .putExtra("request_id", order.id));
                }

                pendingRequestsList.remove(pos);
                //notifyItemRemoved(pos);
                //notifyItemRangeChanged(pos, pendingRequestsList.size());
                notifyDataSetChanged();
    //            Utils.showToast(context, detailsResponse.getMessage());
                if(mOnDataChangeListener != null){
                    mOnDataChangeListener.onDataChanged(pendingRequestsList.size());
                }

                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dialog.dismiss();

        switch (apiResponse.getRequestTag()) {
            case DECLINED_REQUEST_TAG:
                pendingRequestsList.remove(pos);
                notifyDataSetChanged();

       //         Utils.showToast(context, apiResponse.getMessage());
                dialogDecline.dismiss();
                if(mOnDataChangeListener != null){
                    mOnDataChangeListener.onDataChanged(pendingRequestsList.size());
                }
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorWithMessageEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dialog.dismiss();
        // Utils.showToast(context, event.getResultMsgUser());
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dialog.dismiss();
        // Utils.showToast(context, Constants.SERVER_ERROR);
    }

}
