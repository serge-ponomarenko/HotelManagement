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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet({"/cancelReservationAction"})
public class CancelReservationController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        ReservationDAO reservationDAO = factory.getReservationDAO();

        User user = ((User) req.getSession().getAttribute("user"));
        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        int reservationId = Integer.parseInt(Optional.ofNullable(req.getParameter("reservationId")).orElse("-1"));

        List<Reservation> reservations = reservationDAO.findByUser(user, locale);

        Reservation reservation = reservations.stream().filter(r -> r.getId() == reservationId).findAny().orElseThrow();

        reservation.setStatus(Reservation.Status.CANCELED);
        reservationDAO.updateStatus(reservation);

        //req.getRequestDispatcher("index.jsp").forward(req, resp);
        resp.sendRedirect("myBookingsAction");

    }

}
