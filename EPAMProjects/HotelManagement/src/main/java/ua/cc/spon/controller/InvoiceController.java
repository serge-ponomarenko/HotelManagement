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
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.service.RequestParametersValidatorService;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.util.List;

import static ua.cc.spon.util.Constants.INVOICE_URL;

@WebServlet({"/invoiceAction"})
public class InvoiceController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        ReservationDAO reservationDAO = factory.getReservationDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();
        User user = ((User) req.getSession().getAttribute("user"));

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        int reservationId;
        Reservation reservation;

        transaction.init(reservationDAO);

        try {
            reservationId = validator.validateAndGetInt("reservationId", new IllegalArgumentException());

            List<Reservation> reservations = reservationDAO.findByUser(user, locale);

            reservation = reservations
                    .stream()
                    .filter(r -> r.getId() == reservationId)
                    .findAny()
                    .orElseThrow(IllegalArgumentException::new);

            req.setAttribute("reservation", reservation);

            HotelHelper.proceedMessages(req);

            req.getRequestDispatcher(INVOICE_URL).forward(req, resp);

        } catch (IllegalArgumentException | DaoException e) {
            LOGGER.error(e.getMessage(), e);
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect("myBookingsAction");
        } finally {
            transaction.end();
        }


    }

}
