package com.rtoosh.provider.views;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.LoginResponse;
import com.rtoosh.provider.model.POJO.RegisterResponse;
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

public class PasswordActivity extends AppBaseActivity {

    private final String PASSWORD_LOGIN_TAG = "LOGIN_WITH_PASSWORD";
    private final String FORGOT_PASSWORD_TAG = "FORGOT_PASSWORD";

    @BindView(R.id.tvProviderName)
    TextView tvProviderName;

    @BindView(R.id.editPassword)
    EditText editPassword;

    @BindView(R.id.passwordLL)
    LinearLayout passwordLL;

    String lang, phone, countryCode;

    Dialog dialogPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);

        ViewCompat.setLayoutDirection(passwordLL, ViewCompat.LAYOUT_DIRECTION_LTR);

        tvProviderName.setText(RPPreferences.readString(mContext, Constants.FULL_NAME_KEY));
        forgotPasswordDialog();
    }

    @OnClick(R.id.btDone)
    public void loginProvider() {
        String password = editPassword.getText().toString().trim();
        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        phone = RPPreferences.readString(mContext, Constants.PHONE_KEY);

        if (password.isEmpty()) {
            showToast(getString(R.string.text_enter_password));
            return;
        }

        showDialog();
        ModelManager.getInstance().getPasswordManager().passwordResponse(mContext, PASSWORD_LOGIN_TAG,
                Operations.passwordParams(phone, password, lang));
    }

    @OnClick(R.id.tvForgotPassword)
    public void forgotPassword() {
        dialogPassword.show();
    }

    public void forgotPasswordDialog() {
        dialogPassword = Utils.createDialog(mContext, R.layout.dialog_forgot_password);
        EditText editPhone = dialogPassword.findViewById(R.id.editPhone);

        dialogPassword.findViewById(R.id.btSend).setOnClickListener(view -> {
            countryCode = RPPreferences.readString(mContext, Constants.COUNTRY_CODE_KEY);
            lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
            if (editPhone.getText().toString().trim().isEmpty()) {
                showToast(getString(R.string.text_enter_password));
                return;
            }

            showDialog();
            ModelManager.getInstance().getPasswordManager().forgotPasswordResponse(mContext, FORGOT_PASSWORD_TAG,
                    Operations.forgotPasswordParams(editPhone.getText().toString().trim(), countryCode, lang));
        });
    }

    private void saveData(LoginResponse loginResponse) {
        LoginResponse.Data data = loginResponse.data;
        RegisterResponse.User user = data.user;

        if (user != null) {
            RPPreferences.putString(mContext, Constants.USER_ID_KEY, user.id);
            RPPreferences.putString(mContext, Constants.ACCOUNT_STATUS_KEY, user.accountStatus);
            RPPreferences.putString(mContext, Constants.ID_NUMBER_KEY, user.idNumber);
            RPPreferences.putString(mContext, Constants.COUNTRY_CODE_KEY, user.countryCode);
            RPPreferences.putString(mContext, Constants.EMAIL_KEY, user.email);
            RPPreferences.putString(mContext, Constants.FULL_NAME_KEY, user.fullName);
            RPPreferences.putString(mContext, Constants.PROFILE_PIC_KEY, user.profilePic);
            RPPreferences.putString(mContext, Constants.WORK_ONLINE_KEY, user.workOnline);
            RPPreferences.putString(mContext, Constants.WORK_SCHEDULE_KEY, user.workSchedule);
            RPPreferences.putString(mContext, Constants.VACATION_MODE_KEY, user.vacationMode);
            RPPreferences.putString(mContext, Constants.USER_STATUS_KEY, user.online);
            RPPreferences.putBoolean(mContext, Constants.REGISTERED_KEY, true);
        }

        startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        Utils.gotoNextActivityAnimation(this);
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(LoginResponse loginResponse) {
        EventBus.getDefault().removeAllStickyEvents();

        switch (loginResponse.getRequestTag()) {
            case PASSWORD_LOGIN_TAG:
                dismissDialog();
                showToast(loginResponse.getMessage());
                saveData(loginResponse);
                break;

            default:
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        switch (apiResponse.getRequestTag()) {
            case FORGOT_PASSWORD_TAG:
                showToast(apiResponse.getMessage());

                if (dialogPassword != null)
                    dialogPassword.dismiss();

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
        showToast(getString(R.string.something_went_wrong));
    }

}
