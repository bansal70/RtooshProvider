package com.rtoosh.provider.controller;

/*
 * Created by rishav on 10/27/2017.
 */

import android.content.Context;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Helper;
import com.rtoosh.provider.model.POJO.OtpResponse;
import com.rtoosh.provider.model.network.APIClient;
import com.rtoosh.provider.model.network.APIService;
import com.rtoosh.provider.model.network.ApiCallback;

import retrofit2.Call;

public class OtpManager {
    public void otpTask(Context mContext, String tag, String phone, String otp, String deviceToken, String lang) {
        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<OtpResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }
        Call<OtpResponse> call = apiService.otpResponse(phone, otp, deviceToken, Constants.DEVICE_TYPE, lang, Constants.USER_TYPE);
        call.enqueue(callback);
    }
}
