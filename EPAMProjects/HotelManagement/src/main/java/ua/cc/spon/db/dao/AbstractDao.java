package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Entity;

import java.sql.Connection;

public abstract class AbstractDao<T extends Entity> implements BaseDao<T> {

    protected Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

}
