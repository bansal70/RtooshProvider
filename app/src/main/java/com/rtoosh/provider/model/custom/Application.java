package com.rtoosh.provider.model.custom;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.rtoosh.provider.BuildConfig;
import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.LocaleManager;
import com.rtoosh.provider.model.RPPreferences;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

//@ReportsCrashes(formUri = "", mailTo = "rishav.orem@gmail.com")
public final class Application extends android.app.Application {

    public static String Font_Text = "fonts/AvenirLTStd-Light.otf";
    public static String sDefSystemLanguage;

    @Override
    public void onCreate() {
        super.onCreate();
        //ACRA.init(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)           // Enables Crashlytics debugger
                .build();
        Fabric.with(fabric);

        sDefSystemLanguage = Locale.getDefault().getLanguage();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(Font_Text)
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        sDefSystemLanguage = newConfig.locale.getLanguage();

        if (RPPreferences.readString(getApplicationContext(), Constants.LANGUAGE_KEY).isEmpty())
            RPPreferences.putString(getApplicationContext(), Constants.LANGUAGE_KEY, sDefSystemLanguage);
        //LocaleHelper.setLocale(this, RPPreferences.readString(this, Constants.LANGUAGE_KEY));
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            FakeCrashLibrary.log(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    FakeCrashLibrary.logError(t);
                } else if (priority == Log.WARN) {
                    FakeCrashLibrary.logWarning(t);
                }
            }
        }
    }
}