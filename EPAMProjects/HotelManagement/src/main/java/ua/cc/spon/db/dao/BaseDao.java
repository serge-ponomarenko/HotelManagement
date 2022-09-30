package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Entity;
import ua.cc.spon.exception.DaoException;

import java.util.List;

public interface BaseDao<T extends Entity> {

    List<T> findAll() throws DaoException;

    List<T> findAll(String locale) throws DaoException;

    T find(int id) throws DaoException;

    T find(int id, String locale) throws DaoException;

    void insert(T t) throws DaoException;

    void update(T t) throws DaoException;

    void update(T t, String locale) throws DaoException;

    void delete(int id) throws DaoException;

    void delete(T t) throws DaoException;

}

