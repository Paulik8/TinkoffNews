package ru.paul.tinkoffnews.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PublicationDate implements Serializable {

    @SerializedName("milliseconds")
    private Long milliseconds;

    public Long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(Long milliseconds) {
        this.milliseconds = milliseconds;
    }
}
