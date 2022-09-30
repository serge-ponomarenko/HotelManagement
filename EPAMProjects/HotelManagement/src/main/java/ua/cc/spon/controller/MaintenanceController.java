package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.EntityTransaction;
import ua.cc.spon.db.dao.ReservationDAO;
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.exception.RoomHasAlreadyBookedException;
import ua.cc.spon.service.RequestParametersValidatorService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@WebServlet({"/setMaintenanceAction"})
public class MaintenanceController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceController.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

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
            LOGGER.warn(e.getMessage());
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect("hotelOccupancyAction");
            return;
        }

        transaction.initTransaction(roomDAO, reservationDAO);

        try {

            Room room;

            room = Optional.ofNullable(roomDAO.getFreeRoomById(roomId, checkinDate, checkoutDate, locale))
                    .orElseThrow(RoomHasAlreadyBookedException::new);

            Reservation reservation = new Reservation();
            reservation.setCheckinDate(checkinDate);
            reservation.setCheckoutDate(checkoutDate);
            reservation.setRooms(List.of(room));
            reservation.setUser(user);
            reservation.setStatus(Reservation.Status.UNAVAILABLE);
            reservation.setPersons(1);
            reservation.setPrice(BigDecimal.ZERO);

            reservationDAO.insert(reservation);

            transaction.commit();

            LOGGER.info("Maintenance for Room #{} from {} to {} set", room.getId(), checkinDate, checkoutDate);

        } catch (RoomHasAlreadyBookedException e) {
            LOGGER.warn(e.getMessage());
            transaction.rollback();
            req.getSession().setAttribute("fail_message", "error." + e.getMessage());
        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            transaction.rollback();
            req.getSession().setAttribute("fail_message", "error.someDBError");
        } finally {
            transaction.endTransaction();
        }

        resp.sendRedirect("hotelOccupancyAction");

    }

}
