package ua.cc.spon.db.dao.postgres;

import ua.cc.spon.db.DataSource;
import ua.cc.spon.db.dao.RoomCategoryDAO;
import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.exception.DBException;
import ua.cc.spon.exception.UserIsAlreadyRegisteredException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgresRoomCategoryDAO implements RoomCategoryDAO {

    private static final String FIND_ALL_ROOM_CATEGORIES =
            "SELECT c.category_id, c_tr.name, c_tr.description, c.created_at " +
                    "FROM categories c " +
                    "INNER JOIN categories_tr c_tr ON c_tr.category_id = c.category_id " +
                    "INNER JOIN locales l USING(locale_id) " +
                    "WHERE l.name = ?";

    private static final String FIND_ALL_ROOM_CATEGORIES_BY_ID_GROUP_BY_LOCALE =
            "SELECT c.category_id, c_tr.name, c_tr.description, c.created_at, l.name " +
                    "FROM categories c " +
                    "INNER JOIN categories_tr c_tr ON c_tr.category_id = c.category_id " +
                    "INNER JOIN locales l USING(locale_id) " +
                    "WHERE c.category_id = ?";

    private static final String FIND_ALL_FOR_REQUESTS =
            "SELECT c.category_id, c_tr.name, c_tr.description, c.created_at " +
                    "FROM categories c " +
                    "INNER JOIN categories_tr c_tr ON c_tr.category_id = c.category_id " +
                    "INNER JOIN locales l USING(locale_id) " +
                    "INNER JOIN reservation_requests_categories rrc on c.category_id = rrc.category_id " +
                    "WHERE l.name = ? AND rrc.reservation_request_id = ?";

    private static final String UPDATE_CATEGORY = "UPDATE categories_tr SET " +
            "name = ?, description = ? " +
            "WHERE category_id = ? AND categories_tr.locale_id = (SELECT locales.locale_id FROM locales WHERE locales.name = ?)";
    
    private static final String INSERT_CATEGORY = "INSERT INTO categories DEFAULT VALUES";

    private static final String DELETE_CATEGORY = "DELETE FROM categories WHERE category_id = ?";

    private static final String INSERT_CATEGORY_EMPTY_TR =
            "INSERT INTO categories_tr(category_id, locale_id, name, description) " +
            "(SELECT ?, l.locale_id, ?, ? FROM locales l)";


    @Override
    public void deleteById(long categoryId) throws DBException {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_CATEGORY)) {

            statement.setLong(1, categoryId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting category failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public void create(RoomCategory roomCategory) {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement1 = con.prepareStatement(INSERT_CATEGORY, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement statement2 = con.prepareStatement(INSERT_CATEGORY_EMPTY_TR)) {

            int affectedRows = statement1.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating category failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement1.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    roomCategory.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating category failed, no ID obtained.");
                }
            }

            statement2.setLong(1, roomCategory.getId());
            statement2.setString(2, roomCategory.getName());
            statement2.setString(3, roomCategory.getDescription());

            affectedRows = statement2.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating category failed, no rows affected.");
            }

        } catch (SQLException e) {
            // TODO: 08.09.2022
        }
    }

    @Override
    public void update(RoomCategory roomCategory, String locale) {
        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_CATEGORY)) {

            statement.setString(1, roomCategory.getName());
            statement.setString(2, roomCategory.getDescription());
            statement.setLong(3, roomCategory.getId());
            statement.setString(4, locale);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating category failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(); // TODO: 07.09.2022
        }
    }

    @Override
    public Map<String, RoomCategory> findByIdGroupByLocale(long categoryId) {
        Map<String, RoomCategory> result = new HashMap<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_ALL_ROOM_CATEGORIES_BY_ID_GROUP_BY_LOCALE)) {

            statement.setLong(1, categoryId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    RoomCategory roomCategory = new RoomCategory();

                    roomCategory.setId(resultSet.getLong(1));
                    roomCategory.setName(resultSet.getString(2));
                    roomCategory.setDescription(resultSet.getString(3));
                    roomCategory.setCreationDate(resultSet.getTimestamp(4));

                    String locale = resultSet.getString(5);

                    result.put(locale, roomCategory);

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<RoomCategory> findAllForRequest(long requestId, String locale) {
        List<RoomCategory> result = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_ALL_FOR_REQUESTS)) {

            statement.setString(1, locale);
            statement.setLong(2, requestId);

            fillCategories(result, statement);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void fillCategories(List<RoomCategory> result, PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                RoomCategory roomCategory = new RoomCategory();

                roomCategory.setId(resultSet.getInt(1));
                roomCategory.setName(resultSet.getString(2));
                roomCategory.setDescription(resultSet.getString(3));
                roomCategory.setCreationDate(resultSet.getTimestamp(4));

                result.add(roomCategory);

            }
        }
    }

    @Override
    public List<RoomCategory> findALL(String locale) {
        List<RoomCategory> result = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_ALL_ROOM_CATEGORIES)) {

            statement.setString(1, locale);

            fillCategories(result, statement);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
