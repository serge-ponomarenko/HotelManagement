package ua.cc.spon.db.dao.postgres;

import ua.cc.spon.db.DataSource;
import ua.cc.spon.db.dao.RoomCategoryDAO;
import ua.cc.spon.db.entity.RoomCategory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresRoomCategoryDAO implements RoomCategoryDAO {

    private static final String FIND_ALL_ROOM_CATEGORIES =
            "SELECT c.category_id, c_tr.name, c_tr.description " +
                    "FROM categories c " +
                    "INNER JOIN categories_tr c_tr ON c_tr.category_id = c.category_id AND c_tr.locale = ?";

    @Override
    public List<RoomCategory> findALL(String locale) {
        List<RoomCategory> result = new ArrayList<>();

        try (Connection con = DataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(FIND_ALL_ROOM_CATEGORIES)) {

            statement.setString(1, locale);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    RoomCategory roomCategory = new RoomCategory();

                    roomCategory.setId(resultSet.getInt(1));
                    roomCategory.setName(resultSet.getString(2));
                    roomCategory.setDescription(resultSet.getString(3));

                    result.add(roomCategory);

                }
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
