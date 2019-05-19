package ru.paul.tinkoffnews.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PublicationDate implements Serializable {

    @SerializedName("publicationDate")

    Integer milliseconds;
}
