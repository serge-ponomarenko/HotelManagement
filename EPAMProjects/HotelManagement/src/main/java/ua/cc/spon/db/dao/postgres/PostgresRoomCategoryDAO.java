package ua.cc.spon.db.dao.postgres;

import ua.cc.spon.db.dao.RoomCategoryDAO;
import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.exception.DaoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgresRoomCategoryDAO extends RoomCategoryDAO {

    private static final String FIND_ALL_ROOM_CATEGORIES =
            "SELECT c.category_id, c_tr.name, c_tr.description, c.created_at " +
                    "FROM categories c " +
                    "INNER JOIN categories_tr c_tr ON c_tr.category_id = c.category_id " +
                    "INNER JOIN locales l ON c_tr.locale_id = l.locale_id " +
                    "WHERE l.name = ?";

    private static final String FIND_ALL_ROOM_CATEGORIES_BY_ID_GROUP_BY_LOCALE =
            "SELECT c.category_id, c_tr.name AS tr_name, c_tr.description, c.created_at, l.name AS l_name " +
                    "FROM categories c " +
                    "INNER JOIN categories_tr c_tr ON c_tr.category_id = c.category_id " +
                    "INNER JOIN locales l ON c_tr.locale_id = l.locale_id " +
                    "WHERE c.category_id = ?";

    private static final String FIND_ALL_FOR_REQUESTS =
            "SELECT c.category_id, c_tr.name, c_tr.description, c.created_at " +
                    "FROM categories c " +
                    "INNER JOIN categories_tr c_tr ON c_tr.category_id = c.category_id " +
                    "INNER JOIN locales l ON c_tr.locale_id = l.locale_id " +
                    "INNER JOIN reservation_requests_categories rrc on c.category_id = rrc.category_id " +
                    "WHERE l.name = ? AND rrc.reservation_request_id = ?";

    private static final String UPDATE_CATEGORY =
            "UPDATE categories_tr SET " +
                    "name = ?, description = ? " +
                    "WHERE category_id = ? AND categories_tr.locale_id = " +
                    "(SELECT locales.locale_id FROM locales WHERE locales.name = ?)";

    private static final String INSERT_CATEGORY =
            "INSERT INTO categories DEFAULT VALUES";

    private static final String DELETE_CATEGORY =
            "DELETE FROM categories WHERE category_id = ?";

    private static final String INSERT_CATEGORY_EMPTY_TR =
            "INSERT INTO categories_tr(category_id, locale_id, name, description) " +
                    "(SELECT ?, l.locale_id, ?, ? FROM locales l)";


    @Override
    public void delete(int categoryId) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_CATEGORY)) {

            statement.setInt(1, categoryId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting category failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public void update(RoomCategory roomCategory, String locale) throws DaoException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_CATEGORY)) {
            int k = 0;
            statement.setString(++k, roomCategory.getName());
            statement.setString(++k, roomCategory.getDescription());
            statement.setInt(++k, roomCategory.getId());
            statement.setString(++k, locale);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating category failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public Map<String, RoomCategory> findByIdGroupByLocale(int categoryId) throws DaoException {
        Map<String, RoomCategory> result = new HashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_ROOM_CATEGORIES_BY_ID_GROUP_BY_LOCALE)) {

            statement.setInt(1, categoryId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    RoomCategory roomCategory = new RoomCategory();

                    roomCategory.setId(resultSet.getInt("category_id"));
                    roomCategory.setName(resultSet.getString("tr_name"));
                    roomCategory.setDescription(resultSet.getString("description"));
                    roomCategory.setCreationDate(resultSet.getTimestamp("created_at"));

                    String locale = resultSet.getString("l_name");

                    result.put(locale, roomCategory);

                }
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

        return result;
    }

    @Override
    public List<RoomCategory> findAllForRequest(int requestId, String locale) throws DaoException {
        List<RoomCategory> result;

        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_FOR_REQUESTS)) {

            statement.setString(1, locale);
            statement.setInt(2, requestId);

            try (ResultSet resultSet = statement.executeQuery()) {
                result = fillCategories(resultSet);
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

        return result;
    }

    @Override
    public List<RoomCategory> findAll(String locale) throws DaoException {
        List<RoomCategory> result;

        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_ROOM_CATEGORIES)) {

            statement.setString(1, locale);

            try (ResultSet resultSet = statement.executeQuery()) {
                result = fillCategories(resultSet);
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }

        return result;
    }

    @Override
    public void create(RoomCategory roomCategory) throws DaoException {
        try (PreparedStatement statement1 = connection.prepareStatement(INSERT_CATEGORY, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement statement2 = connection.prepareStatement(INSERT_CATEGORY_EMPTY_TR)) {

            int affectedRows = statement1.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating category failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement1.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    roomCategory.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating category failed, no ID obtained.");
                }
            }
            int k = 0;
            statement2.setInt(++k, roomCategory.getId());
            statement2.setString(++k, roomCategory.getName());
            statement2.setString(++k, roomCategory.getDescription());

            affectedRows = statement2.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating category failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    private List<RoomCategory> fillCategories(ResultSet resultSet) throws SQLException {
        List<RoomCategory> result = new ArrayList<>();

        while (resultSet.next()) {
            RoomCategory roomCategory = new RoomCategory();

            roomCategory.setId(resultSet.getInt("category_id"));
            roomCategory.setName(resultSet.getString("name"));
            roomCategory.setDescription(resultSet.getString("description"));
            roomCategory.setCreationDate(resultSet.getTimestamp("created_at"));

            result.add(roomCategory);
        }

        return result;
    }

    @Override
    public List<RoomCategory> findAll() throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public RoomCategory find(int id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public RoomCategory find(int id, String locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(RoomCategory roomCategory) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(RoomCategory roomCategory) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(RoomCategory roomCategory) throws DaoException {
        throw new UnsupportedOperationException();
    }
}
