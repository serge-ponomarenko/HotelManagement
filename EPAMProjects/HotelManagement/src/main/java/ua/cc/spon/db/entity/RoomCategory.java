package ua.cc.spon.db.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class RoomCategory {

    private long id;
    private String name;
    private String description;
    private Timestamp creationDate;

}
