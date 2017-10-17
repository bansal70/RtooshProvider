package com.rtoosh.provider.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.custom.Utils;

public class PhoneVerificationActivity extends AppBaseActivity {

    EditText editPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        initViews();
    }

    private void initViews() {
        editPhone = (EditText) findViewById(R.id.editPhone);
    }

    public void sendOtp(View view) {
        String number = editPhone.getText().toString().trim();
        if (number.isEmpty() || number.length() < 10)
            Toast.makeText(mContext, R.string.toast_invalid_number, Toast.LENGTH_SHORT).show();
        else {
            startActivity(new Intent(this, OtpActivity.class));
            Utils.gotoNextActivityAnimation(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
