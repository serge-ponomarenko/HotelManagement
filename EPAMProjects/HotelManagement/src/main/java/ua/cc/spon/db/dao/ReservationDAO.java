package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.DaoException;

import java.util.List;

/**
 * Class represents DAO layer for {@link Reservation} class.
 *
 * @author Sergiy Ponomarenko
 */
public abstract class ReservationDAO extends AbstractDao<Reservation> {
    /**
     * @param user   {@link User} object for which {@link Reservation} are searched
     * @param locale short name of {@link ua.cc.spon.db.entity.Locale} for
     *               {@link Reservation} strings data
     * @return List of {@link Reservation} for provided {@link User} or empty List
     * if {@link Reservation} is not found.
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract List<Reservation> findByUser(User user, String locale) throws DaoException;

    /**
     * Method updates in Database only status_id field in {@link Reservation} db table
     * for provided {@link Reservation} object.
     *
     * @param reservation A Reservation object where need be updated Reservation Status.
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract void updateStatus(Reservation reservation) throws DaoException;

    /**
     * Method updates status for all {@link Reservation} objects where time from reservation creation
     * to current time is greater than two days. Or if interval between current time and 9 a.m.
     * checkin date is less or equal 0. Other words, user have time to pay for his reservation
     * during two days after make reservation or time till 9 a.m. of checkin date
     * (but not greater than 2 days).
     *
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract void updateExpiredPaidStatuses() throws DaoException;

    /**
     * Method calls at 12 p.m. and change status to BUSY for all reservations that are paid and
     * checkin date is greater or equal current date.
     *
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract void updateCheckinStatuses() throws DaoException;

    /**
     * Method calls at 12 p.m. and change status to COMPLETED for all reservations that are Busy
     * status and checkout date is greater or equal current date.
     *
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract void updateCheckoutStatuses() throws DaoException;
}