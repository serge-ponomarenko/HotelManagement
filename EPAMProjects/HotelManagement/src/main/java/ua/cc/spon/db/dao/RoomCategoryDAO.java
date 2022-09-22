package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.exception.DBException;

import java.util.List;
import java.util.Map;

public interface RoomCategoryDAO {

	List<RoomCategory> findALL(String locale);
	List<RoomCategory> findAllForRequest(long requestId, String locale);

	Map<String, RoomCategory> findByIdGroupByLocale(long categoryId);

	void update(RoomCategory roomCategory, String locale);

	void create(RoomCategory roomCategory);

	void deleteById(long categoryId) throws DBException;
}
