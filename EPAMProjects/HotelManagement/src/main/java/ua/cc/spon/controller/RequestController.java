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
import ua.cc.spon.db.dao.RequestDAO;
import ua.cc.spon.db.dao.RoomCategoryDAO;
import ua.cc.spon.db.entity.Request;
import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.service.RequestParametersValidatorService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet({"/requestAction"})
public class RequestController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestController.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        RoomCategoryDAO roomCategoryDAO = factory.getRoomCategoryDAO();
        RequestDAO requestDAO = factory.getRequestDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();
        User user = ((User) req.getSession().getAttribute("user"));

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        LocalDate checkinDate;
        LocalDate checkoutDate;
        int personCount;
        int roomCount;
        List<Integer> roomCategoriesId;
        String additionalInformation;

        try {
            checkinDate = validator.validateAndGetDate("checkin-date", new IllegalArgumentException());
            checkoutDate = validator.validateAndGetDate("checkout-date", new IllegalArgumentException());
            personCount = validator.validateAndGetInt("persons", new IllegalArgumentException());
            roomCount = validator.validateAndGetInt("rooms", new IllegalArgumentException());
            roomCategoriesId = validator.validateAndGetIntArray("room-category");
            additionalInformation = validator.validateAndGetString("additional-information", "");
        } catch (IllegalArgumentException e) {
            LOGGER.warn(e.getMessage());
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect("indexAction");
            return;
        }

        transaction.initTransaction(roomCategoryDAO, requestDAO);

        try {
            List<RoomCategory> roomCategories = roomCategoryDAO.findAll(locale);

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

            transaction.commit();

            LOGGER.info("Request #{} created", request.getId());

            req.getSession().setAttribute("success_message", "index.your-request-has-been-send");

        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            transaction.rollback();
            req.getSession().setAttribute("fail_message", "error.someDBError");
        } finally {
            transaction.endTransaction();
        }

        resp.sendRedirect("indexAction");

    }

}
