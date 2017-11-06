package com.rtoosh.provider.controller;

/*
 * Created by rishav on 10/30/2017.
 */

import android.content.Context;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Helper;
import com.rtoosh.provider.model.POJO.register.RegisterServiceResponse;
import com.rtoosh.provider.model.network.APIClient;
import com.rtoosh.provider.model.network.APIService;
import com.rtoosh.provider.model.network.ApiCallback;

import retrofit2.Call;

public class ServicesListManager {
    public void serviceTask(Context mContext, String tag, String lang) {
        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<RegisterServiceResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }
        Call<RegisterServiceResponse> call = apiService.registerServiceResponse(lang);
        call.enqueue(callback);
    }
}
