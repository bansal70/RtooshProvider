package com.rtoosh.provider.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
        if (edit1.getText().toString().isEmpty() || edit2.getText().toString().isEmpty() ||
                edit3.getText().toString().isEmpty() || edit4.getText().toString().isEmpty()) {
            Toast.makeText(mContext, R.string.toast_digits_access_code, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(mContext, RegistrationActivity.class));
            Utils.gotoNextActivityAnimation(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
