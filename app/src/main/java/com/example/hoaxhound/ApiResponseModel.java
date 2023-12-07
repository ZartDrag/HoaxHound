package com.example.hoaxhound;

import com.google.gson.annotations.SerializedName;

public class ApiResponseModel {

    @SerializedName("response")
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String key1) {
        this.response = key1;
    }
}
