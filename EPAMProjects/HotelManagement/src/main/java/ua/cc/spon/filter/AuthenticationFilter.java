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
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DBException;
import ua.cc.spon.exception.NoUserFoundException;
import ua.cc.spon.service.LoginService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter(filterName = "/AuthenticationFilter", urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final List<String> allowedPagesWithoutLogin
            = Arrays.asList("/sign-in.jsp", "/sign-up.jsp",
                            "/dist", "/static",
                            "/signInAction", "/signUpAction");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

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
            UserDAO userDAO = factory.getUserDAO();
            UserSettingsDAO userSettingsDAO = factory.getUserSettingsDAO();

            UserSettings userSettings;
            User user;
            try {
                userSettings = userSettingsDAO.findByHash(userHash);
                user = userDAO.find(userSettings.getUserId());
                LoginService.initializeSession(req, res, user, false);
            } catch (NoUserFoundException | DBException e) {
                userCookie.setMaxAge(0);
                res.addCookie(userCookie);
                res.sendRedirect("signInAction");
                return;
            }

            res.sendRedirect("indexAction");
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
