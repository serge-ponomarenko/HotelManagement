package ua.cc.spon.db.entity;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author Sergiy Ponomarenko
 */
@Data
public class RoomCategory implements Entity, Serializable {

    private int id;
    private String name;
    private String description;
    private Timestamp creationDate;

}
