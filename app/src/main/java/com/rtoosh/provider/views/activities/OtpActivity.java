package com.rtoosh.provider.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.custom.Utils;

public class OtpActivity extends AppBaseActivity {

    EditText edit1, edit2, edit3, edit4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        initViews();
    }

    private void initViews() {
        edit1 = (EditText) findViewById(R.id.edit1);
        edit2 = (EditText) findViewById(R.id.edit2);
        edit3 = (EditText) findViewById(R.id.edit3);
        edit4 = (EditText) findViewById(R.id.edit4);

        Utils.setTextWatcherMoveFocus(edit1, edit2);
        Utils.setTextWatcherMoveFocus(edit2, edit3);
        Utils.setTextWatcherMoveFocus(edit3, edit4);
    }

    public void otpDone(View v) {
        startActivity(new Intent(mContext, RegistrationActivity.class));
        Utils.gotoNextActivityAnimation(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
