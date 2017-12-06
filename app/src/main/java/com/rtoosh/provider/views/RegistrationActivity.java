package com.rtoosh.provider.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.POJO.RegisterResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegistrationActivity extends AppBaseActivity implements AdapterView.OnItemSelectedListener{

    private final String REGISTER_TAG = "RegistrationActivity";

    @BindView(R.id.editName) EditText editName;
    @BindView(R.id.editEmail) EditText editEmail;
    @BindView(R.id.editPassword) EditText editPassword;
    @BindView(R.id.spinnerSelect) Spinner spinnerSelect;

    String deviceToken, lang, phone , country_code;
    List<String> listSelect;
    String select = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        deviceToken = FirebaseInstanceId.getInstance().getToken();
        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        phone = RPPreferences.readString(mContext, Constants.PHONE_KEY);
        country_code = RPPreferences.readString(mContext, Constants.COUNTRY_CODE_KEY);

        listSelect = new ArrayList<>();
        listSelect.add("Select");
        listSelect.add("Salon Owner");
        listSelect.add("Independent Service Provider");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, listSelect);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSelect.setAdapter(arrayAdapter);
        spinnerSelect.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0) {
            select = listSelect.get(position);
        } else {
            select = "";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void registerDone(View v) {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString();

        if (name.isEmpty() || select.isEmpty() || email.isEmpty() || password.isEmpty())
            Toast.makeText(mContext, R.string.toast_fill_data, Toast.LENGTH_SHORT).show();
        else if (!Utils.emailValidator(email))
            Toast.makeText(mContext, R.string.toast_invalid_email, Toast.LENGTH_SHORT).show();
        else if (password.length() < 6)
            Toast.makeText(mContext, R.string.toast_invalid_password_length, Toast.LENGTH_SHORT).show();
        else {
            showDialog();
            ModelManager.getInstance().getRegistrationManager().registrationTask(mContext, REGISTER_TAG, name,
                    email, country_code, phone, password, select, deviceToken, lang);
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(RegisterResponse registerResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (registerResponse.getRequestTag()) {
            case REGISTER_TAG:
                showToast(registerResponse.getMessage());
                RegisterResponse.Data data = registerResponse.data;
                RegisterResponse.User user = data.user;
                RPPreferences.putString(mContext, Constants.USER_ID_KEY, user.id);

                startActivity(new Intent(this, RegisterIDActivity.class)
                        .putExtra("user_id", user.id));
                finish();
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
            case REGISTER_TAG:
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
            case REGISTER_TAG:
                dismissDialog();
                showToast(getString(R.string.something_went_wrong));
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
