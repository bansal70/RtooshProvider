package com.rtoosh.provider.controller;

/*
 * Created by rishav on 10/27/2017.
 */

import android.content.Context;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Helper;
import com.rtoosh.provider.model.POJO.RegisterResponse;
import com.rtoosh.provider.model.network.APIClient;
import com.rtoosh.provider.model.network.APIService;
import com.rtoosh.provider.model.network.ApiCallback;

import retrofit2.Call;

public class RegistrationManager {

    public void registrationTask(Context mContext, String tag, String name, String email, String code, String phone,
                                 String password, String user_role, String deviceToken, String lang) {
        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<RegisterResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }
        Call<RegisterResponse> call = apiService.registerResponse(name, email, code, phone, password,
                Constants.USER_TYPE, user_role, deviceToken, Constants.DEVICE_TYPE, lang);
        call.enqueue(callback);
    }
}
