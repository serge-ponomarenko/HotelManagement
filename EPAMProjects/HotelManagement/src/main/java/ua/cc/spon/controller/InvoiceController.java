package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.ReservationDAO;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DBException;
import ua.cc.spon.service.RequestParametersValidatorService;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet({"/invoiceAction"})
public class InvoiceController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        ReservationDAO reservationDAO = factory.getReservationDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();
        User user = ((User) req.getSession().getAttribute("user"));

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        long reservationId;
        Reservation reservation;

        try {
            reservationId = validator.validateAndGetLong("reservationId", new IllegalArgumentException());

            List<Reservation> reservations = reservationDAO.findByUser(user, locale);

            reservation = reservations
                    .stream()
                    .filter(r -> r.getId() == reservationId)
                    .findAny()
                    .orElseThrow(IllegalArgumentException::new);

        } catch (IllegalArgumentException | DBException e) {
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect("myBookingsAction");
            return;
        }

        req.setAttribute("reservation", reservation);

        HotelHelper.proceedMessages(req);

        req.getRequestDispatcher("invoice.jsp").forward(req, resp);

    }

}
