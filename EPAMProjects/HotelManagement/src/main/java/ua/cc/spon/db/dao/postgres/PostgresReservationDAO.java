package ua.cc.spon.db.dao.postgres;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.DataSource;
import ua.cc.spon.db.dao.ReservationDAO;
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.DBException;
import ua.cc.spon.exception.NoUserFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresReservationDAO implements ReservationDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
                    "checkin_date + INTERVAL '9 hour' - now() < INTERVAL '0 hour' OR " +
                    "created_at + INTERVAL '2 day' - now() < INTERVAL '0 hour') " +
                    ")";

    private static final String UPDATE_CHECKIN_STATUSES = "UPDATE reservations " +
            "SET status_id = 4 WHERE " +
            "reservation_id IN ( " +
            "SELECT reservation_id FROM reservations " +
            "WHERE status_id IN (3) AND (checkin_date <= current_date))";

    private static final String UPDATE_CHECKOUT_STATUSES = "UPDATE reservations " +
            "SET status_id = 6 WHERE " +
            "reservation_id IN ( " +
            "SELECT reservation_id FROM reservations " +
            "WHERE status_id IN (4) AND (checkout_date = current_date))";

    @Override
    public List<Reservation> findByUser(User user, String locale) throws DBException {
        List<Reservation> result = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_BY_USER)) {

            statement.setLong(1, user.getId());

            fillReservations(result, statement, locale);

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DBException(e);
        }

        return result;
    }

    @Override
    public List<Reservation> findAll(String locale) throws DBException {

        List<Reservation> result = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_ALL)) {

            fillReservations(result, statement, locale);

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DBException(e);
        }

        return result;

    }


    @Override
    public Reservation find(long reservationId) {

        return null;
    }

    @Override
    public void insert(Connection con, Reservation reservation) {
        try (PreparedStatement statement1 = con.prepareStatement(INSERT_RESERVATION, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement statement2 = con.prepareStatement(INSERT_RESERVATIONS_ROOMS)) {

            statement1.setString(1, reservation.getCheckinDate().toString());
            statement1.setString(2, reservation.getCheckoutDate().toString());
            statement1.setLong(3, reservation.getStatus().getId());
            statement1.setLong(4, reservation.getUser().getId());
            statement1.setInt(5, reservation.getPersons());
            statement1.setBigDecimal(6, reservation.getPrice());

            int affectedRows = statement1.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating reservation failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement1.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating reservation failed, no ID obtained.");
                }
            }
            for (int i = 0; i < reservation.getRooms().size(); i++) {
                statement2.setLong(1, reservation.getId());
                statement2.setLong(2, reservation.getRooms().get(i).getId());
                statement2.addBatch();
            }

            try {
                statement2.executeBatch();
            } catch (SQLException e) {
                throw new SQLException("Creating reservation failed, no values added to linked table.");
            }

        } catch (SQLException e) {
            e.printStackTrace();  // TODO: 29.08.2022
        }


    }

    @Override
    public void insert(Reservation reservation) {

        try (Connection con = DataSource.getConnection()) {

            con.setAutoCommit(false);

            insert(con, reservation);

            con.commit();

        } catch (SQLException e) {
            e.printStackTrace();  // TODO: 29.08.2022
        }


    }

    @Override
    public void update(Reservation reservation) {

    }

    @Override
    public void updateStatus(Reservation reservation) {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_RESERVATION_STATUS)) {

            statement.setLong(1, reservation.getStatus().getId());
            statement.setLong(2, reservation.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating failed, no rows affected.");
            }

        } catch (SQLException e) {
            e.printStackTrace();  // TODO: 29.08.2022
        }
    }

    @Override
    public void updateExpiredPaidStatuses() {
        executeUpdateQuery(UPDATE_NOT_PAID_STATUSES);
    }

    @Override
    public void updateCheckinStatuses() {
        executeUpdateQuery(UPDATE_CHECKIN_STATUSES);
    }

    @Override
    public void updateCheckoutStatuses() {
        executeUpdateQuery(UPDATE_CHECKOUT_STATUSES);
    }

    @Override
    public void delete(Reservation reservation) {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_RESERVATION)) {

            statement.setLong(1, reservation.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deletion failed, no rows affected.");
            }

        } catch (SQLException e) {
            e.printStackTrace();  // TODO: 29.08.2022
        }

    }

    private void fillReservations(List<Reservation> result, PreparedStatement statement, String locale) throws SQLException {

        PostgresUserDAO postgresUserDAO = new PostgresUserDAO();

        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Reservation reservation = new Reservation();
                RoomDAO roomDAO = new PostgresRoomDAO();

                reservation.setId(resultSet.getLong(1));
                reservation.setCheckinDate(resultSet.getDate(2).toLocalDate());
                reservation.setCheckoutDate(resultSet.getDate(3).toLocalDate());
                reservation.setStatus(Reservation.Status.valueOf(resultSet.getString(4)));
                reservation.setCreatedAt(resultSet.getTimestamp(6));
                reservation.setPersons(resultSet.getInt(7));
                reservation.setPrice(resultSet.getBigDecimal(8));
                reservation.setUser(postgresUserDAO.find(resultSet.getLong(5)));

                reservation.setRooms(roomDAO.findByReservation(reservation.getId(), locale));

                result.add(reservation);

            }
        } catch (NoUserFoundException e) {
            throw new RuntimeException(e); // TODO: 20.09.2022
        }
    }

    private static void executeUpdateQuery(String query) {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();  // TODO: 29.08.2022
        }
    }


}
