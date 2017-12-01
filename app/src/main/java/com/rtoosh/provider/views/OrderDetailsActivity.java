package com.rtoosh.provider.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.RequestDetailsResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.MySupportMapFragment;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.views.adapters.OrdersAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderDetailsActivity extends AppBaseActivity implements OnMapReadyCallback, RoutingListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        MySupportMapFragment.OnTouchListener {

    private final String ORDER_DETAILS_TAG = "ORDER_DETAILS";
    private final String TAG = "OrderDetailsActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.recyclerOrders)
    RecyclerView recyclerOrders;
    @BindView(R.id.tvTotalPrice)
    TextView tvTotalPrice;
    @BindView(R.id.tvCustomerName)
    TextView tvCustomerName;
    @BindView(R.id.tvCall)
    TextView tvCall;
    @BindView(R.id.tvSms)
    TextView tvSms;
    @BindView(R.id.tvTotalPersons)
    TextView tvTotalPersons;
    @BindView(R.id.tvHour)
    TextView tvHour;
    @BindView(R.id.tvMinutes)
    TextView tvMinutes;
    @BindView(R.id.tvEstimatedTime)
    TextView tvEstimatedTime;
    @BindView(R.id.tvExpectedMin)
    TextView tvExpectedMin;
    @BindView(R.id.tvTimeToStart)
    TextView tvTimeToStart;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.rlDiscount)
    RelativeLayout rlDiscount;
    @BindView(R.id.tvDiscount)
    TextView tvDiscount;

    GoogleMap mGoogleMap;
    OrdersAdapter ordersAdapter;
    String user_id, lang, request_id, phone = "";
    RequestDetailsResponse requestDetailsResponse;
    int totalPersons = 0, hour = 0, minutes = 0;
    double amount = 0, price = 0;

    /*private static final int[] COLORS = new int[]{R.color.colorPrimaryDark,R.color.colorPrimary,
            R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary};*/

    private List<Polyline> polylines;
    protected LatLng start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        toolbarTitle.setText(getString(R.string.order_details));

        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        user_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);
        request_id = getIntent().getStringExtra("request_id");
        //requestDetailsResponse = (RequestDetailsResponse) getIntent().getSerializableExtra("requestDetails");

        showDialog();
        ModelManager.getInstance().getOrderDetailsManager().requestDetailsTask(mContext, ORDER_DETAILS_TAG,
                Operations.requestDetailsParams(request_id, lang));

        polylines = new ArrayList<>();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    private void setData(RequestDetailsResponse requestDetailsResponse) {
        RequestDetailsResponse.Data data = requestDetailsResponse.data;
        RequestDetailsResponse.Client client = data.client;
        RequestDetailsResponse.OrderDetails order = data.order;

        List<RequestDetailsResponse.OrderItem> listOrders = data.orderItem;
        tvCustomerName.setText(client.fullName);
        phone = client.phone;
        if (phone.isEmpty()) {
            tvCall.setVisibility(View.GONE);
            tvSms.setVisibility(View.GONE);
        }

        if (order.discount.equals("0") || order.discount.isEmpty())
            rlDiscount.setVisibility(View.GONE);
        else
            rlDiscount.setVisibility(View.VISIBLE);

        for (int i = 0; i < listOrders.size(); i++) {
            RequestDetailsResponse.Service service = listOrders.get(i).service;

            int persons = Integer.parseInt(listOrders.get(i).noOfPerson);
            totalPersons += persons;
            String[] time = service.duration.split(":");
            hour += persons * Integer.parseInt(time[0]);
            minutes += persons * Integer.parseInt(time[1]);

            amount = Double.parseDouble(listOrders.get(i).amount);
            price += persons * amount;
        }

        price -= Integer.parseInt(order.discount);

        int mHours = minutes / 60;
        int mMinutes = minutes % 60;
        hour += mHours;

        tvTotalPersons.setText(String.format("%s %s", String.valueOf(totalPersons), getString(R.string.persons)));
        tvHour.setText(String.valueOf(hour));
        tvMinutes.setText(String.valueOf(mMinutes));
        tvDiscount.setText(order.discount);

        recyclerOrders.setLayoutManager(new LinearLayoutManager(mContext));
        ordersAdapter = new OrdersAdapter(mContext, listOrders);
        recyclerOrders.setAdapter(ordersAdapter);
        ordersAdapter.notifyDataSetChanged();
        tvTotalPrice.setText(String.format("%s %s", String.valueOf(price), Constants.CURRENCY));

        MySupportMapFragment mapFragment = (MySupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setListener(this);

        fetchLocation();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    mLastLocation = location;
                    start = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    Routing routing = new Routing.Builder()
                            .travelMode(AbstractRouting.TravelMode.DRIVING)
                            .withListener(OrderDetailsActivity.this)
                            .alternativeRoutes(false)
                            .waypoints(start, end)
                            .build();
                    routing.execute();
                }
            }

        };

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        request_id = intent.getStringExtra("request_id");
    }

    @OnClick(R.id.tvSms)
    public void smsUser() {
        Utils.smsIntent(mContext, phone);
    }

    @OnClick(R.id.tvCall)
    public void callUser() {
        Utils.callIntent(mContext, phone);
    }

    @OnClick({R.id.tvStartService, R.id.tvReady})
    public void startService(View view) {
        showDialog();
        ModelManager.getInstance().getServiceStartedManager().startServiceTask(mContext, TAG,
                Operations.serviceStartedParams(request_id, lang));
    }

    @Subscribe(sticky = true)
    public void onEvent(RequestDetailsResponse detailsResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (detailsResponse.getRequestTag()) {
            case ORDER_DETAILS_TAG:
                setData(detailsResponse);
                requestDetailsResponse = detailsResponse;
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (apiResponse.getRequestTag()) {
            case TAG:
                startActivity(new Intent(mContext, ServiceActivity.class)
                        // .putExtra("requestDetails", requestDetailsResponse)
                        .putExtra("request_id", request_id));
                Utils.gotoNextActivityAnimation(mContext);
                removeLocationUpdates();
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
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        mGoogleMap.setMyLocationEnabled(true);

        RequestDetailsResponse.Data data = requestDetailsResponse.data;
        RequestDetailsResponse.OrderDetails order = data.order;
        RequestDetailsResponse.Client client = data.client;

        double providerLat = Double.parseDouble(order.lat);
        double providerLng = Double.parseDouble(order.lng);

        double clientLat = Double.parseDouble(client.lat);
        double clientLng = Double.parseDouble(client.lng);

        start = new LatLng(providerLat, providerLng);
        end = new LatLng(clientLat, clientLng);
       // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 15));

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(start, end)
                .build();
        routing.execute();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        e.printStackTrace();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        mGoogleMap.clear();
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        //       for (int i=0; i<1; i++) {

        //In case of more than 5 alternative routes
        // int colorIndex = i % COLORS.length;

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(ContextCompat.getColor(mContext, R.color.colorPrimary));
        polyOptions.width(10);
        polyOptions.addAll(route.get(0).getPoints());
        Polyline polyline = mGoogleMap.addPolyline(polyOptions);
        polylines.add(polyline);

            /*Toast.makeText(getApplicationContext(),"Route "+ (i+1) +
                    ": distance - "+ route.get(i).getDistanceValue()+": duration - "+
                    route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();*/

        String distanceWithTime = route.get(0).getDurationText() + " ("
                + route.get(0).getDistanceText() + ")";

        tvEstimatedTime.setText(distanceWithTime);
        int time = route.get(0).getDurationValue();
        if (time < 60)
            time = 1;
        else
            time = time / 60;

        tvExpectedMin.setText(String.valueOf(time));
        tvTimeToStart.setText(String.format("%s left", route.get(0).getDurationText()));
        //     }

        // Start marker
        MarkerOptions first = new MarkerOptions();
        first.position(start);
        first.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_pin));
        mGoogleMap.addMarker(first);

        // End marker
       // MarkerOptions second = new MarkerOptions();
       // second.position(end);
       // second.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_pin));
       // mGoogleMap.addMarker(second);

        /*LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(start);
        builder.include(end);
        *//* initialize the padding for map boundary*//*
        LatLngBounds bounds = builder.build();*/
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(start);
        builder.include(end);
        LatLngBounds bounds = builder.build();
        LatLng center = bounds.getCenter();
        builder.include(new LatLng(center.latitude - 0.001f, center.longitude - 0.001f));
        builder.include(new LatLng(center.latitude + 0.001f, center.longitude + 0.001f));
        bounds = builder.build();

        int padding = 50;
      //  LatLngBounds latLngBounds = Utils.createBoundsWithMinDiagonal(first, second);
        /* create the camera with bounds and padding to set into map*/
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mGoogleMap.animateCamera(cu);
    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        /*mLastLocation = location;
        start = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(start, end)
                .build();
        routing.execute();*/

        // Start marker
      /*  MarkerOptions first = new MarkerOptions();
        first.position(start);
        first.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_pin));
        mGoogleMap.addMarker(first);

        // End marker
        MarkerOptions second = new MarkerOptions();
        second.position(end);
        second.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_pin));
        mGoogleMap.addMarker(second);

        int padding = 30;
        LatLngBounds latLngBounds = Utils.createBoundsWithMinDiagonal(first, second);
        *//* create the camera with bounds and padding to set into map*//*
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(latLngBounds, padding);
        mGoogleMap.moveCamera(cu);*/
    }

    @OnClick(R.id.tvOpenMap)
    public void openMap(){
        Utils.mapIntent(mContext, start.latitude, start.longitude, end.latitude, end.longitude);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onTouch() {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }
}
