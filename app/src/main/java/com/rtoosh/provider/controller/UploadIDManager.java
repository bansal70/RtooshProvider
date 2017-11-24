package com.rtoosh.provider.controller;

/*
 * Created by rishav on 10/31/2017.
 */

import android.content.Context;
import android.os.Environment;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Helper;
import com.rtoosh.provider.model.POJO.UploadFile;
import com.rtoosh.provider.model.network.APIClient;
import com.rtoosh.provider.model.network.APIService;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.model.network.ApiCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class UploadIDManager {
    public void uploadIDTask(Context mContext, String tag, String user_id, String lang, MultipartBody.Part filePart) {

        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<AbstractApiResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }

        /* File file = new  File(image);
        RequestBody requestImage = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part bodyImage = MultipartBody.Part.createFormData("id_image", image, requestImage);;*/


      /*  ProgressRequestBody fileBody = new ProgressRequestBody(file, this);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(), fileBody);*/

        Call<AbstractApiResponse> call = apiService.uploadIdResponse(user_id, lang, filePart);
        call.enqueue(callback);
    }

    public void updateDocTask(Context mContext, String tag, HashMap<String, String> hashParams, String image) {
        APIService apiService = APIClient.getClient().create(APIService.class);
        ApiCallback<AbstractApiResponse> callback = new ApiCallback<>(tag);

        if (!Helper.isInternetActive(mContext)) {
            callback.postUnexpectedError(mContext.getString(R.string.error_no_internet));
            return;
        }

        if (image.isEmpty()) {
            Call<AbstractApiResponse> call = apiService.updateDocResponse(hashParams);
            call.enqueue(callback);
            return;
        }

        File file = new  File(image);
        RequestBody requestImage = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part bodyImage = MultipartBody.Part.createFormData("id_image", image, requestImage);


        Call<AbstractApiResponse> call = apiService.updateDocResponse(hashParams, bodyImage);
        call.enqueue(callback);
    }

    private void uploadFile(ResponseBody body) throws IOException {

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "file.zip");
        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {

            total += count;
            int totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);

            long currentTime = System.currentTimeMillis() - startTime;

            UploadFile upload = new UploadFile();
            upload.setTotalFileSize(totalFileSize);

            if (currentTime > 1000 * timeCount) {

                upload.setCurrentFileSize((int) current);
                upload.setProgress(progress);
                timeCount++;
            }

            output.write(data, 0, count);
        }
        onUploadComplete();
        output.flush();
        output.close();
        bis.close();
    }

    private void onUploadComplete(){
        UploadFile download = new UploadFile();
        download.setProgress(100);
        EventBus.getDefault().postSticky(download);
    }
}
