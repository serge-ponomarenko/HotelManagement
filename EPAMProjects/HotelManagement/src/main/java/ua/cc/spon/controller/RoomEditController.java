package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.dao.*;
import ua.cc.spon.db.entity.Locale;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.exception.EmptyDescriptionException;
import ua.cc.spon.exception.EmptyNameException;
import ua.cc.spon.service.RequestParametersValidatorService;
import ua.cc.spon.util.Constants;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.cc.spon.util.Constants.EDIT_ROOM_URL;

@WebServlet({"/editRoomAction"})
public class RoomEditController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomEditController.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        RoomDAO roomDAO = factory.getRoomDAO();
        RoomCategoryDAO roomCategoryDAO = factory.getRoomCategoryDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        int roomId;

        try {
            roomId = validator.validateAndGetInt("room_id", new IllegalArgumentException());

        } catch (IllegalArgumentException e) {
            LOGGER.warn(e.getMessage());
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect("manageRoomsAction");
            return;
        }

        transaction.init(roomDAO);

        if (req.getParameter("action") != null && req.getParameter("action").equals("delete")) {
            try {
                roomDAO.delete(roomId);
            } catch (DaoException e) {
                req.getSession().setAttribute("fail_message", "error.someDBError");
            } finally {
                transaction.end();
            }
            resp.sendRedirect("manageRoomsAction");
            return;
        }

        Map<String, Room> editRoomMap;

        transaction.initTransaction(roomDAO, roomCategoryDAO);

        try {

            if (roomId == -1) {
                Room room = new Room();
                roomDAO.insert(room);
                roomId = room.getId();
            }

            editRoomMap = roomDAO.findByIdGroupByLocale(roomId);
            if (editRoomMap.isEmpty()) throw new DaoException("No rooms obtained");

            String roomNumber;
            int roomOccupancy;
            int roomCategoryId;
            BigDecimal roomPrice;

            try {
                roomNumber = validator.validateAndGetString("number", Constants.ANY_SYMBOLS, new IllegalArgumentException());
                roomOccupancy = validator.validateAndGetInt("occupancy", new IllegalArgumentException());
                roomCategoryId = validator.validateAndGetInt("room-category", new IllegalArgumentException());
                roomPrice = validator.validateAndGetBigDecimal("price", new IllegalArgumentException());
            } catch (IllegalArgumentException e) {
                LOGGER.warn(e.getMessage());
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

            roomDAO.update(room, locale);

            editRoomMap = roomDAO.findByIdGroupByLocale(roomId);

            for (String l : editRoomMap.keySet()) {
                String roomName;
                String roomDescription;
                try {
                    roomName = validator.validateAndGetString("name_" + l, Constants.ANY_SYMBOLS, new EmptyNameException());
                    roomDescription = validator.validateAndGetString("description_" + l, Constants.ANY_SYMBOLS, new EmptyDescriptionException());
                } catch (EmptyNameException | EmptyDescriptionException e) {
                    LOGGER.warn(e.getMessage());
                    req.getSession().setAttribute("fail_message", "error." + e.getMessage());
                    resp.sendRedirect("editRoomAction?room_id=" + roomId);
                    return;
                }

                room = editRoomMap.get(l);
                room.setName(roomName);
                room.setDescription(roomDescription);

                roomDAO.update(room, l);
            }

            transaction.commit();

            LOGGER.info("Room #{} saved", room.getId());

        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            transaction.rollback();
            req.getSession().setAttribute("fail_message", "error.someDBError");
        } finally {
            transaction.endTransaction();
        }
        resp.sendRedirect("editRoomAction?room_id=" + roomId);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        RoomDAO roomDAO = factory.getRoomDAO();
        LocaleDAO localeDAO = factory.getLocaleDAO();
        RoomCategoryDAO roomCategoryDAO = factory.getRoomCategoryDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        int roomId = validator.validateAndGetInt("room_id", -1);

        Map<String, Room> editRoomMap = new HashMap<>();

        transaction.initTransaction(localeDAO, roomDAO, roomCategoryDAO);

        try {
            List<RoomCategory> roomCategories = roomCategoryDAO.findAll(locale);

            for (Locale loc : localeDAO.findAllMapByName().values()) {
                editRoomMap.put(loc.getName(), new Room());
            }

            if (req.getParameter("new") == null) {
                editRoomMap = roomDAO.findByIdGroupByLocale(roomId);
            }

            transaction.commit();

            req.setAttribute("editRoomMap", editRoomMap);
            req.setAttribute("roomId", roomId);
            req.setAttribute("categories", roomCategories);

            HotelHelper.proceedMessages(req);

            req.getRequestDispatcher(EDIT_ROOM_URL).forward(req, resp);

        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            req.getSession().setAttribute("fail_message", "error.someDBError");
            resp.sendRedirect("manageRoomsAction");
        } finally {
            transaction.endTransaction();
        }

    }

}
