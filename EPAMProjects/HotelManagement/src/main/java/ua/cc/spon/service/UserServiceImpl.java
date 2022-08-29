package ua.cc.spon.service;

import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.IllegalPasswordException;
import ua.cc.spon.exception.NoUserFoundException;
import ua.cc.spon.exception.UserIsAlreadyRegisteredException;

import java.util.List;

public class UserServiceImpl implements UserService {

    @Override
    public void insert(User user) throws UserIsAlreadyRegisteredException {

    }

    @Override
    public User findByEmailAndPassword(String email, String password) throws NoUserFoundException, IllegalPasswordException {
        return null;
    }

    @Override
    public User find(long userid) {
        return null;
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(int userId) {

    }

    @Override
    public List<User> findALL() {
        return null;
    }
}
