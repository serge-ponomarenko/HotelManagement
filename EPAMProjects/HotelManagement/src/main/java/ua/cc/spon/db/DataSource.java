package ua.cc.spon.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ua.cc.spon.util.HotelHelper;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Data Source class. Configures Hikari Pool Connection.
 * Retrieves configuration parameters from "app.properties".
 */
public class DataSource {

    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        String connectionUrl = HotelHelper.getProperty("db.url");
        String dbUser = HotelHelper.getProperty("db.user");
        String dbPassword = HotelHelper.getProperty("db.password");
        config.setJdbcUrl(connectionUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setDriverClassName(HotelHelper.getProperty("db.driver"));
        ds = new HikariDataSource(config);
    }

    private DataSource() {    }

    /**
     * @return {@link Connection} from pool.
     * @throws SQLException if couldn't retrieve connection from pool.
     */
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
