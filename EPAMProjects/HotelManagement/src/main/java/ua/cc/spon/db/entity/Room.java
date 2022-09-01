package ua.cc.spon.db.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class Room {

    private long id;
    private String number;
    private int occupancy;
    private BigDecimal price;
    private String name;
    private String description;

    private RoomCategory roomCategory;

    private List<String> images = new ArrayList<>();

}
