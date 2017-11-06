package com.rtoosh.provider.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.iid.FirebaseInstanceId;
import com.rtoosh.provider.R;
import com.rtoosh.provider.model.RPPreferences;

public class SplashActivity extends AppBaseActivity {

    // launcher screen timer
    static int SPLASH_TIME_OUT = 2000;

    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseInstanceId.getInstance().getToken();

        RPPreferences.putString(mContext, "lang", "en");

        handler = new Handler();
        runnable = () -> {
            Intent intent = new Intent(mContext, IntroSliderActivity.class);
            startActivity(intent);
            finish();
        };
        handler.postDelayed(runnable, SPLASH_TIME_OUT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }
}
