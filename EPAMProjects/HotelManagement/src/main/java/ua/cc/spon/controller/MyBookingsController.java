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
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet({"/myBookingsAction"})
public class MyBookingsController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        ReservationDAO reservationDAO = factory.getReservationDAO();
        StatusDAO statusDAO = factory.getStatusDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();
        User user = ((User) req.getSession().getAttribute("user"));

        Map<Reservation.Status, String> statusesTranslated = statusDAO.findNames(locale);

        int page = 1;
        if (req.getSession().getAttribute("page") != null) {
            page = (int) req.getSession().getAttribute("page");
        }

        int showBy = 5;
        if (req.getSession().getAttribute("showBy") != null) {
            showBy = (int) req.getSession().getAttribute("showBy");
        }

        List<Reservation> reservations = reservationDAO.findByUser(user, locale);

        reservations.sort(Comparator.comparingLong(Reservation::getId).reversed()); // TODO: 30.08.2022 Sorting!

        int resultSize = reservations.size();

        int showByCalc = Math.min(showBy, resultSize);

        int start = Math.min((page-1) * showByCalc, resultSize);
        int end = Math.min(page * showByCalc, resultSize);

        reservations = reservations.subList(start, end);


        req.setAttribute("reservations", reservations);
        req.setAttribute("statusesTranslated", statusesTranslated);

        req.setAttribute("page", page);
        req.setAttribute("showBy", showBy);
        req.setAttribute("pages", (int) Math.ceil((double) resultSize / showByCalc));
        req.setAttribute("resultSize", resultSize);

        req.getRequestDispatcher("my-bookings.jsp").forward(req, resp);

    }

}
