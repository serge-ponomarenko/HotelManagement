package ua.cc.spon.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.NoUserFoundException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter(filterName = "/AuthorisationFilter", urlPatterns = {"/*"})
public class AuthorisationFilter implements Filter {

    private static final List<String> allowedPages
            = Arrays.asList("/sign-in.jsp", "/sign-up.jsp", "/index.jsp",
                            "/dist", "/static",
                            "/signInAction", "/signUpAction", "/indexAction");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");

        String uri = req.getRequestURI();

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

            UserSettings userSettings = userSettingsDAO.findByHash(userHash);
            User user = null;
            try {
                user = userDAO.find(userSettings.getUserId());
            } catch (NoUserFoundException e) {
                userCookie.setMaxAge(0);
                res.addCookie(userCookie);
                res.sendRedirect("sign-in.jsp");
                return;
            }

            HttpSession httpSession = req.getSession();
            httpSession.setAttribute("user", user);
            httpSession.setAttribute("userSettings", userSettings);
            httpSession.setMaxInactiveInterval(30 * 60);

            res.sendRedirect("indexAction");
            return;

        }

        if ((session == null || session.getAttribute("user") == null || session.getAttribute("userSettings") == null)
                && allowedPages.stream().noneMatch(uri::startsWith)) {
            res.sendRedirect("sign-in.jsp");
        } else {
            chain.doFilter(request, response);
        }


    }

}
