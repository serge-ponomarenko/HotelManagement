package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Request;
import ua.cc.spon.exception.DaoException;

import java.util.List;

/**
 * Class represents DAO layer for {@link Request} class.
 *
 * @author Sergiy Ponomarenko
 */
public abstract class RequestDAO extends AbstractDao<Request> {

    /**
     * Method returns List of pending request from Database.
     *
     * @param locale short name of {@link ua.cc.spon.db.entity.Locale} for {@link Request} strings data
     * @return List of {@link Request}. If no values found it returns empty List.
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract List<Request> findAllPending(String locale) throws DaoException;

    /**
     * Method updates in Database only reservation_id field in {@link Request} db
     * table for provided {@link Request} object.
     *
     * @param request A {@link Request} object where need be updated {@link ua.cc.spon.db.entity.Reservation} ID.
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract void updateReservation(Request request) throws DaoException;

}