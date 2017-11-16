package com.rtoosh.provider.controller;

/*
 * Created by rishav on 11/16/2017.
 */

import android.content.Context;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Helper;
import com.rtoosh.provider.model.POJO.WalletResponse;
import com.rtoosh.provider.model.network.APIClient;
import com.rtoosh.provider.model.network.APIService;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.model.network.ApiCallback;

import java.util.HashMap;

import retrofit2.Call;

public class WalletManager {

    public void walletTask(Context mContext, String tag, HashMap<String, String> hashParams) {

        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<WalletResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }

        Call<WalletResponse> call = apiService.walletResponse(hashParams);
        call.enqueue(callback);
    }

    public void updateWalletTask(Context mContext, String tag, HashMap<String, String> hashParams) {

        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<AbstractApiResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }

        Call<AbstractApiResponse> call = apiService.updateWalletResponse(hashParams);
        call.enqueue(callback);
    }
}
