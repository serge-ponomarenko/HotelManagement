package ua.cc.spon.db.dao.postgres;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.cc.spon.db.dao.LocaleDAO;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.Locale;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.DaoException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Sergiy Ponomarenko
 */
class PostgresLocaleDAOTest {

    Connection con, emptyCon;
    @BeforeEach
    void setUp() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:h2:~/hotel_db;MODE=PostgreSQL;INIT=RUNSCRIPT FROM 'classpath:locales_init.sql'");
            emptyCon = DriverManager.getConnection(
                    "jdbc:h2:~/bad_db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
     void findAllMapByNameShouldReturnProperMap() {
        LocaleDAO localeDAO = new PostgresLocaleDAO();
        localeDAO.setConnection(con);
        Map<String, Locale> allMapByName;

        try {
            allMapByName = localeDAO.findAllMapByName();
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        Locale en = allMapByName.get("en");
        Locale uk = allMapByName.get("uk");

        assertEquals("en", en.getName());
        assertEquals("English", en.getFullName());
        assertEquals("flag-country-gb", en.getIconPath());
        assertEquals("uk", uk.getName());
        assertEquals("Українська", uk.getFullName());
        assertEquals("flag-country-ua", uk.getIconPath());
    }

    @Test
    void findAllMapByNameShouldThrowDaoExceptionOnDBError() {
        LocaleDAO localeDAO = new PostgresLocaleDAO();
        localeDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, localeDAO::findAllMapByName);
    }

    @Test
    void unsupportedMethodsMustThrowsException() {
        LocaleDAO localeDAO = new PostgresLocaleDAO();
        localeDAO.setConnection(con);

        assertThrows(UnsupportedOperationException.class, localeDAO::findAll);
        assertThrows(UnsupportedOperationException.class, () -> localeDAO.findAll("en"));
        assertThrows(UnsupportedOperationException.class, () -> localeDAO.find(1));
        assertThrows(UnsupportedOperationException.class, () -> localeDAO.find(1, "en"));
        assertThrows(UnsupportedOperationException.class, () -> localeDAO.insert( null));
        assertThrows(UnsupportedOperationException.class, () -> localeDAO.update( null));
        assertThrows(UnsupportedOperationException.class, () -> localeDAO.update( null, "en"));
        assertThrows(UnsupportedOperationException.class, () -> localeDAO.delete(1));
        assertThrows(UnsupportedOperationException.class, () -> localeDAO.delete(null));

    }

}