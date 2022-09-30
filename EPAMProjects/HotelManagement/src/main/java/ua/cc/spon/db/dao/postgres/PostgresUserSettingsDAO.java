package ua.cc.spon.db.dao.postgres;

import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class PostgresUserSettingsDAO extends UserSettingsDAO {

    private static final String INSERT_USER_SETTINGS =
            "INSERT INTO user_settings (user_id, locale_id, hash) " +
                    "VALUES(?, (SELECT locale_id FROM locales WHERE locales.name = ?), ?)";

    private static final String UPDATE_USER_SETTINGS =
            "UPDATE user_settings SET " +
                    "user_id = ?, locale_id = (SELECT locale_id FROM locales WHERE locales.name = ?), hash = ? " +
                    "WHERE user_settings_id = ?";

    private static final String FIND_BY_USER_ID =
            "SELECT user_settings_id, user_id, l.name, hash " +
                    "FROM user_settings us " +
                    "INNER JOIN locales l ON us.locale_id = l.locale_id " +
                    "WHERE user_id = ?";

    private static final String FIND_BY_HASH =
            "SELECT user_settings_id, user_id, l.name, hash " +
                    "FROM user_settings us " +
                    "INNER JOIN locales l ON us.locale_id = l.locale_id " +
                    "WHERE hash = ?";

    @Override
    public void insert(UserSettings userSettings) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_USER_SETTINGS,
                Statement.RETURN_GENERATED_KEYS)) {
            int k = 0;
            statement.setInt(++k, userSettings.getUserId());
            statement.setString(++k, userSettings.getLocale());
            statement.setString(++k, userSettings.getHash());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    userSettings.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public UserSettings findByUserId(int userId) throws DaoException {
        UserSettings userSettings = null;

        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_ID)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) userSettings = extractUserSettings(resultSet);
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
        return userSettings;
    }

    @Override
    public UserSettings findByHash(String userHash) throws DaoException {
        UserSettings userSettings = null;

        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_HASH)) {

            statement.setString(1, userHash);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) userSettings = extractUserSettings(resultSet);
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
        return userSettings;
    }

    @Override
    public void update(UserSettings userSettings) throws DaoException {

        try (PreparedStatement statement = connection.prepareStatement(UPDATE_USER_SETTINGS)) {
            int k = 0;
            statement.setInt(++k, userSettings.getUserId());
            statement.setString(++k, userSettings.getLocale());
            statement.setString(++k, userSettings.getHash());
            statement.setInt(++k, userSettings.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("UserSettings update failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    private UserSettings extractUserSettings(ResultSet resultSet) throws SQLException {
        UserSettings userSettings = new UserSettings();

        userSettings.setId(resultSet.getInt("user_settings_id"));
        userSettings.setUserId(resultSet.getInt("user_id"));
        userSettings.setLocale(resultSet.getString("name"));
        userSettings.setHash(resultSet.getString("hash"));

        return userSettings;
    }

    @Override
    public List<UserSettings> findAll() throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<UserSettings> findAll(String locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserSettings find(int id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserSettings find(int id, String locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(UserSettings userSettings, String locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(int id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(UserSettings userSettings) throws DaoException {
        throw new UnsupportedOperationException();
    }
}
