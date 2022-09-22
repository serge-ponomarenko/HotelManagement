package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.DBException;
import ua.cc.spon.exception.IllegalPasswordException;
import ua.cc.spon.exception.NoUserFoundException;
import ua.cc.spon.exception.UserIsAlreadyRegisteredException;

import java.util.List;

public interface UserDAO {

	void insert(User user) throws UserIsAlreadyRegisteredException;

	User findByEmailAndPassword(String email, String password) throws NoUserFoundException, IllegalPasswordException;
	
	User find(long userid) throws NoUserFoundException;

	void update(User user) throws DBException;
	
	void delete(int userId);
	
	List<User> findALL();

}
