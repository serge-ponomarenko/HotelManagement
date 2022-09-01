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

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
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

        LocalDate checkinDate = LocalDate.parse(Optional.ofNullable(req.getParameter("checkin-date")).orElse("1970-01-01"));
        LocalDate checkoutDate = LocalDate.parse(Optional.ofNullable(req.getParameter("checkout-date")).orElse("2100-01-01"));
        int nights = (int) (checkoutDate.toEpochDay() - checkinDate.toEpochDay());

        int personCount = Integer.parseInt(Optional.ofNullable(req.getParameter("persons")).orElse("999"));
        String[] roomCategoryId = req.getParameterValues("room-category");

        String stringPriceFrom = req.getParameter("price-from");
        String stringPriceTo = req.getParameter("price-to");
        BigDecimal priceFrom = new BigDecimal(stringPriceFrom == null || stringPriceFrom.isEmpty() ? "0" : stringPriceFrom);
        BigDecimal priceTo = new BigDecimal(stringPriceTo == null || stringPriceTo.isEmpty() ? "0" : stringPriceTo);

        int page = 1;
        if (req.getSession().getAttribute("page") != null) {
            page = (int) req.getSession().getAttribute("page");
        }

        int showBy = 5;
        if (req.getSession().getAttribute("showBy") != null) {
            showBy = (int) req.getSession().getAttribute("showBy");
        }

        String sortBy = "price";
        if (req.getSession().getAttribute("indexSortBy") != null) {
            sortBy = (String) req.getSession().getAttribute("indexSortBy");
        }

        List<Room> rooms = roomDAO.findFreeRooms(checkinDate, checkoutDate, locale);
        List<RoomCategory> roomCategories = roomCategoryDAO.findALL(locale);
        roomCategories.sort(Comparator.comparingLong(RoomCategory::getId));

        Predicate<Room> pricePredicate = room -> {
            if (priceTo.equals(BigDecimal.ZERO)) return room.getPrice().compareTo(priceFrom) >= 0;
            return room.getPrice().compareTo(priceFrom) >= 0 && room.getPrice().compareTo(priceTo) <= 0;
        };

        Predicate<Room> categoryPredicate = room -> {
            if (roomCategoryId == null) return true;
            for (String s : roomCategoryId) {
                if (Long.parseLong(s) == room.getRoomCategory().getId()) return true;
            }
            return false;
        };

        Predicate<Room> personsPredicate = room -> room.getOccupancy() >= personCount;

        rooms = rooms.stream()
                .filter(pricePredicate)
                .filter(categoryPredicate)
                .filter(personsPredicate)
                .collect(Collectors.toList());

        String finalSortBy = sortBy;
        Comparator<Room> roomComparator = (o1, o2) -> {
            switch (finalSortBy) {
                case "occupancy" : return Integer.compare(o1.getOccupancy(), o2.getOccupancy());
                case "category" : return Long.compare(o1.getRoomCategory().getId(), o2.getRoomCategory().getId());
                case "price" :
                default:
                    return o1.getPrice().compareTo(o2.getPrice());
            }
        };

        rooms.sort(roomComparator);

        int resultSize = rooms.size();

        int showByCalc = Math.min(showBy, resultSize);

        int start = Math.min((page-1) * showByCalc, resultSize);
        int end = Math.min(page * showByCalc, resultSize);

        rooms = rooms.subList(start, end);

        req.setAttribute("rooms", rooms);
        req.setAttribute("roomCategories", roomCategories);
        req.setAttribute("nights", nights);

        req.setAttribute("page", page);
        req.setAttribute("showBy", showBy);
        req.setAttribute("pages", (int) Math.ceil((double) resultSize / showByCalc));
        req.setAttribute("resultSize", resultSize);
        req.setAttribute("indexSortBy", sortBy);

        req.getRequestDispatcher("index.jsp").forward(req, resp);

    }

}
