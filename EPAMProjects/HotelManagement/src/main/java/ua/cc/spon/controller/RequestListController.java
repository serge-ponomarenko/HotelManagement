package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.RequestDAO;
import ua.cc.spon.db.dao.ReservationDAO;
import ua.cc.spon.db.dao.StatusDAO;
import ua.cc.spon.db.entity.Request;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.service.PaginatorService;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet({"/reservationRequestsAction"})
public class RequestListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        ReservationDAO reservationDAO = factory.getReservationDAO();
        RequestDAO requestDAO = factory.getRequestDAO();
        StatusDAO statusDAO = factory.getStatusDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();
        User user = ((User) req.getSession().getAttribute("user"));

        Map<Reservation.Status, String> statusesTranslated = statusDAO.findNames(locale);

        PaginatorService paginator =
                new PaginatorService(req, "reservationRequests", new Integer[]{5, 10, 20});

        List<Request> requests = requestDAO.findAllPending(locale);

        requests.sort(Comparator.comparingLong(Request::getId).reversed()); // TODO: 30.08.2022 Sorting!

        requests = paginator.generateSublist(requests);
        paginator.setRequestAttributes();

        req.setAttribute("requests", requests);
        req.setAttribute("statusesTranslated", statusesTranslated);

        req.getRequestDispatcher("pending-requests.jsp").forward(req, resp);

    }

}
