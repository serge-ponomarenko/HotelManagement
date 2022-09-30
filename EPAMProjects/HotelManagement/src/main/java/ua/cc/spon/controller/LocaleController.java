package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.EntityTransaction;
import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;
import ua.cc.spon.service.RequestParametersValidatorService;

import java.io.IOException;

@WebServlet("/localeAction")
public class LocaleController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocaleController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        UserSettingsDAO userSettingsDAO = factory.getUserSettingsDAO();

        if (req.getHeader("referer") == null) {
            resp.sendRedirect("indexAction");
            return;
        }

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);
        String locale = validator.validateAndGetString("locale", "");

        UserSettings userSettings = (UserSettings) req.getSession().getAttribute("userSettings");

        userSettings.setLocale(locale);

        transaction.init(userSettingsDAO);

        try {
            userSettingsDAO.update(userSettings);
            req.getSession().setAttribute("userSettings", userSettings);
        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
            req.getSession().setAttribute("fail_message", "error.invalidLocaleParameter");
        } finally {
            transaction.end();
        }

        resp.sendRedirect(req.getHeader("referer"));

    }


}
