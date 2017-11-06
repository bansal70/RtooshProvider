package com.rtoosh.provider.views;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.Services;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.views.adapters.ServiceAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppBaseActivity implements CompoundButton.OnCheckedChangeListener,
        OnMapReadyCallback, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.switchOnline) SwitchCompat switchOnline;

    TextView tvNewRequests, tvApprovedRequests;
    List<Services> listServices;
    Dialog dialogServices, dialogTerms, dialogRequest, dialogDecline;
    Handler handler;
    GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

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
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switchOnline.setOnCheckedChangeListener(this);
        handler = new Handler();

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Utils.hideKeyboard(mContext, getCurrentFocus());
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
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

        dialogServices.findViewById(R.id.tvNext).setOnClickListener(view -> {
            dialogTerms.show();
            dialogServices.dismiss();
        });
    }

    private void initTermsDialog() {
        dialogTerms = Utils.createDialog(this, R.layout.dialog_terms);

        dialogTerms.findViewById(R.id.tvAgree).setOnClickListener(view -> {
            dialogTerms.dismiss();
            findViewById(R.id.textOnline).setVisibility(View.VISIBLE);
            findViewById(R.id.llRequesting).setVisibility(View.VISIBLE);
            findViewById(R.id.rlOffline).setVisibility(View.GONE);

            handler.postDelayed(() -> {
                //findViewById(R.id.llRequest).setVisibility(View.VISIBLE);
                findViewById(R.id.llRequesting).setVisibility(View.GONE);
                dialogRequest.show();
            }, 2000);

        });

        dialogTerms.findViewById(R.id.tvCancel).setOnClickListener(view -> {
            dialogTerms.dismiss();
            switchOnline.setChecked(false);
        });
    }

    @SuppressWarnings("ConstantConditions")
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

    public void requestsClick(View view) {
        startActivity(new Intent(this, RequestsActivity.class));
        Utils.gotoNextActivityAnimation(mContext);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        /*try {

            boolean success = mGoogleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            if (!success) {
                Log.e("sorry try again", "Style parsing failed.");
            }
        }catch (Resources.NotFoundException e){
            e.printStackTrace();
        }*/

        LatLng mohali = new LatLng(30.706326, 76.704865);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mohali, 15));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_profile:

                break;
            case R.id.nav_wallet:

                break;
            case R.id.nav_history:
                startActivity(new Intent(mContext, RequestsActivity.class));
                Utils.gotoNextActivityAnimation(mContext);
                break;
            case R.id.nav_report:

                break;
            case R.id.nav_setting:

                break;
            case R.id.nav_contact:

                break;
            case R.id.nav_logout:

                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
