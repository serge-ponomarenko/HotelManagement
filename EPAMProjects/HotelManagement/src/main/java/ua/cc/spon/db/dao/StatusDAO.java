package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Reservation;

import java.util.List;
import java.util.Map;

public interface StatusDAO {

	List<Reservation.Status> findALL();

	Map<Reservation.Status, String> findNames(String locale);

}
