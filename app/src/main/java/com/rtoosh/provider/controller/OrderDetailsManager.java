package com.rtoosh.provider.controller;

/*
 * Created by rishav on 11/28/2017.
 */

import android.content.Context;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Helper;
import com.rtoosh.provider.model.POJO.RequestDetailsResponse;
import com.rtoosh.provider.model.network.APIClient;
import com.rtoosh.provider.model.network.APIService;
import com.rtoosh.provider.model.network.ApiCallback;

import java.util.HashMap;

import retrofit2.Call;

public class OrderDetailsManager {

    public void requestDetailsTask(Context mContext, String tag, HashMap<String, String> mapParams) {

        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<RequestDetailsResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }

        Call<RequestDetailsResponse> call = apiService.requestDetailsResponse(mapParams);
        call.enqueue(callback);
    }
}
