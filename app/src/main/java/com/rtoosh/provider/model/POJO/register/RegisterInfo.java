package com.rtoosh.provider.model.POJO.register;

/*
 * Created by rishav on 10/31/2017.
 */

import com.google.gson.annotations.SerializedName;

public class RegisterInfo {
    @SerializedName("surname")
    private String surname;
    @SerializedName("bio")
    private String bio;

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
