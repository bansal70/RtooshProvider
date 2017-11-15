package com.rtoosh.provider.model.event;

/*
 * Created by win 10 on 11/14/2017.
 */

public class ApiNotificationEvent {
    private final String requestTag;
    private final String requestID;

    public ApiNotificationEvent(String requestTag, String requestID) {
        this.requestTag = requestTag;
        this.requestID = requestID;
    }

    public String getRequestTag() {
        return requestTag;
    }

    public String getRequestID() {
        return requestID;
    }
}
