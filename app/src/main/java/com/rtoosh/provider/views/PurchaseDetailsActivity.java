package com.rtoosh.provider.views;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.RequestDetailsResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.views.adapters.OrdersAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PurchaseDetailsActivity extends AppBaseActivity implements View.OnClickListener {

    private final String ADDITIONAL_SERVICES_TAG = "AdditionalServices";
    private final String FEEDBACK_TAG = "Feedback";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerOrders) RecyclerView recyclerOrders;
    @BindView(R.id.tvYes) TextView tvYes;
    @BindView(R.id.tvNo) TextView tvNo;
    @BindView(R.id.cardPurchase) CardView cardPurchase;
    @BindView(R.id.tvTotalPrice) TextView tvTotalPrice;
    @BindView(R.id.tvCustomerName) TextView tvCustomerName;
    @BindView(R.id.tvCommission) TextView tvCommission;
    @BindView(R.id.tvActualPrice) TextView tvActualPrice;

    OrdersAdapter ordersAdapter;
    private Dialog dialogService, dialogFeedback;
    boolean isService = false;
    private RequestDetailsResponse requestDetailsResponse;

    int totalPersons = 0;
    double amount = 0, price = 0;
    String user_id, lang, request_id;
    String totalServices = "", totalPaid = "", note = "", comment="", provider_id;
    float ratings = 0;
    EditText editTotalServices, editPaid, editNote, editFeedback;
    RatingBar rbCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_details);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lang = RPPreferences.readString(mContext, "lang");
        user_id = RPPreferences.readString(mContext, "user_id");
        request_id = RPPreferences.readString(mContext, "accepted_request_id");

        requestDetailsResponse = (RequestDetailsResponse) getIntent().getSerializableExtra("requestDetails");

        cardPurchase.setBackgroundResource(R.mipmap.ic_purchase_bg);

        setData();
        initServiceDialog();
        initFeedbackDialog();
    }

    private void setData() {
        RequestDetailsResponse.Data data = requestDetailsResponse.data;
        RequestDetailsResponse.Client client = data.client;
        List<RequestDetailsResponse.OrderItem> listOrders = data.orderItem;
        tvCustomerName.setText(client.fullName);
        provider_id = client.id;

        for (int i=0; i<listOrders.size(); i++) {
            int persons = Integer.parseInt(listOrders.get(i).noOfPerson);
            totalPersons += persons;

            amount = Double.parseDouble(listOrders.get(i).amount);
            price += persons * amount;
        }

        recyclerOrders.setLayoutManager(new LinearLayoutManager(mContext));
        ordersAdapter = new OrdersAdapter(mContext, listOrders);
        recyclerOrders.setAdapter(ordersAdapter);
        ordersAdapter.notifyDataSetChanged();
        double commission = (price / 100.0f) * 20;
        tvCommission.setText(String.valueOf(commission));
        tvActualPrice.setText(String.format("%s %s", String.valueOf(price), Constants.CURRENCY));

        price -= commission;
        tvTotalPrice.setText(String.format("%s %s", String.valueOf(price), Constants.CURRENCY));
    }

    @OnClick(R.id.tvYes)
    public void yesClick() {
        setBackgroundColor(R.drawable.custom_basic_gradient, R.color.white);
        setTextColor(R.color.white, R.color.colorAccent);
        setDrawable(R.mipmap.ic_check_white, R.mipmap.ic_check_pink);
        isService = true;
    }

    @OnClick(R.id.tvNo)
    public void noClick() {
        setBackgroundColor(R.color.white, R.drawable.custom_basic_gradient);
        setTextColor(R.color.colorAccent, R.color.white);
        setDrawable(R.mipmap.ic_check_pink, R.mipmap.ic_check_white);
        isService = false;
    }

    @OnClick(R.id.tvDone)
    public void doneClick() {
        if (isService)
            dialogService.show();
        else
            dialogFeedback.show();
    }

    private void initServiceDialog() {
        dialogService = Utils.createDialog(this, R.layout.dialog_additional_services);
        editTotalServices = dialogService.findViewById(R.id.editTotalServices);
        editPaid = dialogService.findViewById(R.id.editPaid);
        editNote = dialogService.findViewById(R.id.editNote);
        dialogService.findViewById(R.id.tvSend).setOnClickListener(this);
    }

    private void initFeedbackDialog() {
        dialogFeedback = Utils.createDialog(this, R.layout.dialog_feedback);
        dialogFeedback.setCancelable(false);
        rbCustomer = dialogFeedback.findViewById(R.id.rbCustomer);
        editFeedback = dialogFeedback.findViewById(R.id.editFeedback);
        dialogFeedback.findViewById(R.id.tvSendFeedback).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSend:
                additionalServices();
                break;

            case R.id.tvSendFeedback:
                feedback();
                break;
        }
    }

    private void additionalServices() {
        totalServices = editTotalServices.getText().toString().trim();
        totalPaid = editPaid.getText().toString().trim();
        note = editNote.getText().toString();

        if (totalServices.isEmpty() || totalPaid.isEmpty()) {
            showToast(getString(R.string.toast_fill_data));
        } else {
            showDialog();
            ModelManager.getInstance().getAdditionalServiceManager().additionalServiceTask(mContext, ADDITIONAL_SERVICES_TAG,
                    Operations.additionalServiceParams(request_id, totalServices, totalPaid, note, lang));
        }
    }

    private void feedback() {
        ratings = rbCustomer.getRating();
        comment = editFeedback.getText().toString();
        if (ratings == 0) {
            showToast(getString(R.string.message_add_rating));
        } else {
            showDialog();
            ModelManager.getInstance().getFeedbackManager().feedbackTask(mContext, FEEDBACK_TAG,
                    Operations.feedbackParams(provider_id, user_id, ratings, comment, lang));
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (apiResponse.getRequestTag()) {
            case ADDITIONAL_SERVICES_TAG:
                dialogService.dismiss();
                dialogFeedback.show();
                break;

            case FEEDBACK_TAG:
                dialogFeedback.dismiss();
                Toast.makeText(mContext, R.string.feedback_submitted, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
        showToast(Constants.SERVER_ERROR);
    }

    private void setBackgroundColor(int a, int b) {
        tvYes.setBackgroundResource(a);
        tvNo.setBackgroundResource(b);
    }

    private void setDrawable(int a, int b) {
        tvYes.setCompoundDrawablesWithIntrinsicBounds(0, 0, a, 0);
        tvNo.setCompoundDrawablesWithIntrinsicBounds(0, 0, b, 0);
    }

    private void setTextColor(int a, int b) {
        tvYes.setTextColor(ContextCompat.getColor(mContext, a));
        tvNo.setTextColor(ContextCompat.getColor(mContext, b));
    }

}
