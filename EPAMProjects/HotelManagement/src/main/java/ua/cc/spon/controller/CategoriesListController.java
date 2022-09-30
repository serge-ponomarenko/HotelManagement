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
import ua.cc.spon.db.dao.RoomCategoryDAO;
import ua.cc.spon.db.entity.RoomCategory;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static ua.cc.spon.util.Constants.CATEGORIES_URL;

@WebServlet({"/manageCategoriesAction"})
public class CategoriesListController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoriesListController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        RoomCategoryDAO roomCategoryDAO = factory.getRoomCategoryDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();

        List<RoomCategory> roomCategories;

        transaction.init(roomCategoryDAO);

        try {
            roomCategories = roomCategoryDAO.findAll(locale);

            roomCategories.sort(Comparator.comparingInt(RoomCategory::getId));

            req.setAttribute("roomCategories", roomCategories);

            HotelHelper.proceedMessages(req);

            req.getRequestDispatcher(CATEGORIES_URL).forward(req, resp);

        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            req.getSession().setAttribute("fail_message", "error.someDBError");
            resp.sendRedirect("indexAction");
        } finally {
            transaction.end();
        }

    }

}
