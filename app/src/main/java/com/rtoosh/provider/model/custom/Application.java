package com.rtoosh.provider.model.custom;

import android.content.Context;

import com.rtoosh.provider.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public final class Application extends android.app.Application {

    public static String Font_Text = "fonts/AvenirLTStd-Light.otf";

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(Font_Text)
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}