package com.rtoosh.provider.model.POJO;

/*
 * Created by rishav on 11/21/2017.
 */

import com.google.gson.annotations.SerializedName;
import com.rtoosh.provider.model.network.AbstractApiResponse;

import java.util.List;

public class RatingsResponse extends AbstractApiResponse{
    @SerializedName("data")
    public List<Data> data = null;

    public class Data {
        @SerializedName("Review")
        public Review review;
    }

    public class Review {
        @SerializedName("rating_clean")
        public String ratingClean;
        @SerializedName("message")
        public String message;
        @SerializedName("rating_quality")
        public String ratingQuality;
        @SerializedName("rating_arrival")
        public String ratingArrival;
        @SerializedName("modified")
        public String modified;
    }
}
