package ua.cc.spon.service;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.LocaleDAO;
import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DBException;

import java.util.concurrent.TimeUnit;

public class LoginService {

    public static void initializeSession(HttpServletRequest req, HttpServletResponse resp, User user, boolean remember) throws DBException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        UserSettingsDAO userSettingsDAO = factory.getUserSettingsDAO();
        UserSettings userSettings = userSettingsDAO.findByUserId(user.getId());
        LocaleDAO localeDAO = factory.getLocaleDAO();

        HttpSession session = req.getSession();
        session.setAttribute("locales", localeDAO.findALL());
        session.setAttribute("user", user);
        session.setAttribute("userSettings", userSettings);
        session.setMaxInactiveInterval(30 * 60);

        if (remember) {
            String userHash = userSettings.generateHash();
            userSettingsDAO.update(userSettings);

            Cookie userCookieHash = new Cookie("userHash", userHash);
            userCookieHash.setMaxAge((int) TimeUnit.DAYS.toSeconds(10));
            resp.addCookie(userCookieHash);
        }
    }

}
