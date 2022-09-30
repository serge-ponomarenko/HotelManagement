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
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.exception.UserNotFoundException;
import ua.cc.spon.service.RequestParametersValidatorService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WebServlet({"/cancelReservationAction"})
public class CancelReservationController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CancelReservationController.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        ReservationDAO reservationDAO = factory.getReservationDAO();
        UserDAO userDAO = factory.getUserDAO();

        String callbackUrl = Optional.ofNullable(req.getHeader("referer")).orElse("myBookingsAction");

        User currentUser = ((User) req.getSession().getAttribute("user"));
        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        int reservationId;
        int userId;

        try {
            reservationId = validator.validateAndGetInt("reservationId", new IllegalArgumentException());
            userId = validator.validateAndGetInt("userId", -1);
        } catch (IllegalArgumentException e) {
            LOGGER.warn(e.getMessage());
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect(callbackUrl);
            return;
        }

        List<Reservation> reservations;
        Reservation reservation;
        User user = currentUser;

        transaction.initTransaction(userDAO, reservationDAO);

        try {
            if (Arrays.asList(User.Role.ADMINISTRATOR, User.Role.MANAGER).contains(currentUser.getRole())) {
                user = userDAO.find(userId);
                if (user == null) throw new UserNotFoundException();
            }

            reservations = reservationDAO.findByUser(user, locale);
            reservation = reservations.stream()
                    .filter(r -> r.getId() == reservationId)
                    .findAny()
                    .orElseThrow(IllegalArgumentException::new);

            reservation.setStatus(Reservation.Status.CANCELED);
            reservationDAO.updateStatus(reservation);

            transaction.commit();

            LOGGER.info("Reservation #{} canceled", reservation.getId());

            resp.sendRedirect(callbackUrl);

        } catch (IllegalArgumentException | UserNotFoundException | DaoException e) {
            LOGGER.error(e.getMessage(), e);
            transaction.rollback();
            req.getSession().setAttribute("fail_message", "error.someDBError");
            resp.sendRedirect(callbackUrl);
        } finally {
            transaction.endTransaction();
        }
    }
}
