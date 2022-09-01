package ua.cc.spon.db.dao.postgres;

import ua.cc.spon.db.DataSource;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.IllegalPasswordException;
import ua.cc.spon.exception.NoUserFoundException;
import ua.cc.spon.exception.UserIsAlreadyRegisteredException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresUserDAO implements UserDAO {

    private static final String FIND_ALL_USERS = "SELECT * FROM users";
    private static final String INSERT_USER = "INSERT INTO users (email, hash_password, first_name, last_name, role_id) " +
            "VALUES(?, crypt(?, gen_salt('bf', 8)), ?, ?, (SELECT role_id FROM user_roles where name = ?))";
    private static final String FIND_USER_BY_EMAIL_PASSWORD = "SELECT user_id, email, hash_password, first_name, last_name, " +
            "user_roles.name, (hash_password = crypt(?, hash_password)) as is_password_correct FROM users " +
            "INNER JOIN user_roles USING(role_id) " +
            "WHERE email = ?";
    private static final String FIND_USER_BY_ID = "SELECT user_id, email, hash_password, first_name, last_name, " +
            "user_roles.name FROM users " +
            "INNER JOIN user_roles USING(role_id) " +
            "WHERE user_id = ?";

    @Override
    public void insert(User user) throws UserIsAlreadyRegisteredException {

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getEmail());
            statement.setString(2, user.getHashPassword());
            statement.setString(3, user.getFirstName());
            statement.setString(4, user.getLastName());
            statement.setString(5, user.getRole().toString());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) throw new UserIsAlreadyRegisteredException();
        }

    }

    @Override
    public User findByEmailAndPassword(String email, String password) throws NoUserFoundException, IllegalPasswordException {
        User user = new User();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_USER_BY_EMAIL_PASSWORD)) {

            statement.setString(1, password);
            statement.setString(2, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    if (!resultSet.getBoolean(7)) throw new IllegalPasswordException(); // check if password is correct

                    fillUser(user, resultSet);

                } else {
                    throw new NoUserFoundException();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public User find(long userid) throws NoUserFoundException {
        User user = new User();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_USER_BY_ID)) {

            statement.setLong(1, userid);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    fillUser(user, resultSet);
                } else {
                    throw new NoUserFoundException();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    private void fillUser(User user, ResultSet resultSet) throws SQLException {
        user.setId(resultSet.getLong(1));
        user.setEmail(resultSet.getString(2));
        user.setHashPassword(resultSet.getString(3));
        user.setFirstName(resultSet.getString(4));
        user.setLastName(resultSet.getString(5));
        user.setRole(User.Role.valueOf(resultSet.getString(6)));
    }

    @Override
    public void update(User user) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(int userId) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<User> findALL() {
        List<User> users = new ArrayList<>();
        try (Connection con = DataSource.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(FIND_ALL_USERS);
            while (rs.next()) {
                users.add(extractUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setHashPassword(rs.getString("hash_password"));
        return user;
    }

}
