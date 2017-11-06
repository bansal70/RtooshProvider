package com.rtoosh.provider.model.POJO.register;

/*
 * Created by rishav on 10/30/2017.
 */

import com.google.gson.annotations.SerializedName;

public class RegisterOrder {
    @SerializedName("max_persons")
    private String maxPersons;
    @SerializedName("max_services")
    private String maxServices;
    @SerializedName("min_order")
    private String minOrder;

    public String getMaxPersons() {
        return maxPersons;
    }

    public void setMaxPersons(String maxPersons) {
        this.maxPersons = maxPersons;
    }

    public String getMaxServices() {
        return maxServices;
    }

    public void setMaxServices(String maxServices) {
        this.maxServices = maxServices;
    }

    public String getMinOrder() {
        return minOrder;
    }

    public void setMinOrder(String minOrder) {
        this.minOrder = minOrder;
    }
}
