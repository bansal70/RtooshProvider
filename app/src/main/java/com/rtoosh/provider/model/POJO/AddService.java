package com.rtoosh.provider.model.POJO;

/*
 * Created by rishav on 10/24/2017.
 */

public class AddService {
    private String name, description, price, duration;

    public AddService(String name, String description, String price, String duration) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getDuration() {
        return duration;
    }
}
