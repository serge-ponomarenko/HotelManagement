package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.ReservationDAO;
import ua.cc.spon.db.dao.StatusDAO;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;

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

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        Map<Reservation.Status, String> statusesTranslated = statusDAO.findNames(locale);

        List<User> users = userDAO.findALL();

        List<Reservation> reservations = new ArrayList<>();

        for (User user : users) {
            reservations.addAll(reservationDAO.findByUser(user, locale));
        }

        Map<Reservation.Status, List<Reservation>> reservationsMap = reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getStatus));


        req.setAttribute("reservations", reservationsMap);
        req.setAttribute("statusesTranslated", statusesTranslated);

        req.getRequestDispatcher("hotel-occupancy.jsp").forward(req, resp);

    }

}
