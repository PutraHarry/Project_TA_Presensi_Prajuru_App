package com.harry.presensiprajuru.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EspResponse {
    @SerializedName("message")
    @Expose
    public String message;
    public String getName() {
        return message;
    }
    public void setName(String name) {
        this.message = message;
    }
}
