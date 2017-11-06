package com.rtoosh.provider.controller;

/*
 * Created by rishav on 10/31/2017.
 */

import android.content.Context;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Helper;
import com.rtoosh.provider.model.network.APIClient;
import com.rtoosh.provider.model.network.APIService;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.model.network.ApiCallback;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class UploadIDManager {
    public void uploadIDTask(Context mContext, String tag, String user_id, String lang, String image) {

        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<AbstractApiResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }

        File file = new File(image);
        RequestBody requestImage = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part bodyImage = MultipartBody.Part.createFormData("id_image", image, requestImage);;

        Call<AbstractApiResponse> call = apiService.uploadIdResponse(user_id, lang, bodyImage);
        call.enqueue(callback);
    }
}
