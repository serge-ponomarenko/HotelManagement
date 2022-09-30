package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.exception.IllegalPasswordException;
import ua.cc.spon.exception.UserIsAlreadyRegisteredException;

/**
 * Class represents DAO layer for {@link User} class.
 *
 * @author Sergiy Ponomarenko
 */
public abstract class UserDAO extends AbstractDao<User> {
    /**
     * @param user {@link User} object that will be inserted into DB.
     * @throws UserIsAlreadyRegisteredException if User is already registered in DB.
     * @throws DaoException                     on {@link java.sql.SQLException}
     */
    public abstract void insertUser(User user) throws UserIsAlreadyRegisteredException, DaoException;

    /**
     * Method returns {@link User} if provided credentials are correct.
     * Note: Field password in returned object is hashed value of password.
     *
     * @param email    User login to find in DB.
     * @param password User password to find in DB.
     * @return {@link User} object if login present in DB and password match.
     * Null if user not found.
     * @throws IllegalPasswordException If provided password doesn't match.
     * @throws DaoException             on {@link java.sql.SQLException}
     */
    public abstract User findByEmailAndPassword(String email, String password) throws IllegalPasswordException, DaoException;
}
