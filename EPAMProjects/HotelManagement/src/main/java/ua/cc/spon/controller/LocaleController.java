package ua.cc.spon.controller;

import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.UserSettings;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

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
