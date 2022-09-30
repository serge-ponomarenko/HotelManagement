package ua.cc.spon.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.cc.spon.db.dao.DAOFactory;
import ua.cc.spon.db.dao.EntityTransaction;
import ua.cc.spon.db.dao.ReservationDAO;
import ua.cc.spon.db.entity.Reservation;
import ua.cc.spon.db.entity.User;
import ua.cc.spon.db.entity.UserSettings;
import ua.cc.spon.exception.DaoException;

import java.util.List;

public class BookingsBadgeTagHandler extends TagSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingsBadgeTagHandler.class);

    @Override
    public int doStartTag() {
        ServletContext context = pageContext.getServletContext();
        DAOFactory factory = (DAOFactory) context.getAttribute("DAOFactory");
        EntityTransaction transaction = new EntityTransaction();

        ReservationDAO reservationDAO = factory.getReservationDAO();

        String locale = ((UserSettings) pageContext.getSession().getAttribute("userSettings")).getLocale();
        User user = ((User) pageContext.getSession().getAttribute("user"));

        List<Reservation> reservations = null;

        transaction.init(reservationDAO);
        try {
            reservations = reservationDAO.findByUser(user, locale);
        } catch (DaoException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            transaction.end();
        }

        if (reservations != null && !reservations.isEmpty()) {
            long count = reservations.stream().filter(r -> r.getStatus() == Reservation.Status.BOOKED).count();

            if (count > 0) {
                JspWriter out = pageContext.getOut();
                try {
                    out.print("<span class=\"badge bg-red\"></span>");
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return SKIP_BODY;
    }

}