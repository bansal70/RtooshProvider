package com.rtoosh.provider.views;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.util.Log;
import android.util.SparseArray;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.HistoryResponse;
import com.rtoosh.provider.model.POJO.ProfileResponse;
import com.rtoosh.provider.model.POJO.RequestDetailsResponse;
import com.rtoosh.provider.model.POJO.Services;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.Utility;
import com.rtoosh.provider.model.custom.DateUtils;
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
import timber.log.Timber;

public class MainActivity extends AppBaseActivity implements OnMapReadyCallback,
        View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";
    private final String ACCEPT_REQUEST_TAG = "AcceptRequest";
    private final String DECLINE_REQUEST_TAG = "DeclineRequest";
    final String UPDATE_LOCATION_TAG = "UpdateLocation";
    private final String HISTORY_TAG = "TOTAL_REQUESTS";
    final String PROFILE_TAG = "PROVIDER_PROFILE";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.switchOnline) SwitchCompat switchOnline;
    @BindView(R.id.textOnline) TextView textOnline;
    @BindView(R.id.llRequesting) LinearLayout linearRequest;
    @BindView(R.id.rlOffline) RelativeLayout relativeOffline;
    @BindView(R.id.tvNewRequest) TextView tvNewRequests;
    @BindView(R.id.tvApprovedRequests) TextView tvApprovedRequests;
    @BindView(R.id.tvRecentDate) TextView tvRecentDate;
    @BindView(R.id.tvRecentTime) TextView tvRecentTime;

    TextView tvMinutes, tvSeconds;

    EditText editReason;
    TextView tvProviderName;
    ImageView ivProfilePic;
    View navHeader;

    Dialog dialogServices, dialogTerms, dialogRequest, dialogDecline;
    Handler handler;
    GoogleMap mGoogleMap;
    String lang, user_id, fullName, request_id;
    boolean online = false;

    MyCountDownTimer myCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

      //  initServiceDialog();
        initTermsDialog();
        initRequestDialog();
        initDeclineDialog();
        initViews();

        fetchLocation();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    mLastLocation = location;
                    Timber.e("Updated location:: "
                            + "latitude-- "+mLastLocation.getLatitude()
                            + "\nlongitude-- "+mLastLocation.getLongitude());
                    LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    if (mGoogleMap != null)
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    ModelManager.getInstance().getUpdateLocationManager().updateLocationTask(UPDATE_LOCATION_TAG,
                            Operations.updateLocationParams(user_id, latLng.latitude, latLng.longitude, lang));
                }
            }
        };
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        user_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);
        fullName = RPPreferences.readString(mContext, Constants.FULL_NAME_KEY);

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

        if (Utility.isOnline(mContext)){
            setOnline();
        } else {
            setOffline();
        }
    }

    @OnTouch(R.id.switchOnline)
    boolean changeStatus() {
        if (!isAccountActive()) {
            showToast("Please wait for your account activation to provide any services.");
            return false;
        }

        if (!Utility.checkStatus(mContext)) {
            return false;
        }
        if (!switchOnline.isChecked()) {
           /* showDialog();
            ModelManager.getInstance().getProfileManager().profileTask(mContext, PROFILE_TAG, user_id, lang);*/
            dialogTerms.show();
            // dialogServices.show();

            online = true;
        } else {
            online = false;
            showDialog();
            ModelManager.getInstance().getStatusManager().statusTask(mContext, TAG, user_id, false, lang);
        }
        return false;
    }

    private void initServiceDialog(ProfileResponse profileResponse) {
        SparseArray<Services> servicesArray = new SparseArray<>();

        //listServices = new ArrayList<>();
        ProfileResponse.Data data = profileResponse.data;
        List<ProfileResponse.Service> serviceList = data.service;

        for (ProfileResponse.Service service : serviceList) {
            ProfileResponse.Category category = service.category;

            servicesArray.put(Integer.parseInt(category.id), new Services(category.catName, false));
           /* if (listServices.size() < 1) {
                listServices.add(new Services(category.catName, false));
            } else {
                for (Services services : listServices) {
                    if (!services.getName().equals(category.catName))
                        listServices.add(new Services(category.catName, false));
                }
            }*/
        }

        dialogServices = Utils.createDialog(mContext, R.layout.dialog_services);

        RecyclerView recyclerView = dialogServices.findViewById(R.id.recyclerServices);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        ServiceAdapter serviceAdapter = new ServiceAdapter(mContext, servicesArray);
        recyclerView.setAdapter(serviceAdapter);

        dialogServices.findViewById(R.id.tvNext).setOnClickListener(view -> {
            dialogServices.dismiss();
            dialogTerms.show();
        });

        dialogServices.show();
    }

    private void initTermsDialog() {
        dialogTerms = Utils.createDialog(mContext, R.layout.dialog_terms);
        dialogTerms.findViewById(R.id.tvAgree).setOnClickListener(view -> {
         //   RPPreferences.putBoolean(mContext, Constants.TERMS_ACCEPTED_KEY, true);
            showDialog();
            ModelManager.getInstance().getStatusManager().statusTask(mContext, TAG, user_id,
                    true, lang);
        });

        dialogTerms.findViewById(R.id.tvCancel).setOnClickListener(view -> dialogTerms.dismiss());
    }

    @SuppressWarnings("ConstantConditions")
    private void initRequestDialog() {
        dialogRequest = Utils.createDialog(mContext, R.layout.dialog_request);
        dialogRequest.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogRequest.findViewById(R.id.tvAccept).setOnClickListener(this);
        dialogRequest.findViewById(R.id.tvDecline).setOnClickListener(this);

        tvMinutes = dialogRequest.findViewById(R.id.tvMinutes);
        tvSeconds = dialogRequest.findViewById(R.id.tvSeconds);
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
        RPPreferences.putString(mContext, Constants.USER_STATUS_KEY, Constants.STATUS_ONLINE);
    }

    private void setOffline() {
        switchOnline.setChecked(false);
        textOnline.setVisibility(View.GONE);
        linearRequest.setVisibility(View.GONE);
        relativeOffline.setVisibility(View.VISIBLE);
        RPPreferences.putString(mContext, Constants.USER_STATUS_KEY, Constants.STATUS_OFFLINE);
    }

    private void acceptRequest() {
        showDialog();
        ModelManager.getInstance().getRequestManager().acceptRequestTask(mContext, ACCEPT_REQUEST_TAG,
                Operations.acceptRequestParams(request_id, lang));
    }

    private void declineRequest() {
        showDialog();
        ModelManager.getInstance().getRequestManager().declineRequestTask(mContext, DECLINE_REQUEST_TAG,
                Operations.declineRequestParams(request_id, editReason.getText().toString(), lang));
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
                    showToast(getString(R.string.msg_offline));
                    setOffline();
                }
                break;

            case DECLINE_REQUEST_TAG:
                showToast(apiResponse.getMessage());
                dialogDecline.dismiss();
                RPPreferences.removeKey(mContext, Constants.REQUEST_ID_KEY);
                myCountDownTimer.cancel();
                setOffline();
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
                    //    .putExtra("requestDetails", detailsResponse)
                        .putExtra("request_id", request_id));
                myCountDownTimer.cancel();
                finish();
                Utils.gotoNextActivityAnimation(mContext);
                removeLocationUpdates();

                setOffline();

                break;
        }
    }

    private void setHistory(HistoryResponse historyResponse) {
        List<HistoryResponse.Data> pendingRequestsList = new ArrayList<>();
        List<HistoryResponse.Data> approvedRequestsList = new ArrayList<>();

        String serverTime = historyResponse.serverTime;
        List<HistoryResponse.Data> dataList = historyResponse.data;
        String timeOut;
        int day = 0, hour = 0, min, sec;
        for (int i=0; i<dataList.size(); i++) {
            RequestDetailsResponse.Order order = dataList.get(i).order;
            if (order.orderType.equals(Constants.ORDER_ONLINE)) {
                 String time = DateUtils.twoDatesBetweenTime(order.created, serverTime);
                 timeOut = DateUtils.getTimeout(time);
                String[] dateTime = timeOut.split(":");
                min = Integer.parseInt(dateTime[0]);
                sec = Integer.parseInt(dateTime[1]);
            } else {
                timeOut = DateUtils.printDifference(order.scheduleDate, serverTime);
                String[] dateTime = timeOut.split(":");
                day = Integer.parseInt(dateTime[0]);
                hour = Integer.parseInt(dateTime[1]);
                min = Integer.parseInt(dateTime[2]);
                sec = Integer.parseInt(dateTime[3]);
            }

            order.timeRemains = timeOut;

            switch (order.status) {
                case Constants.ORDER_PENDING:
                    if (day > 1 || hour > 1 || min > 1 || sec > 1)
                        pendingRequestsList.add(dataList.get(i));
                    break;
                case Constants.ORDER_ACCEPTED:
                    if (day > 1 || hour > 1 || min > 1 || sec > 1)
                        approvedRequestsList.add(dataList.get(i));
                    break;
            }
        }

        if (approvedRequestsList.size() != 0) {
            tvRecentDate.setText(DateUtils.getDateFormat(approvedRequestsList.get(0).order.created));
            tvRecentTime.setText(DateUtils.getTimeFormat(approvedRequestsList.get(0).order.created));
        } else {
            tvRecentDate.setVisibility(View.GONE);
            tvRecentTime.setVisibility(View.GONE);
        }

        tvNewRequests.setText(String.valueOf(pendingRequestsList.size()));
        tvApprovedRequests.setText(String.valueOf(approvedRequestsList.size()));
    }

    @Subscribe(sticky = true)
    public void onEvent(HistoryResponse historyResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (historyResponse.getRequestTag()) {
            case HISTORY_TAG:
                setHistory(historyResponse);
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
            case PROFILE_TAG:
                //showToast(profileResponse.getMessage());
              //  initServiceDialog(profileResponse);
                break;

            default:
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorWithMessageEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (event.getRequestTag()) {
            case HISTORY_TAG:
                tvRecentDate.setVisibility(View.GONE);
                tvRecentTime.setVisibility(View.GONE);
                tvNewRequests.setText("0");
                tvApprovedRequests.setText("0");
                break;

            default:
                showToast(event.getResultMsgUser());
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        showToast(getString(R.string.something_went_wrong));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Utility.onVacationMode(mContext) || !Utility.workOnline(mContext)) {
            switchOnline.setChecked(false);
            setOffline();
        }

        if (!isAccountActive()) {
            setOffline();
        }

        ModelManager.getInstance().getRequestsHistoryManager().historyTask(mContext, HISTORY_TAG,
                Operations.historyParams(user_id, lang));

        String profilePic = RPPreferences.readString(mContext, Constants.PROFILE_PIC_KEY);

        Glide.with(mContext).load(profilePic)
                .apply(RequestOptions.circleCropTransform().placeholder(R.mipmap.ic_user_placeholder))
                .into(ivProfilePic);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        request_id = intent.getStringExtra("order_id");

        if (request_id != null && !request_id.isEmpty()) {
            linearRequest.setVisibility(View.GONE);
            dialogRequest.show();
            tvMinutes.setText(Constants.TIMEOUT_MINUTES);

            if (myCountDownTimer != null)
                myCountDownTimer.cancel();

            myCountDownTimer = new MyCountDownTimer(Constants.COUNTDOWN_TIME, 1000);
            myCountDownTimer.start();
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

       /* LatLng mohali = new LatLng(30.706326, 76.704865);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mohali, 15));*/
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
            case R.id.nav_reviews:
                startActivity(new Intent(mContext, ReviewsActivity.class));
                break;
            case R.id.nav_report:
                startActivity(new Intent(mContext, ReportActivity.class));
                break;
            case R.id.nav_calendar:
                startActivity(new Intent(mContext, CalendarActivity.class));
                break;
            case R.id.nav_setting:
                startActivity(new Intent(mContext, SettingsActivity.class));
                break;
            case R.id.nav_contact:
                startActivity(new Intent(mContext, ContactActivity.class));
                break;
            case R.id.nav_logout:
                Utils.logoutAlert(this);
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

        if (myCountDownTimer != null)
            myCountDownTimer.cancel();
    }

    public class MyCountDownTimer extends CountDownTimer {

        int countSec = 60, countMin = 9;
        String sec, min;

        private MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sec = String.valueOf(--countSec);

            if (countSec < 10)
                sec = "0" + sec;
            tvSeconds.setText(sec);

            if (countSec < 1) {
                countSec = 60;
                if (countMin > 0) {
                    min = String.valueOf(--countMin);
                    min = "0" + min;
                    tvMinutes.setText(min);
                }
            }
        }

        @Override
        public void onFinish() {
            if (!isFinishing()) {
                dialogRequest.dismiss();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
}