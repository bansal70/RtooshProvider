package com.rtoosh.provider.views;

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

    private NewRequestsAdapter newRequestsAdapter;
    private ApprovedRequestAdapter approvedRequestAdapter;
    private CompletedRequestsAdapter completedRequestsAdapter;

    private List<HistoryResponse.Data> pendingRequestsList;
    private List<HistoryResponse.Data> approvedRequestsList;
    private List<HistoryResponse.Data> completedRequestsList;

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

        pendingRequestsList = new ArrayList<>();
        approvedRequestsList = new ArrayList<>();
        completedRequestsList = new ArrayList<>();

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
        serverTime = historyResponse.serverTime;

        List<HistoryResponse.Data> dataList = historyResponse.data;

        for (int i=0; i<dataList.size(); i++) {
            RequestDetailsResponse.Order order = dataList.get(i).order;
            switch (order.status) {
                case Constants.ORDER_PENDING:
                    pendingRequestsList.add(dataList.get(i));
                    break;
                case Constants.ORDER_ACCEPTED:
                    approvedRequestsList.add(dataList.get(i));
                    break;
                case Constants.ORDER_COMPLETED:
                    completedRequestsList.add(dataList.get(i));
                    break;
            }
        }

        approvedRequestAdapter = new ApprovedRequestAdapter(mContext, approvedRequestsList, serverTime);
        newRequestsAdapter = new NewRequestsAdapter(mContext, pendingRequestsList,
                approvedRequestsList, approvedRequestAdapter, serverTime);
        completedRequestsAdapter = new CompletedRequestsAdapter(mContext, completedRequestsList);

        recyclerNewRequests.setAdapter(newRequestsAdapter);
        recyclerApprovedRequests.setAdapter(approvedRequestAdapter);
        recyclerCompletedRequests.setAdapter(completedRequestsAdapter);

        newRequestsAdapter.setOnDataChangeListener(size -> {
            tvNewRequests.setText(String.valueOf(pendingRequestsList.size()));
            tvApprovedRequests.setText(String.valueOf(approvedRequestsList.size()));
        });

        tvNewRequests.setText(String.valueOf(pendingRequestsList.size()));
        tvApprovedRequests.setText(String.valueOf(approvedRequestsList.size()));
        tvCompletedRequests.setText(String.valueOf(completedRequestsList.size()));
    }

    @Subscribe(sticky = true)
    public void onEvent(HistoryResponse historyResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (historyResponse.getRequestTag()) {
            case HISTORY_TAG:
                showToast(historyResponse.getMessage());
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
