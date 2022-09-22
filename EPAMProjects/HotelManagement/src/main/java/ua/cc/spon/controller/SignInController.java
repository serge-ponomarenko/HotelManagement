package ua.cc.spon.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.LocaleDAO;
import ua.cc.spon.db.dao.UserDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.exception.*;
import ua.cc.spon.service.LoginService;
import ua.cc.spon.service.RequestParametersValidatorService;

import java.io.IOException;

import static ua.cc.spon.util.Constants.*;

@WebServlet("/signInAction")
public class SignInController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        LocaleDAO localeDAO = factory.getLocaleDAO();

        req.setAttribute("locales", localeDAO.findALL());

        req.getRequestDispatcher(SIGN_IN_URL).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext context = req.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        UserDAO userDAO = factory.getUserDAO();

        RequestParametersValidatorService validator = new RequestParametersValidatorService(req);

        String userEmail;
        String password;
        boolean remember;

        User user;

        try {
            userEmail = validator.validateAndGetString("email",
                    EMAIL_PATTERN,
                    new InvalidEmailParameterException());
            password = validator.validateAndGetString("password",
                    PASSWORD_PATTERN,
                    new InvalidPasswordParameterException());
            remember = validator.validateAndGetBoolean("remember");
        } catch (InvalidEmailParameterException | InvalidPasswordParameterException e) {
            resp.sendRedirect("signInAction?msg=" + e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            resp.sendRedirect("signInAction?msg=invalidParameters");
            return;
        }


        try {
            user = userDAO.findByEmailAndPassword(userEmail, password);
            LoginService.initializeSession(req, resp, user, remember);

        } catch (NoUserFoundException | IllegalPasswordException e) {
            resp.sendRedirect("signInAction?msg=" + e.getMessage() + "&email=" + userEmail);
            return;
        } catch (DBException e) {
            resp.sendRedirect("signInAction?msg=invalidParameters");
            return;
        }

        resp.sendRedirect("indexAction");

    }

}
