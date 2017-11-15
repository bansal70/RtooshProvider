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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.RequestDetailsResponse;
import com.rtoosh.provider.model.POJO.Services;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.views.adapters.ServiceAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public class MainActivity extends AppBaseActivity implements OnMapReadyCallback, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";
    private final String ACCEPT_REQUEST_TAG = "AcceptRequest";
    private final String DECLINE_REQUEST_TAG = "DeclineRequest";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.switchOnline) SwitchCompat switchOnline;
    @BindView(R.id.textOnline) TextView textOnline;
    @BindView(R.id.llRequesting) LinearLayout linearRequest;
    @BindView(R.id.rlOffline) RelativeLayout relativeOffline;
    EditText editReason;
    TextView tvProviderName;
    ImageView ivProfilePic;
    View navHeader;

    List<Services> listServices;
    Dialog dialogServices, dialogTerms, dialogRequest, dialogDecline;
    Handler handler;
    GoogleMap mGoogleMap;
    String lang, user_id, fullName, request_id;
    boolean online = false;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        initServiceDialog();
        initTermsDialog();
        initRequestDialog();
        initDeclineDialog();
        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lang = RPPreferences.readString(mContext, "lang");
        user_id = RPPreferences.readString(mContext, "user_id");
        fullName = RPPreferences.readString(mContext, "full_name");

        navHeader = navigationView.getHeaderView(0);
        tvProviderName = navHeader.findViewById(R.id.tvProviderName);
        ivProfilePic = navHeader.findViewById(R.id.ivProfilePic);
        tvProviderName.setText(fullName);

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

        switchOnline.setClickable(false);
        navigationView.setNavigationItemSelectedListener(this);

        if (RPPreferences.readBoolean(mContext, "online")) {
            setOnline();
        }

        if (!flag) {
            request_id = RPPreferences.readString(mContext, "request_id");
            if (!request_id.isEmpty()) {
                linearRequest.setVisibility(View.GONE);
                dialogRequest.show();
            }
        }
        flag = true;
    }

    @OnTouch(R.id.switchOnline)
    boolean changeStatus() {
        if (!switchOnline.isChecked()) {
            dialogServices.show();
            online = true;
        } else {
            online = false;
            showDialog();
            ModelManager.getInstance().getStatusManager().statusTask(mContext, TAG, user_id,
                    switchOnline.isChecked(), lang);
        }
        return false;
    }

    private void initServiceDialog() {
        dialogServices = Utils.createDialog(mContext, R.layout.dialog_services);
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
            dialogServices.dismiss();
            if (RPPreferences.readBoolean(mContext, "terms_accepted")) {
                showDialog();
                ModelManager.getInstance().getStatusManager().statusTask(mContext, TAG, user_id,
                        switchOnline.isChecked(), lang);
                return;
            }
            dialogTerms.show();
        });
    }

    private void initTermsDialog() {
        dialogTerms = Utils.createDialog(mContext, R.layout.dialog_terms);
        dialogTerms.findViewById(R.id.tvAgree).setOnClickListener(view -> {
            RPPreferences.putBoolean(mContext, "terms_accepted", true);
            showDialog();
            ModelManager.getInstance().getStatusManager().statusTask(mContext, TAG, user_id,
                    switchOnline.isChecked(), lang);
        });

        dialogTerms.findViewById(R.id.tvCancel).setOnClickListener(view -> dialogTerms.dismiss());
    }

    @SuppressWarnings("ConstantConditions")
    private void initRequestDialog() {
        dialogRequest = Utils.createDialog(mContext, R.layout.dialog_request);
        dialogRequest.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogRequest.findViewById(R.id.tvAccept).setOnClickListener(this);
        dialogRequest.findViewById(R.id.tvDecline).setOnClickListener(this);
    }


    private void initDeclineDialog() {
        dialogDecline = Utils.createDialog(mContext, R.layout.dialog_decline_request);

        editReason = dialogDecline.findViewById(R.id.editReason);
        dialogDecline.findViewById(R.id.tvDeclineRequest).setOnClickListener(this);
        dialogDecline.findViewById(R.id.tvDeclineCancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvAccept:
                acceptRequest();
                break;

            case R.id.tvDecline:
                dialogRequest.dismiss();
                dialogDecline.show();
                break;

            case R.id.tvDeclineRequest:
                declineRequest();
                flag = true;
                break;

            case R.id.tvDeclineCancel:
                dialogRequest.show();
                dialogDecline.dismiss();
                break;
        }
    }

    private void setOnline() {
        switchOnline.setChecked(true);
        textOnline.setVisibility(View.VISIBLE);
        linearRequest.setVisibility(View.VISIBLE);
        relativeOffline.setVisibility(View.GONE);
        RPPreferences.putBoolean(mContext, "online", true);
    }

    private void setOffline() {
        showToast(getString(R.string.msg_offline));
        switchOnline.setChecked(false);
        textOnline.setVisibility(View.GONE);
        linearRequest.setVisibility(View.GONE);
        relativeOffline.setVisibility(View.VISIBLE);
        RPPreferences.putBoolean(mContext, "online", false);
    }

    private void acceptRequest() {
        showDialog();
        ModelManager.getInstance().getRequestManager().acceptRequestTask(mContext, ACCEPT_REQUEST_TAG,
                Operations.acceptRequestParams(RPPreferences.readString(mContext, "request_id"), lang));
    }

    private void declineRequest() {
        showDialog();
        ModelManager.getInstance().getRequestManager().declineRequestTask(mContext, DECLINE_REQUEST_TAG,
                Operations.declineRequestParams(RPPreferences.readString(mContext, "request_id"),
                        editReason.getText().toString(), lang));
    }

    @Subscribe(sticky = true)
    public void onEvent(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (apiResponse.getRequestTag()) {
            case TAG:
                if (online) {
                    dialogTerms.dismiss();
                    setOnline();
                } else {
                    setOffline();
                }
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(RequestDetailsResponse detailsResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (detailsResponse.getRequestTag()) {
            case ACCEPT_REQUEST_TAG:
                dialogRequest.dismiss();
                startActivity(new Intent(mContext, OrderDetailsActivity.class)
                .putExtra("requestDetails", detailsResponse));
                Utils.gotoNextActivityAnimation(mContext);

                RPPreferences.removeKey(mContext, "request_id");
                RPPreferences.putString(mContext, "accepted_request_id", request_id);
                break;

            case DECLINE_REQUEST_TAG:
                // showToast(detailsResponse.getMessage());
                dialogDecline.dismiss();
                RPPreferences.removeKey(mContext, "request_id");
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
    protected void onResume() {
        super.onResume();
        if (flag) {
            request_id = RPPreferences.readString(mContext, "request_id");
            if (!request_id.isEmpty()) {
                linearRequest.setVisibility(View.GONE);
                dialogRequest.show();
                flag = false;
            }
        }

        String profilePic = RPPreferences.readString(mContext, "profile_pic");

        Glide.with(mContext).load(profilePic)
                .apply(RequestOptions.circleCropTransform().placeholder(R.mipmap.ic_user_placeholder))
                .into(ivProfilePic);
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
                startActivity(new Intent(mContext, ProfileActivity.class));
                break;
            case R.id.nav_wallet:
                startActivity(new Intent(mContext, WalletActivity.class));
                break;
            case R.id.nav_history:
                startActivity(new Intent(mContext, RequestsActivity.class));
                break;
            case R.id.nav_report:
                startActivity(new Intent(mContext, ReportActivity.class));
                break;
            case R.id.nav_setting:

                break;
            case R.id.nav_contact:
                startActivity(new Intent(mContext, ContactActivity.class));
                break;
            case R.id.nav_logout:
                Utils.logoutAlert(mContext);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        Utils.gotoNextActivityAnimation(mContext);
        return true;
    }

    public void requestsClick(View view) {
        startActivity(new Intent(this, RequestsActivity.class));
        Utils.gotoNextActivityAnimation(mContext);
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
