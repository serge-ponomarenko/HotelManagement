package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.IllegalPasswordException;
import ua.cc.spon.exception.NoUserFoundException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@WebServlet("/signInAction")
public class SignInController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        UserDAO userDAO = factory.getUserDAO();
        UserSettingsDAO userSettingsDAO = factory.getUserSettingsDAO();

        String userEmail = req.getParameter("email");
        String password = req.getParameter("password");
        boolean remember = req.getParameter("remember") != null;

        User user = null;

        try {
            user = userDAO.findByEmailAndPassword(userEmail, password);

        } catch (NoUserFoundException e) {
            resp.sendRedirect("sign-in.jsp?msg=noUserFound");
        } catch (IllegalPasswordException e) {
            resp.sendRedirect("sign-in.jsp?msg=wrongPassword&email=" + userEmail);
        }

        if (user != null) {
            UserSettings userSettings = userSettingsDAO.findByUserId(user.getId());

            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userSettings", userSettings);
            session.setMaxInactiveInterval(30 * 60);

            if (remember) {
                saveUserHashToCookies(resp, userSettingsDAO, userSettings);
            }

            resp.sendRedirect("indexAction");
        }

    }

    private static void saveUserHashToCookies(HttpServletResponse resp, UserSettingsDAO userSettingsDAO, UserSettings userSettings) {
        String userHash = userSettings.generateHash();
        userSettingsDAO.update(userSettings);

        Cookie userCookieHash = new Cookie("userHash", userHash);
        userCookieHash.setMaxAge((int) TimeUnit.DAYS.toSeconds(10));
        resp.addCookie(userCookieHash);
    }

}
