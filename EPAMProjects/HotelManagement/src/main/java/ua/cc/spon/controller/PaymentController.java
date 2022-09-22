package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.ReservationDAO;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DBException;
import ua.cc.spon.exception.NoUserFoundException;
import ua.cc.spon.service.RequestParametersValidatorService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WebServlet({"/paymentAction"})
public class PaymentController extends HttpServlet {

    private static boolean USER_HAS_MONEY = true;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        ReservationDAO reservationDAO = factory.getReservationDAO();
        UserDAO userDAO = factory.getUserDAO();

        String callbackUrl = Optional.ofNullable(req.getHeader("referer")).orElse("myBookingsAction");

        User currentUser = ((User) req.getSession().getAttribute("user"));
        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        long reservationId;
        long userId;

        try {
            reservationId = validator.validateAndGetLong("reservationId", new IllegalArgumentException());
            userId = validator.validateAndGetLong("userId", -1);
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect(callbackUrl);
            return;
        }

        List<Reservation> reservations;
        Reservation reservation;

        User user = currentUser;

        try {
            if (Arrays.asList(User.Role.ADMINISTRATOR, User.Role.MANAGER).contains(currentUser.getRole())) {
                user = userDAO.find(userId);
            }

            reservations = reservationDAO.findByUser(user, locale);
            reservation = reservations.stream().filter(r -> r.getId() == reservationId).findAny().orElseThrow(IllegalArgumentException::new);

        } catch (DBException | IllegalArgumentException | NoUserFoundException e) {
            req.setAttribute("fail_message", "error.someDBError");
            resp.sendRedirect(callbackUrl);
            return;
        }

        if (USER_HAS_MONEY) {
            reservation.setStatus(Reservation.Status.PAID);
            reservationDAO.updateStatus(reservation);
        } else {
            //throw new UserHasNotEnoughMoneyException();
        }

        req.setAttribute("success_message", "index.succeed");

        resp.sendRedirect(callbackUrl);

    }

}
