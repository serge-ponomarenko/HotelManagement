package ua.cc.spon.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/pageAction")
public class PageController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int page = Integer.parseInt(Optional.ofNullable(req.getParameter("page")).orElse("1"));

        req.getSession().setAttribute("page", page);

        resp.sendRedirect(req.getHeader("referer"));

    }


}
