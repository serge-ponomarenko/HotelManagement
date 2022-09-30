package ua.cc.spon.db.dao.postgres;

import ua.cc.spon.db.dao.EntityTransaction;
import ua.cc.spon.db.dao.LocaleDAO;
import ua.cc.spon.db.dao.RoomCategoryDAO;
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.db.entity.Locale;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.exception.DaoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

public class PostgresRoomDAO extends RoomDAO {

    private static final String FIND_FREE_ROOMS =
            "SELECT r.room_id, number, occupancy, r.category_id, r_tr.name, r_tr.description, price, r.created_at " +
                    "FROM rooms r " +
                    "INNER JOIN categories c ON r.category_id = c.category_id " +
                    "INNER JOIN rooms_tr r_tr ON r.room_id = r_tr.room_id " +
                    "INNER JOIN locales l ON r_tr.locale_id = l.locale_id " +
                    "WHERE l.name = ? AND r.room_id NOT IN " +
                    "(SELECT rs.room_id FROM rooms rs " +
                    "JOIN reservations_rooms rr on rs.room_id = rr.room_id " +
                    "JOIN reservations r on rr.reservation_id = r.reservation_id " +
                    "WHERE (status_id NOT IN (1, 6, 7)) AND NOT (?::date >= checkout_date OR ?::date <= checkin_date))";

    private static final String FIND_ROOMS_WITHOUT_RESERVATION =
            "SELECT r.room_id, number, occupancy, r.category_id, r_tr.name, r_tr.description, price, r.created_at " +
                    "FROM rooms r " +
                    "INNER JOIN categories c ON r.category_id = c.category_id " +
                    "INNER JOIN rooms_tr r_tr ON r.room_id = r_tr.room_id " +
                    "INNER JOIN locales l ON r_tr.locale_id = l.locale_id " +
                    "WHERE l.name = ? AND r.room_id NOT IN " +
                    "(SELECT rs.room_id FROM rooms rs " +
                    "JOIN reservations_rooms rr on rs.room_id = rr.room_id " +
                    "JOIN reservations r on rr.reservation_id = r.reservation_id " +
                    "WHERE (status_id != 1))";

    private static final String FIND_ALL_ROOMS =
            "SELECT r.room_id, number, occupancy, r.category_id, r_tr.name, r_tr.description, price, r.created_at " +
                    "FROM rooms r " +
                    "INNER JOIN categories c ON r.category_id = c.category_id " +
                    "INNER JOIN rooms_tr r_tr ON r.room_id = r_tr.room_id " +
                    "INNER JOIN locales l ON r_tr.locale_id = l.locale_id " +
                    "WHERE l.name = ?";

    private static final String FIND_ALL_ROOM_IMAGES =
            "SELECT room_images.path FROM room_images WHERE room_id = ?";

    private static final String FIND_ROOMS_BY_RESERVATION =
            "SELECT r.room_id, number, occupancy, r.category_id, r_tr.name, r_tr.description, price, r.created_at " +
                    "FROM rooms r " +
                    "INNER JOIN reservations_rooms rr on r.room_id = rr.room_id " +
                    "INNER JOIN categories c ON r.category_id = c.category_id " +
                    "INNER JOIN rooms_tr r_tr ON r.room_id = r_tr.room_id " +
                    "INNER JOIN locales l ON r_tr.locale_id = l.locale_id " +
                    "WHERE l.name = ? AND reservation_id = ?";

    private static final String ADD_IMAGE =
            "INSERT INTO room_images(room_id, path) VALUES (?, ?)";

    private static final String DELETE_IMAGE =
            "DELETE FROM room_images WHERE room_id = ? AND path = ?";

    private static final String DELETE_BY_ID =
            "DELETE FROM rooms WHERE room_id = ?";

    private static final String INSERT_ROOM =
            "INSERT INTO rooms(number, occupancy, category_id, price) " +
                    "VALUES ('', 0, (SELECT categories.category_id FROM categories LIMIT 1), 0)";

    private static final String INSERT_ROOM_EMPTY_TR =
            "INSERT INTO rooms_tr(room_id, locale_id, name, description) " +
                    "(SELECT ?, l.locale_id, ?, ? FROM locales l)";

