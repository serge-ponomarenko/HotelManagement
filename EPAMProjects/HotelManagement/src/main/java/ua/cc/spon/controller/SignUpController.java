package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.dao.*;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.exception.InvalidEmailParameterException;
import ua.cc.spon.exception.InvalidPasswordParameterException;
import ua.cc.spon.exception.UserIsAlreadyRegisteredException;
import ua.cc.spon.service.LoginService;
import ua.cc.spon.service.RequestParametersValidatorService;

import java.io.IOException;

import static ua.cc.spon.util.Constants.*;

@WebServlet("/signUpAction")
public class SignUpController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignUpController.class);

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

        req.getRequestDispatcher(SIGN_UP_URL).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        UserDAO userDAO = factory.getUserDAO();
        UserSettingsDAO userSettingsDAO = factory.getUserSettingsDAO();

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        String userFirstName;
        String userLastName;
        String userLocale;
        String userEmail;
        String userPassword;

        try {
            userEmail = validator.validateAndGetString("email",
                    EMAIL_PATTERN,
                    new InvalidEmailParameterException());
            userPassword = validator.validateAndGetString("password",
                    PASSWORD_PATTERN,
                    new InvalidPasswordParameterException());
            userFirstName = validator.validateAndGetString("firstName", FIRST_LAST_NAME_PATTERN, new IllegalArgumentException());
            userLastName = validator.validateAndGetString("lastName", FIRST_LAST_NAME_PATTERN, new IllegalArgumentException());
            userLocale = validator.validateAndGetString("locale", LOCALE_PATTERN, new IllegalArgumentException());
        } catch (InvalidEmailParameterException | InvalidPasswordParameterException e) {
            LOGGER.warn(e.getMessage());
            resp.sendRedirect("signUpAction?msg=" + e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            LOGGER.warn(e.getMessage());
            resp.sendRedirect("signUpAction?msg=invalidParameters");
            return;
        }

        User user = new User();
        user.setEmail(userEmail);
        user.setPassword(userPassword);
        user.setFirstName(userFirstName);
        user.setLastName(userLastName);
        user.setRole(User.Role.USER);

        transaction.initTransaction(userDAO, userSettingsDAO);

        try {
            userDAO.insertUser(user);

            UserSettings userSettings = new UserSettings();
            userSettings.setUserId(user.getId());
            userSettings.setLocale(userLocale);
            userSettingsDAO.insert(userSettings);

            transaction.commit();

            LOGGER.info("User #{} - {} created", user.getId(), user.getEmail());

            LoginService.initializeSession(req, resp, user, false);

            resp.sendRedirect("indexAction");

        } catch (UserIsAlreadyRegisteredException e) {
            LOGGER.warn(e.getMessage());
            transaction.rollback();
            resp.sendRedirect("signUpAction?msg=" + e.getMessage() + "&locale=" + userLocale);
        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            transaction.rollback();
            resp.sendRedirect("signUnAction?msg=invalidParameters");
        } finally {
            transaction.endTransaction();
        }

    }

}
