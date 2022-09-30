package ua.cc.spon.db.dao.postgres;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.cc.spon.db.dao.*;
import ua.cc.spon.db.entity.Request;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.exception.UserIsAlreadyRegisteredException;
import ua.cc.spon.util.BcryptDecoder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sergiy Ponomarenko
 */
class PostgresRequestDAOTest {

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
                            "RUNSCRIPT FROM 'classpath:requests_init.sql'\\;" +
                            "");
            emptyCon = DriverManager.getConnection(
                    "jdbc:h2:~/bad_db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteShouldProperDelete() {
        RequestDAO requestDAO = new PostgresRequestDAO();
        requestDAO.setConnection(con);

        try {
            Request req1 = requestDAO.find(1, "en");
            assertNotNull(req1);
            requestDAO.delete(1);
            Request req2 = requestDAO.find(1, "en");
            assertNull(req2);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteShouldThrowDaoExceptionOnDBError() {
        RequestDAO requestDAO = new PostgresRequestDAO();
        requestDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> requestDAO.delete(1));
    }

    @Test
    void findByIdShouldReturnProperUserValue() {
        RequestDAO requestDAO = new PostgresRequestDAO();
        requestDAO.setConnection(con);

        Request request;
        try {
            request = requestDAO.find(1, "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(1, request.getId());
        assertEquals(LocalDate.parse("2022-09-30"), request.getCheckinDate());
        assertEquals(LocalDate.parse("2022-10-06"), request.getCheckoutDate());
        assertEquals(5, request.getPersons());
        assertEquals(3, request.getRooms());
        assertEquals("", request.getAdditionalInformation());
        assertEquals(3, request.getRoomCategories().size());
        assertEquals(1, request.getReservation().getId());
        assertEquals(1, request.getUser().getId());
    }

    @Test
    void findByIdShouldThrowDaoExceptionOnDBError() {
        RequestDAO requestDAO = new PostgresRequestDAO();
        requestDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> requestDAO.find(1, "en"));
    }

    @Test
    void findAllPendingShouldReturnAllPendingRequests() {
        RequestDAO requestDAO = new PostgresRequestDAO();
        requestDAO.setConnection(con);

        List<Request> requests;
        try {
            requests = requestDAO.findAllPending("en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(1, requests.size());
        assertEquals(3, requests.get(0).getId());
        assertEquals("New request", requests.get(0).getAdditionalInformation());

    }

    @Test
    void findAllPendingShouldThrowDaoExceptionOnDBError() {
        RequestDAO requestDAO = new PostgresRequestDAO();
        requestDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> requestDAO.findAllPending("en"));
    }

    @Test
    void insertShouldProperInsertObject() {
        RequestDAO requestDAO = new PostgresRequestDAO();
        UserDAO userDAO = new PostgresUserDAO();
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();
        requestDAO.setConnection(con);
        userDAO.setConnection(con);
        roomCategoryDAO.setConnection(con);

        Request request = new Request();

        try {
            request.setCheckinDate(LocalDate.parse("2022-12-12"));
            request.setCheckoutDate(LocalDate.parse("2022-12-30"));
            request.setPersons(5);
            request.setRooms(4);
            request.setAdditionalInformation("Additional information");
            request.setRoomCategories(roomCategoryDAO.findAll("en"));
            request.setUser(userDAO.find(1));

            requestDAO.insert(request);

        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        Request requestExp;
        try {
            requestExp = requestDAO.find(request.getId(), "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(LocalDate.parse("2022-12-12"), requestExp.getCheckinDate());
        assertEquals(LocalDate.parse("2022-12-30"), requestExp.getCheckoutDate());
        assertEquals("Additional information", requestExp.getAdditionalInformation());
        assertEquals("pat_mair@gmail.com", requestExp.getUser().getEmail());
        assertEquals(4, requestExp.getRooms());
        assertEquals(5, requestExp.getPersons());
        assertEquals(5, requestExp.getRoomCategories().size());

    }

    @Test
    void insertShouldThrowDaoExceptionOnDBError() {
        RequestDAO requestDAO = new PostgresRequestDAO();
        requestDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> requestDAO.insert(null));
    }


    @Test
    void updateReservationShouldProperUpdateReservationID() {
        RequestDAO requestDAO = new PostgresRequestDAO();
        ReservationDAO reservationDAO = new PostgresReservationDAO();
        requestDAO.setConnection(con);
        reservationDAO.setConnection(con);

        Request request;

        try {
            request = requestDAO.find(3, "en");
            request.setReservation(reservationDAO.find(1, "en"));
            requestDAO.updateReservation(request);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        Request requestExp;
        try {
            requestExp = requestDAO.find(3, "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(3, requestExp.getId());
        assertEquals(1, requestExp.getReservation().getId());

    }

    @Test
    void updateReservationShouldThrowDaoExceptionOnDBError() {
        RequestDAO requestDAO = new PostgresRequestDAO();
        requestDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> requestDAO.updateReservation(null));
    }

    @Test
    void unsupportedMethodsMustThrowsException() {
        RequestDAO requestDAO = new PostgresRequestDAO();
        requestDAO.setConnection(con);

        assertThrows(UnsupportedOperationException.class, requestDAO::findAll);
        assertThrows(UnsupportedOperationException.class, () -> requestDAO.findAll("en"));
        assertThrows(UnsupportedOperationException.class, () -> requestDAO.find(1));
        assertThrows(UnsupportedOperationException.class, () -> requestDAO.update(null));
        assertThrows(UnsupportedOperationException.class, () -> requestDAO.update(null, "en"));
        assertThrows(UnsupportedOperationException.class, () -> requestDAO.delete(null));

    }


}