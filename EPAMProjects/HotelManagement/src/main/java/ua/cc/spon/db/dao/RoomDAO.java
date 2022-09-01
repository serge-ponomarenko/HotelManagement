package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Room;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface RoomDAO {

	void insert(Room room);

	Room find(long roomId);
	List<Room> findByReservation(long reservationId, String locale);

	void update(Room room);
	
	void delete(int roomId);
	
	List<Room> findALL(String locale);

	List<Room> findFreeRooms(LocalDate checkin, LocalDate checkout, String locale);

}
