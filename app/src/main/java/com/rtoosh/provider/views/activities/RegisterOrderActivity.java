package com.rtoosh.provider.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.custom.Utils;

public class RegisterOrderActivity extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_order);
    }

    public void nextSchedule(View v) {
        startActivity(new Intent(this, RegisterServiceActivity.class));
        Utils.gotoNextActivityAnimation(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
