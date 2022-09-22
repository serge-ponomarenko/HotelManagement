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
import ua.cc.spon.db.entity.Request;
import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.service.RequestParametersValidatorService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
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

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        LocalDate checkinDate;
        LocalDate checkoutDate;
        int personCount;
        int roomCount;
        List<Long> roomCategoriesId;
        String additionalInformation;

        try {
            checkinDate = validator.validateAndGetDate("checkin-date", new IllegalArgumentException());
            checkoutDate = validator.validateAndGetDate("checkout-date", new IllegalArgumentException());
            personCount = validator.validateAndGetInt("persons", new IllegalArgumentException());
            roomCount = validator.validateAndGetInt("rooms", new IllegalArgumentException());
            roomCategoriesId = validator.validateAndGetLongArray("room-category");
            additionalInformation = validator.validateAndGetString("additional-information", "");
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect("indexAction");
            return;
        }

        Request request = new Request();
        request.setCheckinDate(checkinDate);
        request.setCheckoutDate(checkoutDate);
        request.setPersons(personCount);
        request.setRooms(roomCount);
        request.setUser(user);
        request.setAdditionalInformation(additionalInformation);

        List<RoomCategory> filteredCategories = roomCategories.stream()
                .filter(rc -> roomCategoriesId.contains(rc.getId()))
                .collect(Collectors.toList());

        if (filteredCategories.isEmpty()) filteredCategories = roomCategories;

        request.setRoomCategories(filteredCategories);

        requestDAO.insert(request);

        req.getSession().setAttribute("success_message", "index.your-request-has-been-send");

        resp.sendRedirect("indexAction");

    }

}
