package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Room;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RoomDAO {

	void insert(Room room);

	Room find(long roomId);
	List<Room> findByReservation(long reservationId, String locale);

	void update(Room room, String locale);
	void delete(int roomId);
	List<Room> findALL(String locale);
	List<Room> findFreeRooms(LocalDate checkin, LocalDate checkout, String locale);
    Map<String, Room> findByIdGroupByLocale(long roomId);
    void addImage(long roomId, String path);
    void deleteImage(long roomId, String path);
    void deleteById(long roomId);
	void create(Room room);
}
