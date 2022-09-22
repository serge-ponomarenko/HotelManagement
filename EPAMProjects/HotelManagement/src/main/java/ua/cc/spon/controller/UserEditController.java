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
import ua.cc.spon.exception.*;
import ua.cc.spon.service.LoginService;
import ua.cc.spon.service.RequestParametersValidatorService;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;

import static ua.cc.spon.util.Constants.*;

@WebServlet({"/editUserAction"})
public class UserEditController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        UserDAO userDAO = factory.getUserDAO();

        User user = ((User) req.getSession().getAttribute("user"));

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        long userId = -1;
        String userFirstName;
        String userLastName;
        String oldPassword;
        String newPassword;
        User.Role role = null;

        try {
            userId = validator.validateAndGetLong("user_id", -1L);
            userFirstName = validator.validateAndGetString("firstName", FIRST_LAST_NAME_PATTERN, new IllegalArgumentException());
            userLastName = validator.validateAndGetString("lastName", FIRST_LAST_NAME_PATTERN, new IllegalArgumentException());
            oldPassword = validator.validateAndGetString("oldPassword", "");
            newPassword = validator.validateAndGetString("newPassword","");
            if (req.getParameter("role") != null) role = User.Role.valueOf(req.getParameter("role"));

        } catch (InvalidEmailParameterException | InvalidPasswordParameterException e) {
            req.getSession().setAttribute("fail_message", "error." + e.getMessage());
            resp.sendRedirect("editUserAction?user_id=" + userId);
            return;
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect("editUserAction?user_id=" + userId);
            return;
        }

        if (user.getRole() != User.Role.ADMINISTRATOR && user.getId() != userId) {
            resp.sendRedirect("indexAction");
            return;
        }

        User editUser;

        try {
            editUser = userDAO.find(userId);

            if (!oldPassword.isEmpty() && !newPassword.isEmpty()) {
                editUser = userDAO.findByEmailAndPassword(editUser.getEmail(), oldPassword);
            }
        } catch (NoUserFoundException e) {
            req.getSession().setAttribute("fail_message", "error.noUserFound");
            resp.sendRedirect("manageUsersAction");
            return;
        } catch (IllegalPasswordException e) {
            req.getSession().setAttribute("fail_message", "error.wrongPassword");
            resp.sendRedirect("editUserAction?user_id=" + userId);
            return;
        }

        editUser.setFirstName(userFirstName);
        editUser.setLastName(userLastName);
        if (!oldPassword.isEmpty() && !newPassword.isEmpty()) {
            try {
                newPassword = validator.validateAndGetString("newPassword",
                        PASSWORD_PATTERN,
                        new InvalidPasswordParameterException());
            } catch (InvalidPasswordParameterException e) {
                req.getSession().setAttribute("fail_message", "error." + e.getMessage());
                resp.sendRedirect("editUserAction?user_id=" + userId);
                return;
            }
            editUser.setPassword(newPassword);
        }
        if (role != null) editUser.setRole(role);

        try {
            userDAO.update(editUser);
            if (user.getId() == editUser.getId()) {
                LoginService.initializeSession(req, resp, editUser, false);
            }
        } catch (DBException e) {
            req.getSession().setAttribute("fail_message", "edit-user.update-fail");
            resp.sendRedirect("editUserAction?user_id=" + editUser.getId());
            return;
        }

        req.getSession().setAttribute("success_message", "edit-user.update-successful");
        resp.sendRedirect("editUserAction?user_id=" + editUser.getId());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        UserDAO userDAO = factory.getUserDAO();

        User user = ((User) req.getSession().getAttribute("user"));

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        long userId = validator.validateAndGetLong("user_id", -1L);

        if (user.getRole() != User.Role.ADMINISTRATOR && user.getId() != userId) {
            resp.sendRedirect("indexAction");
            return;
        }

        User editUser;
        try {
            editUser = userDAO.find(userId);
        } catch (NoUserFoundException e) {
            req.getSession().setAttribute("fail_message", "error.noUserFound");
            resp.sendRedirect("manageUsersAction");
            return;
        }

        req.setAttribute("editUser", editUser);
        req.setAttribute("roles", User.Role.values());

        HotelHelper.proceedMessages(req);

        req.getRequestDispatcher("edit-user.jsp").forward(req, resp);

    }

}
