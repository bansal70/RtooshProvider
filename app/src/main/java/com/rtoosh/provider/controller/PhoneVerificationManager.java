package com.rtoosh.provider.controller;

/*
 * Created by rishav on 10/26/2017.
 */

import android.content.Context;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Helper;
import com.rtoosh.provider.model.POJO.LoginResponse;
import com.rtoosh.provider.model.network.APIClient;
import com.rtoosh.provider.model.network.APIService;
import com.rtoosh.provider.model.network.ApiCallback;

import retrofit2.Call;

public class PhoneVerificationManager {

    public void execute(Context mContext, String tag, String code, String phone, String deviceToken, String lang) {
        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<LoginResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }
        Call<LoginResponse> call = apiService.phoneResponse(code, phone,
                deviceToken, Constants.DEVICE_TYPE, lang);
        call.enqueue(callback);
    }
}
