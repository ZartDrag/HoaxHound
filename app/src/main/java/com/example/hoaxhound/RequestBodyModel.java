package com.example.hoaxhound;

import com.google.gson.annotations.SerializedName;

public class RequestBodyModel {

    @SerializedName("input_text")
    private String input_text;

    public RequestBodyModel(String key1) {
        this.input_text = key1;
    }
}
