package ua.cc.spon.scheduler;

import jakarta.servlet.ServletContext;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.ReservationDAO;

public class ExpiringPaymentStatusChangerJob implements ScheduledJob {

    private final ServletContext ctx;

    public ExpiringPaymentStatusChangerJob(ServletContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void execute() {

        System.out.println("###> Updating expiring statuses");

        DAOFactory factory = (DAOFactory) ctx.getAttribute("DAOFactory");
        ReservationDAO reservationDAO = factory.getReservationDAO();

        reservationDAO.updateExpiredPaidStatuses();

    }

}