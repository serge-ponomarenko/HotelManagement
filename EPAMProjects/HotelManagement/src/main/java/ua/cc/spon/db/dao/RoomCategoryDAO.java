package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.RoomCategory;

import java.util.List;

public interface RoomCategoryDAO {

	List<RoomCategory> findALL(String locale);

}
