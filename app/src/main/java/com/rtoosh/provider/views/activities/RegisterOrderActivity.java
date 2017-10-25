package com.rtoosh.provider.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.custom.Utils;

public class RegisterOrderActivity extends AppBaseActivity implements View.OnClickListener{

    private TextView tvPersons, tvServices;
    private int persons, services;
    EditText editPrice;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_order);
        initViews();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id = getIntent().getStringExtra("ID");

        tvPersons = findViewById(R.id.tvPersons);
        tvServices = findViewById(R.id.tvServices);
        editPrice = findViewById(R.id.editPrice);

        findViewById(R.id.ivAddPerson).setOnClickListener(this);
        findViewById(R.id.ivRemovePerson).setOnClickListener(this);
        findViewById(R.id.ivAddService).setOnClickListener(this);
        findViewById(R.id.ivRemoveService).setOnClickListener(this);

        persons = Integer.parseInt(tvPersons.getText().toString());
        services = Integer.parseInt(tvServices.getText().toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAddPerson:
                persons++;
                tvPersons.setText(String.valueOf(persons));
                break;

            case R.id.ivRemovePerson:
                if (persons > 1)
                    persons--;
                tvPersons.setText(String.valueOf(persons));
                break;

            case R.id.ivAddService:
                services++;
                tvServices.setText(String.valueOf(services));
                break;

            case R.id.ivRemoveService:
                if (services > 1)
                    services--;
                tvServices.setText(String.valueOf(services));
                break;
        }
    }

    public void nextOrder(View v) {
        if (editPrice.getText().toString().trim().isEmpty()) {
            Toast.makeText(mContext, R.string.set_minimum_order, Toast.LENGTH_SHORT).show();
            return;
        }
        String price = editPrice.getText().toString().trim();
        String order = Operations.makeJsonRegisterOrder(persons, services, price);

        startActivity(new Intent(this, RegisterServiceActivity.class)
                .putExtra("ID", id)
                .putExtra("Order", order));

        Utils.gotoNextActivityAnimation(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
