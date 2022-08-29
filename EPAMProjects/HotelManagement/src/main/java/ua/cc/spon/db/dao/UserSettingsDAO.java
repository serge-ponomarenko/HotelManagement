package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.UserSettings;

public interface UserSettingsDAO {

	void insert(UserSettings userSettings);

	UserSettings findByUserId(long userId);

	UserSettings find(long userSettingsId);

	void update(UserSettings user);
	
	void delete(long userSettingsId);

	UserSettings findByHash(String userHash);
}
