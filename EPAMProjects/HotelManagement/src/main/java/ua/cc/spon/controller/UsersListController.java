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
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.service.PaginatorService;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.util.List;

import static ua.cc.spon.util.Constants.USERS_URL;

@WebServlet({"/manageUsersAction"})
public class UsersListController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersListController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        UserDAO userDAO = factory.getUserDAO();

        transaction.init(userDAO);

        try {

            List<User> users = userDAO.findAll();

            PaginatorService paginator =
                    new PaginatorService(req, "users", new Integer[]{10, 20, 50});
            users = paginator.generateSublist(users);
            paginator.setRequestAttributes();

            req.setAttribute("users", users);

            HotelHelper.proceedMessages(req);

            req.getRequestDispatcher(USERS_URL).forward(req, resp);

        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            req.getSession().setAttribute("fail_message", "error.someDBError");
            resp.sendRedirect("indexAction");
        } finally {
            transaction.end();
        }

    }

}
