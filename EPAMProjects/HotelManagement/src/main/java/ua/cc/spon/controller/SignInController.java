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
import ua.cc.spon.db.dao.LocaleDAO;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.*;
import ua.cc.spon.service.LoginService;
import ua.cc.spon.service.RequestParametersValidatorService;

import java.io.IOException;

import static ua.cc.spon.util.Constants.*;

@WebServlet("/signInAction")
public class SignInController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignInController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        LocaleDAO localeDAO = factory.getLocaleDAO();
        transaction.init(localeDAO);
        try {
            req.setAttribute("locales", localeDAO.findAllMapByName());
        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            resp.sendRedirect("errorAction");
        } finally {
            transaction.end();
        }

        req.getRequestDispatcher(SIGN_IN_URL).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        UserDAO userDAO = factory.getUserDAO();

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        String userEmail;
        String password;
        boolean remember;

        User user;

        try {
            userEmail = validator.validateAndGetString("email",
                    EMAIL_PATTERN,
                    new InvalidEmailParameterException());
            password = validator.validateAndGetString("password",
                    PASSWORD_PATTERN,
                    new InvalidPasswordParameterException());
            remember = validator.validateAndGetBoolean("remember");
        } catch (InvalidEmailParameterException | InvalidPasswordParameterException e) {
            LOGGER.warn(e.getMessage());
            resp.sendRedirect("signInAction?msg=" + e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            LOGGER.warn(e.getMessage());
            resp.sendRedirect("signInAction?msg=invalidParameters");
            return;
        }

        transaction.init(userDAO);

        try {
            user = userDAO.findByEmailAndPassword(userEmail, password);

            if (user == null) throw new UserNotFoundException();

            LoginService.initializeSession(req, resp, user, remember);

            resp.sendRedirect("indexAction");

        } catch (IllegalPasswordException | UserNotFoundException e) {
            LOGGER.warn(e.getMessage());
            resp.sendRedirect("signInAction?msg=" + e.getMessage() + "&email=" + userEmail);
        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            resp.sendRedirect("signInAction?msg=invalidParameters");
        } finally {
            transaction.end();
        }

    }

}
