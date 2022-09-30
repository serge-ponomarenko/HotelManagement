package ua.cc.spon.db.dao.postgres;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sergiy Ponomarenko
 */
class PostgresUserSettingsDAOTest {

    Connection con, emptyCon;

    @BeforeEach
    void setUp() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:h2:~/hotel_db;MODE=PostgreSQL;INIT=" +
                            "RUNSCRIPT FROM 'classpath:locales_init.sql'\\;" +
                            "RUNSCRIPT FROM 'classpath:users_init.sql'\\;" +
                            "RUNSCRIPT FROM 'classpath:user_settings_init.sql'" +
                            "");
            emptyCon = DriverManager.getConnection(
                    "jdbc:h2:~/bad_db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findByUserIdShouldReturnProperObject() {
        UserSettingsDAO userSettingsDAO = new PostgresUserSettingsDAO();
        userSettingsDAO.setConnection(con);

        UserSettings userSettings;
        try {
            userSettings = userSettingsDAO.findByUserId(1);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(1, userSettings.getUserId());
        assertEquals("en", userSettings.getLocale());
        assertEquals("1ec3fadba011470dbc2e", userSettings.getHash());
        assertEquals(1, userSettings.getId());

    }

    @Test
    void findByUserIdShouldReturnNullWhenNotFound() {
        UserSettingsDAO userSettingsDAO = new PostgresUserSettingsDAO();
        userSettingsDAO.setConnection(con);

        UserSettings userSettings;
        try {
            userSettings = userSettingsDAO.findByUserId(999);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertNull(userSettings);
    }

    @Test
    void findByUserIdShouldThrowDaoExceptionOnDBError() {
        UserSettingsDAO userSettingsDAO = new PostgresUserSettingsDAO();
        userSettingsDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> userSettingsDAO.findByUserId(1));
    }

    @Test
    void insertShouldProperInsertObject() {
        UserSettingsDAO userSettingsDAO = new PostgresUserSettingsDAO();
        userSettingsDAO.setConnection(con);

        UserSettings userSettings = new UserSettings();
        userSettings.setUserId(3);
        userSettings.setLocale("uk");
        userSettings.setHash(userSettings.generateHash());

        try {
            userSettingsDAO.insert(userSettings);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        UserSettings userSettingsExp;
        try {
            userSettingsExp = userSettingsDAO.findByUserId(3);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(userSettingsExp, userSettings);
    }

    @Test
    void insertShouldThrowIfDuplicateUserId() {
        UserSettingsDAO userSettingsDAO = new PostgresUserSettingsDAO();
        userSettingsDAO.setConnection(con);

        UserSettings userSettings = new UserSettings();
        userSettings.setUserId(1);
        userSettings.setLocale("uk");
        userSettings.setHash(userSettings.generateHash());

        assertThrows(DaoException.class, () -> userSettingsDAO.insert(userSettings));
    }

    @Test
    void insertShouldThrowIfUnknownLocaleName() {
        UserSettingsDAO userSettingsDAO = new PostgresUserSettingsDAO();
        userSettingsDAO.setConnection(con);

        UserSettings userSettings = new UserSettings();
        userSettings.setUserId(3);
        userSettings.setLocale("AAA");
        userSettings.setHash(userSettings.generateHash());

        assertThrows(DaoException.class, () -> userSettingsDAO.insert(userSettings));
    }

    @Test
    void insertShouldThrowDaoExceptionOnDBError() {
        UserSettingsDAO userSettingsDAO = new PostgresUserSettingsDAO();
        userSettingsDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> userSettingsDAO.insert(new UserSettings()));
    }

    @Test
    void findByHashShouldReturnProperObject() {
        UserSettingsDAO userSettingsDAO = new PostgresUserSettingsDAO();
        userSettingsDAO.setConnection(con);

        UserSettings userSettings;
        try {
            userSettings = userSettingsDAO.findByHash("1ec3fadba011470dbc2e");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(1, userSettings.getUserId());
        assertEquals("en", userSettings.getLocale());
        assertEquals("1ec3fadba011470dbc2e", userSettings.getHash());
        assertEquals(1, userSettings.getId());

    }

    @Test
    void findByHashShouldReturnNullWhenNotFound() {
        UserSettingsDAO userSettingsDAO = new PostgresUserSettingsDAO();
        userSettingsDAO.setConnection(con);

        UserSettings userSettings;
        try {
            userSettings = userSettingsDAO.findByHash("123456789");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertNull(userSettings);
    }

    @Test
    void findByHashShouldThrowDaoExceptionOnDBError() {
        UserSettingsDAO userSettingsDAO = new PostgresUserSettingsDAO();
        userSettingsDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> userSettingsDAO.findByHash("12345678"));
    }

    @Test
    void updateShouldUpdateUserProper() {
        UserSettingsDAO userSettingsDAO = new PostgresUserSettingsDAO();
        userSettingsDAO.setConnection(con);

        UserSettings userSettingsUpd;
        try {
            userSettingsUpd = userSettingsDAO.findByUserId(1);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        userSettingsUpd.setLocale("uk");
        userSettingsUpd.setHash("12345678901234567890");
        userSettingsUpd.setUserId(3);

        try {
            userSettingsDAO.update(userSettingsUpd);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        UserSettings userSettingsExp;
        try {
            userSettingsExp = userSettingsDAO.findByUserId(3);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(userSettingsExp, userSettingsUpd);
    }

    @Test
    void updateShouldThrowDaoExceptionOnDBError() {
        UserSettingsDAO userSettingsDAO = new PostgresUserSettingsDAO();
        userSettingsDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> userSettingsDAO.update(new UserSettings()));
    }


    @Test
    void unsupportedMethodsMustThrowsException() {
        UserSettingsDAO dao = new PostgresUserSettingsDAO();
        dao.setConnection(con);

        assertThrows(UnsupportedOperationException.class, dao::findAll);
        assertThrows(UnsupportedOperationException.class, () -> dao.findAll("en"));
        assertThrows(UnsupportedOperationException.class, () -> dao.find(1));
        assertThrows(UnsupportedOperationException.class, () -> dao.find(1, "en"));
        assertThrows(UnsupportedOperationException.class, () -> dao.update(null, "en"));
        assertThrows(UnsupportedOperationException.class, () -> dao.delete(1));
        assertThrows(UnsupportedOperationException.class, () -> dao.delete(null));
    }

}