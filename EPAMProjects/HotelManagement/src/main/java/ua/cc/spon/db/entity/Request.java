package ua.cc.spon.db.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Class describes Request entity.
 * Request is application from User to Manager to choose the most favorite Room
 * based on request parameters.
 *
 * @author Sergiy Ponomarenko
 */
@Data
public class Request implements Entity, Serializable {

    private int id;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private int persons;
    private int rooms;
    private List<RoomCategory> roomCategories;
    private String additionalInformation;
    private User user;
    private Reservation reservation;

}
