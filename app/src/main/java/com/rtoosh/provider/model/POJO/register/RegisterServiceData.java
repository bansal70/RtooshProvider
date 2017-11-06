package com.rtoosh.provider.model.POJO.register;

/*
 * Created by rishav on 10/31/2017.
 */

import com.rtoosh.provider.model.POJO.AddService;

import java.util.List;

public class RegisterServiceData {
    private String id, name, image;
    private List<AddService> listAddServices;

    public RegisterServiceData(String id, String name, String image, List<AddService> listAddServices) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.listAddServices = listAddServices;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<AddService> getListAddServices() {
        return listAddServices;
    }

    public void setListAddServices(List<AddService> listAddServices) {
        this.listAddServices = listAddServices;
    }
}
