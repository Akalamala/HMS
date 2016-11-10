package ru.innopolis.dao.entity.addition;

import javax.persistence.Column;

/**
 * Создано: Денис
 * Дата:  10.11.2016
 * Описание: Расширенная информация о комнате
 * Внимание! Наследоваться от класса {@link ru.innopolis.dao.entity.Room} нельзя из-за особенностей сборки сущностей в {@link ru.innopolis.dao.processor.SQLProcessor}
 */
public class ExtendedRoom {
    @Column(name = "ID")
    private Long id;
    @Column(name = "ROOM_NUMBER")
    private Long roomNumber;
    @Column(name = "HOTEL_ID")
    private Long hotelId;
    @Column(name = "ROOM_TYPE_ID")
    private Long type;
    @Column(name = "COST")
    private Double cost;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Long roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
