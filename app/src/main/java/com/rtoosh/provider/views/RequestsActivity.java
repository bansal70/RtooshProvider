package com.rtoosh.provider.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.HistoryResponse;
import com.rtoosh.provider.model.POJO.RequestDetailsResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.DateUtils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.views.adapters.ApprovedRequestAdapter;
import com.rtoosh.provider.views.adapters.CompletedRequestsAdapter;
import com.rtoosh.provider.views.adapters.NewRequestsAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequestsActivity extends AppBaseActivity {

    private final String HISTORY_TAG = "REQUESTS_HISTORY";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerNewRequests) RecyclerView recyclerNewRequests;
    @BindView(R.id.recyclerApprovedRequests) RecyclerView recyclerApprovedRequests;
    @BindView(R.id.recyclerCompletedRequests) RecyclerView recyclerCompletedRequests;
    @BindView(R.id.tvNewRequest) TextView tvNewRequests;
    @BindView(R.id.tvApprovedRequests) TextView tvApprovedRequests;
    @BindView(R.id.tvCompletedRequests) TextView tvCompletedRequests;

    NewRequestsAdapter newRequestsAdapter;
    ApprovedRequestAdapter approvedRequestAdapter;
    CompletedRequestsAdapter completedRequestsAdapter;

    List<HistoryResponse.Data> pendingRequestsList;
    List<HistoryResponse.Data> approvedRequestsList;
    List<HistoryResponse.Data> completedRequestsList;

    String lang, user_id, serverTime;

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

        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        user_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);

        recyclerNewRequests.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerApprovedRequests.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerCompletedRequests.setLayoutManager(new LinearLayoutManager(mContext));

        recyclerNewRequests.setNestedScrollingEnabled(false);
        recyclerApprovedRequests.setNestedScrollingEnabled(false);
        recyclerCompletedRequests.setNestedScrollingEnabled(false);

        showDialog();
        ModelManager.getInstance().getRequestsHistoryManager().historyTask(mContext, HISTORY_TAG,
                Operations.historyParams(user_id, lang));
    }

    private void setHistory(HistoryResponse historyResponse) {
        pendingRequestsList = new ArrayList<>();
        approvedRequestsList = new ArrayList<>();
        completedRequestsList = new ArrayList<>();
        serverTime = historyResponse.serverTime;

        List<HistoryResponse.Data> dataList = historyResponse.data;

        String timeOut;
        int day = 0, hour = 0, min, sec;

        for (int i=0; i<dataList.size(); i++) {
            RequestDetailsResponse.Order order = dataList.get(i).order;
            if (order.orderType.equals(Constants.ORDER_ONLINE)) {
                String time = DateUtils.twoDatesBetweenTime(order.created, serverTime);
                timeOut = DateUtils.getTimeout(time);
                String[] dateTime = timeOut.split(":");
                min = Integer.parseInt(dateTime[0]);
                sec = Integer.parseInt(dateTime[1]);
            } else {
                timeOut = DateUtils.printDifference(order.scheduleDate, serverTime);
                String[] dateTime = timeOut.split(":");
                day = Integer.parseInt(dateTime[0]);
                hour = Integer.parseInt(dateTime[1]);
                min = Integer.parseInt(dateTime[2]);
                sec = Integer.parseInt(dateTime[3]);
            }

            order.timeRemains = timeOut;

            switch (order.status) {
                case Constants.ORDER_PENDING:
                    if (day > 1 || hour > 1 || min > 1 || sec > 1)
                        pendingRequestsList.add(dataList.get(i));
                    break;
                case Constants.ORDER_ACCEPTED:
                    if (day > 1 || hour > 1 || min > 1 || sec > 1)
                        approvedRequestsList.add(dataList.get(i));
                    break;
                case Constants.ORDER_COMPLETED:
                    completedRequestsList.add(dataList.get(i));
                    break;
                case Constants.ORDER_REVIEWED:
                    completedRequestsList.add(dataList.get(i));
                    break;
            }
        }

        approvedRequestAdapter = new ApprovedRequestAdapter(mContext, approvedRequestsList, serverTime);
        newRequestsAdapter = new NewRequestsAdapter(mContext, pendingRequestsList);
        completedRequestsAdapter = new CompletedRequestsAdapter(mContext, completedRequestsList);

        recyclerNewRequests.setAdapter(newRequestsAdapter);
        recyclerApprovedRequests.setAdapter(approvedRequestAdapter);
        recyclerCompletedRequests.setAdapter(completedRequestsAdapter);

        newRequestsAdapter.setOnDataChangeListener(size -> {
            showDialog();
            ModelManager.getInstance().getRequestsHistoryManager().historyTask(mContext, HISTORY_TAG,
                    Operations.historyParams(user_id, lang));
        });

        approvedRequestAdapter.setOnDataChangeListener(size -> {
            showDialog();
            ModelManager.getInstance().getRequestsHistoryManager().historyTask(mContext, HISTORY_TAG,
                    Operations.historyParams(user_id, lang));
        });

        tvNewRequests.setText(String.valueOf(pendingRequestsList.size()));
        tvApprovedRequests.setText(String.valueOf(approvedRequestsList.size()));
        tvCompletedRequests.setText(String.valueOf(completedRequestsList.size()));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ModelManager.getInstance().getRequestsHistoryManager().historyTask(mContext, HISTORY_TAG,
                Operations.historyParams(user_id, lang));
    }

    @Subscribe(sticky = true)
    public void onEvent(HistoryResponse historyResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (historyResponse.getRequestTag()) {
            case HISTORY_TAG:
                //showToast(historyResponse.getMessage());
                setHistory(historyResponse);
                break;

            default:
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorWithMessageEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        showToast(event.getResultMsgUser());
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        showToast(getString(R.string.something_went_wrong));
    }

}
