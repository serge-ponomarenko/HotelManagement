package ua.cc.spon.listener;

import ua.cc.spon.db.dao.DAOFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

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
