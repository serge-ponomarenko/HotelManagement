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
import ua.cc.spon.db.dao.StatusDAO;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.service.PaginatorService;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static ua.cc.spon.util.Constants.MY_BOOKINGS_URL;

@WebServlet({"/myBookingsAction"})
public class BookingsListController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingsListController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        ReservationDAO reservationDAO = factory.getReservationDAO();
        StatusDAO statusDAO = factory.getStatusDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();
        User user = ((User) req.getSession().getAttribute("user"));

        List<Reservation> reservations;
        Map<Reservation.Status, String> statusesTranslated;

        transaction.initTransaction(statusDAO, reservationDAO);

        try {

            statusesTranslated = statusDAO.findNames(locale);
            reservations = reservationDAO.findByUser(user, locale);

            transaction.commit();

            reservations.sort(Comparator.comparingInt(Reservation::getId).reversed());

            PaginatorService paginator =
                    new PaginatorService(req, "myBookings", new Integer[]{5, 10, 20});
            reservations = paginator.generateSublist(reservations);
            paginator.setRequestAttributes();

            req.setAttribute("reservations", reservations);
            req.setAttribute("statusesTranslated", statusesTranslated);

            HotelHelper.proceedMessages(req);

            req.getRequestDispatcher(MY_BOOKINGS_URL).forward(req, resp);

        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            req.getSession().setAttribute("fail_message", "error.someDBError");
            resp.sendRedirect("indexAction");
        } finally {
            transaction.endTransaction();
        }

    }

}
