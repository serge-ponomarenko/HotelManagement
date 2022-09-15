package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.RequestDAO;
import ua.cc.spon.db.dao.RoomCategoryDAO;
import ua.cc.spon.db.entity.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet({"/requestAction"})
public class RequestController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        RoomCategoryDAO roomCategoryDAO = factory.getRoomCategoryDAO();
        RequestDAO requestDAO = factory.getRequestDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();
        User user = ((User) req.getSession().getAttribute("user"));

        List<RoomCategory> roomCategories = roomCategoryDAO.findALL(locale);

        LocalDate checkinDate = LocalDate.parse(Optional.ofNullable(req.getParameter("checkin-date")).orElse("1970-01-01"));
        LocalDate checkoutDate = LocalDate.parse(Optional.ofNullable(req.getParameter("checkout-date")).orElse("2100-01-01"));

        int personCount = Integer.parseInt(Optional.ofNullable(req.getParameter("persons")).orElse("999"));
        int roomCount = Integer.parseInt(Optional.ofNullable(req.getParameter("rooms")).orElse("999"));
        String[] roomCategoryId = req.getParameterValues("room-category");
        String additionalInformation = req.getParameter("additional-information");

        Request request = new Request();
        request.setCheckinDate(checkinDate);
        request.setCheckoutDate(checkoutDate);
        request.setPersons(personCount);
        request.setRooms(roomCount);
        request.setUser(user);
        request.setAdditionalInformation(additionalInformation);

        String[] finalRoomCategoryId = (roomCategoryId == null ? new String[0] : roomCategoryId);
        List<RoomCategory> filteredCategories = roomCategories.stream()
                .filter(rc -> {
                    for (String s : finalRoomCategoryId) {
                        if (rc.getId() == Long.parseLong(s)) return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());

        if (filteredCategories.isEmpty()) filteredCategories = roomCategories;

        request.setRoomCategories(filteredCategories);

        requestDAO.insert(request);

        req.getSession().setAttribute("message", "index.your-request-has-been-send");

        resp.sendRedirect("indexAction");

    }

}
