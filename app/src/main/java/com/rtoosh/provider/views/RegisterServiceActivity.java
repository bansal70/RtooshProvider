package com.rtoosh.provider.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.POJO.register.RegisterServiceData;
import com.rtoosh.provider.model.POJO.register.RegisterServiceResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.views.adapters.RegisterServiceAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RegisterServiceActivity extends AppBaseActivity {

    private final String SERVICE_TAG = "RegisterServiceActivity";
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerServices) RecyclerView recyclerServices;

    String lang;
    List<RegisterServiceResponse.Data> listServices;
    private List<RegisterServiceData> listData;
    private RegisterServiceAdapter registerServiceAdapter;
    String id, order, services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_service);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lang = RPPreferences.readString(mContext, "lang");
        id = getIntent().getStringExtra("ID");
        order = getIntent().getStringExtra("order");

        listServices = new ArrayList<>();
        listData = new ArrayList<>();

        ModelManager.getInstance().getServicesListManager().serviceTask(mContext, SERVICE_TAG, lang);
        showDialog();

        recyclerServices.setLayoutManager(new LinearLayoutManager(mContext));
        registerServiceAdapter = new RegisterServiceAdapter(mContext, listData);
        recyclerServices.setAdapter(registerServiceAdapter);
    }

    public void nextService(View v) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("services", registerServiceAdapter.getArray());
            services = jsonObject.toString();
            Timber.e("json services-- " + services);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(this, RegisterInfoActivity.class)
                .putExtra("ID", id)
                .putExtra("order", order)
                .putExtra("services", services));
        Utils.gotoNextActivityAnimation(this);
    }

    @Subscribe(sticky = true)
    public void onEvent(RegisterServiceResponse registerServiceResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (registerServiceResponse.getRequestTag()) {
            case SERVICE_TAG:
                for (RegisterServiceResponse.Data data : registerServiceResponse.data) {
                    //   listServices.addAll(registerServiceResponse.data);
                    RegisterServiceResponse.Category category = data.category;
                    RegisterServiceData serviceData = new RegisterServiceData(category.id, category.catName,
                            category.image, new ArrayList<>());
                    listData.add(serviceData);
                    registerServiceAdapter.notifyDataSetChanged();
                }
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
        showToast(Constants.SERVER_ERROR);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}