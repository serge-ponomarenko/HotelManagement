package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.exception.DaoException;

import java.util.Map;

/**
 * Class represents DAO layer for {@link Reservation.Status} class.
 *
 * @author Sergiy Ponomarenko
 */
public abstract class StatusDAO extends AbstractDao<Reservation.Status> {
    /**
     * @param locale locale short name of {@link ua.cc.spon.db.entity.Locale} for {@link Reservation.Status} strings data
     * @return Map of pairs where the key is Enum value {@link Reservation.Status}
     * and the values is translated to Locale names. Returns empty Map if no objects found.
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract Map<Reservation.Status, String> findNames(String locale) throws DaoException;
}
