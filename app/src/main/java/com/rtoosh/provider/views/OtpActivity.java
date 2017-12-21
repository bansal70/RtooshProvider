package com.rtoosh.provider.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.POJO.LoginResponse;
import com.rtoosh.provider.model.POJO.OtpResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OtpActivity extends AppBaseActivity {

    private final String OTP_TAG = "OtpActivity";
    private final String RESEND_OTP_TAG = "RESEND_OTP";

    @BindView(R.id.layoutOtp) LinearLayout layoutOtp;
    @BindView(R.id.edit1) EditText edit1;
    @BindView(R.id.edit2) EditText edit2;
    @BindView(R.id.edit3) EditText edit3;
    @BindView(R.id.edit4) EditText edit4;
    @BindView(R.id.tvResendCode) TextView tvResendCode;
    @BindView(R.id.textResend) TextView textResend;

    String deviceToken, lang, phone, idNumber;
    static long RESEND_CODE_TIME = 2 * 60 * 1000;
    MyCountDownTimer myCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);

        ViewCompat.setLayoutDirection(layoutOtp, ViewCompat.LAYOUT_DIRECTION_LTR);
        ViewCompat.setLayoutDirection(tvResendCode, ViewCompat.LAYOUT_DIRECTION_LTR);

        initViews();
    }

    private void initViews() {
        deviceToken = FirebaseInstanceId.getInstance().getToken();
        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        phone = RPPreferences.readString(mContext, Constants.PHONE_KEY);
        idNumber = getIntent().getStringExtra("id_number");

        Utils.setTextWatcherMoveFocus(edit1, edit2);
        Utils.setTextWatcherMoveFocus(edit2, edit3);
        Utils.setTextWatcherMoveFocus(edit3, edit4);

        timeToResendCode();
    }

    public void otpDone(View v) {
        String resultedOTP = edit1.getText().toString() + edit2.getText().toString()
                + edit3.getText().toString() + edit4.getText().toString();
        if (edit1.getText().toString().isEmpty() || edit2.getText().toString().isEmpty() ||
                edit3.getText().toString().isEmpty() || edit4.getText().toString().isEmpty()) {
            Toast.makeText(mContext, R.string.toast_digits_access_code, Toast.LENGTH_SHORT).show();
        } else {
            showDialog();
            ModelManager.getInstance().getOtpManager().otpTask(mContext, OTP_TAG, phone, resultedOTP, deviceToken, lang);
        }
    }

    @OnClick(R.id.tvResendCode)
    public void resendCode() {
        showDialog();
        ModelManager.getInstance().getPhoneVerificationManager().execute(mContext, RESEND_OTP_TAG,
                getString(R.string.country_code), phone, deviceToken, lang);
    }

    private void timeToResendCode() {
        textResend.setVisibility(View.VISIBLE);
        tvResendCode.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGrayDark));
        tvResendCode.setEnabled(false);
        myCountDownTimer = new MyCountDownTimer(RESEND_CODE_TIME, 1000);
        myCountDownTimer.start();
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(LoginResponse loginResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (loginResponse.getRequestTag()) {
            case RESEND_OTP_TAG:
                showToast(loginResponse.getMessage());
                timeToResendCode();
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(OtpResponse otpResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        switch (otpResponse.getRequestTag()) {
            case OTP_TAG:
                dismissDialog();
                if (myCountDownTimer != null) {
                    myCountDownTimer.cancel();
                }

                if (idNumber !=null && !idNumber.equals("0") && !idNumber.isEmpty()) {

                    String loginData = getIntent().getStringExtra("loginData");
                    LoginResponse loginResponse = new Gson().fromJson(loginData, LoginResponse.class);
                    LoginResponse.Data data = loginResponse.data;

                    if (data != null) {
                        if (data.accountStatus.equals(Constants.ACCOUNT_SUSPENDED)) {
                            showToast(getString(R.string.error_account_suspended));
                            return;
                        }

                        showToast(otpResponse.getMessage());
                        RPPreferences.putString(mContext, Constants.FULL_NAME_KEY, data.fullName);

                        startActivity(new Intent(mContext, PasswordActivity.class));
                        Utils.gotoNextActivityAnimation(this);
                    }
                    return;
                }

                showToast(otpResponse.getMessage());
                if (otpResponse.register.equals("1"))
                    startActivity(new Intent(mContext, RegisterIDActivity.class));
                else
                    startActivity(new Intent(mContext, RegistrationActivity.class));

                Utils.gotoNextActivityAnimation(this);
                finish();
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

    public class MyCountDownTimer extends CountDownTimer {

        int countSec = 120;
        String sec;

        private MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sec = String.valueOf(--countSec);

            if (countSec < 10)
                sec = "0" + sec;
            String activatedIn = getString(R.string.text_resend_activation) + " " + sec + " " + getString(R.string.text_resend_time);
            textResend.setText(activatedIn);
            //tvSeconds.setText(sec);
        }

        @Override
        public void onFinish() {
            if (!isFinishing()) {
                textResend.setVisibility(View.GONE);
                tvResendCode.setEnabled(true);
                tvResendCode.setBackgroundResource(R.drawable.custom_basic_gradient);
            }
        }
    }

}
