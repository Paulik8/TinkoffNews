package ru.paul.tinkoffnews.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ResponseAPI implements Serializable {

    @SerializedName("resultCode")
    String resultCode;

    public void setPayload(List<Post> payload) {
        this.payload = payload;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<Post> getPayload() {
        return payload;
    }

    @SerializedName("payload")
    List<Post> payload;
}
