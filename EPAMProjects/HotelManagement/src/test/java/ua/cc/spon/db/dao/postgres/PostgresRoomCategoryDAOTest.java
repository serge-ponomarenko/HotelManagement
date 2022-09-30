package ua.cc.spon.db.dao.postgres;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.cc.spon.db.dao.LocaleDAO;
import ua.cc.spon.db.dao.RoomCategoryDAO;
import ua.cc.spon.db.dao.StatusDAO;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.exception.UserIsAlreadyRegisteredException;
import ua.cc.spon.util.BcryptDecoder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sergiy Ponomarenko
 */
class PostgresRoomCategoryDAOTest {

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
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();
        roomCategoryDAO.setConnection(con);

        int beforeSize;
        int afterSize;
        try {
            beforeSize = roomCategoryDAO.findAll("en").size();
            roomCategoryDAO.delete(2);
            afterSize = roomCategoryDAO.findAll("en").size();
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(beforeSize - 1, afterSize);
    }

    @Test
    void deleteShouldThrowDaoExceptionOnDBError() {
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();
        roomCategoryDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> roomCategoryDAO.delete(1));
    }

    @Test
    void findAllShouldFoundPropertyValues() {
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();
        roomCategoryDAO.setConnection(con);

        List<RoomCategory> all;
        try {
            all = roomCategoryDAO.findAll("en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(5, all.size());

        assertEquals(1, all.get(0).getId());
        assertEquals("Economy", all.get(0).getName());
        assertEquals("Minimal amenities for a short stay", all.get(0).getDescription());
        assertEquals(Timestamp.valueOf("2022-08-26 11:07:23"), all.get(0).getCreationDate());

        assertEquals("Standart", all.get(1).getName());
        assertEquals("Business", all.get(2).getName());
        assertEquals("Luxe", all.get(3).getName());
        assertEquals("President", all.get(4).getName());

    }

    @Test
    void findAllShouldThrowDaoExceptionOnDBError() {
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();
        roomCategoryDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> roomCategoryDAO.findAll("aaa"));
    }

    @Test
    void findByIdGroupByLocaleShouldReturnProperMap() {
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();
        roomCategoryDAO.setConnection(con);

        Map<String, RoomCategory> byLocale;
        try {
            byLocale = roomCategoryDAO.findByIdGroupByLocale(1);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals("Economy", byLocale.get("en").getName());
        assertEquals(Timestamp.valueOf("2022-08-26 11:07:23"), byLocale.get("en").getCreationDate());
        assertEquals("Minimal amenities for a short stay", byLocale.get("en").getDescription());
        assertEquals(1, byLocale.get("en").getId());

        assertEquals("Економ", byLocale.get("uk").getName());
        assertEquals(Timestamp.valueOf("2022-08-26 11:07:23"), byLocale.get("uk").getCreationDate());
        assertEquals("Мінімальні зручності для короткочасного перебування", byLocale.get("uk").getDescription());
        assertEquals(1, byLocale.get("uk").getId());

    }

    @Test
    void findByIdGroupByLocaleShouldThrowDaoExceptionOnDBError() {
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();
        roomCategoryDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> roomCategoryDAO.findByIdGroupByLocale(2));
    }

    @Test
    void findAllForRequestShouldFindAllForRequest() {
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();
        roomCategoryDAO.setConnection(con);

        List<RoomCategory> all;
        try {
            all = roomCategoryDAO.findAllForRequest(1, "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(3, all.size());

        assertEquals(1, all.get(0).getId());
        assertEquals("Economy", all.get(0).getName());
        assertEquals("Minimal amenities for a short stay", all.get(0).getDescription());
        assertEquals(Timestamp.valueOf("2022-08-26 11:07:23"), all.get(0).getCreationDate());

        assertEquals("Standart", all.get(1).getName());
        assertEquals("Business", all.get(2).getName());
    }

    @Test
    void findAllForRequestShouldThrowDaoExceptionOnDBError() {
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();
        roomCategoryDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> roomCategoryDAO.findAllForRequest(1, "en"));
    }

    @Test
    void updateShouldProperUpdateRoomCategory() {
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();
        roomCategoryDAO.setConnection(con);

        RoomCategory roomCategoryUpd;
        try {
            roomCategoryUpd = roomCategoryDAO.findAll("en").get(0);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        roomCategoryUpd.setName("New room category");
        roomCategoryUpd.setDescription("New room description");

        try {
            roomCategoryDAO.update(roomCategoryUpd, "en");
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        RoomCategory roomCategoryExp;
        try {
            roomCategoryExp = roomCategoryDAO.findAll("en").get(0);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals("New room category", roomCategoryExp.getName());
        assertEquals("New room description", roomCategoryExp.getDescription());

    }

    @Test
    void updateShouldThrowDaoExceptionOnDBError() {
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();
        roomCategoryDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> roomCategoryDAO.update(null, "en"));
    }

    @Test
    void createShouldProperCreateANDInsertValue() {
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();
        roomCategoryDAO.setConnection(con);

        RoomCategory roomCategory = new RoomCategory();
        roomCategory.setName("Name");
        roomCategory.setDescription("Description");

        try {
            roomCategoryDAO.create(roomCategory);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(6, roomCategory.getId());
    }


    @Test
    void createShouldThrowDaoExceptionOnDBError() {
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();
        roomCategoryDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> roomCategoryDAO.create(null));
    }


    @Test
    void unsupportedMethodsMustThrowsException() {
        RoomCategoryDAO roomCategoryDAO = new PostgresRoomCategoryDAO();
        roomCategoryDAO.setConnection(con);

        assertThrows(UnsupportedOperationException.class, roomCategoryDAO::findAll);
        assertThrows(UnsupportedOperationException.class, () -> roomCategoryDAO.find(1));
        assertThrows(UnsupportedOperationException.class, () -> roomCategoryDAO.find(1, "en"));
        assertThrows(UnsupportedOperationException.class, () -> roomCategoryDAO.update( null));
        assertThrows(UnsupportedOperationException.class, () -> roomCategoryDAO.delete(null));
        assertThrows(UnsupportedOperationException.class, () -> roomCategoryDAO.insert(null));
    }

}