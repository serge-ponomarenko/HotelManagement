package ua.cc.spon.db.entity;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class RoomCategory implements Entity, Serializable {

    private long id;
    private String name;
    private String description;
    private Timestamp creationDate;

}
