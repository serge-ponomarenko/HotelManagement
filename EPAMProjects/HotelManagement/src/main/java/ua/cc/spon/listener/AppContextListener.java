package ua.cc.spon.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ua.cc.spon.db.dao.DAOFactory;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext ctx = servletContextEvent.getServletContext();

        DAOFactory factory = DAOFactory.getInstance();

        ctx.setAttribute("DAOFactory", factory);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

}
