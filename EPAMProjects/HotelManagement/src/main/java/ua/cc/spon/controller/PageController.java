package ua.cc.spon.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.service.PaginatorService;

import java.io.IOException;

@WebServlet("/pageAction")
public class PageController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PaginatorService.proceedParametersToSession(req);

        if (req.getHeader("referer") == null) {
            resp.sendRedirect("indexAction");
            return;
        }

        resp.sendRedirect(req.getHeader("referer"));

    }


}
