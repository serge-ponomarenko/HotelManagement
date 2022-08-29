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
        List<Room> rooms = roomDAO.findALL(locale);
        List<RoomCategory> roomCategories = roomCategoryDAO.findALL(locale);
        roomCategories.sort(Comparator.comparingLong(RoomCategory::getId));

        String checkinDate = Optional.ofNullable(req.getParameter("checkin-date")).orElse("1970-01-01");
        String checkoutDate = Optional.ofNullable(req.getParameter("checkout-date")).orElse("2100-01-01");
        int personCount = Integer.parseInt(Optional.ofNullable(req.getParameter("persons")).orElse("999"));
        String[] roomCategoryId = req.getParameterValues("room-category");

        String stringPriceFrom = req.getParameter("price-from");
        String stringPriceTo = req.getParameter("price-to");
        BigDecimal priceFrom = new BigDecimal(stringPriceFrom == null || stringPriceFrom.isEmpty() ? "0" : stringPriceFrom);
        BigDecimal priceTo = new BigDecimal(stringPriceTo == null || stringPriceTo.isEmpty() ? "0" : stringPriceTo);

        int showBy = Integer.parseInt(Optional.ofNullable(req.getParameter("showBy")).orElse("5"));

        int page = 1;
        if (req.getSession().getAttribute("page") != null) {
            page = (int) req.getSession().getAttribute("page");
            req.getSession().removeAttribute("page");
        }

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

        int resultSize = rooms.size();

        showBy = Math.min(showBy, resultSize);

        int start = Math.min((page-1) * showBy, resultSize);
        int end = Math.min(page * showBy, resultSize);

        rooms = rooms.subList(start, end);

        req.setAttribute("rooms", rooms);
        req.setAttribute("roomCategories", roomCategories);

        req.setAttribute("page", page);
        req.setAttribute("showBy", showBy);
        req.setAttribute("pages", (int) Math.ceil((double) resultSize / showBy));
        req.setAttribute("resultSize", resultSize);

        req.getRequestDispatcher("index.jsp").forward(req, resp);

    }

}
