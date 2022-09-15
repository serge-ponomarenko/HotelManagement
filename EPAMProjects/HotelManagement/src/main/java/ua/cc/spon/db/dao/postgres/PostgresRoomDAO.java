package ua.cc.spon.db.dao.postgres;

import ua.cc.spon.db.DataSource;
import ua.cc.spon.db.dao.LocaleDAO;
import ua.cc.spon.db.dao.RoomCategoryDAO;
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.db.entity.Locale;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.RoomCategory;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class PostgresRoomDAO implements RoomDAO {

    private static final String FIND_FREE_ROOMS =
            "SELECT r.room_id, number, occupancy, r.category_id, r_tr.name, r_tr.description, price, r.created_at " +
                    "FROM rooms r " +
                    "INNER JOIN categories c USING(category_id) " +
                    "INNER JOIN rooms_tr r_tr ON r.room_id = r_tr.room_id " +
                    "INNER JOIN locales l USING(locale_id) " +
                    "WHERE l.name = ? AND r.room_id NOT IN " +
                    "(SELECT rs.room_id FROM rooms rs " +
                    "JOIN reservations_rooms rr on rs.room_id = rr.room_id " +
                    "JOIN reservations r on rr.reservation_id = r.reservation_id " +
                    "WHERE (status_id NOT IN (1, 6, 7)) AND NOT (?::date >= checkout_date OR ?::date <= checkin_date))";

    private static final String FIND_ALL_ROOMS =
            "SELECT r.room_id, number, occupancy, r.category_id, r_tr.name, r_tr.description, price, r.created_at " +
                    "FROM rooms r " +
                    "INNER JOIN categories c USING(category_id) " +
                    "INNER JOIN rooms_tr r_tr ON r.room_id = r_tr.room_id " +
                    "INNER JOIN locales l USING(locale_id) " +
                    "WHERE l.name = ?";

    private static final String FIND_ALL_ROOM_IMAGES =
            "SELECT room_images.path FROM room_images WHERE room_id = ?";

    private static final String FIND_ROOMS_BY_RESERVATION =
            "SELECT r.room_id, number, occupancy, r.category_id, r_tr.name, r_tr.description, price, r.created_at " +
                    "FROM rooms r " +
                    "INNER JOIN reservations_rooms rr on r.room_id = rr.room_id " +
                    "INNER JOIN categories c USING(category_id) " +
                    "INNER JOIN rooms_tr r_tr ON r.room_id = r_tr.room_id " +
                    "INNER JOIN locales l USING(locale_id) " +
                    "WHERE l.name = ? AND reservation_id = ?";

    private static final String ADD_IMAGE =
            "INSERT INTO room_images(room_id, path) VALUES (?, ?)";

    private static final String DELETE_IMAGE =
            "DELETE FROM room_images WHERE room_id = ? AND path = ?";

    private static final String DELETE_BY_ID =
            "DELETE FROM rooms WHERE room_id = ?";

    private static final String INSERT_ROOM = "INSERT INTO rooms(number, occupancy, category_id, price) " +
            "VALUES ('', 0, (SELECT categories.category_id FROM categories LIMIT 1), 0)";

    private static final String INSERT_ROOM_EMPTY_TR =
            "INSERT INTO rooms_tr(room_id, locale_id, name, description) " +
                    "(SELECT ?, l.locale_id, ?, ? FROM locales l)";

    private static final String UPDATE_ROOM = "UPDATE rooms SET " +
            "number = ?, occupancy = ?, category_id = ?, price = ? " +
            "WHERE room_id = ?";

    private static final String UPDATE_ROOM_TR = "UPDATE rooms_tr SET " +
            "name = ?, description = ? " +
            "WHERE room_id = ? AND rooms_tr.locale_id = (SELECT locales.locale_id FROM locales WHERE locales.name = ?)";

    @Override
    public void update(Room room, String locale) {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement1 = con.prepareStatement(UPDATE_ROOM);
             PreparedStatement statement2 = con.prepareStatement(UPDATE_ROOM_TR)) {

            statement1.setString(1, room.getNumber());
            statement1.setInt(2, room.getOccupancy());
            statement1.setLong(3, room.getRoomCategory().getId());
            statement1.setBigDecimal(4, room.getPrice());
            statement1.setLong(5, room.getId());

            statement2.setString(1, room.getName());
            statement2.setString(2, room.getDescription());
            statement2.setLong(3, room.getId());
            statement2.setString(4, locale);

            int affectedRows = statement1.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating room failed, no rows affected.");
            }

             affectedRows = statement2.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating room failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(); // TODO: 07.09.2022
        }

    }

    @Override
    public void create(Room room) {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement1 = con.prepareStatement(INSERT_ROOM, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement statement2 = con.prepareStatement(INSERT_ROOM_EMPTY_TR)) {

            int affectedRows = statement1.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating room failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement1.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    room.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating room failed, no ID obtained.");
                }
            }

            statement2.setLong(1, room.getId());
            statement2.setString(2, room.getName());
            statement2.setString(3, room.getDescription());

            affectedRows = statement2.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating room failed, no rows affected.");
            }

        } catch (SQLException e) {
            // TODO: 08.09.2022
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(long roomId) {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_BY_ID)) {

            statement.setLong(1, roomId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting room failed, no rows affected.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteImage(long roomId, String path) {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_IMAGE)) {

            statement.setLong(1, roomId);
            statement.setString(2, path);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting image failed, no rows affected.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addImage(long roomId, String path) {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(ADD_IMAGE)) {

            statement.setLong(1, roomId);
            statement.setString(2, path);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Adding image failed, no rows affected.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Room> findByIdGroupByLocale(long roomId) {

        Map<String, Room> result = new HashMap<>();

        LocaleDAO localeDAO = new PostgresLocaleDAO();
        Collection<Locale> locales = localeDAO.findALL().values();

        for (Locale locale : locales) {
            Room room = findALL(locale.getName()).stream().filter(r -> r.getId() == roomId).findFirst().get();
            result.put(locale.getName(), room);
        }

        return result;
    }

    @Override
    public List<Room> findByReservation(long reservationId, String locale) {

        List<Room> result = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_ROOMS_BY_RESERVATION)) {

            statement.setString(1, locale);
            statement.setLong(2, reservationId);

            fillRooms(result, statement, locale);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<Room> findFreeRooms(LocalDate checkin, LocalDate checkout, String locale) {
        List<Room> result = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_FREE_ROOMS)) {

            statement.setString(1, locale);
            statement.setString(2, checkin.toString());
            statement.setString(3, checkout.toString());

            fillRooms(result, statement, locale);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


    @Override
    public List<Room> findALL(String locale) {
        List<Room> result = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_ALL_ROOMS)) {

            statement.setString(1, locale);

            fillRooms(result, statement, locale);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private List<String> getImages(long roomId) {

        List<String> result = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_ALL_ROOM_IMAGES)) {

            statement.setLong(1, roomId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(resultSet.getString(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;

    }

    @Override
    public Room find(long roomId) {

        return null;
    }

    @Override
    public void insert(Room room) {

    }


    @Override
    public void delete(int roomId) {

    }

    private void fillRooms(List<Room> result, PreparedStatement statement, String locale) throws SQLException {
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();
        List<RoomCategory> roomCategories = roomCategoryDAO.findALL(locale);

        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Room room = new Room();

                room.setId(resultSet.getLong(1));
                room.setNumber(resultSet.getString(2));
                room.setOccupancy(resultSet.getInt(3));

                long roomCategoryId = resultSet.getLong(4);
                RoomCategory roomCategory = roomCategories.stream()
                        .filter(rc -> rc.getId() == roomCategoryId)
                        .findFirst()
                        .orElseThrow();
                room.setRoomCategory(roomCategory);

                room.setName(resultSet.getString(5));
                room.setDescription(resultSet.getString(6));
                room.setPrice(resultSet.getBigDecimal(7));
                room.setCreationDate(resultSet.getTimestamp(8));
                room.setImages(getImages(room.getId()));

                result.add(room);

            }
        }
    }
}
