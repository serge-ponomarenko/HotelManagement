package ua.cc.spon.db.dao.postgres;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.cc.spon.db.dao.LocaleDAO;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.exception.IllegalPasswordException;
import ua.cc.spon.exception.UserIsAlreadyRegisteredException;
import ua.cc.spon.util.BcryptDecoder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sergiy Ponomarenko
 */
class PostgresUserDAOTest {

    Connection con, emptyCon;

    @BeforeEach
    void setUp() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:h2:~/hotel_db;MODE=PostgreSQL;INIT=" +
                            "RUNSCRIPT FROM 'classpath:locales_init.sql'\\;" +
                            "RUNSCRIPT FROM 'classpath:users_init.sql'\\;" +
                            "");
            emptyCon = DriverManager.getConnection(
                    "jdbc:h2:~/bad_db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findByIdShouldReturnProperUserValue() {
        UserDAO userDAO = new PostgresUserDAO();
        userDAO.setConnection(con);

        User user;
        try {
            user = userDAO.find(1);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(1, user.getId());
        assertEquals("pat_mair@gmail.com", user.getEmail());
        assertEquals("Patryk", user.getFirstName());
        assertEquals("Mair", user.getLastName());
        assertEquals("$2a$08$yvuqGKWCJ.I1/gttH/4KGuQfyu2Kos94HdcvhswQlZCAGvL3xtRxe", user.getPassword());
        assertEquals(User.Role.USER, user.getRole());
        assertEquals(Timestamp.valueOf("2022-09-21 14:19:19"), user.getRegisteredDate());
    }

    @Test
    void findByIdShouldReturnNullIfNotFound() {
        UserDAO userDAO = new PostgresUserDAO();
        userDAO.setConnection(con);

        User user;
        try {
            user = userDAO.find(999);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertNull(user);
    }

    @Test
    void findByIdShouldThrowDaoExceptionOnDBError() {
        UserDAO userDAO = new PostgresUserDAO();
        userDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> userDAO.find(1));
    }

    @Test
    void findByEmailAndPasswordShouldReturnProperUser() {
        UserDAO userDAO = new PostgresUserDAO();
        userDAO.setConnection(con);

        User user;
        try {
            user = userDAO.findByEmailAndPassword("pat_mair@gmail.com", "qwertyuiop123");
        } catch (DaoException | IllegalPasswordException e) {
            throw new RuntimeException(e);
        }

        assertEquals(1, user.getId());
        assertEquals("pat_mair@gmail.com", user.getEmail());
        assertEquals("Patryk", user.getFirstName());
        assertEquals("Mair", user.getLastName());
        assertEquals("$2a$08$yvuqGKWCJ.I1/gttH/4KGuQfyu2Kos94HdcvhswQlZCAGvL3xtRxe", user.getPassword());
        assertEquals(User.Role.USER, user.getRole());
        assertEquals(Timestamp.valueOf("2022-09-21 14:19:19"), user.getRegisteredDate());
    }

    @Test
    void findByEmailAndPasswordShouldReturnNullIfUserNotFound() {
        UserDAO userDAO = new PostgresUserDAO();
        userDAO.setConnection(con);

        User user;
        try {
            user = userDAO.findByEmailAndPassword("12345@gmail.com", "12345");
        } catch (DaoException | IllegalPasswordException e) {
            throw new RuntimeException(e);
        }

        assertNull(user);
    }

    @Test
    void findByEmailAndPasswordShouldThrowExceptionIfWrongPassword() {
        UserDAO userDAO = new PostgresUserDAO();
        userDAO.setConnection(con);

        assertThrows(IllegalPasswordException.class,
                () -> userDAO.findByEmailAndPassword("pat_mair@gmail.com", "111"));
    }

    @Test
    void findByEmailAndPasswordShouldThrowDaoExceptionOnDBError() {
        UserDAO userDAO = new PostgresUserDAO();
        userDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> userDAO.findByEmailAndPassword("pat_mair@gmail.com", "111"));
    }

    @Test
    void insertUserShouldProperInsertObject() {
        UserDAO userDAO = new PostgresUserDAO();
        userDAO.setConnection(con);

        User user = new User();
        user.setFirstName("First");
        user.setLastName("Last");
        user.setPassword("asdfghjk123");
        user.setRole(User.Role.ADMINISTRATOR);
        user.setEmail("email@asd.asd");

        try {
            userDAO.insertUser(user);
        } catch (DaoException | UserIsAlreadyRegisteredException e) {
            throw new RuntimeException(e);
        }

        User userExp;
        try {
            userExp = userDAO.find(user.getId());
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals("email@asd.asd", userExp.getEmail());
        assertEquals("First", userExp.getFirstName());
        assertEquals("Last", userExp.getLastName());
        assertTrue(BcryptDecoder.verify("asdfghjk123", userExp.getPassword()));
        assertEquals(User.Role.ADMINISTRATOR, userExp.getRole());

    }

    @Test
    void insertUserShouldThrowsExceptionIfUserAlreadyExist() {
        UserDAO userDAO = new PostgresUserDAO();
        userDAO.setConnection(con);

        User user = new User();
        user.setEmail("f.seymour@gmail.com");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setPassword("asdfghjk123");
        user.setRole(User.Role.ADMINISTRATOR);

        assertThrows(UserIsAlreadyRegisteredException.class, () -> userDAO.insertUser(user));

    }

    @Test
    void insertUserShouldThrowDaoExceptionOnDBError() {
        UserDAO userDAO = new PostgresUserDAO();
        userDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> userDAO.insertUser(new User()));
    }

    @Test
    void findAllShouldReturnProperList() {
        UserDAO userDAO = new PostgresUserDAO();
        userDAO.setConnection(con);

        List<User> users;
        try {
            users = userDAO.findAll();
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals(3, users.size());

        assertEquals(1, users.get(0).getId());
        assertEquals("pat_mair@gmail.com", users.get(0).getEmail());
        assertEquals("Patryk", users.get(0).getFirstName());
        assertEquals("Mair", users.get(0).getLastName());
        assertEquals("$2a$08$yvuqGKWCJ.I1/gttH/4KGuQfyu2Kos94HdcvhswQlZCAGvL3xtRxe", users.get(0).getPassword());
        assertEquals(User.Role.USER, users.get(0).getRole());
        assertEquals(Timestamp.valueOf("2022-09-21 14:19:19"), users.get(0).getRegisteredDate());

        assertEquals("f.seymour@gmail.com", users.get(1).getEmail());
        assertEquals("s.stefaniv@gmail.com", users.get(2).getEmail());

    }

    @Test
    void findAllShouldThrowDaoExceptionOnDBError() {
        UserDAO userDAO = new PostgresUserDAO();
        userDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, userDAO::findAll);
    }


    @Test
    void updateShouldUpdateUserProper() {
        UserDAO userDAO = new PostgresUserDAO();
        userDAO.setConnection(con);

        User userUpd;
        try {
            userUpd = userDAO.find(1);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        userUpd.setEmail("new@email");
        userUpd.setRole(User.Role.USER);
        userUpd.setPassword("12345678");
        userUpd.setLastName("Last");
        userUpd.setFirstName("First");

        try {
            userDAO.update(userUpd);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        User userExp;
        try {
            userExp = userDAO.find(1);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        assertEquals("new@email", userExp.getEmail());
        assertEquals("First", userExp.getFirstName());
        assertEquals("Last", userExp.getLastName());
        assertTrue(BcryptDecoder.verify("12345678", userExp.getPassword()));
        assertEquals(User.Role.USER, userExp.getRole());
    }


    @Test
    void updateShouldThrowDaoExceptionOnDBError() {
        UserDAO userDAO = new PostgresUserDAO();
        userDAO.setConnection(emptyCon);
        assertThrows(DaoException.class, () -> userDAO.update(new User()));
    }


    @Test
    void unsupportedMethodsMustThrowsException() {
        UserDAO userDAO = new PostgresUserDAO();
        userDAO.setConnection(con);

        assertThrows(UnsupportedOperationException.class, () -> userDAO.update( null, "en"));
        assertThrows(UnsupportedOperationException.class, () -> userDAO.find(1, "en"));
        assertThrows(UnsupportedOperationException.class, () -> userDAO.delete(1));
        assertThrows(UnsupportedOperationException.class, () -> userDAO.findAll("en"));
        assertThrows(UnsupportedOperationException.class, () -> userDAO.insert( null));
        assertThrows(UnsupportedOperationException.class, () -> userDAO.delete(null));
    }
}