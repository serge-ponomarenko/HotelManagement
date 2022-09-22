package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DBException;

public interface UserSettingsDAO {

	void insert(UserSettings userSettings) throws DBException;

	UserSettings findByUserId(long userId) throws DBException;

	void update(UserSettings user) throws DBException;
	
	UserSettings findByHash(String userHash) throws DBException;
}
