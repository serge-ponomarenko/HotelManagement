package ua.cc.spon.db.dao.postgres;

import ua.cc.spon.db.dao.*;
import ua.cc.spon.db.entity.Request;
import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.DaoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostgresRequestDAO extends RequestDAO {

    private static final String INSERT_REQUEST =
            "INSERT INTO reservation_requests " +
                    "(checkin_date, checkout_date, persons, rooms, additional_information, user_id) " +
                    "VALUES(?::date, ?::date, ?, ?, ?, ?)";

    private static final String INSERT_REQUEST_CATEGORIES =
            "INSERT INTO reservation_requests_categories " +
                    "(reservation_request_id, category_id) " +
                    "VALUES(?, ?)";

    private static final String FIND_ALL_PENDING =
            "SELECT * FROM reservation_requests " +
                    "WHERE reservation_id IS null";

    private static final String UPDATE_STATUS =
            "UPDATE reservation_requests " +
                    "SET reservation_id = ? " +
                    "WHERE reservation_request_id = ?";

    private static final String DELETE_BY_ID =
            "DELETE FROM reservation_requests " +
                    "WHERE reservation_request_id = ?";

    private static final String FIND_BY_ID =
            "SELECT * FROM reservation_requests " +
                    "WHERE reservation_request_id = ?";


    @Override
    public void delete(int requestId) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID)) {

            statement.setInt(1, requestId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public void updateReservation(Request request) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_STATUS)) {

            statement.setInt(1, request.getReservation().getId());
            statement.setInt(2, request.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public Request find(int requestId, String locale) throws DaoException {
        List<Request> result;

        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {

            statement.setInt(1, requestId);

            try (ResultSet resultSet = statement.executeQuery()) {
                result = fillRequests(resultSet, locale);
            }
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Request> findAllPending(String locale) throws DaoException {
        List<Request> result;

        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(FIND_ALL_PENDING)) {
                result = fillRequests(resultSet, locale);
            }
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

        return result;
    }

    @Override
    public void insert(Request request) throws DaoException {
        try (PreparedStatement statement1 = connection.prepareStatement(INSERT_REQUEST, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement statement2 = connection.prepareStatement(INSERT_REQUEST_CATEGORIES)) {
            int k = 0;
            statement1.setString(++k, request.getCheckinDate().toString());
            statement1.setString(++k, request.getCheckoutDate().toString());
            statement1.setInt(++k, request.getPersons());
            statement1.setInt(++k, request.getRooms());
            statement1.setString(++k, request.getAdditionalInformation());
            statement1.setInt(++k, request.getUser().getId());

            int affectedRows = statement1.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating request failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement1.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    request.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating request failed, no ID obtained.");
                }
            }

            for (int i = 0; i < request.getRoomCategories().size(); i++) {
                statement2.setInt(1, request.getId());
                statement2.setInt(2, request.getRoomCategories().get(i).getId());
                statement2.addBatch();
            }

            try {
                statement2.executeBatch();
            } catch (SQLException e) {
                throw new SQLException("Creating request failed, no values added to linked table.");
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

    }

    private List<Request> fillRequests(ResultSet resultSet, String locale) throws DaoException, SQLException {
        List<Request> result = new ArrayList<>();
        EntityTransaction transaction = new EntityTransaction(connection);
        UserDAO userDAO = new PostgresUserDAO();
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();
        ReservationDAO reservationDAO = new PostgresReservationDAO();

        transaction.initTransaction(userDAO, roomCategoryDAO, reservationDAO);

        try {

            while (resultSet.next()) {

                Request request = new Request();

                request.setId(resultSet.getInt("reservation_request_id"));
                request.setCheckinDate(resultSet.getDate("checkin_date").toLocalDate());
                request.setCheckoutDate(resultSet.getDate("checkout_date").toLocalDate());
                request.setPersons(resultSet.getInt("persons"));
                request.setRooms(resultSet.getInt("rooms"));
                request.setAdditionalInformation(resultSet.getString("additional_information"));
                request.setReservation(reservationDAO.find(resultSet.getInt("reservation_id"), locale));

                User user = userDAO.find(resultSet.getInt("user_id"));
                if (user == null) throw new DaoException("User not found");
                request.setUser(user);

                List<RoomCategory> roomCategories = roomCategoryDAO.findAllForRequest(request.getId(), locale);
                request.setRoomCategories(roomCategories);

                result.add(request);
            }

            transaction.commit();

        } catch (DaoException e) {
            transaction.rollback();
            throw new DaoException(e.getMessage());
        }

        return result;
    }

    @Override
    public List<Request> findAll() throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Request> findAll(String locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Request find(int id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Request request) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Request request, String locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Request request) throws DaoException {
        throw new UnsupportedOperationException();
    }
}
