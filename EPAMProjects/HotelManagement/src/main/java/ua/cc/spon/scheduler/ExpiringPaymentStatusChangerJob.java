package ua.cc.spon.scheduler;

import jakarta.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.ReservationDAO;

public class ExpiringPaymentStatusChangerJob implements ScheduledJob {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ServletContext ctx;

    public ExpiringPaymentStatusChangerJob(ServletContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void execute() {

        logger.info("Updating expiring statuses");

        DAOFactory factory = (DAOFactory) ctx.getAttribute("DAOFactory");
        ReservationDAO reservationDAO = factory.getReservationDAO();

        reservationDAO.updateExpiredPaidStatuses();

    }

}