    private static final String UPDATE_ROOM = "UPDATE rooms SET " +
            "number = ?, occupancy = ?, category_id = ?, price = ? " +
            "WHERE room_id = ?";

    private static final String UPDATE_ROOM_TR =
            "UPDATE rooms_tr SET " +
                    "name = ?, description = ? " +
                    "WHERE room_id = ? AND rooms_tr.locale_id = (SELECT locales.locale_id FROM locales WHERE locales.name = ?)";

    private static final String FIND_BY_ID =
            "SELECT r.room_id, number, occupancy, r.category_id, r_tr.name, r_tr.description, price, r.created_at " +
                    "FROM rooms r " +
                    "INNER JOIN categories c ON r.category_id = c.category_id " +
                    "INNER JOIN rooms_tr r_tr ON r.room_id = r_tr.room_id " +
                    "INNER JOIN locales l ON r_tr.locale_id = l.locale_id " +
                    "WHERE l.name = ? AND r.room_id = ?";

    private static final String GET_FREE_ROOM_BY_ID =
            "SELECT r.room_id, number, occupancy, r.category_id, r_tr.name, r_tr.description, price, r.created_at " +
                    "FROM rooms r " +
                    "INNER JOIN categories c ON r.category_id = c.category_id " +
                    "INNER JOIN rooms_tr r_tr ON r.room_id = r_tr.room_id " +
                    "INNER JOIN locales l ON r_tr.locale_id = l.locale_id " +
                    "WHERE l.name = ? AND r.room_id NOT IN " +
                    "(SELECT rs.room_id FROM rooms rs " +
                    "JOIN reservations_rooms rr on rs.room_id = rr.room_id " +
                    "JOIN reservations r on rr.reservation_id = r.reservation_id " +
                    "WHERE (status_id NOT IN (1, 6, 7)) AND NOT (?::date >= checkout_date OR ?::date <= checkin_date)) " +
                    "AND r.room_id = ?";

    @Override
    public void update(Room room, String locale) throws DaoException {
        try (PreparedStatement statement1 = connection.prepareStatement(UPDATE_ROOM);
             PreparedStatement statement2 = connection.prepareStatement(UPDATE_ROOM_TR)) {
            int k = 0;
            statement1.setString(++k, room.getNumber());
            statement1.setInt(++k, room.getOccupancy());
            statement1.setInt(++k, room.getRoomCategory().getId());
            statement1.setBigDecimal(++k, room.getPrice());
            statement1.setInt(++k, room.getId());
            k = 0;
            statement2.setString(++k, room.getName());
            statement2.setString(++k, room.getDescription());
            statement2.setInt(++k, room.getId());
            statement2.setString(++k, locale);

            int affectedRows = statement1.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating room failed, no rows affected.");
            }

            affectedRows = statement2.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating room failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

    }

