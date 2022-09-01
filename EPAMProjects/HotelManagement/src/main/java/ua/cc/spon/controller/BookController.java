package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.ReservationDAO;
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@WebServlet({"/bookAction"})
public class BookController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        RoomDAO roomDAO = factory.getRoomDAO();
        ReservationDAO reservationDAO = factory.getReservationDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();
        User user = ((User) req.getSession().getAttribute("user"));

        LocalDate checkinDate = LocalDate.parse(Optional.ofNullable(req.getParameter("checkin-date")).orElse("1970-01-01"));
        LocalDate checkoutDate = LocalDate.parse(Optional.ofNullable(req.getParameter("checkout-date")).orElse("2100-01-01"));
        int nights = (int) (checkoutDate.toEpochDay() - checkinDate.toEpochDay());

        int roomId = Integer.parseInt(Optional.ofNullable(req.getParameter("room")).orElse("-1"));
        int persons = Integer.parseInt(Optional.ofNullable(req.getParameter("persons")).orElse("1"));

        // TODO: 29.08.2022 TRANSACTION!!!
        List<Room> rooms = roomDAO.findFreeRooms(checkinDate, checkoutDate, locale);

        Room room = rooms.stream().filter(r -> r.getId() == roomId).findAny().orElseThrow(); // TODO: 29.08.2022 !!!

            Reservation reservation = new Reservation();
            reservation.setCheckinDate(checkinDate);
            reservation.setCheckoutDate(checkoutDate);
            reservation.setRooms(List.of(room));
            reservation.setUser(user);
            reservation.setStatus(Reservation.Status.BOOKED);
            reservation.setPersons(persons);
            reservation.setPrice(room.getPrice().multiply(BigDecimal.valueOf(nights)));

            reservationDAO.insert(reservation);


        //req.getRequestDispatcher("index.jsp").forward(req, resp);
        resp.sendRedirect("myBookingsAction");

    }

}
