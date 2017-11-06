package com.rtoosh.provider.model.POJO.register;

/*
 * Created by rishav on 10/30/2017.
 */

import com.google.gson.annotations.SerializedName;
import com.rtoosh.provider.model.POJO.AddService;
import com.rtoosh.provider.model.network.AbstractApiResponse;

import java.util.List;

public class RegisterServiceResponse extends AbstractApiResponse {
    @SerializedName("data")
    public List<Data> data = null;

    public class Data {
        @SerializedName("Category")
        public Category category;
        private List<AddService> listAddServices;

        public Data(Category category, List<AddService> listAddServices) {
            this.category = category;
            this.listAddServices = listAddServices;
        }

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public List<AddService> getListAddServices() {
            return listAddServices;
        }

        public void setListAddServices(List<AddService> listAddServices) {
            this.listAddServices = listAddServices;
        }
    }

    public class Category {
        @SerializedName("id")
        public String id;
        @SerializedName("cat_name")
        public String catName;
        @SerializedName("image")
        public String image;
    }
}
