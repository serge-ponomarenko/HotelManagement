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
import ua.cc.spon.db.entity.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet({"/editRoomAction"})
public class RoomEditController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        RoomDAO roomDAO = factory.getRoomDAO();
        RoomCategoryDAO roomCategoryDAO = factory.getRoomCategoryDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();
        User user = ((User) req.getSession().getAttribute("user"));

        String room_id = req.getParameter("room_id");
        System.out.println(room_id);
        long roomId = Long.parseLong(room_id);

        if (req.getParameter("action") != null && req.getParameter("action").equals("delete")) {
            roomDAO.deleteById(roomId);
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

        String roomNumber = req.getParameter("number");
        int roomOccupancy = Integer.parseInt(req.getParameter("occupancy"));
        long roomCategoryId = Long.parseLong(req.getParameter("room-category"));
        BigDecimal roomPrice = BigDecimal.valueOf(Double.parseDouble(req.getParameter("price")));

        Room room = editRoomMap.values().iterator().next();
        room.setNumber(roomNumber);
        room.setOccupancy(roomOccupancy);
        RoomCategory roomCategory = roomCategoryDAO.findByIdGroupByLocale(roomCategoryId).values().iterator().next();
        room.setRoomCategory(roomCategory);
        room.setPrice(roomPrice);
        roomDAO.update(room, locale);

        editRoomMap = roomDAO.findByIdGroupByLocale(roomId);

        for (String l : editRoomMap.keySet()) {
            String roomName = req.getParameter("name_" + l);
            String roomDescription = req.getParameter("description_" + l);

            room = editRoomMap.get(l);
            room.setName(roomName);
            room.setDescription(roomDescription);

            roomDAO.update(room, l);

        }

        resp.sendRedirect("manageRoomsAction");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        RoomDAO roomDAO = factory.getRoomDAO();
        LocaleDAO localeDAO = factory.getLocaleDAO();
        RoomCategoryDAO roomCategoryDAO = factory.getRoomCategoryDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        long roomId = Long.parseLong(Optional.ofNullable(req.getParameter("room_id")).orElse("-1"));

        Map<String, Room> editRoomMap = new HashMap<>();

        for (Locale loc: localeDAO.findALL().values()) {
            editRoomMap.put(loc.getName(), new Room());
        }

        if (req.getParameter("new") == null) {
            editRoomMap = roomDAO.findByIdGroupByLocale(roomId);
        }

        req.setAttribute("editRoomMap", editRoomMap);
        req.setAttribute("roomId", roomId);
        req.setAttribute("categories", roomCategoryDAO.findALL(locale));

        req.getRequestDispatcher("edit-room.jsp").forward(req, resp);

    }

}
