package com.rtoosh.provider.views;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.ProfileResponse;
import com.rtoosh.provider.model.POJO.register.OpeningHours;
import com.rtoosh.provider.model.POJO.register.OpeningTime;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.Utility;
import com.rtoosh.provider.model.custom.DateUtils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.views.adapters.ScheduleHoursAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import timber.log.Timber;

public class CalendarActivity extends AppBaseActivity {

    private final String VACATION_TAG = "VACATION_MODE";
    private final String ONLINE_TAG = "ONLINE_MODE";
    private final String SCHEDULE_TAG = "SCHEDULE_MODE";
    private final String PROFILE_TAG = "PROVIDER_PROFILE";
    private final String UPDATE_HOURS = "UPDATE_SCHEDULE_HOURS";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbarTitle) TextView toolbarTitle;
    @BindView(R.id.switchVacation) SwitchCompat switchVacation;
    @BindView(R.id.switchOnline) SwitchCompat switchOnline;
    @BindView(R.id.switchSchedule) SwitchCompat switchSchedule;
    @BindView(R.id.imgEdit) ImageView imgEdit;
    @BindView(R.id.tvScheduleText) TextView tvScheduleText;
    @BindView(R.id.scheduleLayout) LinearLayout scheduleLayout;

    String lang, user_id;
    String work_online, work_schedule, vacation_mode;
    boolean isEdit = false;

    @BindView(R.id.recyclerSchedule) RecyclerView recyclerSchedule;
    public ScheduleHoursAdapter scheduleHoursAdapter;
    public List<OpeningHours> openingHoursList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText(getString(R.string.nav_calendar));

        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        user_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);

        work_online = RPPreferences.readString(mContext, Constants.WORK_ONLINE_KEY);
        work_schedule = RPPreferences.readString(mContext, Constants.WORK_SCHEDULE_KEY);
        vacation_mode = RPPreferences.readString(mContext, Constants.VACATION_MODE_KEY);

        if (Utility.onVacationMode(mContext)) {
            switchOnline.setEnabled(false);
            switchSchedule.setEnabled(false);
        }

        showDialog();
        ModelManager.getInstance().getProfileManager().profileTask(mContext, PROFILE_TAG, user_id, lang);
    }

    @OnTouch(R.id.switchVacation)
    boolean changeVacationMode() {
        vacation_mode = RPPreferences.readString(mContext, Constants.VACATION_MODE_KEY);
        showDialog();
        if (vacation_mode.equals(Constants.VACATION_MODE_ON)) {
            ModelManager.getInstance().getCalendarManager().updateVacationTask(mContext, VACATION_TAG,
                    Operations.vacationParams(user_id, Constants.VACATION_MODE_OFF, lang));
        } else {
            ModelManager.getInstance().getCalendarManager().updateVacationTask(mContext, VACATION_TAG,
                    Operations.vacationParams(user_id, Constants.VACATION_MODE_ON, lang));
        }
        return false;
    }

    @OnTouch(R.id.switchOnline)
    boolean changeOnlineStatus() {
        showDialog();
        work_online = RPPreferences.readString(mContext, Constants.WORK_ONLINE_KEY);
        if (work_online.equals(Constants.WORK_TYPE_ONLINE)) {
            ModelManager.getInstance().getCalendarManager().updateWorkTask(mContext, ONLINE_TAG,
                    Operations.updateWorkParams(user_id, Constants.WORK_TYPE_OFFLINE, work_schedule, lang));
        } else {
            ModelManager.getInstance().getCalendarManager().updateWorkTask(mContext, ONLINE_TAG,
                    Operations.updateWorkParams(user_id, Constants.WORK_TYPE_ONLINE, work_schedule, lang));
        }
        return false;
    }

    @OnTouch(R.id.switchSchedule)
    boolean changeScheduleStatus() {
        showDialog();
        work_schedule = RPPreferences.readString(mContext, Constants.WORK_SCHEDULE_KEY);
        if (work_schedule.equals(Constants.WORK_TYPE_SCHEDULE)) {
            ModelManager.getInstance().getCalendarManager().updateWorkTask(mContext, SCHEDULE_TAG,
                    Operations.updateWorkParams(user_id, work_online, Constants.WORK_TYPE_OFFLINE, lang));
        } else {
            ModelManager.getInstance().getCalendarManager().updateWorkTask(mContext, SCHEDULE_TAG,
                    Operations.updateWorkParams(user_id, work_online, Constants.WORK_TYPE_SCHEDULE, lang));
        }
        return false;
    }

    @OnClick(R.id.imgEdit)
    public void editProfile() {
        if (isEdit) {
           // showDialog();
            recyclerSchedule.setEnabled(true);
            addHours();
        } else {
            imgEdit.setImageResource(R.drawable.ic_profile_done);
            recyclerSchedule.setEnabled(true);
            isEdit = true;
        }
    }

    private void setCalendar(ProfileResponse profileResponse) {
        ProfileResponse.Data data = profileResponse.data;
        ProfileResponse.User user = data.user;

        if (user.workOnline.equals(Constants.WORK_TYPE_ONLINE)) {
            switchOnline.setChecked(true);
            RPPreferences.putString(mContext, Constants.WORK_ONLINE_KEY, Constants.WORK_TYPE_ONLINE);
        } else {
            switchOnline.setChecked(false);
            RPPreferences.putString(mContext, Constants.WORK_ONLINE_KEY, Constants.WORK_TYPE_OFFLINE);
        }
        if (user.workSchedule.equals(Constants.WORK_TYPE_SCHEDULE)) {
            switchSchedule.setChecked(true);
            tvScheduleText.setVisibility(View.GONE);
            scheduleLayout.setVisibility(View.VISIBLE);
            RPPreferences.putString(mContext, Constants.WORK_SCHEDULE_KEY, Constants.WORK_TYPE_SCHEDULE);
        } else {
            switchSchedule.setChecked(false);
            tvScheduleText.setVisibility(View.VISIBLE);
            scheduleLayout.setVisibility(View.GONE);
            RPPreferences.putString(mContext, Constants.WORK_SCHEDULE_KEY, Constants.WORK_TYPE_OFFLINE);
        }

        if (user.vacationMode.equals(Constants.VACATION_MODE_ON)) {
            switchVacation.setChecked(true);
            RPPreferences.putString(mContext, Constants.VACATION_MODE_KEY, Constants.VACATION_MODE_ON);
        } else {
            switchVacation.setChecked(false);
            RPPreferences.putString(mContext, Constants.VACATION_MODE_KEY, Constants.VACATION_MODE_OFF);
        }

        List<ProfileResponse.Hour> hoursList = data.hour;

        openingHoursList = new ArrayList<>();
        for (ProfileResponse.Hour hour : hoursList) {
            OpeningTime time = new OpeningTime(hour.open, hour.close);
            OpeningHours hours = new OpeningHours(hour.day, time);
            openingHoursList.add(hours);
        }

        if (hoursList.size() == 0) {
            String[] days = DateUtils.getAllDays();
            for (String day : days) {
                OpeningTime time = new OpeningTime("", "");
                OpeningHours hours = new OpeningHours(day, time);
                openingHoursList.add(hours);
            }
        }

        recyclerSchedule.setHasFixedSize(true);
        recyclerSchedule.setNestedScrollingEnabled(false);
        recyclerSchedule.setLayoutManager(new LinearLayoutManager(mContext));

        scheduleHoursAdapter = new ScheduleHoursAdapter(mContext, openingHoursList, getSupportFragmentManager());
        recyclerSchedule.setAdapter(scheduleHoursAdapter);
        scheduleHoursAdapter.notifyDataSetChanged();
    }

    public void addHours() {
        Gson gson = new Gson();
        String jsonHours = gson.toJson(scheduleHoursAdapter.hoursList());
        Timber.e("hours-- "+jsonHours);

        showDialog();
        ModelManager.getInstance().getCalendarManager().updateScheduleTask(mContext, UPDATE_HOURS,
                Operations.updateScheduleParams(user_id, jsonHours, lang));
    }

    @Subscribe(sticky = true)
    public void onEvent(ProfileResponse profileResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (profileResponse.getRequestTag()) {
            case PROFILE_TAG:
                setCalendar(profileResponse);
                break;

            default:
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (apiResponse.getRequestTag()) {
            case VACATION_TAG:
                showToast(apiResponse.getMessage());

                if (vacation_mode.equals(Constants.VACATION_MODE_OFF)) {
                    RPPreferences.putString(mContext, Constants.VACATION_MODE_KEY, Constants.VACATION_MODE_ON);
                    switchOnline.setEnabled(false);
                    switchSchedule.setEnabled(false);
                    switchVacation.setChecked(true);
                } else {
                    RPPreferences.putString(mContext, Constants.VACATION_MODE_KEY, Constants.VACATION_MODE_OFF);
                    switchOnline.setEnabled(true);
                    switchSchedule.setEnabled(true);
                    switchVacation.setChecked(false);
                }
                break;

            case ONLINE_TAG:
                if (work_online.equals(Constants.WORK_TYPE_ONLINE)) {
                    RPPreferences.putString(mContext, Constants.WORK_ONLINE_KEY, Constants.WORK_TYPE_OFFLINE);
                    work_online = Constants.WORK_TYPE_OFFLINE;
                    switchOnline.setChecked(false);
                } else {
                    RPPreferences.putString(mContext, Constants.WORK_ONLINE_KEY, Constants.WORK_TYPE_ONLINE);
                    work_online = Constants.WORK_TYPE_ONLINE;
                    switchOnline.setChecked(true);
                }
                break;

            case SCHEDULE_TAG:
                if (work_schedule.equals(Constants.WORK_TYPE_SCHEDULE)) {
                    RPPreferences.putString(mContext, Constants.WORK_SCHEDULE_KEY, Constants.WORK_TYPE_OFFLINE);
                    work_schedule = Constants.WORK_TYPE_OFFLINE;
                    switchSchedule.setChecked(false);
                    tvScheduleText.setVisibility(View.VISIBLE);
                    scheduleLayout.setVisibility(View.GONE);
                } else {
                    RPPreferences.putString(mContext, Constants.WORK_SCHEDULE_KEY, Constants.WORK_TYPE_SCHEDULE);
                    work_schedule = Constants.WORK_TYPE_SCHEDULE;
                    switchSchedule.setChecked(true);
                    tvScheduleText.setVisibility(View.GONE);
                    scheduleLayout.setVisibility(View.VISIBLE);
                }
                break;

            case UPDATE_HOURS:
                showToast(apiResponse.getMessage());
                isEdit = false;
                imgEdit.setImageResource(R.drawable.ic_edit_service);
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

}
