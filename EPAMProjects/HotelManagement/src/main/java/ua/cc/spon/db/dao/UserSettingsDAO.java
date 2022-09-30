package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;

/**
 * Class represents DAO layer for {@link UserSettings} class.
 *
 * @author Sergiy Ponomarenko
 */
public abstract class UserSettingsDAO extends AbstractDao<UserSettings> {
    /**
     * @param userId ID of {@link ua.cc.spon.db.entity.User} for which
     *               {@link UserSettings} are searched
     * @return {@link UserSettings} object for provided userId parameter, or null if not found.
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract UserSettings findByUserId(int userId) throws DaoException;

    /**
     * @param userHash String value for recognize user over Cookies.
     * @return {@link UserSettings} object for provided userHash parameter.
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract UserSettings findByHash(String userHash) throws DaoException;
}
