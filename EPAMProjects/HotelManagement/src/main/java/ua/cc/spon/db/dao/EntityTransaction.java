package ua.cc.spon.db.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Class realizes Transactions for AbstractDao objects.
 *
 * @author Sergiy Ponomarenko
 */
public class EntityTransaction {

    static final Logger LOGGER = LoggerFactory.getLogger(EntityTransaction.class);

    private Connection connection;

    public EntityTransaction(Connection connection) {
        this.connection = connection;
    }

    public EntityTransaction() {
    }

    /**
     * Method retrieve and sets the connection to provided {@link AbstractDao} object.
     *
     * @param dao {@link AbstractDao} object to init
     */
    public void init(AbstractDao dao) {
        if (connection == null) {
            establishConnection();
        }
        dao.setConnection(connection);
    }

    /**
     * Closes the connection
     */
    public void end() {
        if (connection != null) {
            closeConnection();
        }
    }

    /**
     * Method creates and sets connections to provided {@link AbstractDao} object and
     * initialize transaction.
     *
     * @param dao  {@link AbstractDao} object
     * @param daos {@link AbstractDao} objects
     */
    public void initTransaction(AbstractDao dao, AbstractDao... daos) {
        if (connection == null) {
            establishConnection();
        }
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            LOGGER.warn("Cant' set Auto commit OFF");
        }

        dao.setConnection(connection);
        for (AbstractDao daoElement : daos) {
            daoElement.setConnection(connection);
        }
    }

    /**
     * Method closes opened connection and sets AutoCommit mode to true.
     */
    public void endTransaction() {
        if (connection == null) return;
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            LOGGER.warn("Cant' set Auto commit ON");
        }
        closeConnection();
    }

    /**
     * Method commits current transaction.
     */
    public void commit() {
        if (connection == null) return;
        try {
            connection.commit();
        } catch (SQLException e) {
            LOGGER.warn("Cant' Commit");
        }
    }

    /**
     * Method rollbacks current transaction.
     */
    public void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            LOGGER.warn("Cant' Rollback");
        }
    }

    private void establishConnection() {
        try {
            if (connection == null) connection = DataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.warn("Can't establish connection");
        }
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.warn("Can't close connection");
        }
        connection = null;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

}
