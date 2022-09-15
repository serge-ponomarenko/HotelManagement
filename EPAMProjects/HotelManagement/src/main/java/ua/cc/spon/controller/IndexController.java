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

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

        String message = null;
        if (req.getSession().getAttribute("message") != null) {
            message = (String) req.getSession().getAttribute("message");
            req.getSession().removeAttribute("message");
        }

        LocalDate checkinDate = LocalDate.parse(Optional.ofNullable(req.getParameter("checkin-date")).orElse("1970-01-01"));
        LocalDate checkoutDate = LocalDate.parse(Optional.ofNullable(req.getParameter("checkout-date")).orElse("2100-01-01"));
        int nights = (int) (checkoutDate.toEpochDay() - checkinDate.toEpochDay());

        int personCount = Integer.parseInt(Optional.ofNullable(req.getParameter("persons")).orElse("999"));
        String[] roomCategoryId = req.getParameterValues("room-category");

        String stringPriceFrom = req.getParameter("price-from");
        String stringPriceTo = req.getParameter("price-to");
        BigDecimal priceFrom = new BigDecimal(stringPriceFrom == null || stringPriceFrom.isEmpty() ? "0" : stringPriceFrom);
        BigDecimal priceTo = new BigDecimal(stringPriceTo == null || stringPriceTo.isEmpty() ? "0" : stringPriceTo);

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
            if (roomCategoryId == null) return true;
            return Arrays.stream(roomCategoryId).anyMatch(s -> Long.parseLong(s) == room.getRoomCategory().getId());
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

        req.setAttribute("message", message);

        req.getRequestDispatcher("index.jsp").forward(req, resp);

    }

}
