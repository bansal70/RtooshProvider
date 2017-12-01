package com.rtoosh.provider.model.POJO;

/*
 * Created by rishav on 11/28/2017.
 */

import com.google.gson.annotations.SerializedName;

public class OrderDetailsResponse {
    @SerializedName("data")
    public RequestDetailsResponse.Data data;
}
