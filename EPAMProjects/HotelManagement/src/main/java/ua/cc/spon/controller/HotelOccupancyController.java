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
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ua.cc.spon.util.Constants.HOTEL_OCCUPANCY_URL;

@WebServlet({"/hotelOccupancyAction"})
public class HotelOccupancyController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(HotelOccupancyController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        ReservationDAO reservationDAO = factory.getReservationDAO();
        StatusDAO statusDAO = factory.getStatusDAO();
        UserDAO userDAO = factory.getUserDAO();
        RoomDAO roomDAO = factory.getRoomDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        Map<Reservation.Status, String> statusesTranslated;
        Map<Reservation.Status, List<Reservation>> reservationsMap;
        List<Room> freeRooms;
        List<Room> allRooms;
        List<User> users;

        transaction.initTransaction(userDAO, roomDAO, statusDAO, reservationDAO);

        try {
            statusesTranslated = statusDAO.findNames(locale);

            users = userDAO.findAll();

            List<Reservation> reservations = new ArrayList<>();

            for (User user : users) {
                reservations.addAll(reservationDAO.findByUser(user, locale));
            }

            reservationsMap = reservations.stream()
                    .collect(Collectors.groupingBy(Reservation::getStatus));

            freeRooms = roomDAO.findRoomsWithoutReservation(locale);
            allRooms = roomDAO.findAll(locale);
            allRooms.sort(Comparator.comparing(Room::getNumber));

            transaction.commit();

            LocalDate minDate = reservationsMap.values().stream()
                    .flatMap(Collection::stream)
                    .map(Reservation::getCheckinDate)
                    .min(LocalDate::compareTo)
                    .orElseGet(LocalDate::now);
            LocalDate maxDate = reservationsMap.values().stream()
                    .flatMap(Collection::stream)
                    .map(Reservation::getCheckoutDate)
                    .max(LocalDate::compareTo)
                    .orElseGet(LocalDate::now);


            req.setAttribute("freeRooms", freeRooms);
            req.setAttribute("minDate", minDate);
            req.setAttribute("maxDate", maxDate);
            req.setAttribute("allRooms", allRooms);
            req.setAttribute("reservations", reservationsMap);
            req.setAttribute("statusesTranslated", statusesTranslated);

            HotelHelper.proceedMessages(req);

            req.getRequestDispatcher(HOTEL_OCCUPANCY_URL).forward(req, resp);

        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            transaction.rollback();
            req.getSession().setAttribute("fail_message", "error.someDBError");
            resp.sendRedirect("indexAction");
        } finally {
            transaction.endTransaction();
        }

    }

}
