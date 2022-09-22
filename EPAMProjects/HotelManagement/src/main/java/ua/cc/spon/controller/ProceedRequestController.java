package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.RequestDAO;
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.db.entity.Request;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DBException;
import ua.cc.spon.service.PaginatorService;
import ua.cc.spon.service.RequestParametersValidatorService;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet({"/proceedRequestAction"})
public class ProceedRequestController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        RoomDAO roomDAO = factory.getRoomDAO();
        RequestDAO requestDAO = factory.getRequestDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        long requestId;

        try {
            requestId = validator.validateAndGetLong("request_id", new IllegalArgumentException());
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect("reservationRequestsAction");
            return;
        }

        if (req.getParameter("action") != null && req.getParameter("action").equals("delete")) {
            try {
                requestDAO.deleteById(requestId);
            } catch (DBException e) {
                req.getSession().setAttribute("fail_message", "error.someDBError");
            }
            resp.sendRedirect("reservationRequestsAction");
            return;
        }

        Request request = requestDAO.find(requestId, locale);

        LocalDate checkinDate = request.getCheckinDate();
        LocalDate checkoutDate = request.getCheckoutDate();
        int nights = (int) (checkoutDate.toEpochDay() - checkinDate.toEpochDay());

        int roomCount = request.getRooms();
        int personsCount = request.getPersons();

        PaginatorService paginator =
                new PaginatorService(req, "bookingOptions", new Integer[]{10, 20, 50});

        List<Room> freeRooms = roomDAO.findFreeRooms(checkinDate, checkoutDate, locale);

        freeRooms = freeRooms.stream()
                .filter(room -> request.getRoomCategories().contains(room.getRoomCategory()))
                .collect(Collectors.toList());

        List<List<Room>> collect = getCombinations(freeRooms, roomCount).stream()
                .filter(rooms -> rooms.stream().mapToInt(Room::getOccupancy).sum() >= personsCount)
                .sorted(((Comparator<List<Room>>) (o1, o2) -> {
                    int sumOccupancy1 = o1.stream()
                            .mapToInt(Room::getOccupancy)
                            .sum();
                    int sumOccupancy2 = o2.stream()
                            .mapToInt(Room::getOccupancy)
                            .sum();
                    return Integer.compare(sumOccupancy1, sumOccupancy2);
                }).thenComparing((o1, o2) -> {
                    BigDecimal sumPrice1 = o1.stream()
                            .map(Room::getPrice)
                            .reduce(BigDecimal::add).get();
                    BigDecimal sumPrice2 = o2.stream()
                            .map(Room::getPrice)
                            .reduce(BigDecimal::add).get();
                    return sumPrice1.compareTo(sumPrice2);
                })).collect(Collectors.toList());

        List<Integer> occupancies = collect.stream()
                .map(value -> value.stream()
                        .mapToInt(Room::getOccupancy)
                        .sum())
                .collect(Collectors.toList());
        List<BigDecimal> prices = collect.stream()
                .map(value -> value.stream()
                        .map(Room::getPrice)
                        .reduce(BigDecimal::add).get())
                .collect(Collectors.toList());
        List<String> roomsNumbers = collect.stream()
                .map(rooms -> rooms.stream()
                        .map(room -> room.getNumber() + "(" + room.getRoomCategory().getName() + "-" + room.getOccupancy()+")")
                        .collect(Collectors.joining(", ")))
                .collect(Collectors.toList());

        occupancies = paginator.generateSublist(occupancies);
        prices = paginator.generateSublist(prices);
        roomsNumbers = paginator.generateSublist(roomsNumbers);
        collect = paginator.generateSublist(collect);

        paginator.setRequestAttributes();

        req.setAttribute("options", collect);
        req.setAttribute("occupancies", occupancies);
        req.setAttribute("prices", prices);
        req.setAttribute("roomsNumbers", roomsNumbers);
        req.setAttribute("request", request);
        req.setAttribute("nights", nights);

        HotelHelper.proceedMessages(req);

        req.getRequestDispatcher("booking-options.jsp").forward(req, resp);

    }

    private List<List<Room>> getCombinations(List<Room> input, int size) {

        List<List<Room>> result = new ArrayList<>();

        int[] s = new int[size];                  // here we'll keep indices
        // pointing to elements in input array

        if (size <= input.size()) {
            // first index sequence: 0, 1, 2, ...
            for (int i = 0; (s[i] = i) < size - 1; i++) ;
            result.add(getSubset(input, s));
            for (; ; ) {
                int i;
                // find position of item that can be incremented
                for (i = size - 1; i >= 0 && s[i] == input.size() - size + i; i--) ;
                if (i < 0) {
                    break;
                }
                s[i]++;                    // increment this item
                for (++i; i < size; i++) {    // fill up remaining items
                    s[i] = s[i - 1] + 1;
                }
                result.add(getSubset(input, s));
            }
        }

        return result;

    }

    private List<Room> getSubset(List<Room> input, int[] subset) {
        List<Room> result = Arrays.asList(new Room[subset.length]);
        for (int i = 0; i < subset.length; i++)
            result.set(i, input.get(subset[i]));
        return result;
    }

}
