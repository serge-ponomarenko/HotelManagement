package ua.cc.spon.db.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class Reservation implements Entity, Serializable {

    private long id;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private Status status;
    private int persons;
    private BigDecimal price;
    private User user;
    private Timestamp createdAt;

    private List<Room> rooms = new ArrayList<>();

    public enum Status {
        FREE(1),
        BOOKED(2),
        PAID(3),
        BUSY(4),
        UNAVAILABLE(5),
        COMPLETED(6),
        CANCELED(7);

        private long id;

        Status(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }
    }
}
