package ua.cc.spon.db.dao.postgres;

import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.exception.IllegalPasswordException;
import ua.cc.spon.exception.UserIsAlreadyRegisteredException;
import ua.cc.spon.util.BcryptDecoder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostgresUserDAO extends UserDAO {

    private static final String FIND_ALL_USERS =
            "SELECT user_id, email, first_name, last_name, hash_password, created_at, ur.name FROM users u " +
                    "INNER JOIN user_roles ur ON u.role_id = ur.role_id";

    private static final String INSERT_USER =
            "INSERT INTO users (email, hash_password, first_name, last_name, role_id) " +
                    "VALUES(?, ?, ?, ?, (SELECT role_id FROM user_roles where name = ?))";

    private static final String FIND_USER_BY_EMAIL_PASSWORD =
            "SELECT user_id, email, hash_password, first_name, last_name, created_at, " +
                    "ur.name FROM users u " +
                    "INNER JOIN user_roles ur ON u.role_id = ur.role_id " +
                    "WHERE email = ?";

    private static final String FIND_USER_BY_ID =
            "SELECT user_id, email, hash_password, first_name, last_name, created_at," +
                    "ur.name FROM users u " +
                    "INNER JOIN user_roles ur ON u.role_id = ur.role_id " +
                    "WHERE user_id = ?";

    private static final String UPDATE_USER =
            "UPDATE users SET " +
                    "email = ?, first_name = ?, last_name = ?, " +
                    "role_id = (SELECT role_id FROM user_roles where name = ?), " +
                    "hash_password = ? " +
                    "WHERE user_id = ?";


    @Override
    public void insertUser(User user) throws UserIsAlreadyRegisteredException, DaoException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_USER,
                Statement.RETURN_GENERATED_KEYS)) {
            int k = 0;
            statement.setString(++k, user.getEmail());
            String password = BcryptDecoder.generateHash(user.getPassword());
            statement.setString(++k, password);
            statement.setString(++k, user.getFirstName());
            statement.setString(++k, user.getLastName());
            statement.setString(++k, user.getRole().toString());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) throw new UserIsAlreadyRegisteredException();
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public User findByEmailAndPassword(String email, String password) throws IllegalPasswordException, DaoException {
        User user = null;

        try (PreparedStatement statement = connection.prepareStatement(FIND_USER_BY_EMAIL_PASSWORD)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String hashPassword = resultSet.getString("hash_password");
                    if (!BcryptDecoder.verify(password, hashPassword))
                        throw new IllegalPasswordException(); // check if password is correct

                    user = extractUser(resultSet);

                }
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

        return user;
    }

    @Override
    public User find(int userid) throws DaoException {
        User user = null;

        try (PreparedStatement statement = connection.prepareStatement(FIND_USER_BY_ID)) {

            statement.setInt(1, userid);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) user = extractUser(resultSet);
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
        return user;
    }

    @Override
    public void update(User user) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_USER)) {

            User userCheck = find(user.getId());
            String password = user.getPassword();
            if (!password.equals(userCheck.getPassword()))
                password = BcryptDecoder.generateHash(password);

            statement.setString(1, user.getEmail());
            statement.setString(2, user.getFirstName());
            statement.setString(3, user.getLastName());
            statement.setString(4, user.getRole().toString());
            statement.setString(5, password);
            statement.setInt(6, user.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public List<User> findAll() throws DaoException {
        List<User> users = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(FIND_ALL_USERS)) {

            while (rs.next()) {
                users.add(extractUser(rs));
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
        return users;

    }

    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("hash_password"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setRole(User.Role.valueOf(rs.getString("name")));
        user.setRegisteredDate(rs.getTimestamp("created_at"));
        return user;
    }

    @Override
    public void update(User user, String locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public User find(int id, String locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(int userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<User> findAll(String locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(User user) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(User user) throws DaoException {
        throw new UnsupportedOperationException();
    }


}
