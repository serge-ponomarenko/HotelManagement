package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Request;
import ua.cc.spon.exception.DBException;

import java.util.List;

public interface RequestDAO {

    void insert(Request request);

    List<Request> findAllPending(String locale);

    Request find(long requestId, String locale);

    void updateReservation(Request request);

    void deleteById(long requestId) throws DBException;
}