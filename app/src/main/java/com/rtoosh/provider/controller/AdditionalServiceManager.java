package com.rtoosh.provider.controller;

import android.content.Context;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Helper;
import com.rtoosh.provider.model.network.APIClient;
import com.rtoosh.provider.model.network.APIService;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.model.network.ApiCallback;

import java.util.HashMap;

import retrofit2.Call;

/***
 * Created by rishav on 11/15/2017.
 **/

public class AdditionalServiceManager {
    public void additionalServiceTask(Context mContext, String tag, HashMap<String, String> hashParams) {

        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<AbstractApiResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }

        Call<AbstractApiResponse> call = apiService.additionalServiceResponse(hashParams);
        call.enqueue(callback);
    }
}
