package com.rtoosh.provider.model.POJO;

/*
 * Created by rishav on 10/27/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rtoosh.provider.model.network.AbstractApiResponse;

public class OtpResponse extends AbstractApiResponse{
    @SerializedName("register")
    @Expose
    public String register;
}
