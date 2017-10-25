package com.rtoosh.provider.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.AddService;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.views.adapters.RegisterServiceAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegisterServiceActivity extends AppBaseActivity {

    private Dialog dialogSelection;

    JSONObject jsonObject;
    RecyclerView recyclerHair, recyclerMakeup, recyclerNails;
    RegisterServiceAdapter registerServiceAdapter;
    private List<AddService> listHairs, listNails, listMakeup;
    String services = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_service);

        initViews();
        initSelectionDialog();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        jsonObject = new JSONObject();
        listHairs = new ArrayList<>();
        listNails = new ArrayList<>();
        listMakeup = new ArrayList<>();
        recyclerHair = findViewById(R.id.recyclerHair);
        recyclerMakeup = findViewById(R.id.recyclerMakeup);
        recyclerNails = findViewById(R.id.recyclerNails);
        recyclerHair.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerNails.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerMakeup.setLayoutManager(new LinearLayoutManager(mContext));

        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(Operations.makeJsonRegisterService("Hair", "Hair cut",
                    "Best service", "$39", "45 min"));

            jsonArray.put(Operations.makeJsonRegisterService("Hair", "Hair Spa",
                    "Remove dullness", "$50", "55 min"));

            jsonArray.put(Operations.makeJsonRegisterService("Nails", "NailArt",
                    "Best nail art", "$112", "1 hour 40 mins"));

            jsonObject.put("services", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("Register Service", "JSON Service: "+ jsonObject.toString());
    }

    private void initSelectionDialog() {
        dialogSelection = Utils.createDialog(this, R.layout.dialog_add_services);
        final EditText editServiceName = dialogSelection.findViewById(R.id.editServiceName);
        final EditText editServiceContent = dialogSelection.findViewById(R.id.editServiceContent);
        final EditText editServicePrice = dialogSelection.findViewById(R.id.editServicePrice);
        final EditText editServiceDuration = dialogSelection.findViewById(R.id.editServiceDuration);

        dialogSelection.findViewById(R.id.tvDoneSelection)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSelection.dismiss();
//                findViewById(R.id.cardServices).setVisibility(View.VISIBLE);
                String name = editServiceName.getText().toString().trim();
                String description = editServiceContent.getText().toString().trim();
                String price = editServicePrice.getText().toString().trim();
                String duration = editServiceDuration.getText().toString().trim();

                AddService addService = new AddService(name, description, price, duration);
                switch (services) {
                    case "hair":
                        listHairs.add(addService);
                        registerServiceAdapter = new RegisterServiceAdapter(mContext, listHairs);
                        recyclerHair.setAdapter(registerServiceAdapter);
                        break;

                    case "nails":
                        listNails.add(addService);
                        registerServiceAdapter = new RegisterServiceAdapter(mContext, listNails);
                        recyclerNails.setAdapter(registerServiceAdapter);
                        break;

                    case "makeup":
                        listMakeup.add(addService);
                        registerServiceAdapter = new RegisterServiceAdapter(mContext, listMakeup);
                        recyclerMakeup.setAdapter(registerServiceAdapter);
                        break;
                }
            }
        });
    }

    public void nailsClick(View view) {
        services = "nails";
        dialogSelection.show();
    }

    public void makeupClick(View view) {
        services = "makeup";
        dialogSelection.show();
    }

    public void hairClick(View view) {
        services = "hair";
        dialogSelection.show();
    }

    public void nextService(View v) {
        startActivity(new Intent(this, RegisterInfoActivity.class));
        Utils.gotoNextActivityAnimation(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
