package ua.cc.spon.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.scheduler.CheckinCheckoutStatusChangerJob;
import ua.cc.spon.scheduler.ExpiringPaymentStatusChangerJob;
import ua.cc.spon.scheduler.InTimeExecutor;
import ua.cc.spon.scheduler.RegularExecutor;

import java.util.concurrent.TimeUnit;

@WebListener
public class BackgroundJobManager implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackgroundJobManager.class);

    private InTimeExecutor inTimeExecutor;
    private RegularExecutor regularExecutor;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext ctx = event.getServletContext();

        inTimeExecutor = new InTimeExecutor(new CheckinCheckoutStatusChangerJob(ctx));
        inTimeExecutor.startExecutionAt(12, 0, 0);

        regularExecutor = new RegularExecutor(new ExpiringPaymentStatusChangerJob(ctx));
        regularExecutor.startExecutionForEvery(1, TimeUnit.HOURS);

        LOGGER.info("Executors have started");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (inTimeExecutor.stop() && regularExecutor.stop()) {
            LOGGER.info("Executors stopped");
        }

    }
}


