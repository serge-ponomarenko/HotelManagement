package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.service.PaginatorService;
import ua.cc.spon.util.Constants;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@WebServlet({"/manageRoomsAction"})
public class RoomsListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        RoomDAO roomDAO = factory.getRoomDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        PaginatorService paginator =
                new PaginatorService(req, "rooms", new Integer[]{10, 20, 50});

        List<Room> rooms = roomDAO.findALL(locale);
        rooms.sort(Comparator.comparingLong(Room::getId));

        rooms = paginator.generateSublist(rooms);
        paginator.setRequestAttributes();

        req.setAttribute("rooms", rooms);

        HotelHelper.proceedMessages(req);

        req.getRequestDispatcher(Constants.ROOMS_URL).forward(req, resp);

    }

}
