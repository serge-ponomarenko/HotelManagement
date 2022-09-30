package ua.cc.spon.db.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergiy Ponomarenko
 */
@Data
@ToString(of = {"number"})
public class Room implements Entity, Serializable {

    private int id;
    private String number;
    private int occupancy;
    private BigDecimal price;
    private String name;
    private String description;
    private Timestamp creationDate;

    private RoomCategory roomCategory;

    private List<String> images = new ArrayList<>();

}
