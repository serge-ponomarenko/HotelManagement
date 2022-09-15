package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Request;

import java.util.List;

public interface RequestDAO {

    void insert(Request request);

    List<Request> findAllPending(String locale);

    Request find(long requestId, String locale);

    void updateReservation(Request request);
}