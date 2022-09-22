package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.service.PaginatorService;
import ua.cc.spon.util.Constants;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;
import java.util.List;

@WebServlet({"/manageUsersAction"})
public class UsersListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        UserDAO userDAO = factory.getUserDAO();

        PaginatorService paginator =
                new PaginatorService(req, "users", new Integer[]{10, 20, 50});

        List<User> users = userDAO.findALL();

        users = paginator.generateSublist(users);
        paginator.setRequestAttributes();

        req.setAttribute("users", users);

        HotelHelper.proceedMessages(req);

        req.getRequestDispatcher(Constants.USERS_URL).forward(req, resp);

    }

}
