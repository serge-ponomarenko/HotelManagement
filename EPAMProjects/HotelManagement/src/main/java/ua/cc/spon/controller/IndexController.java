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
import ua.cc.spon.db.dao.RoomCategoryDAO;
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.service.PaginatorService;
import ua.cc.spon.service.RequestParametersValidatorService;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ua.cc.spon.util.Constants.INDEX_URL;

@WebServlet({"/indexAction"})
public class IndexController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        RoomDAO roomDAO = factory.getRoomDAO();
        RoomCategoryDAO roomCategoryDAO = factory.getRoomCategoryDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        LocalDate checkinDate;
        LocalDate checkoutDate;
        int personCount;
        List<Integer> roomCategoryId;
        BigDecimal priceFrom;
        BigDecimal priceTo;

        checkinDate = validator.validateAndGetDate("checkin-date", LocalDate.of(1970, 1, 1));
        checkoutDate = validator.validateAndGetDate("checkout-date", LocalDate.now().plusYears(50));
        personCount = validator.validateAndGetInt("persons", 50);
        roomCategoryId = validator.validateAndGetIntArray("room-category");
        priceFrom = validator.validateAndGetBigDecimal("price-from", BigDecimal.ZERO);
        priceTo = validator.validateAndGetBigDecimal("price-to", BigDecimal.ZERO);


        int nights = (int) (checkoutDate.toEpochDay() - checkinDate.toEpochDay());

        PaginatorService paginator =
                new PaginatorService(req, "index", new Integer[]{5, 10, 20});

        transaction.initTransaction(roomDAO, roomCategoryDAO);

        try {
            List<Room> rooms = roomDAO.findFreeRooms(checkinDate, checkoutDate, locale);
            List<RoomCategory> roomCategories = roomCategoryDAO.findAll(locale);

            transaction.commit();

            roomCategories.sort(Comparator.comparingInt(RoomCategory::getId));

            Predicate<Room> pricePredicate = room -> {
                if (priceTo.equals(BigDecimal.ZERO)) return room.getPrice().compareTo(priceFrom) >= 0;
                return room.getPrice().compareTo(priceFrom) >= 0 && room.getPrice().compareTo(priceTo) <= 0;
            };

            Predicate<Room> categoryPredicate = room -> {
                if (roomCategoryId.isEmpty()) return true;
                return roomCategoryId.stream().anyMatch(l -> l == room.getRoomCategory().getId());
            };

            Predicate<Room> personsPredicate = room -> room.getOccupancy() >= personCount;

            rooms = rooms.stream()
                    .filter(pricePredicate)
                    .filter(categoryPredicate)
                    .filter(personsPredicate)
                    .collect(Collectors.toList());

            Comparator<Room> roomComparator = (o1, o2) -> {
                switch (paginator.getSortBy()) {
                    case "occupancy":
                        return Integer.compare(o1.getOccupancy(), o2.getOccupancy());
                    case "category":
                        return Integer.compare(o1.getRoomCategory().getId(), o2.getRoomCategory().getId());
                    case "price":
                    default:
                        return o1.getPrice().compareTo(o2.getPrice());
                }
            };

            rooms.sort(roomComparator);

            rooms = paginator.generateSublist(rooms);
            paginator.setRequestAttributes();

            req.setAttribute("rooms", rooms);
            req.setAttribute("roomCategories", roomCategories);
            req.setAttribute("nights", nights);

            HotelHelper.proceedMessages(req);

            req.getRequestDispatcher(INDEX_URL).forward(req, resp);

        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            transaction.rollback();
            resp.sendRedirect("errorAction");
        } finally {
            transaction.endTransaction();
        }

    }

}
