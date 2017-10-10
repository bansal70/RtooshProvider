package com.rtoosh.provider.model.POJO;

/*
 * Created by rishav on 10/10/2017.
 */

public class Order {
    private String service;
    private int quantity, price;

    public Order(String service, int quantity, int price) {
        this.service = service;
        this.quantity = quantity;
        this.price = price;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
