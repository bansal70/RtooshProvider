package com.rtoosh.provider.views;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.AddService;
import com.rtoosh.provider.model.POJO.ProfileResponse;
import com.rtoosh.provider.model.POJO.register.RegisterOrder;
import com.rtoosh.provider.model.POJO.register.RegisterServiceData;
import com.rtoosh.provider.model.POJO.register.RegisterServiceResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.views.adapters.RegisterServiceAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class SettingsActivity extends AppBaseActivity {

    private final String SERVICES_TAG = "PROVIDER_SERVICES";
    private final String UPDATE_SERVICES_TAG = "UPDATE_SERVICES";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbarTitle) TextView toolbarTitle;
    @BindView(R.id.recyclerServices) RecyclerView recyclerServices;
    @BindView(R.id.tvPersons) TextView tvPersons;
    @BindView(R.id.tvServices) TextView tvServices;
    @BindView(R.id.editPrice) EditText editPrice;

    private int persons, services;
    String lang, user_id;

    List<RegisterServiceResponse.Data> listServices;
    private List<RegisterServiceData> listData;
    private RegisterServiceAdapter registerServiceAdapter;
    List<AddService> listAddServices;
    String totalServices, price;
    JSONArray jsonServices;

    RegisterServiceResponse registerServiceResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText(getString(R.string.settings));

        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        user_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);

        listServices = new ArrayList<>();
        listData = new ArrayList<>();
        listAddServices = new ArrayList<>();
        jsonServices = new JSONArray();

        showDialog();
        ModelManager.getInstance().getServicesListManager().serviceTask(mContext, SERVICES_TAG, lang);

        recyclerServices.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerServices.setNestedScrollingEnabled(false);
        registerServiceAdapter = new RegisterServiceAdapter(mContext, listData, listAddServices);
        recyclerServices.setAdapter(registerServiceAdapter);
    }

    @OnClick(R.id.ivAddPerson)
    public void addPersons() {
        persons++;
        tvPersons.setText(String.valueOf(persons));
    }

    @OnClick(R.id.ivRemovePerson)
    public void deletePersons() {
        if (persons > 1)
            persons--;
        tvPersons.setText(String.valueOf(persons));
    }

    @OnClick(R.id.ivAddService)
    public void addServices() {
        services++;
        tvServices.setText(String.valueOf(services));
    }

    @OnClick(R.id.ivRemoveService)
    public void deleteServices() {
        if (services > 1)
            services--;
        tvServices.setText(String.valueOf(services));
    }

    @OnClick(R.id.tvDone)
    public void updateServices() {
        if (editPrice.getText().toString().trim().isEmpty()) {
            showToast(getString(R.string.set_minimum_order));
            return;
        }
        price = editPrice.getText().toString().trim();

        String order = new Gson().toJson(createOrder());
        Timber.e("json order-- " + order);

        try {
            for (RegisterServiceData data : listData) {
                List<AddService> addServiceList = data.getListAddServices();
                for (AddService service : addServiceList) {
                    jsonServices.put(Operations.makeJsonRegisterService(data.getId(),
                            service.getName(), service.getDescription(), service.getPrice(), service.getDuration()));
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("services", jsonServices);
            totalServices = jsonObject.toString();

            Timber.e("json services-- " + totalServices);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        showDialog();
        ModelManager.getInstance().getUpdateInfoManager().updateInfoTask(mContext, UPDATE_SERVICES_TAG,
                Operations.updateInfoParams(user_id, totalServices, order, lang));
    }

    private RegisterOrder createOrder() {
        RegisterOrder registerOrder = new RegisterOrder();
        registerOrder.setMaxPersons(String.valueOf(persons));
        registerOrder.setMaxServices(String.valueOf(services));
        registerOrder.setMinOrder(price);

        return registerOrder;
    }

    private void getAllServices() {
        for (RegisterServiceResponse.Data data : registerServiceResponse.data) {
            listAddServices = new ArrayList<>();
            RegisterServiceResponse.Category category = data.category;

            RegisterServiceData serviceData = new RegisterServiceData(category.id, category.catName,
                    category.image, listAddServices);
            listData.add(serviceData);
        }
        registerServiceAdapter.notifyDataSetChanged();
        ModelManager.getInstance().getProfileManager().profileTask(mContext, SERVICES_TAG, user_id, lang);
    }

    private void setServices(ProfileResponse profileResponse) {
        ProfileResponse.Data profileData = profileResponse.data;
        List<ProfileResponse.Service> serviceList = profileData.service;
        for (int j = 0; j < listData.size(); j++) {
            listAddServices = new ArrayList<>();
            RegisterServiceData data = listData.get(j);
            for (int i = 0; i < serviceList.size(); i++) {
                ProfileResponse.Service service = serviceList.get(i);
                if (data.getId().equals(service.catId)) {
                    AddService addService = new AddService(service.serviceName, service.description,
                            service.price, service.duration);
                    listAddServices.add(addService);

                    RegisterServiceData serviceData = new RegisterServiceData(data.getId(), data.getName(),
                            data.getImage(), listAddServices);
                    listData.set(j, serviceData);
                }
            }
        }
        registerServiceAdapter.notifyDataSetChanged();

        setOrder(profileData);
    }

    private void setOrder(ProfileResponse.Data profileData) {
        ProfileResponse.User user = profileData.user;
        persons = Integer.parseInt(user.maxPersons);
        services = Integer.parseInt(user.maxServices);

        tvPersons.setText(user.maxPersons);
        tvServices.setText(user.maxServices);
        editPrice.setText(user.minOrder);
    }

    @Subscribe(sticky = true)
    public void onEvent(RegisterServiceResponse registerServiceResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        //dismissDialog();
        switch (registerServiceResponse.getRequestTag()) {
            case SERVICES_TAG:
                this.registerServiceResponse = registerServiceResponse;
                getAllServices();
                break;

            default:
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(ProfileResponse profileResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();

        switch (profileResponse.getRequestTag()) {
            case SERVICES_TAG:
                setServices(profileResponse);
                break;

            default:
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (apiResponse.getRequestTag()) {
            case UPDATE_SERVICES_TAG:
                showToast(apiResponse.getMessage());
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
