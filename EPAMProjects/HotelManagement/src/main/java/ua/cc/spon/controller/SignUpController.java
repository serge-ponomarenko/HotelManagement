package ua.cc.spon.controller;

import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.UserIsAlreadyRegisteredException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/signUpAction")
public class SignUpController extends HttpServlet {

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
        user.setHashPassword(userPassword);
        user.setFirstName(userFirstName);
        user.setLastName(userLastName);
        user.setRole(User.Role.USER);

        try {
            userDAO.insert(user);
        } catch (UserIsAlreadyRegisteredException e) {
            resp.sendRedirect("sign-up.jsp?msg=userAlreadyRegistered&locale=" + userLocale);
        }

        if (user.getId() > 0) {
            UserSettings userSettings = new UserSettings();
            userSettings.setUserId(user.getId());
            userSettings.setLocale(userLocale);
            userSettingsDAO.insert(userSettings);

            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userSettings", userSettings);

            resp.sendRedirect("indexAction");
        }

    }

}