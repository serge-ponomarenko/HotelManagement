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

@WebServlet({"/allBookingsAction"})
public class AllBookingsListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        ReservationDAO reservationDAO = factory.getReservationDAO();
        StatusDAO statusDAO = factory.getStatusDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        Map<Reservation.Status, String> statusesTranslated = statusDAO.findNames(locale);

        PaginatorService paginator =
                new PaginatorService(req, "allBookings", new Integer[]{10, 20, 50});

        List<Reservation> reservations = null;
        try {
            reservations = reservationDAO.findAll(locale);
        } catch (DBException e) {
            req.setAttribute("fail_message", "error.someDBError");
            req.getRequestDispatcher(Constants.MY_BOOKINGS_URL).forward(req, resp);
            return;
        }

        String sortBy = paginator.getSortBy().equals("") ? "id-desc" : paginator.getSortBy();
        String[] compareString = sortBy.split("-");
        if (compareString.length == 1) compareString = new String[]{"id", "desc"};
        int order = compareString[1].equals("desc") ? -1 : 1;

        String[] finalCompareString = compareString;
        Comparator<Reservation> reservationComparator = (o1, o2) -> {
            switch (finalCompareString[0]) {
                case "id":
                    return order * Long.compare(o1.getId(), o2.getId());
                case "checkin":
                    return order * o1.getCheckinDate().compareTo(o2.getCheckinDate());
                case "checkout":
                    return order * o1.getCheckoutDate().compareTo(o2.getCheckoutDate());
                case "nights": {
                    long n1 = o1.getCheckoutDate().toEpochDay() - o1.getCheckinDate().toEpochDay();
                    long n2 = o2.getCheckoutDate().toEpochDay() - o2.getCheckinDate().toEpochDay();
                    return order * Long.compare(n1, n2);
                }
                case "persons":
                    return order * Integer.compare(o1.getPersons(), o2.getPersons());
                case "status":
                    return order * Long.compare(o1.getStatus().getId(), o2.getStatus().getId());
                case "price":
                    return order * o1.getPrice().compareTo(o2.getPrice());
                case "client":
                    return order * (o1.getUser().getFirstName() + o1.getUser().getLastName())
                            .compareTo(o2.getUser().getFirstName() + o2.getUser().getLastName());
                default:
                    return Long.compare(o1.getId(), o2.getId());
            }
        };

        reservations.sort(reservationComparator);

        reservations = paginator.generateSublist(reservations);
        paginator.setRequestAttributes();

        req.setAttribute("reservations", reservations);
        req.setAttribute("statusesTranslated", statusesTranslated);

        HotelHelper.proceedMessages(req);

        req.getRequestDispatcher(Constants.ALL_BOOKINGS_URL).forward(req, resp);

    }

}
