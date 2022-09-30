package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Locale;
import ua.cc.spon.exception.DaoException;

import java.util.Map;

/**
 * Class represents DAO layer for {@link Locale} class.
 *
 * @author Sergiy Ponomarenko
 */
public abstract class LocaleDAO extends AbstractDao<Locale> {

    /**
     * @return Map of pairs where key is short {@link Locale} name and value is {@link Locale} object.
     * If no values found it returns empty Map.
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract Map<String, Locale> findAllMapByName() throws DaoException;

}
