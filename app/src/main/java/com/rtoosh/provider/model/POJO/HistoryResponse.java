package com.rtoosh.provider.model.POJO;

/*
 * Created by rishav on 11/17/2017.
 */

import com.google.gson.annotations.SerializedName;
import com.rtoosh.provider.model.network.AbstractApiResponse;

import org.parceler.Parcel;

import java.util.List;

public class HistoryResponse extends AbstractApiResponse{
    @SerializedName("server_time")
    public String serverTime;
    @SerializedName("data")
    public List<Data> data = null;

    @Parcel
    public static class Data {
        @SerializedName("Order")
        public RequestDetailsResponse.Order order;
        @SerializedName("client")
        public RequestDetailsResponse.Client client;
        @SerializedName("OrderItem")
        public List<RequestDetailsResponse.OrderItem> orderItem = null;
    }

   /* @Parcel
    public static class OrderItem {
        @SerializedName("id")
        public String id;
        @SerializedName("order_id")
        public String orderId;
        @SerializedName("service_id")
        public String serviceId;
        @SerializedName("amount")
        public String amount;
        @SerializedName("no_of_person")
        public String noOfPerson;
        @SerializedName("created")
        public String created;
        @SerializedName("modified")
        public String modified;
        @SerializedName("Service")
        public Service service = null;
    }

    @Parcel
    public static class Service {
        @SerializedName("id")
        public String id;
        @SerializedName("cat_id")
        public String catId;
        @SerializedName("user_id")
        public String userId;
        @SerializedName("service_name")
        public String serviceName;
        @SerializedName("description")
        public String description;
        @SerializedName("price")
        public String price;
        @SerializedName("duration")
        public String duration;
    }*/
}
