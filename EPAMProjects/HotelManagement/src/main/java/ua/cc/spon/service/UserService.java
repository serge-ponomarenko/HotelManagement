package ua.cc.spon.service;

import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.IllegalPasswordException;
import ua.cc.spon.exception.NoUserFoundException;
import ua.cc.spon.exception.UserIsAlreadyRegisteredException;

import java.util.List;

public interface UserService {

    void insert(User user) throws UserIsAlreadyRegisteredException;

    User findByEmailAndPassword(String email, String password) throws NoUserFoundException, IllegalPasswordException;

    User find(long userid);

    void update(User user);

    void delete(int userId);

    List<User> findALL();

}
