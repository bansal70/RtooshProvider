package com.rtoosh.provider.views;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

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

public class ContactActivity extends AppBaseActivity {

    private final String CONTACT_TAG = "ContactActivity";
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvEmail) TextView tvEmail;
    @BindView(R.id.editComment) EditText editComment;
    String name, email, user_id, lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lang = RPPreferences.readString(mContext, "lang");
        user_id = RPPreferences.readString(mContext, "user_id");
        email = RPPreferences.readString(mContext, "email");
        name = RPPreferences.readString(mContext, "full_name");

        tvName.setText(name);
        tvEmail.setText(email);
    }

    @OnClick(R.id.btSend)
    public void sendRequest() {
        String comment = editComment.getText().toString();

        showDialog();
        ModelManager.getInstance().getContactManager().contactTask(mContext,
                CONTACT_TAG, user_id, comment, lang);
    }

    @Subscribe(sticky = true)
    public void onEvent(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (apiResponse.getRequestTag()) {
            case CONTACT_TAG:
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
