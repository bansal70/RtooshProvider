package com.rtoosh.provider.views;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

import static com.rtoosh.provider.R.id.map;

public class OrderDetailsActivity extends AppBaseActivity implements OnMapReadyCallback, RoutingListener {

    private final String TAG = "OrderDetailsActivity";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbarTitle) TextView toolbarTitle;
    @BindView(R.id.recyclerOrders) RecyclerView recyclerOrders;
    @BindView(R.id.tvTotalPrice) TextView tvTotalPrice;
    @BindView(R.id.tvCustomerName) TextView tvCustomerName;
    @BindView(R.id.tvCall) TextView tvCall;
    @BindView(R.id.tvSms) TextView tvSms;
    @BindView(R.id.tvTotalPersons) TextView tvTotalPersons;
    @BindView(R.id.tvHour) TextView tvHour;
    @BindView(R.id.tvMinutes) TextView tvMinutes;
    @BindView(R.id.tvEstimatedTime) TextView tvEstimatedTime;

    GoogleMap mGoogleMap;
    OrdersAdapter ordersAdapter;
    String user_id, lang, request_id, phone = "";
    RequestDetailsResponse requestDetailsResponse;
    int totalPersons = 0, hour = 0, minutes = 0;
    double amount = 0, price = 0;

    private static final int[] COLORS = new int[]{R.color.colorPrimaryDark,R.color.colorPrimary,
            R.color.colorPrimaryDark,R.color.colorAccent,R.color.colorPrimaryDark};

    private List<Polyline> polylines;
    protected LatLng start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        toolbarTitle.setText(getString(R.string.order_details));

        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        user_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);
        request_id = getIntent().getStringExtra("request_id");
        requestDetailsResponse = (RequestDetailsResponse) getIntent().getSerializableExtra("requestDetails");

        polylines = new ArrayList<>();
        setData();
    }

    private void setData() {
        RequestDetailsResponse.Data data = requestDetailsResponse.data;
        RequestDetailsResponse.Client client = data.client;
        List<RequestDetailsResponse.OrderItem> listOrders = data.orderItem;
        tvCustomerName.setText(client.fullName);
        phone = client.phone;
        if (phone.isEmpty()) {
            tvCall.setVisibility(View.GONE);
            tvSms.setVisibility(View.GONE);
        }

        for (int i=0; i<listOrders.size(); i++) {
            RequestDetailsResponse.Service service = listOrders.get(i).service;

            int persons = Integer.parseInt(listOrders.get(i).noOfPerson);
            totalPersons += persons;
            String[] time = service.duration.split(":");
            hour += persons * Integer.parseInt(time[0]);
            minutes += persons * Integer.parseInt(time[1]);

            amount = Double.parseDouble(listOrders.get(i).amount);
            price += persons * amount;
        }

        int mHours = minutes / 60;
        int mMinutes = minutes % 60;
        hour += mHours;

        tvTotalPersons.setText(String.format("%s %s", String.valueOf(totalPersons), getString(R.string.persons)));
        tvHour.setText(String.valueOf(hour));
        tvMinutes.setText(String.valueOf(mMinutes));

        recyclerOrders.setLayoutManager(new LinearLayoutManager(mContext));
        ordersAdapter = new OrdersAdapter(mContext, listOrders);
        recyclerOrders.setAdapter(ordersAdapter);
        ordersAdapter.notifyDataSetChanged();
        tvTotalPrice.setText(String.format("%s %s", String.valueOf(price), Constants.CURRENCY));
    }

    @OnClick(R.id.tvSms)
    public void smsUser() {
        Utils.smsIntent(mContext, phone);
    }

    @OnClick(R.id.tvCall)
    public void callUser() {
        Utils.callIntent(mContext, phone);
    }

    @OnClick(R.id.tvStartService)
    public void startService(View view) {
        showDialog();
        ModelManager.getInstance().getServiceStartedManager().startServiceTask(mContext, TAG,
                Operations.serviceStartedParams(request_id, lang));
    }

    @Subscribe(sticky = true)
    public void onEvent(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (apiResponse.getRequestTag()) {
            case TAG:
                startActivity(new Intent(mContext, ServiceActivity.class)
                        .putExtra("requestDetails", requestDetailsResponse)
                        .putExtra("request_id", request_id));
                Utils.gotoNextActivityAnimation(mContext);
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
        }catch (Resources.NotFoundException e){
            e.printStackTrace();
        }
        RequestDetailsResponse.Data data = requestDetailsResponse.data;
        RequestDetailsResponse.Provider provider = data.provider;
        RequestDetailsResponse.Client client = data.client;

        double providerLat = Double.parseDouble(provider.lat);
        double providerLng = Double.parseDouble(provider.lng);

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

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(ContextCompat.getColor(mContext, COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mGoogleMap.addPolyline(polyOptions);
            polylines.add(polyline);

            /*Toast.makeText(getApplicationContext(),"Route "+ (i+1) +
                    ": distance - "+ route.get(i).getDistanceValue()+": duration - "+
                    route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();*/

            String distanceWithTime = route.get(i).getDurationText() + " ("
                    + route.get(i).getDistanceText() + ")";

            tvEstimatedTime.setText(distanceWithTime);
        }

        // Start marker
        MarkerOptions first = new MarkerOptions();
        first.position(start);
        first.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_pin));
        mGoogleMap.addMarker(first);

        // End marker
        MarkerOptions second = new MarkerOptions();
        second.position(end);
        second.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_pin));
        mGoogleMap.addMarker(second);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(start);
        builder.include(end);
        /* initialize the padding for map boundary*/
        int padding = 20;
        LatLngBounds bounds = builder.build();
        LatLngBounds latLngBounds = Utils.createBoundsWithMinDiagonal(first, second);
        /* create the camera with bounds and padding to set into map*/
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(latLngBounds, padding);
        mGoogleMap.moveCamera(cu);
    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);

    }
}
