package com.rtoosh.provider.model.network;


import android.content.Context;

import com.rtoosh.provider.model.Helper;

/**
 * Base class for the API requests. Provides functionality for cancelling ApiService requests.
 */
public abstract class AbstractApiRequest {
    /**
     * The endpoint for executing the calls.
     */
    protected final APIService apiService;

    /**
     * Identifies the request.
     */
    protected final String tag;

    protected Context context;

    /**
     * Initialize the request with the passed values.
     *
     * @param apiService The {@link APIService} used for executing the calls.
     * @param tag        Identifies the request.
     */
    protected AbstractApiRequest(APIService apiService, String tag) {
        this.apiService = apiService;
        this.tag = tag;
    }

    /**
     * Cancels the running request. The response will still be delivered but will be ignored. The
     * implementation should call invalidate on the callback which is used for the request.
     */
    public abstract void cancel();

    /**
     * Check for active internet connection
     *
     * @return boolean
     */
    public boolean isInternetActive() {
        return Helper.isInternetActive(context);
    }

    public abstract void execute();

}
