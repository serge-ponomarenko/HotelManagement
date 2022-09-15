package ua.cc.spon.db.dao.postgres;

import ua.cc.spon.db.DataSource;
import ua.cc.spon.db.dao.StatusDAO;
import ua.cc.spon.db.entity.Reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgresStatusDAO implements StatusDAO {

    private static final String FIND_STATUS_NAME =
            "SELECT s.name, s_tr.name " +
                    "FROM statuses s " +
                    "INNER JOIN statuses_tr s_tr ON s_tr.status_id = s.status_id " +
                    "INNER JOIN locales l USING(locale_id) " +
                    "WHERE l.name = ?";

    @Override
    public Map<Reservation.Status, String> findNames(String locale) {
        Map<Reservation.Status, String> result = new HashMap<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_STATUS_NAME)) {

            statement.setString(1, locale);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.put(Reservation.Status.valueOf(resultSet.getString(1)), resultSet.getString(2));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


    @Override
    public List<Reservation.Status> findALL() {


        return null;
    }
}
