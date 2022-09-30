package ua.cc.spon.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.EntityTransaction;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.exception.UserNotFoundException;
import ua.cc.spon.service.LoginService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static ua.cc.spon.util.Constants.SIGN_IN_URL;
import static ua.cc.spon.util.Constants.SIGN_UP_URL;

@WebFilter(filterName = "/AuthenticationFilter", urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    private static final List<String> allowedPagesWithoutLogin = Arrays.asList(
            "/" + SIGN_IN_URL, "/" + SIGN_UP_URL,
            "/dist", "/static",
            "/signInAction", "/signUpAction");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getServletPath();

        Cookie[] cookies = req.getCookies();
        String userHash = null;
        Cookie userCookie = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("userHash")) {
                    userHash = cookie.getValue();
                    userCookie = cookie;
                    break;
                }
            }
        }

        HttpSession session = req.getSession(false);

        if (session == null && userHash != null) {

            ServletContext context = req.getServletContext();
            DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
            EntityTransaction transaction = new EntityTransaction();

            UserDAO userDAO = factory.getUserDAO();
            UserSettingsDAO userSettingsDAO = factory.getUserSettingsDAO();

            transaction.initTransaction(userDAO, userSettingsDAO);

            UserSettings userSettings;
            User user;
            try {
                userSettings = userSettingsDAO.findByHash(userHash);
                user = userDAO.find(userSettings.getUserId());
                if (user == null) throw new UserNotFoundException();
                transaction.commit();
                LoginService.initializeSession(req, res, user, false);
                LOGGER.info("User {} - session restored", user.getEmail());
                res.sendRedirect("indexAction");

            } catch (UserNotFoundException | DaoException e) {
                LOGGER.error(e.getMessage(), e);
                transaction.rollback();
                userCookie.setMaxAge(0);
                res.addCookie(userCookie);
                res.sendRedirect("signInAction");

            } finally {
                transaction.endTransaction();
            }

            return;

        }

        if ((session == null || session.getAttribute("user") == null || session.getAttribute("userSettings") == null)
                && allowedPagesWithoutLogin.stream().noneMatch(uri::startsWith)) {
            res.sendRedirect("signInAction");
        } else {
            chain.doFilter(request, response);
        }


    }

}
