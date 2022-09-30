package ua.cc.spon.db.dao.postgres;

import ua.cc.spon.db.dao.LocaleDAO;
import ua.cc.spon.db.entity.Locale;
import ua.cc.spon.exception.DaoException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgresLocaleDAO extends LocaleDAO {

    private static final String FIND_ALL_LOCALES =
            "SELECT locale_id, name, icon_path, full_name FROM locales";

    @Override
    public Map<String, Locale> findAllMapByName() throws DaoException {
        Map<String, Locale> locales = new HashMap<>();
        try (Statement stmt = connection.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(FIND_ALL_LOCALES)) {
                while (rs.next()) {
                    Locale locale = extractLocale(rs);
                    locales.put(locale.getName(), locale);
                }
            }

        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
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

    @Override
    public List<Locale> findAll() throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Locale> findAll(String locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Locale find(int id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Locale find(int id, String locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(Locale locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Locale locale) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Locale locale, String locale2) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(int id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Locale locale) throws DaoException {
        throw new UnsupportedOperationException();
    }
}
