package ua.cc.spon.db.dao.postgres;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.cc.spon.db.dao.LocaleDAO;
import ua.cc.spon.db.dao.StatusDAO;
import ua.cc.spon.db.entity.Locale;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.exception.DaoException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sergiy Ponomarenko
 */
class PostgresStatusDAOTest {

    Connection con, emptyCon;

    @BeforeEach
    void setUp() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:h2:~/hotel_db;MODE=PostgreSQL;INIT=" +
                            "RUNSCRIPT FROM 'classpath:locales_init.sql'\\;" +
                            "RUNSCRIPT FROM 'classpath:statuses_init.sql'\\;" +
                            "");
            emptyCon = DriverManager.getConnection(
                    "jdbc:h2:~/bad_db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findNamesShouldReturnProperMap() {
        StatusDAO statusDAO = new PostgresStatusDAO();
        statusDAO.setConnection(con);

        Map<Reservation.Status, String> names;

        try {
            names = statusDAO.findNames("en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals("Booked", names.get(Reservation.Status.BOOKED));
        assertEquals("Busy", names.get(Reservation.Status.BUSY));
        assertEquals("Canceled", names.get(Reservation.Status.CANCELED));
        assertEquals("Completed", names.get(Reservation.Status.COMPLETED));
        assertEquals("Free", names.get(Reservation.Status.FREE));
        assertEquals("Paid", names.get(Reservation.Status.PAID));
        assertEquals("Unavailable", names.get(Reservation.Status.UNAVAILABLE));
    }

    @Test
    void findNamesShouldThrowDaoExceptionOnDBError() {
        StatusDAO statusDAO = new PostgresStatusDAO();
        statusDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> statusDAO.findNames("en"));
    }

    @Test
    void unsupportedMethodsMustThrowsException() {
        StatusDAO statusDAO = new PostgresStatusDAO();
        statusDAO.setConnection(con);

        assertThrows(UnsupportedOperationException.class, statusDAO::findAll);
        assertThrows(UnsupportedOperationException.class, () -> statusDAO.findAll("en"));
        assertThrows(UnsupportedOperationException.class, () -> statusDAO.find(1));
        assertThrows(UnsupportedOperationException.class, () -> statusDAO.find(1, "en"));
        assertThrows(UnsupportedOperationException.class, () -> statusDAO.insert( null));
        assertThrows(UnsupportedOperationException.class, () -> statusDAO.update( null));
        assertThrows(UnsupportedOperationException.class, () -> statusDAO.update( null, "en"));
        assertThrows(UnsupportedOperationException.class, () -> statusDAO.delete(1));
        assertThrows(UnsupportedOperationException.class, () -> statusDAO.delete(null));

    }
}