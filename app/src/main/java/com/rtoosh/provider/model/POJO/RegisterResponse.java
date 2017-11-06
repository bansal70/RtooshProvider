package com.rtoosh.provider.model.POJO;

/*
 * Created by rishav on 10/27/2017.
 */

import com.google.gson.annotations.SerializedName;
import com.rtoosh.provider.model.network.AbstractApiResponse;

public class RegisterResponse extends AbstractApiResponse{
    @SerializedName("data")
    public Data data;

    public class Data {
        @SerializedName("id")
        public String id;
    }
}
