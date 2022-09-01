package ua.cc.spon.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/pageAction")
public class PageController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getParameter("page") != null) {
            int page = Integer.parseInt(req.getParameter("page"));
            req.getSession().setAttribute("page", page);
        }

        if (req.getParameter("showBy") != null) {
            int showBy = Integer.parseInt(req.getParameter("showBy"));
            req.getSession().setAttribute("showBy", showBy);
        }

        if (req.getParameter("indexSortBy") != null) {
            String indexSortBy = req.getParameter("indexSortBy");
            req.getSession().setAttribute("indexSortBy", indexSortBy);
        }

        resp.sendRedirect(req.getHeader("referer"));

    }


}
