package com.rtoosh.provider.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.POJO.LoginResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhoneVerificationActivity extends AppBaseActivity {

    public static final String OTP_TAG = "PhoneVerificationActivity";

    @BindView(R.id.editPhone) EditText editPhone;
    @BindView(R.id.tvCode) TextView tvCode;
    String deviceToken, lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        RPPreferences.removeKey(mContext, "user_id");
        deviceToken = FirebaseInstanceId.getInstance().getToken();
        lang = RPPreferences.readString(mContext, "lang");
    }

    public void sendOtp(View view) {
        String number = editPhone.getText().toString().trim();
        String code = tvCode.getText().toString();

        if (number.isEmpty() || number.length() < 10)
            showToast(getString(R.string.toast_invalid_number));
        else {
            showDialog();
            ModelManager.getInstance().getPhoneVerificationManager().execute(mContext, OTP_TAG, code, number,
                    deviceToken, lang);
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(LoginResponse loginResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        switch (loginResponse.getRequestTag()) {
            case OTP_TAG:
                dismissDialog();
                showToast(loginResponse.getMessage());
                LoginResponse.Data data = loginResponse.data;
                if (data != null) {
                    RPPreferences.putString(mContext, "user_id", data.id);
                    RPPreferences.putString(mContext, "active", data.accountStatus);
                    RPPreferences.putString(mContext, "id_number", data.idNumber);
                    RPPreferences.putString(mContext, "country_code", tvCode.getText().toString());
                    RPPreferences.putString(mContext, "email", data.email);
                    RPPreferences.putString(mContext, "full_name", data.fullName);
                    RPPreferences.putString(mContext, "profile_pic", data.profilePic);
                }

                RPPreferences.putString(mContext, "phone", editPhone.getText().toString().trim());
                startActivity(new Intent(this, OtpActivity.class));
                Utils.gotoNextActivityAnimation(this);
                break;

            default:
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorWithMessageEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        switch (event.getRequestTag()) {
            case OTP_TAG:
                dismissDialog();
                showToast(event.getResultMsgUser());
                break;

            default:
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        switch (event.getRequestTag()) {
            case OTP_TAG:
                dismissDialog();
                showToast(Constants.SERVER_ERROR);
                break;

            default:
                break;
        }
    }

}
