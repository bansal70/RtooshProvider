package com.rtoosh.provider.views;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.POJO.register.OpeningHours;
import com.rtoosh.provider.model.POJO.register.OpeningTime;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.DateUtils;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.views.adapters.ScheduleHoursAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class ScheduleWorkActivity extends AppBaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerSchedule) RecyclerView recyclerSchedule;
    public ScheduleHoursAdapter scheduleHoursAdapter;
    public List<OpeningHours> openingHoursList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_work);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        openingHoursList = new ArrayList<>();

        String[] days = DateUtils.getAllDays(mContext);
        for (String day : days) {
            OpeningTime time = new OpeningTime("", "");
            OpeningHours hours = new OpeningHours(day, time);
            openingHoursList.add(hours);
        }

        recyclerSchedule.setHasFixedSize(true);
        recyclerSchedule.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        scheduleHoursAdapter = new ScheduleHoursAdapter(mContext, openingHoursList);
        recyclerSchedule.setAdapter(scheduleHoursAdapter);
        scheduleHoursAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.tvAddSchedule)
    public void addHours() {
        Gson gson = new Gson();
        String jsonHours = gson.toJson(scheduleHoursAdapter.hoursList());
        Timber.e("hours-- "+jsonHours);
        RPPreferences.putString(mContext, Constants.SCHEDULE_HOURS_KEY, jsonHours);

        finish();
        Utils.gotoPreviousActivityAnimation(mContext);
    }

}
