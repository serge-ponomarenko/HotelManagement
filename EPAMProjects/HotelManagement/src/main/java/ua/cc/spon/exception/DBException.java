package ua.cc.spon.exception;

import java.sql.SQLException;

public class DBException extends Exception {
    public DBException(SQLException e) {
        super(e);
    }
}
