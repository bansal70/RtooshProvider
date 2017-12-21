package com.rtoosh.provider.model.network;


import android.support.annotation.NonNull;

import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.model.event.RequestFinishedEvent;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Use this class to have a callback which can be used for the api calls in {@link APIService}.
 * Such a callback can be invalidated to not notify its caller about the api response.
 * Furthermore it handles finishing the request after the caller has handled the response.
 */
public class ApiCallback<T extends AbstractApiResponse> implements Callback<T> {

    /**
     * Indicates if the callback was invalidated.
     */
    private boolean isInvalidated;

    /**
     * The tag of the request which uses this callback.
     */
    private final String requestTag;

    /**
     * Creates an {@link ApiCallback} with the passed request tag. The tag is used to finish
     * the request after the response has been handled. See {@link #finishRequest}.
     *
     * @param requestTag The tag of the request which uses this callback.
     */
    public ApiCallback(String requestTag) {
        isInvalidated = false;
        this.requestTag = requestTag;
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (isInvalidated || call.isCanceled()) {
            finishRequest();
            return;
        }
        T result = response.body();
        if (response.isSuccessful() && result != null) {
            Timber.e(requestTag + "response-- " + result.getResponse());
            if (result.getResponse().equals("0")) {
                // Error occurred. Check for error message from api.
                String resultMsg = result.getMessage();

                EventBus.getDefault()
                        .postSticky(new ApiErrorWithMessageEvent(requestTag, resultMsg));
            } else {
//			modifyResponseBeforeDelivery(result); // Enable when needed.
                result.setRequestTag(requestTag);
                EventBus.getDefault().postSticky(result);
            }
        } else {
            // TODO: Move hardcode string
            EventBus.getDefault().postSticky(
                    new ApiErrorWithMessageEvent(requestTag, "Server error"));
        }
        finishRequest();
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        if (!call.isCanceled() && !isInvalidated) {
            EventBus.getDefault().postSticky(new ApiErrorEvent(requestTag, t));
        }

        t.printStackTrace();
        finishRequest();
    }

    /**
     * Invalidates this callback. This means the caller doesn't want to be called back anymore.
     */
    public void invalidate() {
        isInvalidated = true;
    }

    /**
     * Posts a {@link RequestFinishedEvent} on the EventBus to tell the {@link APIClient}
     * to remove the request from the list of running requests.
     */
    private void finishRequest() {
        EventBus.getDefault().postSticky(new RequestFinishedEvent(requestTag));
    }

    /**
     * This is for callbacks which extend ApiCallback and want to modify the response before it is
     * delivered to the caller. It is bit different from the interceptors as it allows to implement
     * this method and change the response.
     *
     * @param result The api response.
     */
    @SuppressWarnings("UnusedParameters")
    protected void modifyResponseBeforeDelivery(T result) {
        // Do nothing here. Only for subclasses.
    }

    /**
     * Call this method if No internet connection or other use.
     *
     * @param resultMsgUser User defined messages.
     */
    public void postUnexpectedError(String resultMsgUser) {
        EventBus.getDefault().postSticky(new ApiErrorWithMessageEvent(requestTag, resultMsgUser));
        finishRequest();
    }
}
