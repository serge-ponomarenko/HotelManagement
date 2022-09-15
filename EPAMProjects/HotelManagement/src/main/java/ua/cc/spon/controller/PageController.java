package ua.cc.spon.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.service.PaginatorService;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Optional;

@WebServlet("/pageAction")
public class PageController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PaginatorService.proceedParametersToSession(req);

        resp.sendRedirect(req.getHeader("referer"));

    }


}
