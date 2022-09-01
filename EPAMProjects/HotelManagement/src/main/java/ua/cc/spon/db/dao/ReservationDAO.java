package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.User;

import java.util.List;

public interface ReservationDAO {

    void insert(Reservation reservation);

    Reservation find(long reservationId);

    List<Reservation> findByUser(User user, String locale);

    void update(Reservation reservation);

    void updateStatus(Reservation reservation);
    void updateExpiredPaidStatuses();

    void updateCheckinStatuses();

    void updateCheckoutStatuses();

    void delete(Reservation reservation);

    List<Reservation> findAll();

}