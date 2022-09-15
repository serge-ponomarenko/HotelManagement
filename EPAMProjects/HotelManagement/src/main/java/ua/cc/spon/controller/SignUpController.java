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
import ua.cc.spon.exception.UserIsAlreadyRegisteredException;
import ua.cc.spon.service.LoginService;

import java.io.IOException;

@WebServlet("/signUpAction")
public class SignUpController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        LocaleDAO localeDAO = factory.getLocaleDAO();

        req.setAttribute("locales", localeDAO.findALL());

        req.getRequestDispatcher("sign-up.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        UserDAO userDAO = factory.getUserDAO();
        UserSettingsDAO userSettingsDAO = factory.getUserSettingsDAO();

        String userFirstName = req.getParameter("firstName");
        String userLastName = req.getParameter("lastName");
        String userEmail = req.getParameter("email");
        String userPassword = req.getParameter("password");
        String userLocale = req.getParameter("locale");

        User user = new User();
        user.setEmail(userEmail);
        user.setPassword(userPassword);
        user.setFirstName(userFirstName);
        user.setLastName(userLastName);
        user.setRole(User.Role.USER);

        try {
            userDAO.insert(user);
        } catch (UserIsAlreadyRegisteredException e) {
            resp.sendRedirect("signUpAction?msg=userAlreadyRegistered&locale=" + userLocale);
        }

        if (user.getId() > 0) {
            UserSettings userSettings = new UserSettings();
            userSettings.setUserId(user.getId());
            userSettings.setLocale(userLocale);
            userSettingsDAO.insert(userSettings);

            LoginService.initializeSession(req, resp, user, false);

            resp.sendRedirect("indexAction");
        }

    }

}
