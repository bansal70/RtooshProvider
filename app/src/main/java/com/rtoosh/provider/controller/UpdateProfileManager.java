package com.rtoosh.provider.controller;

/*
 * Created by rishav on 11/10/2017.
 */

import android.content.Context;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Helper;
import com.rtoosh.provider.model.POJO.Portfolio;
import com.rtoosh.provider.model.event.RequestFinishedEvent;
import com.rtoosh.provider.model.network.APIClient;
import com.rtoosh.provider.model.network.APIService;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.model.network.ApiCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class UpdateProfileManager {

    public void updateProfileTask(Context mContext, String tag, String user_id, String surname, String bio,
                                  String coverFile, String profilePic, String lang) {

        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<AbstractApiResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }

        if (profilePic.isEmpty() && coverFile.isEmpty()) {
            Call<AbstractApiResponse> call = apiService.updateProfileResponse(user_id, surname, bio, lang);
            call.enqueue(callback);
            return;
        }

        RequestBody requestCover, requestProfile;
        MultipartBody.Part bodyCover = null, bodyProfile = null;

        File fileBg = new File(coverFile);
        File fileDp = new File(profilePic);

        if (!coverFile.isEmpty()){
            requestCover = RequestBody.create(MediaType.parse("multipart/form-data"), fileBg);
            bodyCover = MultipartBody.Part.createFormData("cover_image", coverFile, requestCover);
        }

        if (!profilePic.isEmpty()){
            requestProfile = RequestBody.create(MediaType.parse("multipart/form-data"), fileDp);
            bodyProfile = MultipartBody.Part.createFormData("profile_pic", profilePic, requestProfile);
        }


        Call<AbstractApiResponse> call = apiService.updateProfileResponse(user_id, surname, bio, lang,
                bodyCover, bodyProfile);
        call.enqueue(callback);
    }

    public void updatePortfolio(Context mContext, String tag, String user_id, String lang,
                                ArrayList<Portfolio> listPortfolio, List<String> listRemoved) {
        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<AbstractApiResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }

        //new images added
        List<String> workFiles = new ArrayList<>();
        for (int i=0; i<listPortfolio.size(); i++) {
            if (listPortfolio.get(i).isNew()) {
                workFiles.add(listPortfolio.get(i).getImage());
            }
        }

        //old images removed
        HashMap<String, String> hashMap = new HashMap<>();
        MultipartBody.Part[] bodyGalleries = new MultipartBody.Part[0];
        for (int i=0; i<listRemoved.size(); i++) {
            hashMap.put("remove_images"+"["+i+"]", listRemoved.get(i));
        }

        if (workFiles.size() == 0 && listRemoved.size() == 0) {
            EventBus.getDefault().postSticky(new RequestFinishedEvent(tag));
            return;
        }


        if (workFiles.size() == 0) {
            Call<AbstractApiResponse> call = apiService.updateProfileResponse(user_id, lang, hashMap);
            call.enqueue(callback);
            return;
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

        Call<AbstractApiResponse> call = apiService.updateProfileResponse(user_id, lang,
                hashMap, bodyGalleries);
        call.enqueue(callback);
    }
}
