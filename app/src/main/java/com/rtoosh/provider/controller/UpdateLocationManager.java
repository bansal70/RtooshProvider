package com.rtoosh.provider.controller;

/*
 * Created by rishav on 11/16/2017.
 */

import com.rtoosh.provider.model.network.APIClient;
import com.rtoosh.provider.model.network.APIService;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.model.network.ApiCallback;

import java.util.HashMap;

import retrofit2.Call;

public class UpdateLocationManager {

    public void updateLocationTask(String tag, HashMap<String, String> hashParams) {

        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<AbstractApiResponse> callback = new ApiCallback<>(tag);

        Call<AbstractApiResponse> call = apiService.updateLocationResponse(hashParams);
        call.enqueue(callback);
    }
}
