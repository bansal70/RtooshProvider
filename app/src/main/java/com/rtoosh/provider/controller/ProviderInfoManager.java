package com.rtoosh.provider.controller;

/*
 * Created by rishav on 10/31/2017.
 */

import android.content.Context;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Helper;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.network.APIClient;
import com.rtoosh.provider.model.network.APIService;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.model.network.ApiCallback;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class ProviderInfoManager {
    public void providerInfoTask(Context mContext, String tag, String user_id, String register_id, String register_order,
                                 String register_services, String register_info, String deviceToken,
                                 String lang, String coverFile, List<String> workFiles) {

        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<AbstractApiResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }

        String job_hours = RPPreferences.readString(mContext, Constants.SCHEDULE_HOURS_KEY);

        if (coverFile.isEmpty() && workFiles.size() == 0) {
            Call<AbstractApiResponse> call = apiService.registerInfoResponse(user_id, register_id, register_order,
                    job_hours, register_services, register_info, deviceToken, Constants.DEVICE_TYPE, lang);
            call.enqueue(callback);
            return;
        }

        RequestBody requestCover;
        MultipartBody.Part bodyCover = null;
        MultipartBody.Part[] bodyGalleries = new MultipartBody.Part[0];
        File fileBg = new File(coverFile);

        if (!coverFile.isEmpty()){
            requestCover = RequestBody.create(MediaType.parse("multipart/form-data"), fileBg);
            bodyCover = MultipartBody.Part.createFormData("cover_image", coverFile, requestCover);
        }

        if (workFiles.size() != 0) {
            bodyGalleries = new MultipartBody.Part[workFiles.size()];
            for (int i = 0; i < workFiles.size(); i++) {
                File workFile = new File(workFiles.get(i));
                RequestBody requestGallery = RequestBody.create(MediaType.parse("multipart/form-data"),
                        workFile);
                bodyGalleries[i] = MultipartBody.Part.createFormData("work_image" + "[" + i + "]",
                        workFile.getName(), requestGallery);
            }
        }

        Call<AbstractApiResponse> call = apiService.registerInfoResponse(user_id, register_id, register_order, job_hours,
                register_services, register_info, deviceToken, Constants.DEVICE_TYPE, lang, bodyCover, bodyGalleries);
        call.enqueue(callback);
    }
}
