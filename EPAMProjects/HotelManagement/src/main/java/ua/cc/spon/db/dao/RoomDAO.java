package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Room;
import ua.cc.spon.exception.DBException;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RoomDAO {

	void insert(Room room);

	Room find(long roomId);
	List<Room> findByReservation(long reservationId, String locale);

	void update(Room room, String locale) throws DBException;
	void delete(int roomId);
	List<Room> findALL(String locale);
	List<Room> findFreeRooms(LocalDate checkin, LocalDate checkout, String locale);
	List<Room> findRoomsWithoutReservation(String locale);
    Map<String, Room> findByIdGroupByLocale(long roomId);
    void addImage(long roomId, String path);
    void deleteImage(long roomId, String path);
    void deleteById(long roomId) throws DBException;
	void create(Room room);
    List<Room> findFreeRooms(Connection con, LocalDate checkin, LocalDate checkout, String locale);
}
