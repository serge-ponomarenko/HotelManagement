package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.DataSource;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.ReservationDAO;
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.RoomHasAlreadyBookedException;
import ua.cc.spon.service.RequestParametersValidatorService;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@WebServlet({"/setMaintenanceAction"})
public class MaintenanceController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        RoomDAO roomDAO = factory.getRoomDAO();
        ReservationDAO reservationDAO = factory.getReservationDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();
        User user = ((User) req.getSession().getAttribute("user"));

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        LocalDate checkinDate;
        LocalDate checkoutDate;
        int roomId;

        try {
            checkinDate = validator.validateAndGetDate("checkin-date", new IllegalArgumentException());
            checkoutDate = validator.validateAndGetDate("checkout-date", new IllegalArgumentException());
            roomId = validator.validateAndGetInt("room", new IllegalArgumentException());
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect("hotelOccupancyAction");
            return;
        }

        int nights = (int) (checkoutDate.toEpochDay() - checkinDate.toEpochDay());

        try (Connection con = DataSource.getConnection()) {

            Room room;

            con.setAutoCommit(false);

            try {
                List<Room> rooms = roomDAO.findFreeRooms(con, checkinDate, checkoutDate, locale);

                room = rooms.stream()
                        .filter(r -> r.getId() == roomId)
                        .findAny()
                        .orElseThrow(RoomHasAlreadyBookedException::new);

                Reservation reservation = new Reservation();
                reservation.setCheckinDate(checkinDate);
                reservation.setCheckoutDate(checkoutDate);
                reservation.setRooms(List.of(room));
                reservation.setUser(user);
                reservation.setStatus(Reservation.Status.UNAVAILABLE);
                reservation.setPersons(1);
                reservation.setPrice(BigDecimal.ZERO);

                reservationDAO.insert(con, reservation);

                con.commit();

            } catch (RoomHasAlreadyBookedException e) {
                con.rollback();
                req.getSession().setAttribute("fail_message", "error." + e.getMessage());
                resp.sendRedirect("hotelOccupancyAction");
                return;
            }

        } catch (SQLException e) {
            req.getSession().setAttribute("fail_message", "error.someDBError");
            resp.sendRedirect("hotelOccupancyAction");
            return;
        }

        resp.sendRedirect("hotelOccupancyAction");

    }

}
