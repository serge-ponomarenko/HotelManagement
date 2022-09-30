package ua.cc.spon.scheduler;

import jakarta.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.EntityTransaction;
import ua.cc.spon.db.dao.ReservationDAO;
import ua.cc.spon.exception.DaoException;

public class CheckinCheckoutStatusChangerJob implements ScheduledJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckinCheckoutStatusChangerJob.class);

    private final ServletContext ctx;

    public CheckinCheckoutStatusChangerJob(ServletContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void execute() {
        LOGGER.info("Updating checkin-checkout statuses");

        DAOFactory factory = (DAOFactory) ctx.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        ReservationDAO reservationDAO = factory.getReservationDAO();

        transaction.init(reservationDAO);

        try {
            reservationDAO.updateCheckinStatuses();
            reservationDAO.updateCheckoutStatuses();
        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            transaction.end();
        }

    }

}
