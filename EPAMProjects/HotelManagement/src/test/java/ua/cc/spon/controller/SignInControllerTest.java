package ua.cc.spon.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import ua.cc.spon.db.dao.*;
import ua.cc.spon.db.dao.postgres.PostgresDAOFactory;
import ua.cc.spon.db.dao.postgres.PostgresLocaleDAO;
import ua.cc.spon.db.dao.postgres.PostgresUserDAO;
import ua.cc.spon.db.dao.postgres.PostgresUserSettingsDAO;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.service.LoginService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sergiy Ponomarenko
 */
class SignInControllerTest extends Mockito {

    @SneakyThrows
    @Test
    void doPostNormalAction() {
        SignInController controller = new SignInController();

        ServletContext context = mock(ServletContext.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        DAOFactory factory = mock(PostgresDAOFactory.class);

        UserDAO userDAO = mock(PostgresUserDAO.class);
        UserSettingsDAO userSettingsDAO = mock(PostgresUserSettingsDAO.class);
        LocaleDAO localeDAO = mock(PostgresLocaleDAO.class);;

        when(request.getServletContext()).thenReturn(context);
        when(context.getAttribute("DAOFactory")).thenReturn(factory);
        when(factory.getUserDAO()).thenReturn(userDAO);
        when(factory.getLocaleDAO()).thenReturn(localeDAO);
        when(factory.getUserSettingsDAO()).thenReturn(userSettingsDAO);

        User user = mock(User.class);
        UserSettings userSettings = mock(UserSettings.class);
        HttpSession session = mock(HttpSession.class);
        EntityTransaction transaction = mock(EntityTransaction.class);
        Connection connection = mock(Connection.class);
        transaction.setConnection(connection);

        String email = "ss@gmail.com";
        String password = "aaaaaaa123";
        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("password")).thenReturn(password);
        when(request.getSession()).thenReturn(session);

        when(userDAO.findByEmailAndPassword(email, password)).thenReturn(user);

        when(userSettingsDAO.findByUserId(anyInt())).thenReturn(userSettings);
        //when(request.getRequestDispatcher(PAGE)).thenReturn(dispatcher);

        //controller.setUserDaoImpl(userDAO);
        controller.doPost(request, response);

        //verify(transaction, times(1)).init(userDAO);
        verify(context, times(2)).getAttribute("DAOFactory");

        verify(request, times(1)).getParameter("email");
        verify(request, times(1)).getParameter("password");
        verify(request, times(1)).getParameter("remember");
        verify(response, times(1)).sendRedirect("indexAction");
        verify(request, atLeastOnce()).getSession();


        //verify(dispatcher).forward(request, response);
    }

    @Test
    void doGet() {
    }
}