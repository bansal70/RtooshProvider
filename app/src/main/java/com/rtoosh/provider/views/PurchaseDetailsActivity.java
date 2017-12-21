package com.rtoosh.provider.views;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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

    private final String PURCHASE_ORDER_TAG = "ORDER_PURCHASE";
    private final String ADDITIONAL_SERVICES_TAG = "AdditionalServices";
    private final String FEEDBACK_TAG = "Feedback";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerOrders) RecyclerView recyclerOrders;
    @BindView(R.id.tvYes) TextView tvYes;
    @BindView(R.id.tvNo) TextView tvNo;
    @BindView(R.id.cardPurchase) CardView cardPurchase;
    @BindView(R.id.tvTotalPrice) TextView tvTotalPrice;
    @BindView(R.id.tvCustomerName) TextView tvCustomerName;
    @BindView(R.id.tvCommissionRate) TextView tvCommissionRate;
    @BindView(R.id.tvCommission) TextView tvCommission;
    @BindView(R.id.tvActualPrice) TextView tvActualPrice;
    @BindView(R.id.rlDiscount) RelativeLayout rlDiscount;
    @BindView(R.id.tvDiscount) TextView tvDiscount;
    @BindView(R.id.scrollPurchase) NestedScrollView scrollPurchase;
    @BindView(R.id.tvDone) TextView tvDone;

    OrdersAdapter ordersAdapter;
    private Dialog dialogService, dialogFeedback;
    boolean isService = false;

    int totalPersons = 0;
    float amount = 0, price = 0;
    String user_id, lang, request_id;
    String totalServices = "", totalPaid = "", note = "", comment="", provider_id;
    float ratings = 0;
    EditText editTotalServices, editPaid, editNote, editFeedback;
    RatingBar rbCustomer;
    boolean isSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_details);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
      /*  if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        provider_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);
        request_id = getIntent().getStringExtra("request_id");

        cardPurchase.setBackgroundResource(R.mipmap.ic_purchase_bg);

        initServiceDialog();
        initFeedbackDialog();

        //showDialog();
        showProgressBar();
        ModelManager.getInstance().getOrderDetailsManager().requestDetailsTask(mContext, PURCHASE_ORDER_TAG,
                Operations.requestDetailsParams(request_id, lang));
    }

    private void setData(RequestDetailsResponse requestDetailsResponse) {
        RequestDetailsResponse.Data data = requestDetailsResponse.data;
        RequestDetailsResponse.Client client = data.client;
        RequestDetailsResponse.OrderDetails order = data.order;

        user_id = client.id;

        if (order.discount.equals("0") || order.discount.isEmpty()) {
            rlDiscount.setVisibility(View.GONE);
        }
        else {
            rlDiscount.setVisibility(View.VISIBLE);
        }

        List<RequestDetailsResponse.OrderItem> listOrders = data.orderItem;
        for (RequestDetailsResponse.OrderItem orderItem : listOrders) {
            int persons = Integer.parseInt(orderItem.noOfPerson);
            totalPersons += persons;

            amount = Float.parseFloat(orderItem.amount);
            price += persons * amount;
        }

        price -= Integer.parseInt(order.discount);

        tvCustomerName.setText(client.fullName);
        tvDiscount.setText(order.discount);

        recyclerOrders.setLayoutManager(new LinearLayoutManager(mContext));
        ordersAdapter = new OrdersAdapter(mContext, listOrders);
        recyclerOrders.setAdapter(ordersAdapter);
        ordersAdapter.notifyDataSetChanged();

        tvCommissionRate.setText(String.format("%s%% = ", order.commission));
        int commission = (int)((price / 100.0f) * Float.parseFloat(order.commission));
        tvCommission.setText(String.valueOf(commission));
        tvActualPrice.setText(String.format("%s %s", String.valueOf(price), getString(R.string.currency)));

        price -= commission;
        tvTotalPrice.setText(String.format("%s %s", String.valueOf(price), getString(R.string.currency)));
    }

    @OnClick(R.id.tvYes)
    public void yesClick() {
        setBackgroundColor(R.drawable.custom_basic_gradient, R.color.white);
        setTextColor(R.color.white, R.color.colorAccent);
        setDrawable(R.mipmap.ic_check_white, R.mipmap.ic_check_pink);
        isService = true;
        isSelected = true;
        tvDone.setBackgroundResource(R.drawable.custom_basic_gradient);
    }

    @OnClick(R.id.tvNo)
    public void noClick() {
        setBackgroundColor(R.color.white, R.drawable.custom_basic_gradient);
        setTextColor(R.color.colorAccent, R.color.white);
        setDrawable(R.mipmap.ic_check_pink, R.mipmap.ic_check_white);
        isService = false;
        isSelected = true;
        tvDone.setBackgroundResource(R.drawable.custom_basic_gradient);
    }

    @OnClick(R.id.tvDone)
    public void doneClick() {
        if (!isSelected)
            return;

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
                    Operations.feedbackParams(provider_id, request_id, user_id, ratings, comment, lang));
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(RequestDetailsResponse detailsResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        hideProgressBar();
        switch (detailsResponse.getRequestTag()) {
            case PURCHASE_ORDER_TAG:
                setData(detailsResponse);
                scrollPurchase.setVisibility(View.VISIBLE);
                tvDone.setVisibility(View.VISIBLE);
                break;
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
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorWithMessageEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        hideProgressBar();
        showToast(event.getResultMsgUser());
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        hideProgressBar();
        showToast(getString(R.string.something_went_wrong));
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

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
