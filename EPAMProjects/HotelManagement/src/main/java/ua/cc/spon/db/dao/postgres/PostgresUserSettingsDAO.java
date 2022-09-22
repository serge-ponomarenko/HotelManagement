package ua.cc.spon.db.dao.postgres;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.DataSource;
import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DBException;

import java.sql.*;

public class PostgresUserSettingsDAO implements UserSettingsDAO {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String INSERT_USER_SETTINGS = "INSERT INTO user_settings (user_id, locale_id, hash) " +
            "VALUES(?, (SELECT locale_id FROM locales WHERE locales.name = ?), ?)";
    private static final String UPDATE_USER_SETTINGS = "UPDATE user_settings SET " +
            "user_id = ?, locale_id = (SELECT locale_id FROM locales WHERE locales.name = ?), hash = ? " +
            "WHERE user_settings_id = ?";
    private static final String FIND_BY_USER_ID = "SELECT user_settings_id, user_id, l.name, hash " +
            "FROM user_settings " +
            "INNER JOIN locales l USING(locale_id) " +
            "WHERE user_id = ?";
    private static final String FIND_BY_HASH = "SELECT user_settings_id, user_id, l.name, hash " +
            "FROM user_settings " +
            "INNER JOIN locales l USING(locale_id) " +
            "WHERE hash = ?";


    @Override
    public void insert(UserSettings userSettings) throws DBException {

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_USER_SETTINGS, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, userSettings.getUserId());
            statement.setString(2, userSettings.getLocale());
            statement.setString(3, userSettings.getHash());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    userSettings.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DBException(e);
        }
    }

    @Override
    public UserSettings findByUserId(long userId) throws DBException {

        UserSettings userSettings = new UserSettings();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_BY_USER_ID)) {

            statement.setLong(1, userId);

            fillUserSettings(userSettings, statement);

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DBException(e);
        }

        return userSettings;
    }

    @Override
    public void update(UserSettings userSettings) throws DBException {

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_USER_SETTINGS)) {

            statement.setLong(1, userSettings.getUserId());
            statement.setString(2, userSettings.getLocale());
            statement.setString(3, userSettings.getHash());
            statement.setLong(4, userSettings.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("UserSettings update failed, no rows affected.");
            }

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DBException(e);
        }
    }

    @Override
    public UserSettings findByHash(String userHash) throws DBException {
        UserSettings userSettings = new UserSettings();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_BY_HASH)) {

            statement.setString(1, userHash);

            fillUserSettings(userSettings, statement);

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DBException(e);
        }

        return userSettings;
    }

    private void fillUserSettings(UserSettings userSettings, PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                userSettings.setId(resultSet.getLong(1));
                userSettings.setUserId(resultSet.getLong(2));
                userSettings.setLocale(resultSet.getString(3));
                userSettings.setHash(resultSet.getString(4));
            }
        }
    }

}
