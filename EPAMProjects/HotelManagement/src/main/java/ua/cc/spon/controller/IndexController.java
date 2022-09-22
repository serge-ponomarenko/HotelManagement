package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.RoomCategoryDAO;
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.db.entity.UserSettings;
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

@WebServlet({"/indexAction"})
public class IndexController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        RoomDAO roomDAO = factory.getRoomDAO();
        RoomCategoryDAO roomCategoryDAO = factory.getRoomCategoryDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        LocalDate checkinDate;
        LocalDate checkoutDate;
        int personCount;
        List<Long> roomCategoryId;
        BigDecimal priceFrom;
        BigDecimal priceTo;

        checkinDate = validator.validateAndGetDate("checkin-date", LocalDate.of(1970, 1, 1));
        checkoutDate = validator.validateAndGetDate("checkout-date", LocalDate.now().plusYears(50));
        personCount = validator.validateAndGetInt("persons", 50);
        roomCategoryId = validator.validateAndGetLongArray("room-category");
        priceFrom = validator.validateAndGetBigDecimal("price-from", BigDecimal.ZERO);
        priceTo = validator.validateAndGetBigDecimal("price-to", BigDecimal.ZERO);


        int nights = (int) (checkoutDate.toEpochDay() - checkinDate.toEpochDay());

        PaginatorService paginator =
                new PaginatorService(req, "index", new Integer[]{5, 10, 20});

        List<Room> rooms = roomDAO.findFreeRooms(checkinDate, checkoutDate, locale);
        List<RoomCategory> roomCategories = roomCategoryDAO.findALL(locale);
        roomCategories.sort(Comparator.comparingLong(RoomCategory::getId));

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
                case "occupancy" : return Integer.compare(o1.getOccupancy(), o2.getOccupancy());
                case "category" : return Long.compare(o1.getRoomCategory().getId(), o2.getRoomCategory().getId());
                case "price" :
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

        req.getRequestDispatcher("index.jsp").forward(req, resp);

    }

}
