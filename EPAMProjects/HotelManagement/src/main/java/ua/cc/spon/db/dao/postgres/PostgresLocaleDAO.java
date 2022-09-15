package ua.cc.spon.db.dao.postgres;

import ua.cc.spon.db.DataSource;
import ua.cc.spon.db.dao.LocaleDAO;
import ua.cc.spon.db.entity.Locale;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class PostgresLocaleDAO implements LocaleDAO {

    private static final String FIND_ALL_LOCALES =
            "SELECT locale_id, name, icon_path, full_name FROM locales";


    @Override
    public Map<String, Locale> findALL() {
        Map<String, Locale> locales = new HashMap<>();
        try (Connection con = DataSource.getConnection();
             Statement stmt = con.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(FIND_ALL_LOCALES)) {
                while (rs.next()) {
                    Locale locale = extractLocale(rs);
                    locales.put(locale.getName(), locale);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locales;
    }

    private Locale extractLocale(ResultSet rs) throws SQLException {
        Locale locale = new Locale();
        locale.setId(rs.getInt("locale_id"));
        locale.setName(rs.getString("name"));
        locale.setIconPath(rs.getString("icon_path"));
        locale.setFullName(rs.getString("full_name"));
        return locale;
    }

}
