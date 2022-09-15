package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.ReservationDAO;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.service.PaginatorService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet({"/manageUsersAction"})
public class UsersListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        ReservationDAO reservationDAO = factory.getReservationDAO();
        UserDAO userDAO = factory.getUserDAO();

        String locale = ((UserSettings) req.getSession().getAttribute("userSettings")).getLocale();
        User user = ((User) req.getSession().getAttribute("user"));

        PaginatorService paginator =
                new PaginatorService(req, "users", new Integer[]{10, 20, 50});

        List<User> users = userDAO.findALL();

        users = paginator.generateSublist(users);
        paginator.setRequestAttributes();

        req.setAttribute("users", users);

        req.getRequestDispatcher("users.jsp").forward(req, resp);

    }

}
