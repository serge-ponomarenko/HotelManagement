package ua.cc.spon.db.dao.postgres;

import ua.cc.spon.db.dao.StatusDAO;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.exception.DaoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PostgresStatusDAO extends StatusDAO {

    private static final String FIND_STATUS_NAME =
            "SELECT s.name AS name, s_tr.name AS tr_name " +
                    "FROM statuses s " +
                    "INNER JOIN statuses_tr s_tr ON s_tr.status_id = s.status_id " +
                    "INNER JOIN locales l ON l.locale_id = s_tr.locale_id " +
                    "WHERE l.name = ?";

    @Override
    public Map<Reservation.Status, String> findNames(String locale) throws DaoException {
        Map<Reservation.Status, String> result = new EnumMap<>(Reservation.Status.class);

        try (PreparedStatement statement = connection.prepareStatement(FIND_STATUS_NAME)) {

            statement.setString(1, locale);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.put(Reservation.Status.valueOf(resultSet.getString("name")),
                            resultSet.getString("tr_name"));
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
        return result;
    }

    @Override
    public List<Reservation.Status> findAll() throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Reservation.Status> findAll(String locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reservation.Status find(int id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reservation.Status find(int id, String locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(Reservation.Status status) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Reservation.Status status) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Reservation.Status status, String locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(int id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Reservation.Status status) throws DaoException {
        throw new UnsupportedOperationException();
    }
}
