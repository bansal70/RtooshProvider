package com.rtoosh.provider.model.POJO;

import com.google.gson.annotations.SerializedName;
import com.rtoosh.provider.model.network.AbstractApiResponse;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.List;

/*
 * Created by rishav on 11/14/2017.
 */

public class RequestDetailsResponse extends AbstractApiResponse{

    @SerializedName("data")
    public Data data;

    public class Data implements Serializable{
        @SerializedName("client")
        public Client client;
        @SerializedName("Provider")
        public Provider provider;
        @SerializedName("OrderItem")
        public List<OrderItem> orderItem = null;
        @SerializedName("Order")
        public OrderDetails order;
    }

    public class OrderDetails {
        @SerializedName("id")
        public String id;
        @SerializedName("lat")
        public String lat;
        @SerializedName("lng")
        public String lng;
        @SerializedName("discount")
        public String discount;
    }

    public class Provider implements Serializable{
        @SerializedName("lat")
        public String lat;
        @SerializedName("lng")
        public String lng;
    }

    public class Client implements Serializable{
        @SerializedName("id")
        public String id;
        @SerializedName("email")
        public String email;
        @SerializedName("phone")
        public String phone;
        @SerializedName("full_name")
        public String fullName;
        @SerializedName("lat")
        public String lat;
        @SerializedName("lng")
        public String lng;
    }

    @Parcel
    public static class OrderItem implements Serializable{
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
        @SerializedName("Order")
        public Order order;
        @SerializedName("Service")
        public Service service;
    }

    @Parcel
    public static class Service implements Serializable{
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
    }

    @Parcel
    public static class Order implements Serializable{
        @SerializedName("id")
        public String id;
        @SerializedName("lat")
        public String lat;
        @SerializedName("lng")
        public String lng;
        @SerializedName("payment_mode")
        public String paymentMode;
        @SerializedName("provider_id")
        public String providerId;
        @SerializedName("location")
        public String location;
        @SerializedName("created")
        public String created;
        @SerializedName("modified")
        public String modified;
        @SerializedName("schedule_date")
        public String scheduleDate;
        @SerializedName("client_id")
        public String clientId;
        @SerializedName("request_status")
        public String requestStatus;
        @SerializedName("number_of_services")
        public String numberOfServices;
        @SerializedName("total_paid_amount")
        public String totalPaidAmount;
        @SerializedName("note")
        public String note;
        @SerializedName("cancel_reason")
        public String cancelReason;
        @SerializedName("status")
        public String status;
        @SerializedName("order_type")
        public String orderType;
        @SerializedName("discount")
        public String discount;

        public String timeRemains;
    }
}
