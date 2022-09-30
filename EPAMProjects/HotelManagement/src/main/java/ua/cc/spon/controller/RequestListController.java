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
import ua.cc.spon.db.dao.RequestDAO;
import ua.cc.spon.db.dao.StatusDAO;
import ua.cc.spon.db.entity.Request;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.service.PaginatorService;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static ua.cc.spon.util.Constants.PENDING_REQUESTS_URL;

@WebServlet({"/reservationRequestsAction"})
public class RequestListController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestListController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        RequestDAO requestDAO = factory.getRequestDAO();
        StatusDAO statusDAO = factory.getStatusDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();



        transaction.initTransaction(requestDAO, statusDAO);
        try {
            Map<Reservation.Status, String> statusesTranslated = statusDAO.findNames(locale);
            List<Request> requests = requestDAO.findAllPending(locale);

            transaction.commit();

            requests.sort(Comparator.comparingInt(Request::getId));

            PaginatorService paginator =
                    new PaginatorService(req, "reservationRequests", new Integer[]{5, 10, 20});
            requests = paginator.generateSublist(requests);

            paginator.setRequestAttributes();

            HotelHelper.proceedMessages(req);

            req.setAttribute("requests", requests);
            req.setAttribute("statusesTranslated", statusesTranslated);

            req.getRequestDispatcher(PENDING_REQUESTS_URL).forward(req, resp);

        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            req.getSession().setAttribute("fail_message", "error.someDBError");
            resp.sendRedirect("indexAction");
        } finally {
            transaction.endTransaction();
        }

    }

}
