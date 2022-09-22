package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.*;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DBException;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet({"/hotelOccupancyAction"})
public class HotelOccupancyController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        ReservationDAO reservationDAO = factory.getReservationDAO();
        StatusDAO statusDAO = factory.getStatusDAO();
        UserDAO userDAO = factory.getUserDAO();
        RoomDAO roomDAO = factory.getRoomDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        Map<Reservation.Status, String> statusesTranslated = statusDAO.findNames(locale);

        List<User> users = userDAO.findALL();

        List<Reservation> reservations = new ArrayList<>();

        for (User user : users) {
            try {
                reservations.addAll(reservationDAO.findByUser(user, locale));
            } catch (DBException e) {
                throw new RuntimeException(e);  // TODO: 18.09.2022
            }
        }

        Map<Reservation.Status, List<Reservation>> reservationsMap = reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getStatus));

        List<Room> freeRooms = roomDAO.findRoomsWithoutReservation(locale);
        List<Room> allRooms = roomDAO.findALL(locale);
        allRooms.sort(Comparator.comparing(Room::getNumber));

        req.setAttribute("freeRooms", freeRooms);
        req.setAttribute("allRooms", allRooms);
        req.setAttribute("reservations", reservationsMap);
        req.setAttribute("statusesTranslated", statusesTranslated);

        HotelHelper.proceedMessages(req);

        req.getRequestDispatcher("hotel-occupancy.jsp").forward(req, resp);

    }

}
