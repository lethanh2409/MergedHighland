package com.example.testapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PriceUpdateDetail implements Serializable {
    @SerializedName("price_new")
    private int priceNew;

    public int getPriceNew() {
        return priceNew;
    }

    public void setPriceNew(int priceNew) {
        this.priceNew = priceNew;
    }

    // Getter và setter
}

