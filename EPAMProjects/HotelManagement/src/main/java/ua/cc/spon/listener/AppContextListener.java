package ua.cc.spon.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.exception.DBException;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext ctx = servletContextEvent.getServletContext();

        DAOFactory factory;
        try {
            factory = DAOFactory.getInstance();
        } catch (DBException e) {
            LOGGER.info("Obtain DAOFactory instance failed");
            throw new IllegalStateException("Obtain DAOFactory instance failed");
        }

        LOGGER.info("Connection to DataBase successfully");

        ctx.setAttribute("DAOFactory", factory);
    }

}
