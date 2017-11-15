package com.rtoosh.provider.controller;

/*
 * Created by rishav on 11/10/2017.
 */

import android.content.Context;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Helper;
import com.rtoosh.provider.model.network.APIClient;
import com.rtoosh.provider.model.network.APIService;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.model.network.ApiCallback;

import retrofit2.Call;

public class UpdatePasswordManager {
    public void updatePasswordTask(Context mContext, String tag, String user_id,
                           String currentPass, String newPass, String lang) {

        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<AbstractApiResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }

        Call<AbstractApiResponse> call = apiService.updatePasswordResponse(user_id, currentPass, newPass, lang);
        call.enqueue(callback);
    }
}
