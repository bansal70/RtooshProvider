package com.rtoosh.provider.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.Services;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.views.adapters.ServiceAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppBaseActivity implements CompoundButton.OnCheckedChangeListener,
        OnMapReadyCallback, View.OnClickListener {

    Toolbar toolbar;
    TextView tvNewRequests, tvApprovedRequests;
    SwitchCompat switchOnline;
    List<Services> listServices;
    Dialog dialogServices, dialogTerms, dialogRequest, dialogDecline;
    Handler handler;
    GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        initViews();
        initServiceDialog();
        initTermsDialog();
        initRequestDialog();
        initDeclineDialog();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switchOnline = (SwitchCompat) findViewById(R.id.switchOnline);
        switchOnline.setOnCheckedChangeListener(this);
        handler = new Handler();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            dialogServices.show();
        } else {
            findViewById(R.id.textOnline).setVisibility(View.GONE);
            findViewById(R.id.llRequesting).setVisibility(View.GONE);
            findViewById(R.id.rlOffline).setVisibility(View.VISIBLE);
        }
    }

    private void initServiceDialog() {
        dialogServices = Utils.createDialog(this, R.layout.dialog_services);

        listServices = new ArrayList<>();
        listServices.add(new Services("Make Up", false));
        listServices.add(new Services("Hairs", false));
        listServices.add(new Services("Nails", false));
        listServices.add(new Services("Henna & Tattoos", false));
        listServices.add(new Services("All", false));
        RecyclerView recyclerView = dialogServices.findViewById(R.id.recyclerServices);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        ServiceAdapter serviceAdapter = new ServiceAdapter(mContext, listServices);
        recyclerView.setAdapter(serviceAdapter);

        dialogServices.findViewById(R.id.tvNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTerms.show();
                dialogServices.dismiss();
            }
        });
    }

    private void initTermsDialog() {
        dialogTerms = Utils.createDialog(this, R.layout.dialog_terms);

        dialogTerms.findViewById(R.id.tvAgree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTerms.dismiss();
                findViewById(R.id.textOnline).setVisibility(View.VISIBLE);
                findViewById(R.id.llRequesting).setVisibility(View.VISIBLE);
                findViewById(R.id.rlOffline).setVisibility(View.GONE);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //findViewById(R.id.llRequest).setVisibility(View.VISIBLE);
                        findViewById(R.id.llRequesting).setVisibility(View.GONE);
                        dialogRequest.show();
                    }
                }, 2000);

            }
        });

        dialogTerms.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTerms.dismiss();
                switchOnline.setChecked(false);
            }
        });
    }

    private void initRequestDialog() {
        dialogRequest = Utils.createDialog(this, R.layout.dialog_request);
        dialogRequest.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogRequest.findViewById(R.id.tvAccept).setOnClickListener(this);
        dialogRequest.findViewById(R.id.tvDecline).setOnClickListener(this);
    }

    private void initDeclineDialog() {
        dialogDecline = Utils.createDialog(this, R.layout.dialog_decline_request);
        dialogDecline.findViewById(R.id.tvDeclineRequest).setOnClickListener(this);
        dialogDecline.findViewById(R.id.tvDeclineCancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvAccept:
                startActivity(new Intent(mContext, OrderDetailsActivity.class));
                Utils.gotoNextActivityAnimation(mContext);
                dialogRequest.dismiss();
                break;

            case R.id.tvDecline:
                dialogRequest.dismiss();
                dialogDecline.show();
                break;

            case R.id.tvDeclineRequest:
                dialogDecline.dismiss();
                Toast.makeText(mContext, R.string.request_declined, Toast.LENGTH_SHORT).show();
                break;

            case R.id.tvDeclineCancel:
                dialogRequest.show();
                dialogDecline.dismiss();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        try {

            boolean success = mGoogleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            if (!success) {
                Log.e("sorry try again", "Style parsing failed.");
            }
        }catch (Resources.NotFoundException e){
            e.printStackTrace();
        }

        LatLng mohali = new LatLng(30.706326, 76.704865);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mohali, 14));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
