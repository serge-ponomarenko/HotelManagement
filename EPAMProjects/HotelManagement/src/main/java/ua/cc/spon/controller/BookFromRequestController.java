package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.dao.*;
import ua.cc.spon.db.entity.Request;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.service.RequestParametersValidatorService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Controller that realises Book process from chosen favorite option
 * of reservation by admin or manager.
 *
 * @author Sergiy Ponomarenko
 */
@WebServlet({"/makeReservationFromRequestAction"})
public class BookFromRequestController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookFromRequestController.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        RoomDAO roomDAO = factory.getRoomDAO();
        ReservationDAO reservationDAO = factory.getReservationDAO();
        RequestDAO requestDAO = factory.getRequestDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        int requestId;
        List<Integer> roomsId;

        try {
            requestId = validator.validateAndGetInt("request_id", new IllegalArgumentException());
            roomsId = validator.validateAndGetIntArray("room", new IllegalArgumentException());

        } catch (IllegalArgumentException e) {
            LOGGER.warn(e.getMessage());
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect("reservationRequestsAction");
            return;
        }

        Request request;

        transaction.initTransaction(roomDAO, requestDAO, reservationDAO);

        try {

            request = requestDAO.find(requestId, locale);

            LocalDate checkinDate = request.getCheckinDate();
            LocalDate checkoutDate = request.getCheckoutDate();
            int nights = (int) (checkoutDate.toEpochDay() - checkinDate.toEpochDay());
            int persons = request.getPersons();

            List<Room> rooms = roomDAO.findFreeRooms(checkinDate, checkoutDate, locale);

            rooms = rooms.stream()
                    .filter(r -> roomsId.contains(r.getId()))
                    .collect(Collectors.toList());
            if (rooms.size() != roomsId.size()) throw new DaoException();

            BigDecimal price = rooms.stream()
                    .map(Room::getPrice)
                    .reduce(BigDecimal::add)
                    .orElse(BigDecimal.ZERO)
                    .multiply(BigDecimal.valueOf(nights));

            Reservation reservation = new Reservation();
            reservation.setCheckinDate(checkinDate);
            reservation.setCheckoutDate(checkoutDate);
            reservation.setRooms(rooms);
            reservation.setUser(request.getUser());
            reservation.setStatus(Reservation.Status.BOOKED);
            reservation.setPersons(persons);
            reservation.setPrice(price);

            reservationDAO.insert(reservation);

            request.setReservation(reservation);
            requestDAO.updateReservation(request);

            transaction.commit();

            LOGGER.info("Reservation #{} created from Request #{}", reservation.getId(), request.getId());

            resp.sendRedirect("hotelOccupancyAction");

        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            transaction.rollback();
            req.getSession().setAttribute("fail_message", "error.someDBError");
            resp.sendRedirect("reservationRequestsAction");
        } finally {
            transaction.endTransaction();
        }

    }

}
