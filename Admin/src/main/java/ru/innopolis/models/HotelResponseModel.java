package ru.innopolis.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Создано: Денис
 * Дата:  30.10.2016
 * Описание: Модель отеля для ответа
 */
public class HotelResponseModel {
    private long id;
    @JsonProperty("title")
    private String name;
    private String description;
    private String phoneNumber;
    private String address;
    private String mail;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
