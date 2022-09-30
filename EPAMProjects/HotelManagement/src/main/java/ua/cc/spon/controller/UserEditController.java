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
import ua.cc.spon.exception.*;
import ua.cc.spon.service.LoginService;
import ua.cc.spon.service.RequestParametersValidatorService;
import ua.cc.spon.util.HotelHelper;

import java.io.IOException;

import static ua.cc.spon.util.Constants.*;

@WebServlet({"/editUserAction"})
public class UserEditController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEditController.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        UserDAO userDAO = factory.getUserDAO();

        User user = ((User) req.getSession().getAttribute("user"));

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        int userId = -1;
        String userFirstName;
        String userLastName;
        String oldPassword;
        String newPassword;
        User.Role role = null;

        try {
            userId = validator.validateAndGetInt("user_id", -1);
            userFirstName = validator.validateAndGetString("firstName", FIRST_LAST_NAME_PATTERN, new IllegalArgumentException());
            userLastName = validator.validateAndGetString("lastName", FIRST_LAST_NAME_PATTERN, new IllegalArgumentException());
            oldPassword = validator.validateAndGetString("oldPassword", "");
            newPassword = validator.validateAndGetString("newPassword", "");
            if (req.getParameter("role") != null) role = User.Role.valueOf(req.getParameter("role"));

        } catch (InvalidEmailParameterException | InvalidPasswordParameterException e) {
            LOGGER.warn(e.getMessage());
            req.getSession().setAttribute("fail_message", "error." + e.getMessage());
            resp.sendRedirect("editUserAction?user_id=" + userId);
            return;
        } catch (IllegalArgumentException e) {
            LOGGER.warn(e.getMessage());
            req.getSession().setAttribute("fail_message", "error.invalidParameters");
            resp.sendRedirect("editUserAction?user_id=" + userId);
            return;
        }

        if (user.getRole() != User.Role.ADMINISTRATOR && user.getId() != userId) {
            resp.sendRedirect("indexAction");
            return;
        }

        User editUser;

        transaction.initTransaction(userDAO);

        try {
            editUser = userDAO.find(userId);
            if (editUser == null) throw new UserNotFoundException();

            if (!oldPassword.isEmpty() && !newPassword.isEmpty()) {
                editUser = userDAO.findByEmailAndPassword(editUser.getEmail(), oldPassword);
            }
        } catch (UserNotFoundException e) {
            LOGGER.warn(e.getMessage());
            req.getSession().setAttribute("fail_message", "error.noUserFound");
            resp.sendRedirect("manageUsersAction");
            return;
        } catch (IllegalPasswordException e) {
            LOGGER.warn(e.getMessage());
            req.getSession().setAttribute("fail_message", "error.wrongPassword");
            resp.sendRedirect("editUserAction?user_id=" + userId);
            return;
        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            req.getSession().setAttribute("fail_message", "error.someDBError");
            resp.sendRedirect("editUserAction?user_id=" + userId);
            return;
        }

        editUser.setFirstName(userFirstName);
        editUser.setLastName(userLastName);
        if (role != null) editUser.setRole(role);
        if (!oldPassword.isEmpty() && !newPassword.isEmpty()) {
            try {
                newPassword = validator.validateAndGetString("newPassword",
                        PASSWORD_PATTERN,
                        new InvalidPasswordParameterException());
            } catch (InvalidPasswordParameterException e) {
                LOGGER.warn(e.getMessage());
                req.getSession().setAttribute("fail_message", "error." + e.getMessage());
                resp.sendRedirect("editUserAction?user_id=" + userId);
                return;
            }
            editUser.setPassword(newPassword);
        }

        try {
            userDAO.update(editUser);
            transaction.commit();

            if (user.getId() == editUser.getId()) {
                LoginService.initializeSession(req, resp, editUser, false);
            }

            LOGGER.info("User #{} saved", editUser.getId());

            req.getSession().setAttribute("success_message", "edit-user.update-successful");
            resp.sendRedirect("editUserAction?user_id=" + editUser.getId());

        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            transaction.rollback();
            req.getSession().setAttribute("fail_message", "edit-user.update-fail");
            resp.sendRedirect("editUserAction?user_id=" + editUser.getId());
        } finally {
            transaction.endTransaction();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        UserDAO userDAO = factory.getUserDAO();

        User user = ((User) req.getSession().getAttribute("user"));

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        int userId = validator.validateAndGetInt("user_id", -1);

        if (user.getRole() != User.Role.ADMINISTRATOR && user.getId() != userId) {
            resp.sendRedirect("indexAction");
            return;
        }

        User editUser;

        transaction.init(userDAO);

        try {
            editUser = userDAO.find(userId);

            if (editUser == null) throw new UserNotFoundException();

            req.setAttribute("editUser", editUser);
            req.setAttribute("roles", User.Role.values());

            HotelHelper.proceedMessages(req);

            req.getRequestDispatcher(EDIT_USER_URL).forward(req, resp);

        } catch (UserNotFoundException e) {
            LOGGER.warn(e.getMessage());
            req.getSession().setAttribute("fail_message", "error.noUserFound");
            resp.sendRedirect("manageUsersAction");
        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            req.getSession().setAttribute("fail_message", "error.someDBError");
            resp.sendRedirect("manageUsersAction");
        } finally {
            transaction.end();
        }

    }

}
