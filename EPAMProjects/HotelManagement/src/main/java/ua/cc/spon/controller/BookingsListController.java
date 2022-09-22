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
import ua.cc.spon.exception.DBException;
import ua.cc.spon.service.PaginatorService;
import ua.cc.spon.util.Constants;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@WebServlet({"/myBookingsAction"})
public class BookingsListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        ReservationDAO reservationDAO = factory.getReservationDAO();
        StatusDAO statusDAO = factory.getStatusDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();
        User user = ((User) req.getSession().getAttribute("user"));

        Map<Reservation.Status, String> statusesTranslated = statusDAO.findNames(locale);

        PaginatorService paginator =
                new PaginatorService(req, "myBookings", new Integer[]{5, 10, 20});

        List<Reservation> reservations = null;
        try {
            reservations = reservationDAO.findByUser(user, locale);
        } catch (DBException e) {
            req.setAttribute("fail_message", "error.someDBError");
            req.getRequestDispatcher(Constants.MY_BOOKINGS_URL).forward(req, resp);
            return;
        }

        reservations.sort(Comparator.comparingLong(Reservation::getId).reversed());

        reservations = paginator.generateSublist(reservations);
        paginator.setRequestAttributes();

        req.setAttribute("reservations", reservations);
        req.setAttribute("statusesTranslated", statusesTranslated);

        HotelHelper.proceedMessages(req);

        req.getRequestDispatcher(Constants.MY_BOOKINGS_URL).forward(req, resp);

    }

}
