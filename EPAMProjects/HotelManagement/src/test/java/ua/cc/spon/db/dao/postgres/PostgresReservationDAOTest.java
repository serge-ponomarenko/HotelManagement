package ua.cc.spon.db.dao.postgres;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.cc.spon.db.dao.*;
import ua.cc.spon.db.entity.Request;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.exception.DaoException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Sergiy Ponomarenko
 */
class PostgresReservationDAOTest {

    Connection con, emptyCon;

    @BeforeEach
    void setUp() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:h2:~/hotel_db;MODE=PostgreSQL;INIT=" +
                            "RUNSCRIPT FROM 'classpath:locales_init.sql'\\;" +
                            "RUNSCRIPT FROM 'classpath:statuses_init.sql'\\;" +
                            "RUNSCRIPT FROM 'classpath:users_init.sql'\\;" +
                            "RUNSCRIPT FROM 'classpath:categories_init.sql'\\;" +
                            "RUNSCRIPT FROM 'classpath:rooms_init.sql'\\;" +
                            "RUNSCRIPT FROM 'classpath:reservations_init.sql'\\;" +
                            "");
            emptyCon = DriverManager.getConnection(
                    "jdbc:h2:~/bad_db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findByUserShouldReturnProperUserValue() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        UserDAO userDAO = new PostgresUserDAO();
        reservationDAO.setConnection(con);
        userDAO.setConnection(con);

        List<Reservation> reservations;
        try {
            reservations = reservationDAO.findByUser(userDAO.find(1), "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(2, reservations.size());
        assertEquals("2022-08-24", reservations.get(0).getCheckinDate().toString());
        assertEquals("2022-09-20", reservations.get(0).getCheckoutDate().toString());
        assertEquals(1, reservations.get(0).getPersons());
        assertEquals(BigDecimal.valueOf(2538), reservations.get(0).getPrice());
        assertEquals(2, reservations.get(0).getRooms().size());
        assertEquals(Reservation.Status.BOOKED, reservations.get(0).getStatus());
        assertEquals(1, reservations.get(0).getId());
    }

    @Test
    void findByUserShouldThrowDaoExceptionOnDBError() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> reservationDAO.findByUser(null, "en"));
    }

    @Test
    void findByIdLocaleShouldReturnProperObject() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(con);

        Reservation reservation;
        try {
            reservation = reservationDAO.find(1, "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals("2022-08-24", reservation.getCheckinDate().toString());
        assertEquals("2022-09-20", reservation.getCheckoutDate().toString());
        assertEquals(1, reservation.getPersons());
        assertEquals(BigDecimal.valueOf(2538), reservation.getPrice());
        assertEquals(2, reservation.getRooms().size());
        assertEquals(Reservation.Status.BOOKED, reservation.getStatus());
        assertEquals(1, reservation.getId());
    }

    @Test
    void findByIdShouldThrowDaoExceptionOnDBError() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> reservationDAO.find(1, "en"));
    }

    @Test
    void findAllShouldReturnProperObjects() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(con);

        List<Reservation> reservations;
        try {
            reservations = reservationDAO.findAll("en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(3, reservations.size());
        assertEquals("2022-08-24", reservations.get(0).getCheckinDate().toString());
        assertEquals("2022-09-20", reservations.get(0).getCheckoutDate().toString());
        assertEquals(1, reservations.get(0).getPersons());
        assertEquals(BigDecimal.valueOf(2538), reservations.get(0).getPrice());
        assertEquals(2, reservations.get(0).getRooms().size());
        assertEquals(Reservation.Status.BOOKED, reservations.get(0).getStatus());
        assertEquals(1, reservations.get(0).getId());
    }

    @Test
    void findAllShouldThrowDaoExceptionOnDBError() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> reservationDAO.findAll("en"));
    }

    @Test
    void deleteShouldProperDelete() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(con);

        try {
            Reservation res1 = reservationDAO.find(1, "en");
            assertNotNull(res1);
            reservationDAO.delete(res1);
            Reservation res2 = reservationDAO.find(1, "en");
            assertNull(res2);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteShouldThrowDaoExceptionOnDBError() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> reservationDAO.delete(null));
    }

    @Test
    void insertShouldProperInsertObject() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(con);
        UserDAO userDAO = new PostgresUserDAO();
        userDAO.setConnection(con);
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(con);

        Reservation reservation = new Reservation();

        try {
            reservation.setCheckinDate(LocalDate.parse("2022-12-12"));
            reservation.setCheckoutDate(LocalDate.parse("2022-12-30"));
            reservation.setPersons(5);
            reservation.setStatus(Reservation.Status.BUSY);
            reservation.setPrice(BigDecimal.valueOf(555));
            reservation.setRooms(roomDAO.findAll("en"));
            reservation.setUser(userDAO.find(1));

            reservationDAO.insert(reservation);

        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        Reservation reservationExp;
        try {
            reservationExp = reservationDAO.find(reservation.getId(), "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(LocalDate.parse("2022-12-12"), reservationExp.getCheckinDate());
        assertEquals(LocalDate.parse("2022-12-30"), reservationExp.getCheckoutDate());
        assertEquals("pat_mair@gmail.com", reservationExp.getUser().getEmail());
        assertEquals(6, reservationExp.getRooms().size());
        assertEquals(5, reservationExp.getPersons());
        assertEquals(Reservation.Status.BUSY, reservationExp.getStatus());
        assertEquals(BigDecimal.valueOf(555), reservationExp.getPrice());

    }

    @Test
    void insertShouldThrowDaoExceptionOnDBError() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> reservationDAO.insert(null));
    }

    @Test
    void updateStatusShouldProperUpdateStatusID() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(con);

        Reservation reservation;

        try {
            reservation = reservationDAO.find(1, "en");

            assertNotEquals(Reservation.Status.FREE, reservation.getStatus());

            reservation.setStatus(Reservation.Status.FREE);
            reservationDAO.updateStatus(reservation);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        Reservation reservationExp;
        try {
            reservationExp = reservationDAO.find(1, "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(1, reservationExp.getId());
        assertEquals(Reservation.Status.FREE, reservationExp.getStatus());

    }

    @Test
    void updateStatusShouldThrowDaoExceptionOnDBError() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> reservationDAO.updateStatus(null));
    }

    @Test
    void updateExpiredPaidStatuses() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(con);

        Reservation reservation;

        try {
            reservation = reservationDAO.find(1, "en");

            assertEquals(Reservation.Status.BOOKED, reservation.getStatus());

            reservationDAO.updateExpiredPaidStatuses();
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        Reservation reservationExp;
        try {
            reservationExp = reservationDAO.find(1, "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(Reservation.Status.CANCELED, reservationExp.getStatus());
    }

    @Test
    void updateExpiredPaidStatusesShouldThrowDaoExceptionOnDBError() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, reservationDAO::updateExpiredPaidStatuses);
    }

    @Test
    void updateCheckinStatuses() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(con);

        Reservation reservation;

        try {
            reservation = reservationDAO.find(2, "en");

            assertEquals(Reservation.Status.PAID, reservation.getStatus());

            reservationDAO.updateCheckinStatuses();
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        Reservation reservationExp;
        try {
            reservationExp = reservationDAO.find(2, "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(Reservation.Status.BUSY, reservationExp.getStatus());
    }

    @Test
    void updateCheckinStatusesShouldThrowDaoExceptionOnDBError() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, reservationDAO::updateCheckinStatuses);
    }

    @Test
    void updateCheckoutStatusesShouldThrowDaoExceptionOnDBError() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, reservationDAO::updateCheckoutStatuses);
    }

    @Test
    void unsupportedMethodsMustThrowsException() {
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        reservationDAO.setConnection(con);

        assertThrows(UnsupportedOperationException.class, reservationDAO::findAll);
        assertThrows(UnsupportedOperationException.class, () -> reservationDAO.find(1));
        assertThrows(UnsupportedOperationException.class, () -> reservationDAO.update( null));
        assertThrows(UnsupportedOperationException.class, () -> reservationDAO.update( null, "en"));
        assertThrows(UnsupportedOperationException.class, () -> reservationDAO.delete(1));
    }

}