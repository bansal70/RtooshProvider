package com.rtoosh.provider.model.POJO;

/*
 * Created by rishav on 11/9/2017.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.rtoosh.provider.model.network.AbstractApiResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProfileResponse extends AbstractApiResponse implements Serializable {
    @SerializedName("data")
    public Data data;

    public class Data {
        @SerializedName("User")
        public User user;
        @SerializedName("Service")
        public List<Service> service = null;
        @SerializedName("ProviderImages")
        public ArrayList<ProviderImage> providerImages = null;
        @SerializedName("Hour")
        public List<Hour> hour = null;

    }

    public static class ProviderImage implements Parcelable {
        @SerializedName("id")
        public String id;
        @SerializedName("provider_id")
        public String providerId;
        @SerializedName("image")
        public String image;
        @SerializedName("created")
        public String created;
        @SerializedName("modified")
        public String modified;

        private ProviderImage(Parcel in) {
            id = in.readString();
            providerId = in.readString();
            image = in.readString();
            created = in.readString();
            modified = in.readString();
        }

        public static final Creator<ProviderImage> CREATOR = new Creator<ProviderImage>() {
            @Override
            public ProviderImage createFromParcel(Parcel in) {
                return new ProviderImage(in);
            }

            @Override
            public ProviderImage[] newArray(int size) {
                return new ProviderImage[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(providerId);
            dest.writeString(image);
            dest.writeString(created);
            dest.writeString(modified);
        }
    }

    public class Service {
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

    public class User {
        @SerializedName("id")
        public String id;
        @SerializedName("username")
        public String username;
        @SerializedName("password")
        public String password;
        @SerializedName("email")
        public String email;
        @SerializedName("phone")
        public String phone;
        @SerializedName("userType")
        public String userType;
        @SerializedName("status")
        public String status;
        @SerializedName("name")
        public String name;
        @SerializedName("lang")
        public String lang;
        @SerializedName("full_name")
        public String fullName;
        @SerializedName("id_number")
        public String idNumber;
        @SerializedName("id_type")
        public String idType;
        @SerializedName("issue_date")
        public String issueDate;
        @SerializedName("work_online")
        public String workOnline;
        @SerializedName("work_schedule")
        public String workSchedule;
        @SerializedName("vacation_mode")
        public String vacationMode;
        @SerializedName("max_persons")
        public String maxPersons;
        @SerializedName("max_services")
        public String maxServices;
        @SerializedName("min_order")
        public String minOrder;
        @SerializedName("surname")
        public String surname;
        @SerializedName("bio")
        public String bio;
        @SerializedName("cover_image")
        public String coverImage;
        @SerializedName("lat")
        public String lat;
        @SerializedName("lng")
        public String lng;
        @SerializedName("online")
        public String online;
        @SerializedName("balance")
        public String balance;
        @SerializedName("user_role")
        public String userRole;
        @SerializedName("id_image")
        public String idImage;
        @SerializedName("dob")
        public String dob;
        @SerializedName("country_code")
        public String countryCode;
        @SerializedName("created")
        public String created;
        @SerializedName("profile_pic")
        public String profilePic;
        @SerializedName("rating")
        public String rating;
        @SerializedName("total_review")
        public String totalReview;
    }

    public class Hour {
        @SerializedName("id")
        public String id;
        @SerializedName("day")
        public String day;
        @SerializedName("open")
        public String open;
        @SerializedName("provider_id")
        public String providerId;
        @SerializedName("close")
        public String close;
    }
}
