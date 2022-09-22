package ua.cc.spon.db.dao.postgres;

import ua.cc.spon.db.DataSource;
import ua.cc.spon.db.dao.RequestDAO;
import ua.cc.spon.db.dao.RoomCategoryDAO;
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.entity.Request;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.DBException;
import ua.cc.spon.exception.NoUserFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresRequestDAO implements RequestDAO {

    private static final String INSERT_REQUEST =
            "INSERT INTO reservation_requests " +
                    "(checkin_date, checkout_date, persons, rooms, additional_information, user_id) " +
                    "VALUES(?::date, ?::date, ?, ?, ?, ?)";

    private static final String INSERT_REQUEST_CATEGORIES =
            "INSERT INTO reservation_requests_categories " +
                    "(reservation_request_id, category_id) " +
                    "VALUES(?, ?)";

    private static final String FIND_ALL_PENDING =
            "SELECT * FROM reservation_requests WHERE reservation_id IS null";

    private static final String UPDATE_STATUS =
            "UPDATE reservation_requests " +
                    "SET reservation_id = ? " +
                    "WHERE reservation_request_id = ?";

    private static final String DELETE_BY_ID =
            "DELETE FROM reservation_requests " +
                    "WHERE reservation_request_id = ?";


    @Override
    public void deleteById(long requestId) throws DBException {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_BY_ID)) {

            statement.setLong(1, requestId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public void updateReservation(Request request) {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_STATUS)) {

            statement.setLong(1, request.getReservation().getId());
            statement.setLong(2, request.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating failed, no rows affected.");
            }

        } catch (SQLException e) {
            e.printStackTrace();  // TODO: 29.08.2022
        }
    }

    @Override
    public Request find(long requestId, String locale) {
        return findAllPending(locale).stream()
                .filter(request -> request.getId() == requestId)
                .findFirst()
                .orElseThrow();
    }

    @Override
    public List<Request> findAllPending(String locale) {
        List<Request> result = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_ALL_PENDING)) {

            fillRequests(result, statement, locale);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void insert(Request request) {

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement1 = con.prepareStatement(INSERT_REQUEST, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement statement2 = con.prepareStatement(INSERT_REQUEST_CATEGORIES)) {

            con.setAutoCommit(false);

            statement1.setString(1, request.getCheckinDate().toString());
            statement1.setString(2, request.getCheckoutDate().toString());
            statement1.setInt(3, request.getPersons());
            statement1.setInt(4, request.getRooms());
            statement1.setString(5, request.getAdditionalInformation());
            statement1.setLong(6, request.getUser().getId());

            int affectedRows = statement1.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating request failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement1.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    request.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating request failed, no ID obtained.");
                }
            }

            for (int i = 0; i < request.getRoomCategories().size(); i++) {
                statement2.setLong(1, request.getId());
                statement2.setLong(2, request.getRoomCategories().get(i).getId());
                statement2.addBatch();
            }

            try {
                statement2.executeBatch();
            } catch (SQLException e) {
                throw new SQLException("Creating request failed, no values added to linked table.");
            }

            con.commit();

        } catch (SQLException e) {
            e.printStackTrace();  // TODO: 29.08.2022
        }

    }

    private void fillRequests(List<Request> result, PreparedStatement statement, String locale) throws SQLException {
        UserDAO userDAO = new PostgresUserDAO();
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();

        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Request request = new Request();

                request.setId(resultSet.getLong(1));
                request.setCheckinDate(resultSet.getDate(2).toLocalDate());
                request.setCheckoutDate(resultSet.getDate(3).toLocalDate());
                request.setPersons(resultSet.getInt(4));
                request.setRooms(resultSet.getInt(5));
                request.setAdditionalInformation(resultSet.getString(6));
                request.setReservation(null); // TODO: 04.09.2022
                try {
                    request.setUser(userDAO.find(resultSet.getLong(9)));
                } catch (NoUserFoundException e) {
                    throw new RuntimeException(e);
                }

                request.setRoomCategories(roomCategoryDAO.findAllForRequest(request.getId(), locale));

                result.add(request);

            }
        }
    }

}
