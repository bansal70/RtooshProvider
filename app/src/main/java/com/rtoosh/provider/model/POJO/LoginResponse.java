package com.rtoosh.provider.model.POJO;

/*
 * Created by rishav on 10/27/2017.
 */

import com.google.gson.annotations.SerializedName;
import com.rtoosh.provider.model.network.AbstractApiResponse;

public class LoginResponse extends AbstractApiResponse{
    @SerializedName("otp")
    public Integer otp;
    @SerializedName("data")
    public Data data;

    public class Data {
        @SerializedName("id")
        public String id;
        @SerializedName("status")
        public String accountStatus;
        @SerializedName("id_number")
        public String idNumber;
        @SerializedName("email")
        public String email;
        @SerializedName("full_name")
        public String fullName;
        @SerializedName("profile_pic")
        public String profilePic;
        @SerializedName("work_online")
        public String workOnline;
        @SerializedName("work_schedule")
        public String workSchedule;
        @SerializedName("vacation_mode")
        public String vacationMode;
        @SerializedName("online")
        public String online;
    }
}
