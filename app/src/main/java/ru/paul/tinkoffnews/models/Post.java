package ru.paul.tinkoffnews.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Post  implements Serializable {

    @SerializedName("id")
    Integer id;
    @SerializedName("name")
    String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PublicationDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(PublicationDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Integer getBankInfoTypeId() {
        return bankInfoTypeId;
    }

    public void setBankInfoTypeId(Integer bankInfoTypeId) {
        this.bankInfoTypeId = bankInfoTypeId;
    }

    @SerializedName("text")
    String text;
    @SerializedName("publicationDate")
    PublicationDate publicationDate;
    @SerializedName("bankInfoTypeId")
    Integer bankInfoTypeId;
}

