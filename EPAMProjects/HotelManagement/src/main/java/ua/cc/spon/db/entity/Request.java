package ua.cc.spon.db.entity;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class Request {

    private long id;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private int persons;
    private int rooms;
    private List<RoomCategory> roomCategories;
    private String additionalInformation;
    private User user;
    private Reservation reservation;

}
