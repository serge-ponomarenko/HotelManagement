package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.UserSettings;

import java.io.IOException;

@WebServlet("/localeAction")
public class LocaleController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        UserSettingsDAO userSettingsDAO = factory.getUserSettingsDAO();

        String locale = req.getParameter("locale");

        UserSettings userSettings = (UserSettings) req.getSession().getAttribute("userSettings");

        userSettings.setLocale(locale);   // TODO: 27.08.2022 VALIDATION!!

        req.getSession().setAttribute("userSettings", userSettings);

        userSettingsDAO.update(userSettings);

        resp.sendRedirect(req.getHeader("referer"));

    }


}
