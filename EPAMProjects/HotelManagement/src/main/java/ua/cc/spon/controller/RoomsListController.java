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
import ua.cc.spon.db.dao.RoomDAO;
import ua.cc.spon.db.entity.Room;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.service.PaginatorService;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static ua.cc.spon.util.Constants.ROOMS_URL;

@WebServlet({"/manageRoomsAction"})
public class RoomsListController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomsListController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        RoomDAO roomDAO = factory.getRoomDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        transaction.init(roomDAO);

        try {

            List<Room> rooms = roomDAO.findAll(locale);

            rooms.sort(Comparator.comparingInt(Room::getId));

            PaginatorService paginator =
                    new PaginatorService(req, "rooms", new Integer[]{10, 20, 50});
            rooms = paginator.generateSublist(rooms);
            paginator.setRequestAttributes();

            req.setAttribute("rooms", rooms);

            HotelHelper.proceedMessages(req);

            req.getRequestDispatcher(ROOMS_URL).forward(req, resp);

        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            req.getSession().setAttribute("fail_message", "error.someDBError");
            resp.sendRedirect("indexAction");
        } finally {
            transaction.end();
        }

    }

}
