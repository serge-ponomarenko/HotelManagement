package ua.cc.spon.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/logoutAction")
public class LogoutController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //invalidate all cookies
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                resp.addCookie(cookie);
            }
        }

        //invalidate the session if exists
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        LOGGER.info("Logout action performed - {}", req.getRemoteAddr());

        resp.sendRedirect("signInAction");


    }

}
