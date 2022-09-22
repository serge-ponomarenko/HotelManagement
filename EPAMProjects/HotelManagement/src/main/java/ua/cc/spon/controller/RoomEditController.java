package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.LocaleDAO;
import ua.cc.spon.db.dao.RoomCategoryDAO;
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.db.entity.Locale;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DBException;
import ua.cc.spon.exception.EmptyDescriptionException;
import ua.cc.spon.exception.EmptyNameException;
import ua.cc.spon.service.RequestParametersValidatorService;
import ua.cc.spon.util.Constants;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@WebServlet({"/editRoomAction"})
public class RoomEditController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        RoomDAO roomDAO = factory.getRoomDAO();
        RoomCategoryDAO roomCategoryDAO = factory.getRoomCategoryDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        long roomId;

        try {
            roomId = validator.validateAndGetLong("room_id", new IllegalArgumentException());

        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect("manageRoomsAction");
            return;
        }

        if (req.getParameter("action") != null && req.getParameter("action").equals("delete")) {
            try {
                roomDAO.deleteById(roomId);
            } catch (DBException e) {
                req.getSession().setAttribute("fail_message", "error.someDBError");
            }
            resp.sendRedirect("manageRoomsAction");
            return;
        }

        Map<String, Room> editRoomMap;

        if (roomId == -1) {
            Room room = new Room();
            roomDAO.create(room);
            roomId = room.getId();
        }

        editRoomMap = roomDAO.findByIdGroupByLocale(roomId);

        if (editRoomMap.isEmpty()) {
            req.getSession().setAttribute("fail_message", "error.someDBError");
            resp.sendRedirect("manageRoomsAction");
            return;
        }

        String roomNumber;
        int roomOccupancy;
        long roomCategoryId;
        BigDecimal roomPrice;

        try {
            roomNumber = validator.validateAndGetString("number", Constants.ANY_SYMBOLS, new IllegalArgumentException());
            roomOccupancy = validator.validateAndGetInt("occupancy", new IllegalArgumentException());
            roomCategoryId = validator.validateAndGetLong("room-category", new IllegalArgumentException());
            roomPrice = validator.validateAndGetBigDecimal("price", new IllegalArgumentException());
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect("editRoomAction?room_id=" + roomId);
            return;
        }

        Room room = editRoomMap.values().iterator().next();
        room.setNumber(roomNumber);
        room.setOccupancy(roomOccupancy);
        RoomCategory roomCategory = roomCategoryDAO.findByIdGroupByLocale(roomCategoryId).values().iterator().next();
        room.setRoomCategory(roomCategory);
        room.setPrice(roomPrice);
        try {
            roomDAO.update(room, locale);
        } catch (DBException e) {
            req.setAttribute("fail_message", "error.someDBError");
            resp.sendRedirect("editRoomAction?room_id=" + roomId);
            return;
        }

        editRoomMap = roomDAO.findByIdGroupByLocale(roomId);
        System.out.println(editRoomMap);

        for (String l : editRoomMap.keySet()) {
            String roomName;
            String roomDescription;
            try {
                roomName = validator.validateAndGetString("name_" + l, Constants.ANY_SYMBOLS, new EmptyNameException());
                roomDescription = validator.validateAndGetString("description_" + l, Constants.ANY_SYMBOLS, new EmptyDescriptionException());
            } catch (EmptyNameException | EmptyDescriptionException e) {
                req.setAttribute("fail_message", "error." + e.getMessage());
                resp.sendRedirect("editRoomAction?room_id=" + roomId);
                return;
            }
            System.out.println(roomName);

            room = editRoomMap.get(l);
            room.setName(roomName);
            room.setDescription(roomDescription);

            try {
                roomDAO.update(room, l);
            } catch (DBException e) {
                req.setAttribute("fail_message", "error.someDBError");
                resp.sendRedirect("editRoomAction?room_id=" + roomId);
                return;
            }

        }

        resp.sendRedirect("editRoomAction?room_id=" + roomId);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        RoomDAO roomDAO = factory.getRoomDAO();
        LocaleDAO localeDAO = factory.getLocaleDAO();
        RoomCategoryDAO roomCategoryDAO = factory.getRoomCategoryDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        long roomId = validator.validateAndGetLong("room_id", -1L);

        Map<String, Room> editRoomMap = new HashMap<>();

        for (Locale loc : localeDAO.findALL().values()) {
            editRoomMap.put(loc.getName(), new Room());
        }

        if (req.getParameter("new") == null) {
            editRoomMap = roomDAO.findByIdGroupByLocale(roomId);
        }

        req.setAttribute("editRoomMap", editRoomMap);
        req.setAttribute("roomId", roomId);
        req.setAttribute("categories", roomCategoryDAO.findALL(locale));

        HotelHelper.proceedMessages(req);

        req.getRequestDispatcher(Constants.EDIT_ROOM_URL).forward(req, resp);

    }

}
