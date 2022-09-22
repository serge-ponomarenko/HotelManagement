package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.LocaleDAO;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DBException;
import ua.cc.spon.exception.InvalidEmailParameterException;
import ua.cc.spon.exception.InvalidPasswordParameterException;
import ua.cc.spon.exception.UserIsAlreadyRegisteredException;
import ua.cc.spon.service.LoginService;
import ua.cc.spon.service.RequestParametersValidatorService;

import java.io.IOException;

import static ua.cc.spon.util.Constants.*;

@WebServlet("/signUpAction")
public class SignUpController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        LocaleDAO localeDAO = factory.getLocaleDAO();

        req.setAttribute("locales", localeDAO.findALL());

        req.getRequestDispatcher(SIGN_UP_URL).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
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
            resp.sendRedirect("signUpAction?msg=" + e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            resp.sendRedirect("signUpAction?msg=invalidParameters");
            return;
        }

        User user = new User();
        user.setEmail(userEmail);
        user.setPassword(userPassword);
        user.setFirstName(userFirstName);
        user.setLastName(userLastName);
        user.setRole(User.Role.USER);

        try {
            userDAO.insert(user);

            UserSettings userSettings = new UserSettings();
            userSettings.setUserId(user.getId());
            userSettings.setLocale(userLocale);
            userSettingsDAO.insert(userSettings);

            LoginService.initializeSession(req, resp, user, false);

        } catch (UserIsAlreadyRegisteredException e) {
            resp.sendRedirect("signUpAction?msg=" + e.getMessage() + "&locale=" + userLocale);
            return;
        } catch (DBException e) {
            resp.sendRedirect("signUnAction?msg=invalidParameters");
            return;
        }

        resp.sendRedirect("indexAction");

    }

}
