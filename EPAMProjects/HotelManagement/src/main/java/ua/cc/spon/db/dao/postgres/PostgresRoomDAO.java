package ua.cc.spon.db.dao.postgres;

import ua.cc.spon.db.DataSource;
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.RoomCategory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PostgresRoomDAO implements RoomDAO {

    private static final String FIND_FREE_ROOMS =
            "SELECT r.room_id, number, occupancy, r.category_id, c_tr.name, r_tr.name, r_tr.description, price " +
                    "FROM rooms r " +
                    "INNER JOIN categories c USING(category_id) " +
                    "INNER JOIN categories_tr c_tr ON c_tr.category_id = r.category_id AND c_tr.locale = ? " +
                    "INNER JOIN rooms_tr r_tr ON r.room_id = r_tr.room_id AND r_tr.locale = ? " +
                    "WHERE r.room_id NOT IN " +
                    "(SELECT rs.room_id FROM rooms rs " +
                    "JOIN reservations_rooms rr on rs.room_id = rr.room_id " +
                    "JOIN reservations r on rr.reservation_id = r.reservation_id " +
                    "WHERE (status_id NOT IN (1, 6, 7)) AND NOT (?::date >= checkout_date OR ?::date <= checkin_date))";

    private static final String FIND_ALL_ROOMS =
            "SELECT r.room_id, number, occupancy, r.category_id, c_tr.name, r_tr.name, r_tr.description, price " +
                    "FROM rooms r " +
                    "INNER JOIN categories c USING(category_id) " +
                    "INNER JOIN categories_tr c_tr ON c_tr.category_id = r.category_id AND c_tr.locale = ? " +
                    "INNER JOIN rooms_tr r_tr ON r.room_id = r_tr.room_id AND r_tr.locale = ? ";

    private static final String FIND_ALL_ROOM_IMAGES =
            "SELECT room_images.path FROM room_images WHERE room_id = ?";

    private static final String FIND_ROOMS_BY_RESERVATION =
            "SELECT r.room_id, number, occupancy, r.category_id, c_tr.name, r_tr.name, r_tr.description, price FROM rooms r " +
                    "INNER JOIN reservations_rooms rr on r.room_id = rr.room_id " +
                    "INNER JOIN categories c USING(category_id) " +
                    "INNER JOIN categories_tr c_tr ON c_tr.category_id = r.category_id AND c_tr.locale = ? " +
                    "INNER JOIN rooms_tr r_tr ON r.room_id = r_tr.room_id AND r_tr.locale = ? " +
                    "WHERE reservation_id = ?";

    @Override
    public List<Room> findByReservation(long reservationId, String locale) {

        List<Room> result = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_ROOMS_BY_RESERVATION)) {

            statement.setString(1, locale);
            statement.setString(2, locale);
            statement.setLong(3, reservationId);

            fillRooms(result, statement);

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
            statement.setString(2, locale);
            statement.setString(3, checkin.toString());
            statement.setString(4, checkout.toString());

            fillRooms(result, statement);

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
            statement.setString(2, locale);

            fillRooms(result, statement);

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
    public void update(Room room) {

    }

    @Override
    public void delete(int roomId) {

    }

    private void fillRooms(List<Room> result, PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Room room = new Room();
                RoomCategory roomCategory = new RoomCategory();

                room.setId(resultSet.getLong(1));
                room.setNumber(resultSet.getString(2));
                room.setOccupancy(resultSet.getInt(3));
                roomCategory.setId(resultSet.getInt(4));
                roomCategory.setName(resultSet.getString(5));
                room.setRoomCategory(roomCategory);
                room.setName(resultSet.getString(6));
                room.setDescription(resultSet.getString(7));
                room.setPrice(resultSet.getBigDecimal(8));
                room.setImages(getImages(room.getId()));

                result.add(room);

            }
        }
    }
}
