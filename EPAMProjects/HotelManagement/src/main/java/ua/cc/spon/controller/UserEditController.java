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
import ua.cc.spon.exception.IllegalPasswordException;
import ua.cc.spon.exception.NoUserFoundException;
import ua.cc.spon.service.LoginService;

import java.io.IOException;
import java.util.Optional;

@WebServlet({"/editUserAction"})
public class UserEditController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        UserDAO userDAO = factory.getUserDAO();

        User user = ((User) req.getSession().getAttribute("user"));

        long userId = Long.parseLong(Optional.ofNullable(req.getParameter("user_id")).orElse("-1"));

        if (user.getRole() != User.Role.ADMINISTRATOR && user.getId() != userId) {
            resp.sendRedirect("indexAction");
            return;
        }

        String userFirstName = req.getParameter("firstName");
        String userLastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String oldPassword = req.getParameter("oldPassword");
        String newPassword = req.getParameter("newPassword");

        User.Role role = null;
        if (req.getParameter("role") != null) role = User.Role.valueOf(req.getParameter("role"));

        User editUser = null;

        try {
            editUser = userDAO.find(userId);

            if (!oldPassword.isEmpty() && !newPassword.isEmpty()) {
                editUser = userDAO.findByEmailAndPassword(editUser.getEmail(), oldPassword);
            }
        } catch (NoUserFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalPasswordException e) {
            throw new RuntimeException(e);
        }

        editUser.setFirstName(userFirstName);
        editUser.setLastName(userLastName);
        if (!oldPassword.isEmpty() && !newPassword.isEmpty())
            editUser.setPassword(newPassword);
        if (role != null) editUser.setRole(role);

        userDAO.update(editUser);

        if (user.getId() == editUser.getId()) LoginService.initializeSession(req, resp, editUser, false);

        req.getSession().setAttribute("message", "edit-user.update-successful");
        resp.sendRedirect("editUserAction?user_id=" + editUser.getId());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        UserDAO userDAO = factory.getUserDAO();

        User user = ((User) req.getSession().getAttribute("user"));

        long userId = Long.parseLong(Optional.ofNullable(req.getParameter("user_id")).orElse("-1"));

        if (user.getRole() != User.Role.ADMINISTRATOR && user.getId() != userId) {
            resp.sendRedirect("indexAction");
            return;
        }

        User editUser = null;
        try {
            editUser = userDAO.find(userId);
        } catch (NoUserFoundException e) {
            throw new RuntimeException(e);
        }

        if (editUser != null) {

            req.setAttribute("editUser", editUser);
            req.setAttribute("roles", User.Role.values());

        }

        String message = null;
        if (req.getSession().getAttribute("message") != null) {
            message = (String) req.getSession().getAttribute("message");
            req.getSession().removeAttribute("message");
            req.setAttribute("message", message);
        }

        req.getRequestDispatcher("edit-user.jsp").forward(req, resp);

    }

}
