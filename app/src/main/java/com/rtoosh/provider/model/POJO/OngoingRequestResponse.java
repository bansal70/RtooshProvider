package com.rtoosh.provider.model.POJO;

/*
 * Created by rishav on 11/28/2017.
 */

import com.google.gson.annotations.SerializedName;
import com.rtoosh.provider.model.network.AbstractApiResponse;

public class OngoingRequestResponse extends AbstractApiResponse {

    @SerializedName("data")
    public Data data;

    public class Data {
        @SerializedName("Order")
        public RequestDetailsResponse.Order order;
    }
}
