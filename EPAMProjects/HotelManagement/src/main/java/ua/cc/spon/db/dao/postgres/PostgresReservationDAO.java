package ua.cc.spon.db.dao.postgres;

import ua.cc.spon.db.dao.EntityTransaction;
import ua.cc.spon.db.dao.ReservationDAO;
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.DaoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostgresReservationDAO extends ReservationDAO {

    private static final String INSERT_RESERVATION =
            "INSERT INTO reservations " +
                    "(checkin_date, checkout_date, status_id, user_id, persons, price) " +
                    "VALUES(?::date, ?::date, ?, ?, ?, ?)";

    private static final String UPDATE_RESERVATION_STATUS =
            "UPDATE reservations " +
                    "SET status_id = ? " +
                    "WHERE reservation_id = ?";

    private static final String DELETE_RESERVATION =
            "DELETE FROM reservations " +
                    "WHERE reservation_id = ?";

    private static final String INSERT_RESERVATIONS_ROOMS =
            "INSERT INTO reservations_rooms " +
                    "(reservation_id, room_id) " +
                    "VALUES(?, ?)";

    private static final String FIND_BY_USER =
            "SELECT reservation_id, checkin_date, checkout_date, s.name, user_id, created_at, persons, price " +
                    "FROM reservations r " +
                    "INNER JOIN statuses s ON s.status_id = r.status_id " +
                    "WHERE user_id = ?";

    private static final String FIND_ALL =
            "SELECT reservation_id, checkin_date, checkout_date, s.name, user_id, created_at, persons, price " +
                    "FROM reservations r " +
                    "INNER JOIN statuses s ON s.status_id = r.status_id";

    private static final String FIND_BY_ID =
            "SELECT reservation_id, checkin_date, checkout_date, s.name, user_id, created_at, persons, price " +
                    "FROM reservations r " +
                    "INNER JOIN statuses s ON s.status_id = r.status_id " +
                    "WHERE reservation_id = ?";

    private static final String UPDATE_NOT_PAID_STATUSES =
            "UPDATE reservations " +
                    "SET status_id = 7 WHERE " +
                    "reservation_id IN ( " +
                    "SELECT reservation_id FROM reservations " +
                    "WHERE status_id IN (2) AND ( " +
                    "checkin_date + (INTERVAL '9' HOUR) - now() < INTERVAL '0' HOUR OR " +
                    "created_at + INTERVAL '2' DAY - now() < INTERVAL '0' HOUR) " +
                    ")";

    private static final String UPDATE_CHECKIN_STATUSES =
            "UPDATE reservations " +
                    "SET status_id = 4 WHERE " +
                    "reservation_id IN ( " +
                    "SELECT reservation_id FROM reservations " +
                    "WHERE status_id IN (3) AND (checkin_date <= current_date))";

    private static final String UPDATE_CHECKOUT_STATUSES =
            "UPDATE reservations " +
                    "SET status_id = 6 WHERE " +
                    "reservation_id IN ( " +
                    "SELECT reservation_id FROM reservations " +
                    "WHERE status_id IN (4) AND (checkout_date = current_date))";

    @Override
    public List<Reservation> findByUser(User user, String locale) throws DaoException {
        List<Reservation> result;

        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_USER)) {

            statement.setInt(1, user.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                result = fillReservations(resultSet, locale);
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

        return result;
    }

    @Override
    public Reservation find(int id, String locale) throws DaoException {
        List<Reservation> result;

        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                result = fillReservations(resultSet, locale);
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

        return !result.isEmpty() ? result.get(0) : null;
    }

    @Override
    public List<Reservation> findAll(String locale) throws DaoException {
        List<Reservation> result;

        try (Statement statement = connection.createStatement()) {

            try (ResultSet resultSet = statement.executeQuery(FIND_ALL)) {
                result = fillReservations(resultSet, locale);
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

        return result;
    }

    @Override
    public void insert(Reservation reservation) throws DaoException {
        try (PreparedStatement statement1 = connection.prepareStatement(INSERT_RESERVATION, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement statement2 = connection.prepareStatement(INSERT_RESERVATIONS_ROOMS)) {
            int k = 0;
            statement1.setString(++k, reservation.getCheckinDate().toString());
            statement1.setString(++k, reservation.getCheckoutDate().toString());
            statement1.setInt(++k, reservation.getStatus().getId());
            statement1.setInt(++k, reservation.getUser().getId());
            statement1.setInt(++k, reservation.getPersons());
            statement1.setBigDecimal(++k, reservation.getPrice());

            int affectedRows = statement1.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating reservation failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement1.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating reservation failed, no ID obtained.");
                }
            }
            for (int i = 0; i < reservation.getRooms().size(); i++) {
                statement2.setInt(1, reservation.getId());
                statement2.setInt(2, reservation.getRooms().get(i).getId());
                statement2.addBatch();
            }

            try {
                statement2.executeBatch();
            } catch (SQLException e) {
                throw new SQLException("Creating reservation failed, no values added to linked table.");
            }

        } catch (SQLException e) {
            throw new DaoException();
        }

    }

    @Override
    public void updateStatus(Reservation reservation) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_RESERVATION_STATUS)) {
            int k = 0;
            statement.setInt(++k, reservation.getStatus().getId());
            statement.setInt(++k, reservation.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public void updateExpiredPaidStatuses() throws DaoException {
        executeUpdateQuery(UPDATE_NOT_PAID_STATUSES);
    }

    @Override
    public void updateCheckinStatuses() throws DaoException {
        executeUpdateQuery(UPDATE_CHECKIN_STATUSES);
    }

    @Override
    public void updateCheckoutStatuses() throws DaoException {
        executeUpdateQuery(UPDATE_CHECKOUT_STATUSES);
    }

    @Override
    public void delete(Reservation reservation) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_RESERVATION)) {

            statement.setInt(1, reservation.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deletion failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

    }

    private List<Reservation> fillReservations(ResultSet resultSet, String locale) throws DaoException {
        List<Reservation> result = new ArrayList<>();

        UserDAO userDAO = new PostgresUserDAO();
        RoomDAO roomDAO = new PostgresRoomDAO();
        EntityTransaction transaction = new EntityTransaction(connection);

        transaction.initTransaction(userDAO, roomDAO);

        try {

            while (resultSet.next()) {
                Reservation reservation = new Reservation();

                reservation.setId(resultSet.getInt(1));
                reservation.setCheckinDate(resultSet.getDate(2).toLocalDate());
                reservation.setCheckoutDate(resultSet.getDate(3).toLocalDate());
                reservation.setStatus(Reservation.Status.valueOf(resultSet.getString(4)));
                reservation.setCreatedAt(resultSet.getTimestamp(6));
                reservation.setPersons(resultSet.getInt(7));
                reservation.setPrice(resultSet.getBigDecimal(8));
                User user = userDAO.find(resultSet.getInt(5));
                if (user == null) throw new DaoException("User not found");
                reservation.setUser(user);
                reservation.setRooms(roomDAO.findByReservation(reservation.getId(), locale));

                result.add(reservation);

            }
            transaction.commit();

        } catch (DaoException | SQLException e) {
            transaction.rollback();
            throw new DaoException(e.getMessage());
        }
        return result;
    }

    private void executeUpdateQuery(String query) throws DaoException {
        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate(query);

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public List<Reservation> findAll() throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reservation find(int id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Reservation reservation) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Reservation reservation, String locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(int id) throws DaoException {
        throw new UnsupportedOperationException();
    }
}
