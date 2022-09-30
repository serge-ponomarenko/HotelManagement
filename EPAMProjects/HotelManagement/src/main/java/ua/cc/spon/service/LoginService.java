package ua.cc.spon.service;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.EntityTransaction;
import ua.cc.spon.db.dao.LocaleDAO;
import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class LoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    private LoginService() {
    }

    public static void initializeSession(HttpServletRequest req, HttpServletResponse resp, User user, boolean remember) throws DaoException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        UserSettingsDAO userSettingsDAO = factory.getUserSettingsDAO();
        LocaleDAO localeDAO = factory.getLocaleDAO();

        transaction.initTransaction(userSettingsDAO, localeDAO);

        try {
            UserSettings userSettings = Optional.ofNullable(userSettingsDAO.findByUserId(user.getId()))
                    .orElseThrow(DaoException::new);

            HttpSession session = req.getSession();
            session.setAttribute("locales", localeDAO.findAllMapByName());
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

            transaction.commit();

            LOGGER.info("Session initialized for user {}", user.getEmail());

        } catch (DaoException e) {
            transaction.rollback();
            throw new DaoException(e.getMessage());
        } finally {
            transaction.endTransaction();
        }

    }

}
