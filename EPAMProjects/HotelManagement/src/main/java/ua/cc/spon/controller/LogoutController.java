package ua.cc.spon.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/logoutAction")
public class LogoutController extends HttpServlet {

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

        resp.sendRedirect("sign-in.jsp");


    }

}
