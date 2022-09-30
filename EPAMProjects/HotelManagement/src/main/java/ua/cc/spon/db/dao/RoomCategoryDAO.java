package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.exception.DaoException;

import java.util.List;
import java.util.Map;

/**
 * Class represents DAO layer for {@link RoomCategory} class.
 *
 * @author Sergiy Ponomarenko
 */
public abstract class RoomCategoryDAO extends AbstractDao<RoomCategory> {

    /**
     * @param requestId ID of {@link ua.cc.spon.db.entity.Request} for which
     *                  {@link RoomCategory} are searched
     * @param locale    short name of Locale for {@link RoomCategory} strings data
     * @return List of {@link RoomCategory} for provided requestId.
     * If no values found it returns empty List.
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract List<RoomCategory> findAllForRequest(int requestId, String locale) throws DaoException;

    /**
     * @param categoryId ID of @RoomCategory to search
     * @return Map of pairs where key is short {@link ua.cc.spon.db.entity.Locale} name and value is
     * {@link RoomCategory} object. If no values found it returns empty Map.
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract Map<String, RoomCategory> findByIdGroupByLocale(int categoryId) throws DaoException;

    /**
     * Create and inser into DB empty RoomCategory for all locales. Sets ID for created in DB object.
     *
     * @param roomCategory Object that will be updated ID after insertion in DB.
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract void create(RoomCategory roomCategory) throws DaoException;

}
