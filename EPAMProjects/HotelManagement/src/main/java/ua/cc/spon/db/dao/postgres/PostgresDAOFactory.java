package ua.cc.spon.db.dao.postgres;


import ua.cc.spon.db.dao.*;

public class PostgresDAOFactory extends DAOFactory {
    @Override
    public UserDAO getUserDAO() {
        return new PostgresUserDAO();
    }

    @Override
    public UserSettingsDAO getUserSettingsDAO() {
        return new PostgresUserSettingsDAO();
    }

    @Override
    public RoomDAO getRoomDAO() {
        return new PostgresRoomDAO();
    }

    @Override
    public RoomCategoryDAO getRoomCategoryDAO() {
        return new PostgresRoomCategoryDAO();
    }

    @Override
    public ReservationDAO getReservationDAO() {
        return new PostgresReservationDAO();
    }

    @Override
    public StatusDAO getStatusDAO() {
        return new PostgresStatusDAO();
    }

    @Override
    public RequestDAO getRequestDAO() {
        return new PostgresRequestDAO();
    }

    @Override
    public LocaleDAO getLocaleDAO() {
        return new PostgresLocaleDAO();
    }
}
