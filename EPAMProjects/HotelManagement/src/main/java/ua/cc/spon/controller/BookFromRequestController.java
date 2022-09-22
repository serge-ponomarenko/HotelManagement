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
import ua.cc.spon.db.entity.Request;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.service.RequestParametersValidatorService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        long requestId;
        List<Long> roomsId;

        try {
            requestId = validator.validateAndGetLong("request_id", new IllegalArgumentException());
            roomsId = validator.validateAndGetLongArray("room", new IllegalArgumentException());

        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect("reservationRequestsAction");
            return;
        }

        Request request = requestDAO.find(requestId, locale);

        LocalDate checkinDate = request.getCheckinDate();
        LocalDate checkoutDate = request.getCheckoutDate();
        int nights = (int) (checkoutDate.toEpochDay() - checkinDate.toEpochDay());
        int persons = request.getPersons();

        // TODO: 29.08.2022 TRANSACTION!!!
        List<Room> rooms = roomDAO.findFreeRooms(checkinDate, checkoutDate, locale);

        rooms = rooms.stream()
                .filter(r -> roomsId.contains(r.getId()))
                        .collect(Collectors.toList()); 
        if (rooms.size() != roomsId.size()) throw new RuntimeException(); // TODO: 04.09.2022
        
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

        resp.sendRedirect("hotelOccupancyAction");

    }

}
