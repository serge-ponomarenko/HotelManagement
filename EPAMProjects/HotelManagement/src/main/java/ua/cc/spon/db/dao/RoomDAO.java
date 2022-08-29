package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Room;

import java.util.List;

public interface RoomDAO {

	void insert(Room room);

	Room find(long roomId);

	void update(Room room);
	
	void delete(int roomId);
	
	List<Room> findALL(String locale);

}
