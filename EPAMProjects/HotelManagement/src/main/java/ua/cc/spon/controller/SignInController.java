package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.LocaleDAO;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.dao.UserSettingsDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.IllegalPasswordException;
import ua.cc.spon.exception.NoUserFoundException;
import ua.cc.spon.service.LoginService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@WebServlet("/signInAction")
public class SignInController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        LocaleDAO localeDAO = factory.getLocaleDAO();

        req.setAttribute("locales", localeDAO.findALL());

        req.getRequestDispatcher("sign-in.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        UserDAO userDAO = factory.getUserDAO();

        String userEmail = req.getParameter("email");
        String password = req.getParameter("password");
        boolean remember = req.getParameter("remember") != null;

        User user = null;

        try {
            user = userDAO.findByEmailAndPassword(userEmail, password);

        } catch (NoUserFoundException e) {
            resp.sendRedirect("signInAction?msg=noUserFound");
        } catch (IllegalPasswordException e) {
            resp.sendRedirect("signInAction?msg=wrongPassword&email=" + userEmail);
        }

        if (user != null) {
            LoginService.initializeSession(req, resp, user, remember);

            resp.sendRedirect("indexAction");
        }

    }

}
