package ua.cc.spon.db.dao.postgres;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.cc.spon.db.dao.*;
import ua.cc.spon.db.entity.Request;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.exception.DaoException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sergiy Ponomarenko
 */
class PostgresRoomDAOTest {

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
    void deleteShouldProperDelete() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(con);

        try {
            Room room1 = roomDAO.find(1, "en");
            assertNotNull(room1);
            roomDAO.delete(1);
            Room room2 = roomDAO.find(1, "en");
            assertNull(room2);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteShouldThrowDaoExceptionOnDBError() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> roomDAO.delete(1));
    }


    @Test
    void addImageShouldProperAddImage() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(con);

        try {
            roomDAO.addImage(1, "path_to_image");
            Room room = roomDAO.find(1, "en");
            assertTrue(room.getImages().contains("path_to_image"));
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addImageShouldThrowDaoExceptionOnDBError() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> roomDAO.addImage(1, "path"));
    }

    @Test
    void deleteImageShouldProperDeleteImage() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(con);

        try {
            String path = "./uploads/101/202319955.jpg";
            roomDAO.deleteImage(1, path);
            Room room = roomDAO.find(1, "en");
            assertFalse(room.getImages().contains(path));
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteImageShouldThrowDaoExceptionOnDBError() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> roomDAO.deleteImage(1, "path"));
    }

    @Test
    void findRoomsWithoutReservation() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(con);

        List<Room> rooms;
        try {
            rooms = roomDAO.findRoomsWithoutReservation("en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(1, rooms.size());
        assertEquals(6, rooms.get(0).getId());
        assertEquals("301", rooms.get(0).getNumber());

    }

    @Test
    void findRoomsWithoutReservationShouldThrowDaoExceptionOnDBError() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> roomDAO.findRoomsWithoutReservation("en"));
    }

    @Test
    void findByReservation() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(con);

        List<Room> rooms;
        try {
            rooms = roomDAO.findByReservation(1, "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(2, rooms.size());
        assertEquals(1, rooms.get(0).getId());
        assertEquals("102", rooms.get(0).getNumber());
    }

    @Test
    void findByReservationShouldThrowDaoExceptionOnDBError() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> roomDAO.findByReservation(1, "en"));
    }

    @Test
    void findByIdGroupByLocale() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(con);

        Map<String, Room> room;
        try {
            room = roomDAO.findByIdGroupByLocale(1);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals("102", room.get("en").getNumber());
        assertEquals("Business Single Room", room.get("en").getName());
        assertEquals("70", room.get("en").getPrice().toPlainString());
        assertEquals("Одномісний номер бізнес-класу", room.get("uk").getName());
    }

    @Test
    void findByIdGroupByLocaleShouldThrowDaoExceptionOnDBError() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> roomDAO.findByIdGroupByLocale(1));
    }


    @Test
    void findFreeRooms() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(con);

        List<Room> rooms;
        try {
            rooms = roomDAO.findFreeRooms(LocalDate.parse("2022-08-20"),LocalDate.parse("2022-08-30"), "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(2, rooms.size());
        assertEquals(5, rooms.get(0).getId());
        assertEquals("301", rooms.get(1).getNumber());
    }

    @Test
    void findFreeRoomsShouldThrowDaoExceptionOnDBError() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> roomDAO.findFreeRooms(null, null, "en"));
    }

    @Test
    void getFreeRoomById() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(con);

        Room room;
        try {
            room = roomDAO.getFreeRoomById(5, LocalDate.parse("2022-08-20"),LocalDate.parse("2022-08-30"), "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(5, room.getId());
        assertEquals("204", room.getNumber());
    }

    @Test
    void getFreeRoomByIdShouldThrowDaoExceptionOnDBError() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> roomDAO.getFreeRoomById(5, null, null, "en"));
    }

    @Test
    void updateShouldProperUpdateRoomCategory() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(con);

        Room roomUpd;
        try {
            roomUpd = roomDAO.find(1, "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        roomUpd.setName("New room name");
        roomUpd.setDescription("New room description");

        try {
            roomDAO.update(roomUpd, "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        Room roomExp;
        try {
            roomExp = roomDAO.find(1, "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals("New room name", roomExp.getName());
        assertEquals("New room description", roomExp.getDescription());
    }

    @Test
    void updateShouldThrowDaoExceptionOnDBError() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> roomDAO.update(null, "en"));
    }

    @Test
    void unsupportedMethodsMustThrowsException() {
        RoomDAO roomDAO = new PostgresRoomDAO();
        roomDAO.setConnection(con);

        assertThrows(UnsupportedOperationException.class, roomDAO::findAll);
        assertThrows(UnsupportedOperationException.class, () -> roomDAO.find(1));
        assertThrows(UnsupportedOperationException.class, () -> roomDAO.update( null));
        assertThrows(UnsupportedOperationException.class, () -> roomDAO.delete(null));
    }

}