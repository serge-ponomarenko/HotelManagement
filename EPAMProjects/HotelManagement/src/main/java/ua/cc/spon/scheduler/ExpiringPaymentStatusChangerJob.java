package ua.cc.spon.scheduler;

import jakarta.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.EntityTransaction;
import ua.cc.spon.db.dao.ReservationDAO;
import ua.cc.spon.exception.DaoException;

public class ExpiringPaymentStatusChangerJob implements ScheduledJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiringPaymentStatusChangerJob.class);

    private final ServletContext ctx;

    public ExpiringPaymentStatusChangerJob(ServletContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void execute() {
        LOGGER.info("Updating expiring statuses");

        DAOFactory factory = (DAOFactory) ctx.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        ReservationDAO reservationDAO = factory.getReservationDAO();

        transaction.init(reservationDAO);

        try {
            reservationDAO.updateExpiredPaidStatuses();
        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            transaction.end();
        }

    }

}
