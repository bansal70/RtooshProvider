package com.rtoosh.provider.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.register.RegisterOrder;
import com.rtoosh.provider.model.custom.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class RegisterOrderActivity extends AppBaseActivity {

    @BindView(R.id.tvPersons) TextView tvPersons;
    @BindView(R.id.tvServices) TextView tvServices;
    @BindView(R.id.editPrice) EditText editPrice;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private int persons, services;
    private String id, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_order);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id = getIntent().getStringExtra("ID");

        persons = Integer.parseInt(tvPersons.getText().toString());
        services = Integer.parseInt(tvServices.getText().toString());
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

    public void nextOrder(View v) {
        if (editPrice.getText().toString().trim().isEmpty()) {
            Toast.makeText(mContext, R.string.set_minimum_order, Toast.LENGTH_SHORT).show();
            return;
        }
        price = editPrice.getText().toString().trim();

        String order = new Gson().toJson(createOrder());
        Timber.e("json order-- " + order);

        startActivity(new Intent(this, RegisterServiceActivity.class)
                .putExtra("ID", id)
                .putExtra("order", order));

        Utils.gotoNextActivityAnimation(this);
    }

    private RegisterOrder createOrder() {
        RegisterOrder registerOrder = new RegisterOrder();
        registerOrder.setMaxPersons(String.valueOf(persons));
        registerOrder.setMaxServices(String.valueOf(services));
        registerOrder.setMinOrder(price);

        return registerOrder;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
