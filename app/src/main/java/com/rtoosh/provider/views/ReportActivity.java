package com.rtoosh.provider.views;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.model.network.AbstractApiResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportActivity extends AppBaseActivity {

    private final String REPORT_TAG = "ReportActivity";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.editTitle) EditText editTitle;
    @BindView(R.id.editDescription) EditText editDescription;
    String lang, user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lang = RPPreferences.readString(mContext, "lang");
        user_id = RPPreferences.readString(mContext, "user_id");
    }

    @OnClick(R.id.btSubmit)
    public void submitReport() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        if (title.isEmpty() || description.isEmpty()) {
            showToast(getString(R.string.toast_fill_data));
            return;
        }

        showDialog();
        ModelManager.getInstance().getReportManager().reportTask(mContext, REPORT_TAG, user_id,
                title, description, lang);
    }

    @Subscribe(sticky = true)
    public void onEvent(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (apiResponse.getRequestTag()) {
            case REPORT_TAG:
                showToast(apiResponse.getMessage());
                Utils.showAlert(mContext, apiResponse.getMessage());
                break;

            default:
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
}
