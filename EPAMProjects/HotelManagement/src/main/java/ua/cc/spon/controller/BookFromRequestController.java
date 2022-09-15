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
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.db.entity.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet({"/makeReservationFromRequestAction"})
public class BookFromRequestController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        RoomDAO roomDAO = factory.getRoomDAO();
        ReservationDAO reservationDAO = factory.getReservationDAO();
        RequestDAO requestDAO = factory.getRequestDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();
        User user = ((User) req.getSession().getAttribute("user"));

        long requestId = Long.parseLong(Optional.ofNullable(req.getParameter("request_id")).orElse("-1"));
        List<String> roomStrings = List.of(req.getParameterValues("room"));

        Request request = requestDAO.find(requestId, locale);

        LocalDate checkinDate = request.getCheckinDate();
        LocalDate checkoutDate = request.getCheckoutDate();
        int nights = (int) (checkoutDate.toEpochDay() - checkinDate.toEpochDay());
        int persons = request.getPersons();

        // TODO: 29.08.2022 TRANSACTION!!!
        List<Room> rooms = roomDAO.findFreeRooms(checkinDate, checkoutDate, locale);

        rooms = rooms.stream()
                .filter(r -> roomStrings.contains(String.valueOf(r.getId())))
                        .collect(Collectors.toList()); 
        if (rooms.size() != roomStrings.size()) throw new RuntimeException(); // TODO: 04.09.2022  
        
        BigDecimal price = rooms.stream()
                .map(Room::getPrice)
                .reduce(BigDecimal::add)
                .get()
                .multiply(BigDecimal.valueOf(nights));

        Reservation reservation = new Reservation();
        reservation.setCheckinDate(checkinDate);
        reservation.setCheckoutDate(checkoutDate);
        reservation.setRooms(rooms);
        reservation.setUser(request.getUser());
        reservation.setStatus(Reservation.Status.BOOKED);
        reservation.setPersons(persons);
        reservation.setPrice(price);

        reservationDAO.insert(reservation);
        
        request.setReservation(reservation);
        requestDAO.updateReservation(request);

        resp.sendRedirect("myBookingsAction");  // TODO: 04.09.2022

    }

}
