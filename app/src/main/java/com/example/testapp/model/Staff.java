package com.example.testapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Staff implements Serializable {
    @SerializedName("staff_id")
    private int id;
    private String firstName, lastName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
