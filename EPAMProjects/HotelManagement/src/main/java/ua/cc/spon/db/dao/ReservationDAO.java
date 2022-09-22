package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.DBException;

import java.sql.Connection;
import java.util.List;

public interface ReservationDAO {

    void insert(Reservation reservation);

    Reservation find(long reservationId);

    List<Reservation> findByUser(User user, String locale) throws DBException;

    void update(Reservation reservation);

    void updateStatus(Reservation reservation);
    void updateExpiredPaidStatuses();

    void updateCheckinStatuses();

    void updateCheckoutStatuses();

    void delete(Reservation reservation);

    List<Reservation> findAll(String locale) throws DBException;

    void insert(Connection con, Reservation reservation);
}