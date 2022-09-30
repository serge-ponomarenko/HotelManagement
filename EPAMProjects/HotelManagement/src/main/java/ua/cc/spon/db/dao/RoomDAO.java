package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Room;
import ua.cc.spon.exception.DaoException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Class represents DAO layer for {@link Room} class.
 *
 * @author Sergiy Ponomarenko
 */
public abstract class RoomDAO extends AbstractDao<Room> {
    /**
     * @param reservationId ID of {@link ua.cc.spon.db.entity.Reservation} for which
     *                      {@link Room} are searched
     * @param locale        short name of {@link ua.cc.spon.db.entity.Locale} for {@link Room} strings data
     * @return List of {@link Room} for provided reservationId or empty List if no objects are found.
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract List<Room> findByReservation(int reservationId, String locale) throws DaoException;

    /**
     * Find all {$link Room} that are available to book between provided dates.
     *
     * @param checkin  checkin date
     * @param checkout checkout date
     * @param locale   locale short name of {@link ua.cc.spon.db.entity.Locale} for {@link Room} strings data
     * @return List of {@link Room} that are available to book between provided dates or empty List
     * if it isn't available Rooms.
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract List<Room> findFreeRooms(LocalDate checkin, LocalDate checkout, String locale) throws DaoException;

    /**
     * Retrieve free {$link Room} that are available to book between provided dates by ID.
     *
     * @param roomId   room ID to retrieve
     * @param checkin  checkin date
     * @param checkout checkout date
     * @param locale   locale short name of {@link ua.cc.spon.db.entity.Locale} for {@link Room} strings data
     * @return {@link Room} object that are available to book between provided dates or null
     * if it isn't available.
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract Room getFreeRoomById(int roomId, LocalDate checkin, LocalDate checkout, String locale) throws DaoException;

    /**
     * Finds all {@link Room} that not attached to any {@link ua.cc.spon.db.entity.Reservation}.
     *
     * @param locale short name of {@link ua.cc.spon.db.entity.Locale} for {@link Room} strings data
     * @return List of {@link Room} without attached {@link ua.cc.spon.db.entity.Reservation} or empty
     * List if all Rooms are attached to Reservations.
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract List<Room> findRoomsWithoutReservation(String locale) throws DaoException;

    /**
     * Finds all {@link Room} and mapped it by {@link ua.cc.spon.db.entity.Locale}.
     *
     * @param roomId ID of {@link Room} object to search
     * @return Map of pairs where the key is short name of {@link ua.cc.spon.db.entity.Locale}
     * and the values is {@link Room} objects. Returns empty Map if no objects found.
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract Map<String, Room> findByIdGroupByLocale(int roomId) throws DaoException;

    /**
     * Add image path to {@link Room} object.
     *
     * @param roomId ID of {@link Room} object for which Image will be added
     * @param path   relative path of Image to add
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract void addImage(int roomId, String path) throws DaoException;

    /**
     * Delete image path from {@link Room} object.
     *
     * @param roomId ID of {@link Room} object for which Image will be deleted
     * @param path   path of Image to delete
     * @throws DaoException on {@link java.sql.SQLException}
     */
    public abstract void deleteImage(int roomId, String path) throws DaoException;

}
