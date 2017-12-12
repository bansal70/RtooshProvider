package com.rtoosh.provider.views;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.iid.FirebaseInstanceId;
import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.OngoingRequestResponse;
import com.rtoosh.provider.model.POJO.RequestDetailsResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;

import timber.log.Timber;

public class SplashActivity extends AppBaseActivity {

    private final String REQUEST_TAG = "ONGOING_REQUEST_TAG";
    int SPLASH_TIME_OUT = 2000;

    Handler handler;
    Runnable runnable;
    String user_id, lang, idNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseInstanceId.getInstance().getToken();

        initViews();
    }

    private void initViews() {
        handler = new Handler();
        String langCode = Locale.getDefault().getLanguage();
        if (langCode.isEmpty())
            langCode = Resources.getSystem().getConfiguration().locale.getLanguage();

        if (RPPreferences.readString(mContext, Constants.LANGUAGE_KEY).isEmpty()) {
            Timber.e("Language code-- %s", langCode);
            if (langCode.isEmpty())
                langCode = "en";
            RPPreferences.putString(mContext, Constants.LANGUAGE_KEY, langCode);
        }

        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        user_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);
        idNumber = RPPreferences.readString(mContext, Constants.ID_NUMBER_KEY);

        if (!user_id.isEmpty() && (!idNumber.isEmpty() || !idNumber.equals("0"))) {
            ModelManager.getInstance().getOngoingRequestManager().ongoingRequestTask(mContext, REQUEST_TAG,
                    Operations.ongoingRequestParams(user_id, lang));
            return;
        }

        startMain();
    }

    private void checkOrder(OngoingRequestResponse requestResponse) {
        OngoingRequestResponse.Data data = requestResponse.data;
        RequestDetailsResponse.Order order = data.order;

        switch (order.status) {
            case Constants.ORDER_ACCEPTED:
                startActivity(new Intent(mContext, OrderDetailsActivity.class)
                        .putExtra("request_id", order.id));
                break;
            case Constants.ORDER_INITIATED:
                startActivity(new Intent(mContext, OrderDetailsActivity.class)
                        .putExtra("request_id", order.id));
                break;
            case Constants.ORDER_STARTED:
                startActivity(new Intent(mContext, ServiceActivity.class)
                        .putExtra("request_id", order.id));
                break;
            case Constants.ORDER_COMPLETED:
                startActivity(new Intent(mContext, PurchaseDetailsActivity.class)
                        .putExtra("request_id", order.id));
                break;
            default:
                startActivity(new Intent(mContext, MainActivity.class));
                break;
        }

        Utils.gotoNextActivityAnimation(mContext);
        finish();
    }

    private void startMain() {
        runnable = () -> {
            if (RPPreferences.readBoolean(mContext, Constants.REGISTERED_KEY)) {
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
                return;
            }
            Intent intent = new Intent(mContext, IntroSliderActivity.class);
            startActivity(intent);
            finish();
        };
        handler.postDelayed(runnable, SPLASH_TIME_OUT);
    }

    @Subscribe(sticky = true)
    public void onEvent(OngoingRequestResponse requestResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        switch (requestResponse.getRequestTag()) {
            case REQUEST_TAG:
                checkOrder(requestResponse);
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorWithMessageEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        startMain();
        Timber.e("response-- " + event.getResultMsgUser());
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        startMain();
        showToast(getString(R.string.something_went_wrong));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }
}
