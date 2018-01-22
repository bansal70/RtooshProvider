package com.rtoosh.provider.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
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

    @BindView(R.id.numberLayout) LinearLayout numberLayout;
    @BindView(R.id.editPhone) EditText editPhone;
    @BindView(R.id.tvCode) TextView tvCode;
    String deviceToken, lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        ButterKnife.bind(this);

        /*String langCode = Locale.getDefault().getLanguage();
        if (langCode.isEmpty())
            langCode = Resources.getSystem().getConfiguration().locale.getLanguage();*/

        if (RPPreferences.readString(mContext, Constants.LANGUAGE_KEY).isEmpty()) {
            String langCode = "ar";
            RPPreferences.putString(mContext, Constants.LANGUAGE_KEY, langCode);
        }

        ViewCompat.setLayoutDirection(numberLayout, ViewCompat.LAYOUT_DIRECTION_LTR);

        initViews();
    }

    private void initViews() {
        Utils.setTextWatcherPhoneLimit(editPhone);
        RPPreferences.removeKey(mContext, Constants.USER_ID_KEY);
        deviceToken = FirebaseInstanceId.getInstance().getToken();
        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
    }

    public void sendOtp(View view) {
        String number = editPhone.getText().toString().trim();
        String code = tvCode.getText().toString();

        if (!Utils.isValidPhone(number)) {
            showToast(getString(R.string.toast_invalid_number));
            return;
        }

        showDialog();
        ModelManager.getInstance().getPhoneVerificationManager().execute(mContext, OTP_TAG, code, number,
                deviceToken, lang);
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(LoginResponse loginResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        switch (loginResponse.getRequestTag()) {
            case OTP_TAG:
                dismissDialog();
                showToast(loginResponse.getMessage());
                LoginResponse.Data data = loginResponse.data;
                RPPreferences.putString(mContext, Constants.COUNTRY_CODE_KEY, getString(R.string.country_code));
                RPPreferences.putString(mContext, Constants.PHONE_KEY, editPhone.getText().toString().trim());

                if (data.accountStatus != null && data.accountStatus.equals(Constants.ACCOUNT_SUSPENDED)) {
                    showToast(getString(R.string.error_account_suspended));
                    return;
                }

                if (data.id != null) {
                    RPPreferences.putString(mContext, Constants.USER_ID_KEY, data.id);
                }

                if (data.fullName != null) {
                    RPPreferences.putString(mContext, Constants.FULL_NAME_KEY, data.fullName);
                }

                startActivity(new Intent(this, OtpActivity.class)
                        .putExtra("loginData", new Gson().toJson(loginResponse))
                        .putExtra("id_number", data.idNumber));

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
                showToast(getString(R.string.something_went_wrong));
                break;

            default:
                break;
        }
    }

}