    @Override
    public void delete(int roomId) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID)) {

            statement.setInt(1, roomId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting room failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public void deleteImage(int roomId, String path) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_IMAGE)) {

            statement.setInt(1, roomId);
            statement.setString(2, path);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting image failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public void addImage(int roomId, String path) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(ADD_IMAGE)) {

            statement.setInt(1, roomId);
            statement.setString(2, path);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Adding image failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public Map<String, Room> findByIdGroupByLocale(int roomId) throws DaoException {

        Map<String, Room> result = new HashMap<>();

        EntityTransaction transaction = new EntityTransaction(connection);
        LocaleDAO localeDAO = new PostgresLocaleDAO();

        transaction.init(localeDAO);
        Collection<Locale> locales = localeDAO.findAllMapByName().values();

        for (Locale locale : locales) {
            Room room = Optional.ofNullable(find(roomId, locale.getName()))
                    .orElseThrow(DaoException::new);

            result.put(locale.getName(), room);
        }

        return result;
    }

    @Override
    public List<Room> findByReservation(int reservationId, String locale) throws DaoException {
        List<Room> result;

        try (PreparedStatement statement = connection.prepareStatement(FIND_ROOMS_BY_RESERVATION)) {

            statement.setString(1, locale);
            statement.setInt(2, reservationId);

            try (ResultSet resultSet = statement.executeQuery()) {
                result = fillRooms(resultSet, locale);
            }

        } catch (SQLException | DaoException e) {
            throw new DaoException(e.getMessage());
        }

        return result;
    }

    @Override
    public List<Room> findRoomsWithoutReservation(String locale) throws DaoException {
        List<Room> result;

        try (PreparedStatement statement = connection.prepareStatement(FIND_ROOMS_WITHOUT_RESERVATION)) {

            statement.setString(1, locale);

            try (ResultSet resultSet = statement.executeQuery()) {
                result = fillRooms(resultSet, locale);
            }

        } catch (SQLException | DaoException e) {
            throw new DaoException(e.getMessage());
        }

        return result;
    }

    @Override
    public Room getFreeRoomById(int roomId, LocalDate checkin, LocalDate checkout, String locale) throws DaoException {
        List<Room> result;

        try (PreparedStatement statement = connection.prepareStatement(GET_FREE_ROOM_BY_ID)) {
            int k = 0;
            statement.setString(++k, locale);
            statement.setString(++k, checkin.toString());
            statement.setString(++k, checkout.toString());
            statement.setInt(++k, roomId);

            try (ResultSet resultSet = statement.executeQuery()) {
                result = fillRooms(resultSet, locale);
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Room> findFreeRooms(LocalDate checkin, LocalDate checkout, String locale) throws DaoException {
        List<Room> result;

        try (PreparedStatement statement = connection.prepareStatement(FIND_FREE_ROOMS)) {
            int k = 0;
            statement.setString(++k, locale);
            statement.setString(++k, checkin.toString());
            statement.setString(++k, checkout.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                result = fillRooms(resultSet, locale);
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

        return result;
    }

    @Override
    public List<Room> findAll(String locale) throws DaoException {
        List<Room> result;

        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_ROOMS)) {

            statement.setString(1, locale);

            try (ResultSet resultSet = statement.executeQuery()) {
                result = fillRooms(resultSet, locale);
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

        return result;
    }

    private List<String> getImages(int roomId) throws DaoException {
        List<String> result = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_ROOM_IMAGES)) {

            statement.setInt(1, roomId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(resultSet.getString("path"));
                }
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

        return result;

    }

    private List<Room> fillRooms(ResultSet resultSet, String locale) throws SQLException, DaoException {
        List<Room> result = new ArrayList<>();
        EntityTransaction transaction = new EntityTransaction(connection);
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();

        List<RoomCategory> roomCategories;
        transaction.init(roomCategoryDAO);

        try {
            roomCategories = roomCategoryDAO.findAll(locale);
        } catch (DaoException e) {
            throw new DaoException(e.getMessage());
        }

        while (resultSet.next()) {
            Room room = new Room();

            room.setId(resultSet.getInt("room_id"));
            room.setNumber(resultSet.getString("number"));
            room.setOccupancy(resultSet.getInt("occupancy"));

            int roomCategoryId = resultSet.getInt("category_id");
            RoomCategory roomCategory = roomCategories.stream()
                    .filter(rc -> rc.getId() == roomCategoryId)
                    .findFirst()
                    .orElseThrow();
            room.setRoomCategory(roomCategory);
            room.setName(resultSet.getString("name"));
            room.setDescription(resultSet.getString("description"));
            room.setPrice(resultSet.getBigDecimal("price"));
            room.setCreationDate(resultSet.getTimestamp("created_at"));
            room.setImages(getImages(room.getId()));

            result.add(room);

        }

        return result;
    }

    @Override
    public void insert(Room room) throws DaoException {
        try (PreparedStatement statement1 = connection.prepareStatement(INSERT_ROOM, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement statement2 = connection.prepareStatement(INSERT_ROOM_EMPTY_TR)) {

            int affectedRows = statement1.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating room failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement1.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    room.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating room failed, no ID obtained.");
                }
            }
            int k = 0;
            statement2.setInt(++k, room.getId());
            statement2.setString(++k, room.getName());
            statement2.setString(++k, room.getDescription());

            affectedRows = statement2.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating room failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }


    @Override
    public Room find(int id, String locale) throws DaoException {
        List<Room> result;

        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {

            statement.setString(1, locale);
            statement.setInt(2, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                result = fillRooms(resultSet, locale);
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Room> findAll() throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Room find(int id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Room room) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Room room) throws DaoException {
        throw new UnsupportedOperationException();
    }
}
