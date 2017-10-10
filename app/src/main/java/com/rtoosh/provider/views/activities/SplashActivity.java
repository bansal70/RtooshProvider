package com.rtoosh.provider.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.custom.Utils;

public class SplashActivity extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(mContext, IntroSliderActivity.class));
                Utils.gotoNextActivityAnimation(mContext);
            }
        },2000);
    }
}
