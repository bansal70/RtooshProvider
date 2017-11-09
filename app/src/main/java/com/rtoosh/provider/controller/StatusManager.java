package com.rtoosh.provider.controller;

/*
 * Created by rishav on 11/7/2017.
 */

import android.content.Context;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Helper;
import com.rtoosh.provider.model.network.APIClient;
import com.rtoosh.provider.model.network.APIService;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.model.network.ApiCallback;

import retrofit2.Call;

public class StatusManager {
    public void statusTask(Context mContext, String tag, String user_id, boolean status, String lang) {

        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<AbstractApiResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }

        String userStatus = "0";
        if (status)
            userStatus = "1";

        Call<AbstractApiResponse> call = apiService.statusResponse(user_id, userStatus, lang);
        call.enqueue(callback);
    }
}
