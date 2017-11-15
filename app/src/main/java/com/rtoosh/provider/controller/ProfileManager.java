package com.rtoosh.provider.controller;

/*
 * Created by rishav on 11/9/2017.
 */

import android.content.Context;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Helper;
import com.rtoosh.provider.model.POJO.ProfileResponse;
import com.rtoosh.provider.model.network.APIClient;
import com.rtoosh.provider.model.network.APIService;
import com.rtoosh.provider.model.network.ApiCallback;

import retrofit2.Call;

public class ProfileManager {
    public void profileTask(Context mContext, String tag, String user_id, String lang) {
        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<ProfileResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }
        Call<ProfileResponse> call = apiService.profileResponse(user_id, lang);
        call.enqueue(callback);
    }
}
